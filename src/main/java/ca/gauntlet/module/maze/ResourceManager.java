/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2023, rdutta <https://github.com/rdutta>
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
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.AbstractMap;
import java.util.EnumMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;
import net.runelite.client.util.Text;

@Slf4j
@Singleton
class ResourceManager
{
	private static final Pattern PATTERN_RESOURCE_DROP = Pattern.compile("^.+ drop:\\s+((?<quantity>\\d+) x )?(?<name>.+)$");
	private static final int SHARD_COUNT_BREAK_DOWN = 80;
	private final EnumMap<Resource, ResourceCounter> counterByResource = new EnumMap<>(Resource.class);

	@Inject
	private TheGauntletPlugin plugin;
	@Inject
	private TheGauntletConfig config;
	@Inject
	private ItemManager itemManager;
	@Inject
	private InfoBoxManager infoBoxManager;

	private Region region = Region.UNKNOWN;

	void init(final int regionId)
	{
		region = Region.fromId(regionId);

		if (region != Region.UNKNOWN && config.resourceTracker())
		{
			createResourceCountersFromConfig();
		}
	}

	void reset()
	{
		region = Region.UNKNOWN;
		counterByResource.clear();
		infoBoxManager.removeIf(ResourceCounter.class::isInstance);
	}

	void parseChatMessage(final String chatMessage)
	{
		if (!config.resourceTracker() || region == Region.UNKNOWN)
		{
			return;
		}

		if (chatMessage.charAt(0) == '<')
		{
			// Loot drops always start with a color tag
			// e.g. <col=005f00>Player recieved a drop: ...
			// e.g. <col=ef1020>Untradeable drop: ...
			parseNpcChatMessage(chatMessage);
		}
		else
		{
			parseSkillChatMessage(chatMessage);
		}
	}

	boolean hasAcquired(final Resource resource)
	{
		final ResourceCounter resourceCounter = counterByResource.get(resource);
		return resourceCounter == null ? config.resourceRemoveAcquired() : resourceCounter.hasAcquiredTarget();
	}

	private void parseNpcChatMessage(final String message)
	{
		final String noTagsMessage = Text.removeTags(message);

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

		if (resource == null)
		{
			return;
		}

		final String quantity = matcher.group("quantity");

		final int count = quantity != null ? Integer.parseInt(quantity) : 1;

		updateResourceCounter(resource, count);
	}

	private void parseSkillChatMessage(final String message)
	{
		if (message.startsWith("break down", 4))
		{
			final Resource resource = region == Region.CORRUPTED ?
				Resource.CORRUPTED_SHARDS : Resource.CRYSTAL_SHARDS;

			updateResourceCounter(resource, SHARD_COUNT_BREAK_DOWN);
			return;
		}

		final AbstractMap.Entry<Resource, Integer> mapping = Resource.fromPattern(message, region == Region.CORRUPTED);

		if (mapping == null)
		{
			return;
		}

		updateResourceCounter(mapping.getKey(), mapping.getValue());
	}

	private void updateResourceCounter(final Resource resource, final int count)
	{
		final ResourceCounter resourceCounter = counterByResource.get(resource);

		if (resourceCounter == null)
		{
			return;
		}

		resourceCounter.updateCount(count);

		if (config.resourceRemoveAcquired() && resourceCounter.hasAcquiredTarget())
		{
			counterByResource.remove(resource);
			infoBoxManager.removeInfoBox(resourceCounter);
		}
	}

	private void createResourceCounter(final Resource resource, final int count)
	{
		if (count <= 0)
		{
			return;
		}

		final boolean decrement = config.resourceTrackingMode() == TrackingMode.DECREMENT;

		final ResourceCounter resourceCounter = new ResourceCounter(
			itemManager.getImage(resource.getItemId()),
			plugin,
			resource,
			decrement ? count : 0,
			decrement ? 0 : count
		);

		counterByResource.put(resource, resourceCounter);
		infoBoxManager.addInfoBox(resourceCounter);
	}

	private void createResourceCountersFromConfig()
	{
		final boolean corrupted = region == Region.CORRUPTED;
		createResourceCounter(corrupted ? Resource.CORRUPTED_ORE : Resource.CRYSTAL_ORE, config.resourceOre());
		createResourceCounter(corrupted ? Resource.CORRUPTED_PHREN_BARK : Resource.PHREN_BARK, config.resourceBark());
		createResourceCounter(corrupted ? Resource.CORRUPTED_LINUM_TIRINUM : Resource.LINUM_TIRINUM, config.resourceTirinum());
		createResourceCounter(corrupted ? Resource.CORRUPTED_GRYM_LEAF : Resource.GRYM_LEAF, config.resourceGrym());
		createResourceCounter(corrupted ? Resource.CORRUPTED_WEAPON_FRAME : Resource.WEAPON_FRAME, config.resourceFrame());
		createResourceCounter(Resource.RAW_PADDLEFISH, config.resourcePaddlefish());
		createResourceCounter(corrupted ? Resource.CORRUPTED_SHARDS : Resource.CRYSTAL_SHARDS, config.resourceShard());
		createResourceCounter(corrupted ? Resource.CORRUPTED_BOWSTRING : Resource.CRYSTALLINE_BOWSTRING, config.resourceBowstring() ? 1 : 0);
		createResourceCounter(corrupted ? Resource.CORRUPTED_SPIKE : Resource.CRYSTAL_SPIKE, config.resourceSpike() ? 1 : 0);
		createResourceCounter(corrupted ? Resource.CORRUPTED_ORB : Resource.CRYSTAL_ORB, config.resourceOrb() ? 1 : 0);
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

	private static class ResourceCounter extends InfoBox
	{
		private final int target;
		private int count;
		private String text;
		private Color color = Color.WHITE;

		private ResourceCounter(
			final BufferedImage bufferedImage,
			final TheGauntletPlugin plugin,
			final Resource resource,
			final int count,
			final int target)
		{
			super(bufferedImage, plugin);
			setPriority(getPriority(resource));
			this.count = count;
			this.target = Math.max(0, target);
			this.text = String.valueOf(count);
		}

		@Override
		public String getText()
		{
			return text;
		}

		@Override
		public Color getTextColor()
		{
			return color;
		}

		private void updateCount(final int count)
		{
			if (target == 0)
			{
				this.count = Math.max(0, this.count - count);
			}
			else
			{
				this.count += count;
			}

			if (hasAcquiredTarget())
			{
				color = Color.GRAY;
			}

			text = String.valueOf(this.count);
		}

		private boolean hasAcquiredTarget()
		{
			return target == 0 ? count <= target : count >= target;
		}

		private static InfoBoxPriority getPriority(final Resource resource)
		{
			switch (resource)
			{
				case CRYSTAL_ORE:
				case CORRUPTED_ORE:
				case PHREN_BARK:
				case CORRUPTED_PHREN_BARK:
				case LINUM_TIRINUM:
				case CORRUPTED_LINUM_TIRINUM:
					return InfoBoxPriority.HIGH;
				case GRYM_LEAF:
				case CORRUPTED_GRYM_LEAF:
					return InfoBoxPriority.MED;
				case CRYSTAL_SHARDS:
				case CORRUPTED_SHARDS:
				case RAW_PADDLEFISH:
					return InfoBoxPriority.NONE;
				default:
					return InfoBoxPriority.LOW;
			}
		}
	}
}
