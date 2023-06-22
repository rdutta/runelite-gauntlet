/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2020, dutta64 <https://github.com/dutta64>
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

import ca.gauntlet.TheGauntletPlugin;
import java.awt.Color;
import java.awt.image.BufferedImage;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

class ResourceCounter extends InfoBox
{
	@Getter(AccessLevel.PACKAGE)
	private final Resource resource;
	private final ResourceManager resourceManager;
	private final boolean decrement;
	@Getter(AccessLevel.PACKAGE)
	private int count;
	private String text;

	ResourceCounter(final Resource resource,
					final TheGauntletPlugin plugin,
					final BufferedImage bufferedImage,
					final ResourceManager resourceManager,
					final int count,
					final boolean decrement)
	{
		super(bufferedImage, plugin);

		this.resource = resource;
		this.resourceManager = resourceManager;
		this.count = count;
		this.decrement = decrement;
		text = String.valueOf(count);

		setPriority(getPriority(resource));
	}

	@Override
	public String getText()
	{
		return text;
	}

	@Override
	public Color getTextColor()
	{
		return Color.WHITE;
	}

	@Subscribe
	void onResourceEvent(final ResourceEvent event)
	{
		if (resource != event.getResource())
		{
			return;
		}

		if (decrement)
		{
			count = Math.max(0, count - event.getCount());
			text = String.valueOf(count);

			if (count == 0)
			{
				resourceManager.remove(this);
			}
		}
		else
		{
			count += event.getCount();
			text = String.valueOf(count);
		}
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
