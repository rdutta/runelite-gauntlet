package ca.gauntlet.module.maze;

import ca.gauntlet.TheGauntletConfig;
import ca.gauntlet.module.Module;
import ca.gauntlet.module.overlay.TimerOverlay;
import com.google.common.collect.ImmutableSet;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.game.npcoverlay.NpcOverlayService;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@Singleton
public class MazeModule implements Module
{
	private static final Set<Integer> RESOURCE_IDS = ImmutableSet.of(
		ObjectID.CRYSTAL_DEPOSIT, ObjectID.CORRUPT_DEPOSIT,
		ObjectID.PHREN_ROOTS, ObjectID.CORRUPT_PHREN_ROOTS,
		ObjectID.FISHING_SPOT_36068, ObjectID.CORRUPT_FISHING_SPOT,
		ObjectID.GRYM_ROOT, ObjectID.CORRUPT_GRYM_ROOT,
		ObjectID.LINUM_TIRINUM, ObjectID.CORRUPT_LINUM_TIRINUM
	);

	private static final Set<Integer> UTILITY_IDS = ImmutableSet.of(
		ObjectID.SINGING_BOWL_35966, ObjectID.SINGING_BOWL_36063,
		ObjectID.RANGE_35980, ObjectID.RANGE_36077,
		ObjectID.WATER_PUMP_35981, ObjectID.WATER_PUMP_36078
	);

	@Getter(AccessLevel.PACKAGE)
	private final Set<ResourceEntity> resourceEntities = new HashSet<>();
	@Getter(AccessLevel.PACKAGE)
	private final Set<Demiboss> demiBosses = new HashSet<>();
	@Getter(AccessLevel.PACKAGE)
	private final Set<GameObject> utilities = new HashSet<>();
	private final Function<NPC, HighlightedNpc> npcHighlighter = this::highlightNpc;

	@Inject
	private EventBus eventBus;
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private TheGauntletConfig config;
	@Inject
	private NpcOverlayService npcOverlayService;
	@Inject
	private ResourceManager resourceManager;
	@Inject
	private SkillIconManager skillIconManager;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private MazeOverlay mazeOverlay;
	@Inject
	private MinimapOverlay minimapOverlay;
	@Inject
	private TimerOverlay timerOverlay;

	@Override
	public void start()
	{
		eventBus.register(this);
		npcOverlayService.registerHighlighter(npcHighlighter);
		overlayManager.add(mazeOverlay);
		overlayManager.add(minimapOverlay);
		overlayManager.add(timerOverlay);
	}

	@Override
	public void stop()
	{
		eventBus.unregister(this);
		npcOverlayService.unregisterHighlighter(npcHighlighter);
		overlayManager.remove(mazeOverlay);
		overlayManager.remove(minimapOverlay);
		overlayManager.remove(timerOverlay);
		resourceManager.reset();
		resourceEntities.clear();
		utilities.clear();
		demiBosses.clear();
	}

	@Subscribe
	private void onConfigChanged(final ConfigChanged event)
	{
		if (!event.getGroup().equals(TheGauntletConfig.CONFIG_GROUP))
		{
			return;
		}

		clientThread.invoke(() -> {
			switch (event.getKey())
			{
				case "oreDepositOutlineColor":
					if (!resourceEntities.isEmpty())
					{
						resourceEntities.stream()
							.filter(r -> r.isResourceType(ResourceEntity.ResourceType.ORE_DEPOSIT))
							.forEach(r -> r.setOutlineColor(config.oreDepositOutlineColor()));
					}
					break;
				case "oreDepositFillColor":
					if (!resourceEntities.isEmpty())
					{
						resourceEntities.stream()
							.filter(r -> r.isResourceType(ResourceEntity.ResourceType.ORE_DEPOSIT))
							.forEach(r -> r.setFillColor(config.oreDepositFillColor()));
					}
					break;
				case "phrenRootsOutlineColor":
					if (!resourceEntities.isEmpty())
					{
						resourceEntities.stream()
							.filter(r -> r.isResourceType(ResourceEntity.ResourceType.PHREN_ROOTS))
							.forEach(r -> r.setOutlineColor(config.phrenRootsOutlineColor()));
					}
					break;
				case "phrenRootsFillColor":
					if (!resourceEntities.isEmpty())
					{
						resourceEntities.stream()
							.filter(r -> r.isResourceType(ResourceEntity.ResourceType.PHREN_ROOTS))
							.forEach(r -> r.setFillColor(config.phrenRootsFillColor()));
					}
					break;
				case "linumTirinumOutlineColor":
					if (!resourceEntities.isEmpty())
					{
						resourceEntities.stream()
							.filter(r -> r.isResourceType(ResourceEntity.ResourceType.LINUM_TIRINUM))
							.forEach(r -> r.setOutlineColor(config.linumTirinumOutlineColor()));
					}
					break;
				case "linumTirinumFillColor":
					if (!resourceEntities.isEmpty())
					{
						resourceEntities.stream()
							.filter(r -> r.isResourceType(ResourceEntity.ResourceType.LINUM_TIRINUM))
							.forEach(r -> r.setFillColor(config.linumTirinumFillColor()));
					}
					break;
				case "grymRootOutlineColor":
					if (!resourceEntities.isEmpty())
					{
						resourceEntities.stream()
							.filter(r -> r.isResourceType(ResourceEntity.ResourceType.GRYM_ROOT))
							.forEach(r -> r.setOutlineColor(config.grymRootOutlineColor()));
					}
					break;
				case "grymRootFillColor":
					if (!resourceEntities.isEmpty())
					{
						resourceEntities.stream()
							.filter(r -> r.isResourceType(ResourceEntity.ResourceType.GRYM_ROOT))
							.forEach(r -> r.setFillColor(config.grymRootFillColor()));
					}
					break;
				case "fishingSpotOutlineColor":
					if (!resourceEntities.isEmpty())
					{
						resourceEntities.stream()
							.filter(r -> r.isResourceType(ResourceEntity.ResourceType.FISHING_SPOT))
							.forEach(r -> r.setOutlineColor(config.fishingSpotOutlineColor()));
					}
					break;
				case "fishingSpotFillColor":
					if (!resourceEntities.isEmpty())
					{
						resourceEntities.stream()
							.filter(r -> r.isResourceType(ResourceEntity.ResourceType.FISHING_SPOT))
							.forEach(r -> r.setFillColor(config.fishingSpotFillColor()));
					}
					break;
				case "resourceIconSize":
					if (!resourceEntities.isEmpty())
					{
						resourceEntities.forEach(r -> r.setIconSize(config.resourceIconSize()));
					}
					break;
				case "resourceTracker":
				case "resourceTrackingMode":
					resourceManager.reset();
					resourceManager.init();
					break;
				default:
					npcOverlayService.rebuild();
					break;
			}
		});
	}

	@Subscribe
	private void onGameStateChanged(final GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOADING:
				resourceEntities.clear();
				utilities.clear();
				break;
			case LOGIN_SCREEN:
			case HOPPING:
				stop();
				break;
		}
	}

	@Subscribe
	private void onWidgetLoaded(final WidgetLoaded event)
	{
		if (event.getGroupId() == WidgetID.GAUNTLET_TIMER_GROUP_ID)
		{
			resourceManager.init();
			timerOverlay.setGauntletStart();
		}
	}

	@Subscribe
	private void onGameObjectSpawned(final GameObjectSpawned event)
	{
		final GameObject gameObject = event.getGameObject();

		final int id = gameObject.getId();

		if (RESOURCE_IDS.contains(id))
		{
			final ResourceEntity.ResourceType resourceType;
			final Color outlineColor;
			final Color fillColor;

			switch (id)
			{
				case ObjectID.CRYSTAL_DEPOSIT:
				case ObjectID.CORRUPT_DEPOSIT:
					resourceType = ResourceEntity.ResourceType.ORE_DEPOSIT;
					outlineColor = config.oreDepositOutlineColor();
					fillColor = config.oreDepositFillColor();
					break;
				case ObjectID.PHREN_ROOTS:
				case ObjectID.CORRUPT_PHREN_ROOTS:
					resourceType = ResourceEntity.ResourceType.PHREN_ROOTS;
					outlineColor = config.phrenRootsOutlineColor();
					fillColor = config.phrenRootsFillColor();
					break;
				case ObjectID.FISHING_SPOT_36068:
				case ObjectID.CORRUPT_FISHING_SPOT:
					resourceType = ResourceEntity.ResourceType.FISHING_SPOT;
					outlineColor = config.fishingSpotOutlineColor();
					fillColor = config.fishingSpotFillColor();
					break;
				case ObjectID.GRYM_ROOT:
				case ObjectID.CORRUPT_GRYM_ROOT:
					resourceType = ResourceEntity.ResourceType.GRYM_ROOT;
					outlineColor = config.grymRootOutlineColor();
					fillColor = config.grymRootFillColor();
					break;
				case ObjectID.LINUM_TIRINUM:
				case ObjectID.CORRUPT_LINUM_TIRINUM:
					resourceType = ResourceEntity.ResourceType.LINUM_TIRINUM;
					outlineColor = config.linumTirinumOutlineColor();
					fillColor = config.linumTirinumFillColor();
					break;
				default:
					throw new IllegalArgumentException("Unsupported resource id: " + id);
			}

			resourceEntities.add(new ResourceEntity(resourceType, gameObject, skillIconManager,
				config.resourceIconSize(), outlineColor, fillColor));
		}
		else if (UTILITY_IDS.contains(id))
		{
			utilities.add(gameObject);
		}
	}

	@Subscribe
	private void onGameObjectDespawned(final GameObjectDespawned event)
	{
		final GameObject gameObject = event.getGameObject();

		final int id = gameObject.getId();

		if (RESOURCE_IDS.contains(gameObject.getId()))
		{
			resourceEntities.removeIf(o -> o.getGameObject() == gameObject);
		}
		else if (UTILITY_IDS.contains(id))
		{
			utilities.remove(gameObject);
		}
	}

	@Subscribe
	private void onNpcSpawned(final NpcSpawned event)
	{
		final NPC npc = event.getNpc();

		switch (npc.getId())
		{
			case NpcID.CRYSTALLINE_BEAR:
			case NpcID.CORRUPTED_BEAR:
			case NpcID.CRYSTALLINE_DARK_BEAST:
			case NpcID.CORRUPTED_DARK_BEAST:
			case NpcID.CRYSTALLINE_DRAGON:
			case NpcID.CORRUPTED_DRAGON:
				demiBosses.add(new Demiboss(npc, skillIconManager));
			default:
				break;
		}
	}

	@Subscribe
	private void onNpcDespawned(final NpcDespawned event)
	{
		final NPC npc = event.getNpc();

		switch (npc.getId())
		{
			case NpcID.CRYSTALLINE_BEAR:
			case NpcID.CORRUPTED_BEAR:
			case NpcID.CRYSTALLINE_DARK_BEAST:
			case NpcID.CORRUPTED_DARK_BEAST:
			case NpcID.CRYSTALLINE_DRAGON:
			case NpcID.CORRUPTED_DRAGON:
				demiBosses.removeIf(d -> d.getNpc() == npc);
			default:
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
	private void onChatMessage(final ChatMessage event)
	{
		final ChatMessageType type = event.getType();

		if (type == ChatMessageType.SPAM || type == ChatMessageType.GAMEMESSAGE)
		{
			resourceManager.parseChatMessage(event.getMessage());
		}
	}

	private HighlightedNpc highlightNpc(final NPC npc)
	{
		final int id = npc.getId();

		final int borderWidth;
		final Color highlightColor;

		switch (id)
		{
			case NpcID.CRYSTALLINE_BAT:
			case NpcID.CORRUPTED_BAT:
			case NpcID.CRYSTALLINE_RAT:
			case NpcID.CORRUPTED_RAT:
			case NpcID.CRYSTALLINE_SPIDER:
			case NpcID.CORRUPTED_SPIDER:
				return HighlightedNpc.builder()
					.npc(npc)
					.outline(true)
					.borderWidth(config.weakNpcOutlineWidth())
					.highlightColor(config.weakNpcOutlineColor())
					.render(n -> config.weakNpcOutline() && !npc.isDead())
					.build();
			case NpcID.CRYSTALLINE_SCORPION:
			case NpcID.CORRUPTED_SCORPION:
			case NpcID.CRYSTALLINE_UNICORN:
			case NpcID.CORRUPTED_UNICORN:
			case NpcID.CRYSTALLINE_WOLF:
			case NpcID.CORRUPTED_WOLF:
				return HighlightedNpc.builder()
					.npc(npc)
					.outline(true)
					.borderWidth(config.strongNpcOutlineWidth())
					.highlightColor(config.strongNpcOutlineColor())
					.render(n -> config.strongNpcOutline() && !npc.isDead())
					.build();
			case NpcID.CRYSTALLINE_BEAR:
			case NpcID.CORRUPTED_BEAR:
				borderWidth = config.demibossOutlineWidth();
				highlightColor = config.bearOutlineColor();
				break;
			case NpcID.CRYSTALLINE_DARK_BEAST:
			case NpcID.CORRUPTED_DARK_BEAST:
				borderWidth = config.demibossOutlineWidth();
				highlightColor = config.darkBeastOutlineColor();
				break;
			case NpcID.CRYSTALLINE_DRAGON:
			case NpcID.CORRUPTED_DRAGON:
				borderWidth = config.demibossOutlineWidth();
				highlightColor = config.dragonOutlineColor();
				break;
			default:
				return null;
		}

		return HighlightedNpc.builder()
			.npc(npc)
			.outline(true)
			.borderWidth(borderWidth)
			.highlightColor(highlightColor)
			.render(n -> config.demibossOutline() && !npc.isDead())
			.build();
	}
}
