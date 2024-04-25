/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2023, rdutta <https://github.com/rdutta>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ca.gauntlet.module.boss;

import ca.gauntlet.TheGauntletConfig;
import ca.gauntlet.TheGauntletPlugin;
import ca.gauntlet.module.Module;
import ca.gauntlet.module.overlay.TimerOverlay;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.runelite.api.Player;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.game.npcoverlay.NpcOverlayService;
import net.runelite.client.ui.overlay.OverlayManager;

@Singleton
public final class BossModule implements Module
{
	private static final List<Integer> TORNADO_IDS = List.of(NullNpcID.NULL_9025, NullNpcID.NULL_9039);
	private final Function<NPC, HighlightedNpc> npcHighlighter = this::highlightNpc;

	@Getter(AccessLevel.PACKAGE)
	private final List<NPC> tornadoes = new ArrayList<>();

	@Inject
	private EventBus eventBus;
	@Inject
	private Client client;
	@Inject
	private TheGauntletConfig config;
	@Inject
	private NpcOverlayService npcOverlayService;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private TimerOverlay timerOverlay;
	@Inject
	private BossOverlay bossOverlay;
	@Inject
	private BossCounter bossCounter;
	@Inject
	private TheGauntletPlugin theGauntletPlugin;
	private String lastKnownPrayer = "MAGIX"; // Temp as this will change on entering the gauntlent.
	public NPC currentNPC;

	@Override
	public void start()
	{
		eventBus.register(this);
		npcOverlayService.registerHighlighter(npcHighlighter);
		overlayManager.add(timerOverlay);
		overlayManager.add(bossOverlay);
		overlayManager.add(bossCounter);
		timerOverlay.setHunllefStart();
	}

	@Override
	public void stop()
	{
		eventBus.unregister(this);
		npcOverlayService.unregisterHighlighter(npcHighlighter);
		overlayManager.remove(timerOverlay);
		overlayManager.remove(bossOverlay);
		overlayManager.remove(bossCounter);
		bossCounter.resetBossAttackCount();
		bossCounter.resetPlayerAttackCount();
		bossCounter.resetPrayer();
		theGauntletPlugin.resetAnimCount();
		theGauntletPlugin.resetIdCount();
		theGauntletPlugin.resetHunIdCount();
		timerOverlay.reset();
		tornadoes.clear();
	}

	@Subscribe
	void onGameStateChanged(final GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOGIN_SCREEN:
			case HOPPING:
				stop();
				break;
		}
	}

	@Subscribe
	void onActorDeath(final ActorDeath event)
	{
		if (event.getActor() == client.getLocalPlayer())
		{
			timerOverlay.onPlayerDeath();
		}
	}

	@Subscribe
	void onNpcSpawned(final NpcSpawned event)
	{
		final NPC npc = event.getNpc();

		if (TORNADO_IDS.contains(npc.getId()))
		{
			tornadoes.add(npc);
		}
	}

	@Subscribe
	void onNpcDespawned(final NpcDespawned event)
	{
		final NPC npc = event.getNpc();

		if (TORNADO_IDS.contains(npc.getId()))
		{
			tornadoes.removeIf(t -> t == npc);
		}
	}

	@Subscribe
	void onAnimationChanged(final AnimationChanged event)
	{
		if (event.getActor() instanceof NPC)
		{
			final NPC npc = (NPC) event.getActor();
			currentNPC = npc;

			if(npc == null)
				return;

			switch (npc.getAnimation())
			{
				case 8418:
				case 8419:
					bossCounter.incrementBossAttack();
					break;
				case 8754:
					bossCounter.updateBossAttackStyle("MAGIC");
					break;
				case 8755:
					bossCounter.updateBossAttackStyle("RANGE");
			}
		}
		else if (event.getActor() instanceof Player)
		{
			final Player player = (Player) event.getActor();
			final int playerAnimation = player.getAnimation();

			if(currentNPC == null)
				return;
			// First if statement is checking that the players attack is valid enough to be counted
			// e.a. We attack with melee and the boss is NOT praying melee (it can pray magic or range)
			if ((playerAnimation == 428 && (currentNPC.getId() != NpcID.CORRUPTED_HUNLLEF)) ||
					(playerAnimation == 426 && (currentNPC.getId() != NpcID.CORRUPTED_HUNLLEF_9036)) ||
					(playerAnimation == 1167 && (currentNPC.getId() != NpcID.CORRUPTED_HUNLLEF_9037)))
			{
				/* Sometimes this game is stupid and doesn't count the attack animation:
				 - if the player splashes with magic it will never count this attack though the animation played. I guess
				 the animation is only registered on a hit.
				 - if the player attacks too quickly entering the room, the animationID from entering the doorway will
				 override the attack animation, so the attack is never registered though we did attack.

				The following if statements account for the edge case that any attack style wasn't accounted for at some point during
				the fight. It checks the current prayer that the boss is praying, and compares it to the lastKnownPrayer. If the lastKnownPrayer
				is different from what it is currently praying, then at some point an attack from the player wasn't registered. We update
				the last known prayer to what the boss is currently praying, and reset the player attack count as it will be out of sync if any of these cases pass.
				 */
				if(currentNPC.getId() == NpcID.CORRUPTED_HUNLLEF && (!lastKnownPrayer.equals("MELEE")))
				{
					bossCounter.resetPlayerAttackCount();
					lastKnownPrayer = "MELEE";
				}
				else if(currentNPC.getId() == NpcID.CORRUPTED_HUNLLEF_9036 && (!lastKnownPrayer.equals("RANGE")))
				{
					bossCounter.resetPlayerAttackCount();
					lastKnownPrayer = "RANGE";
				}
				else if(currentNPC.getId() == NpcID.CORRUPTED_HUNLLEF_9037 && (!lastKnownPrayer.equals("MAGIC")))
				{
					bossCounter.resetPlayerAttackCount();
					lastKnownPrayer = "MAGIC";
				}
				bossCounter.incrementPlayerAttackCount();
			}
		}
	}

	private HighlightedNpc highlightNpc(final NPC npc)
	{
		final int id = npc.getId();

		switch (id)
		{
			case NpcID.CRYSTALLINE_HUNLLEF:
			case NpcID.CRYSTALLINE_HUNLLEF_9022:
			case NpcID.CRYSTALLINE_HUNLLEF_9023:
			case NpcID.CRYSTALLINE_HUNLLEF_9024:
			case NpcID.CORRUPTED_HUNLLEF:
			case NpcID.CORRUPTED_HUNLLEF_9036:
			case NpcID.CORRUPTED_HUNLLEF_9037:
			case NpcID.CORRUPTED_HUNLLEF_9038:
				return HighlightedNpc.builder()
					.npc(npc)
					.tile(true)
					.borderWidth(config.hunllefTileOutlineWidth())
					.fillColor(config.hunllefFillColor())
					.highlightColor(config.hunllefOutlineColor())
					.render(n -> config.hunllefTileOutline() && !npc.isDead())
					.build();
			default:
				return null;
		}
	}
}
