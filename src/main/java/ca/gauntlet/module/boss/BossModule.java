package ca.gauntlet.module.boss;

import ca.gauntlet.TheGauntletConfig;
import ca.gauntlet.module.Module;
import ca.gauntlet.module.overlay.TimerOverlay;
import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Singleton
public class BossModule implements Module
{
	private static final Set<Integer> TORNADO_IDS = ImmutableSet.of(NullNpcID.NULL_9025, NullNpcID.NULL_9039);
	private final Function<NPC, HighlightedNpc> npcHighlighter = this::highlightNpc;

	@Getter(AccessLevel.PACKAGE)
	private final Set<NPC> tornadoes = new HashSet<>();

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
	private void onGameStateChanged(final GameStateChanged event)
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
	private void onActorDeath(final ActorDeath event)
	{
		if (event.getActor() == client.getLocalPlayer())
		{
			timerOverlay.onPlayerDeath();
		}
	}

	@Subscribe
	private void onNpcSpawned(final NpcSpawned event)
	{
		final NPC npc = event.getNpc();

		if (TORNADO_IDS.contains(npc.getId()))
		{
			tornadoes.add(npc);
		}
	}

	@Subscribe
	private void onNpcDespawned(final NpcDespawned event)
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
