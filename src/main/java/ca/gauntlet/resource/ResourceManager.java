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

package ca.gauntlet.resource;

import ca.gauntlet.TheGauntletConfig;
import ca.gauntlet.TheGauntletPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Singleton;

import ca.gauntlet.entity.ResourceEntity;
import net.runelite.api.Client;
import net.runelite.api.ObjectID;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;

@Singleton
public class ResourceManager
{
	private static final int SHARD_COUNT_BREAK_DOWN = 80;

	private static final Pattern PATTERN_RESOURCE_DROP = Pattern.compile("^.+ drop: ((?<quantity>\\d+) x )?(?<name>.+)$");

	private final Set<Resource> resources = new HashSet<>();

	private final HashMap<Resource, ResourceCounter> resourceCounters = new HashMap<>();

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

	public void init()
	{
		region = Region.fromId(client.getMapRegions()[0]);

		if (config.resourceTracker() && region != Region.UNKNOWN)
		{
			createInfoBoxCountersFromConfig();
		}
	}

	public void reset()
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

	public void parseChatMessage(final String chatMessage)
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

	public void remove(final ResourceCounter resourceCounter)
	{
		resources.remove(resourceCounter.getResource());
		eventBus.unregister(resourceCounter);
		infoBoxManager.removeInfoBox(resourceCounter);
	}

	public boolean hasAcquiredResource(final ResourceEntity resourceEntity)
	{
		final Resource resource = getResourceFromObjectId(resourceEntity.getGameObject().getId());

		if (resource == null)
		{
			return false;
		}

		return getResourceCount(resource) <= 0;
	}

	public int getResourceCount(final Resource resource)
	{
		final ResourceCounter resourceCounter = resourceCounters.get(resource);

		if (resourceCounter == null)
		{
			return 0;
		}

		return resourceCounter.getCount();
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

		if (resource == null || !resources.contains(resource))
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
			processResource(region == Region.CORRUPTED ?
				Resource.CORRUPTED_SHARDS : Resource.CRYSTAL_SHARDS, SHARD_COUNT_BREAK_DOWN);
			return;
		}

		final Map<Resource, Integer> mapping = Resource.fromPattern(parsedMessage, region == Region.CORRUPTED);

		if (mapping == null)
		{
			return;
		}

		final Resource resource = mapping.keySet().iterator().next();

		if (!resources.contains(resource))
		{
			return;
		}

		final int count = mapping.get(resource);

		processResource(resource, count);
	}

	private void processResource(final Resource resource, final int count)
	{
		if (resources.add(resource))
		{
			final ResourceCounter resourceCounter = new ResourceCounter(
				resource,
				itemManager.getImage(resource.getItemId()),
				count,
				plugin,
				this
			);

			eventBus.register(resourceCounter);
			infoBoxManager.addInfoBox(resourceCounter);
			resourceCounters.put(resource, resourceCounter);
		}
		else
		{
			eventBus.post(new ResourceEvent(resource, count * -1));
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

			case ObjectID.PHREN_ROOTS_36066:
				return Resource.PHREN_BARK;
			case ObjectID.PHREN_ROOTS:
				return Resource.CORRUPTED_PHREN_BARK;

			case ObjectID.LINUM_TIRINUM_36072:
				return Resource.LINUM_TIRINUM;
			case ObjectID.LINUM_TIRINUM:
				return Resource.CORRUPTED_LINUM_TIRINUM;

			case ObjectID.GRYM_ROOT_36070:
				return Resource.GRYM_LEAF;
			case ObjectID.GRYM_ROOT:
				return Resource.CORRUPTED_GRYM_LEAF;

			case ObjectID.FISHING_SPOT_35971:
			case ObjectID.FISHING_SPOT_36068:
				return Resource.RAW_PADDLEFISH;

			default:
				return null;
		}
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
