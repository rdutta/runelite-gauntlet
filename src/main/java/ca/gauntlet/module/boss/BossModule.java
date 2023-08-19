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
import ca.gauntlet.module.Module;
import ca.gauntlet.module.overlay.TimerOverlay;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.NullNpcID;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
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

	@Override
	public void start()
	{
		eventBus.register(this);
		npcOverlayService.registerHighlighter(npcHighlighter);
		overlayManager.add(timerOverlay);
		overlayManager.add(bossOverlay);
		timerOverlay.setHunllefStart();
	}

	@Override
	public void stop()
	{
		eventBus.unregister(this);
		npcOverlayService.unregisterHighlighter(npcHighlighter);
		overlayManager.remove(timerOverlay);
		overlayManager.remove(bossOverlay);
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
