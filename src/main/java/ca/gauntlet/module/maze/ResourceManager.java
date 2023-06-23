/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2020, dutta64 <https://github.com/dutta64>
 * Copyright (c) 2020, Anthony Alves
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

package ca.gauntlet.module.maze;

import ca.gauntlet.TheGauntletConfig;
import ca.gauntlet.TheGauntletConfig.TrackingMode;
import ca.gauntlet.TheGauntletPlugin;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.ObjectID;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;

@Singleton
class ResourceManager
{
	private static final int SHARD_COUNT_BREAK_DOWN = 80;

	private static final Pattern PATTERN_RESOURCE_DROP = Pattern.compile("^.+ drop:\\s+((?<quantity>\\d+) x )?(?<name>.+)$");

	private final Set<Resource> resources = new HashSet<>();

	private final Map<Resource, ResourceCounter> resourceCounters = new HashMap<>();

	@Inject
	private Client client;
	@Inject
	private TheGauntletPlugin plugin;
	@Inject
	private TheGauntletConfig config;
	@Inject
	private ItemManager itemManager;
	@Inject
	private InfoBoxManager infoBoxManager;
	@Inject
	private EventBus eventBus;

	private Region region = Region.UNKNOWN;

	void init()
	{
		region = Region.fromId(client.getMapRegions()[0]);

		if (config.resourceTracker() &&
			region != Region.UNKNOWN &&
			config.resourceTrackingMode() == TrackingMode.DECREMENT)
		{
			createInfoBoxCountersFromConfig();
		}
	}

	void reset()
	{
		region = Region.UNKNOWN;

		resources.clear();

		resourceCounters.clear();

		infoBoxManager.getInfoBoxes()
			.stream()
			.filter(ResourceCounter.class::isInstance)
			.forEach(eventBus::unregister);

		infoBoxManager.removeIf(ResourceCounter.class::isInstance);
	}

	void parseChatMessage(final String chatMessage)
	{
		if (!config.resourceTracker() || region == Region.UNKNOWN)
		{
			return;
		}

		if (chatMessage.startsWith("<"))
		{
			// Loot drops always start with a color tag
			// e.g. <col=005f00>Player recieved a drop: ...
			// e.g. <col=ef1020>Untradeable drop: ...
			processNpcResource(chatMessage);
		}
		else
		{
			processSkillResource(chatMessage);
		}
	}

	void remove(final ResourceCounter resourceCounter)
	{
		resources.remove(resourceCounter.getResource());
		eventBus.unregister(resourceCounter);
		infoBoxManager.removeInfoBox(resourceCounter);
	}

	boolean hasAcquiredResource(final ResourceEntity resourceEntity)
	{
		if (!config.resourceTracker() ||
			!config.resourceRemoveOutlineOnceAcquired() ||
			config.resourceTrackingMode() == TrackingMode.INCREMENT)
		{
			return false;
		}

		final Resource resource = getResourceFromObjectId(resourceEntity.getGameObject().getId());

		if (resource == null)
		{
			return false;
		}

		final ResourceCounter resourceCounter = resourceCounters.get(resource);

		if (resourceCounter == null)
		{
			return false;
		}

		return resourceCounter.getCount() == 0;
	}

	private void processNpcResource(final String parsedMessage)
	{
		final String noTagsMessage = Text.removeTags(parsedMessage);

		final Matcher matcher = PATTERN_RESOURCE_DROP.matcher(noTagsMessage);

		if (!matcher.matches())
		{
			return;
		}

		final String name = matcher.group("name");

		if (name == null)
		{
			return;
		}

		final Resource resource = Resource.fromName(name, region == Region.CORRUPTED);

		if (resource == null || !isTrackingResource(resource))
		{
			return;
		}

		final String quantity = matcher.group("quantity");

		final int count = quantity != null ? Integer.parseInt(quantity) : 1;

		processResource(resource, count);
	}

	private void processSkillResource(final String parsedMessage)
	{
		if (parsedMessage.startsWith("break down", 4))
		{
			final Resource resource = region == Region.CORRUPTED ?
				Resource.CORRUPTED_SHARDS : Resource.CRYSTAL_SHARDS;

			if (isTrackingResource(resource))
			{
				processResource(resource, SHARD_COUNT_BREAK_DOWN);
			}

			return;
		}

		final Map<Resource, Integer> mapping = Resource.fromPattern(parsedMessage, region == Region.CORRUPTED);

		if (mapping == null)
		{
			return;
		}

		final Resource resource = mapping.keySet().iterator().next();

		if (!isTrackingResource(resource))
		{
			return;
		}

		processResource(resource, mapping.get(resource));
	}

	private void processResource(final Resource resource, final int count)
	{
		if (resources.add(resource))
		{
			final ResourceCounter resourceCounter = new ResourceCounter(
				resource,
				plugin,
				itemManager.getImage(resource.getItemId()),
				this,
				count,
				config.resourceTrackingMode() == TrackingMode.DECREMENT
			);

			eventBus.register(resourceCounter);
			infoBoxManager.addInfoBox(resourceCounter);
			resourceCounters.put(resource, resourceCounter);
		}
		else
		{
			eventBus.post(new ResourceEvent(resource, count));
		}
	}

	private void createInfoBoxCountersFromConfig()
	{
		final int oreCount = config.resourceOre();
		final int barkCount = config.resourceBark();
		final int tirinumCount = config.resourceTirinum();
		final int grymCount = config.resourceGrym();
		final int frameCount = config.resourceFrame();
		final int fishCount = config.resourcePaddlefish();
		final int shardCount = config.resourceShard();

		final boolean bowstring = config.resourceBowstring();
		final boolean spike = config.resourceSpike();
		final boolean orb = config.resourceOrb();

		final boolean corrupted = region == Region.CORRUPTED;

		if (oreCount > 0)
		{
			processResource(corrupted ? Resource.CORRUPTED_ORE : Resource.CRYSTAL_ORE, oreCount);
		}
		if (barkCount > 0)
		{
			processResource(corrupted ? Resource.CORRUPTED_PHREN_BARK : Resource.PHREN_BARK, barkCount);
		}
		if (tirinumCount > 0)
		{
			processResource(corrupted ? Resource.CORRUPTED_LINUM_TIRINUM : Resource.LINUM_TIRINUM, tirinumCount);
		}
		if (grymCount > 0)
		{
			processResource(corrupted ? Resource.CORRUPTED_GRYM_LEAF : Resource.GRYM_LEAF, grymCount);
		}
		if (frameCount > 0)
		{
			processResource(corrupted ? Resource.CORRUPTED_WEAPON_FRAME : Resource.WEAPON_FRAME, frameCount);
		}
		if (fishCount > 0)
		{
			processResource(Resource.RAW_PADDLEFISH, fishCount);
		}
		if (shardCount > 0)
		{
			processResource(corrupted ? Resource.CORRUPTED_SHARDS : Resource.CRYSTAL_SHARDS, shardCount);
		}
		if (bowstring)
		{
			processResource(corrupted ? Resource.CORRUPTED_BOWSTRING : Resource.CRYSTALLINE_BOWSTRING, 1);
		}
		if (spike)
		{
			processResource(corrupted ? Resource.CORRUPTED_SPIKE : Resource.CRYSTAL_SPIKE, 1);
		}
		if (orb)
		{
			processResource(corrupted ? Resource.CORRUPTED_ORB : Resource.CRYSTAL_ORB, 1);
		}
	}

	private Resource getResourceFromObjectId(final int objectId)
	{
		switch (objectId)
		{
			case ObjectID.CRYSTAL_DEPOSIT:
				return Resource.CRYSTAL_ORE;
			case ObjectID.CORRUPT_DEPOSIT:
				return Resource.CORRUPTED_ORE;

			case ObjectID.PHREN_ROOTS:
				return Resource.PHREN_BARK;
			case ObjectID.CORRUPT_PHREN_ROOTS:
				return Resource.CORRUPTED_PHREN_BARK;

			case ObjectID.LINUM_TIRINUM:
				return Resource.LINUM_TIRINUM;
			case ObjectID.CORRUPT_LINUM_TIRINUM:
				return Resource.CORRUPTED_LINUM_TIRINUM;

			case ObjectID.GRYM_ROOT:
				return Resource.GRYM_LEAF;
			case ObjectID.CORRUPT_GRYM_ROOT:
				return Resource.CORRUPTED_GRYM_LEAF;

			case ObjectID.CORRUPT_FISHING_SPOT:
			case ObjectID.FISHING_SPOT_36068:
				return Resource.RAW_PADDLEFISH;

			default:
				return null;
		}
	}

	private int getResourceTargetCount(final Resource resource)
	{
		switch (resource)
		{
			case CRYSTAL_ORE:
			case CORRUPTED_ORE:
				return config.resourceOre();
			case PHREN_BARK:
			case CORRUPTED_PHREN_BARK:
				return config.resourceBark();
			case LINUM_TIRINUM:
			case CORRUPTED_LINUM_TIRINUM:
				return config.resourceTirinum();
			case GRYM_LEAF:
			case CORRUPTED_GRYM_LEAF:
				return config.resourceGrym();
			case CRYSTAL_SHARDS:
			case CORRUPTED_SHARDS:
				return config.resourceShard();
			case RAW_PADDLEFISH:
				return config.resourcePaddlefish();
			case WEAPON_FRAME:
			case CORRUPTED_WEAPON_FRAME:
				return config.resourceFrame();
			case CRYSTALLINE_BOWSTRING:
			case CORRUPTED_BOWSTRING:
				return config.resourceBowstring() ? 1 : 0;
			case CRYSTAL_SPIKE:
			case CORRUPTED_SPIKE:
				return config.resourceSpike() ? 1 : 0;
			case CRYSTAL_ORB:
			case CORRUPTED_ORB:
				return config.resourceOrb() ? 1 : 0;
			case TELEPORT_CRYSTAL:
			case CORRUPTED_TELEPORT_CRYSTAL:
			default:
				return 0;
		}
	}

	private boolean isTrackingResource(final Resource resource)
	{
		if (config.resourceTrackingMode() == TrackingMode.DECREMENT)
		{
			return resources.contains(resource);
		}

		return getResourceTargetCount(resource) > 0;
	}

	private enum Region
	{
		NORMAL,
		CORRUPTED,
		UNKNOWN;

		private static Region fromId(final int id)
		{
			switch (id)
			{
				case 7512:
					return NORMAL;
				case 7768:
					return CORRUPTED;
				default:
					return UNKNOWN;
			}
		}
	}
}
