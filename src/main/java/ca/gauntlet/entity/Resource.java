/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2020, dutta64 <https://github.com/dutta64>
 * Copyright (c) 2019, ganom <https://github.com/Ganom>
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

package ca.gauntlet.entity;

import java.awt.Color;
import java.awt.image.BufferedImage;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.GameObject;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.util.ImageUtil;

public class Resource
{
	private static final int DEFAULT_ICON_SIZE = 14;

	@Getter
	private final ResourceType resourceType;

	@Getter
	private final GameObject gameObject;

	private final BufferedImage originalIcon;

	private BufferedImage icon;

	private int iconSize;

	@Getter
	@Setter
	private Color outlineColor;

	@Getter
	@Setter
	private Color fillColor;

	public Resource(final ResourceType resourceType, final GameObject gameObject, final SkillIconManager skillIconManager, final int iconSize, final Color outlineColor, final Color fillColor)
	{
		this.resourceType = resourceType;
		this.gameObject = gameObject;
		this.iconSize = iconSize;
		this.outlineColor = outlineColor;
		this.fillColor = fillColor;

		originalIcon = getOriginalIcon(skillIconManager, resourceType);
	}

	public boolean isResourceType(final ResourceType resourceType)
	{
		return this.resourceType == resourceType;
	}

	public void setIconSize(final int iconSize)
	{
		this.iconSize = iconSize;
		final int size = iconSize <= 0 ? DEFAULT_ICON_SIZE : iconSize;
		icon = ImageUtil.resizeImage(originalIcon, size, size);
	}

	public BufferedImage getIcon()
	{
		if (icon == null)
		{
			final int size = iconSize <= 0 ? DEFAULT_ICON_SIZE : iconSize;
			icon = ImageUtil.resizeImage(originalIcon, size, size);
		}

		return icon;
	}

	private static BufferedImage getOriginalIcon(final SkillIconManager skillIconManager, final ResourceType resourceType)
	{
		switch (resourceType)
		{
			case ORE_DEPOSIT:
				return skillIconManager.getSkillImage(Skill.MINING);
			case PHREN_ROOTS:
				return skillIconManager.getSkillImage(Skill.WOODCUTTING);
			case FISHING_SPOT:
				return skillIconManager.getSkillImage(Skill.FISHING);
			case GRYM_ROOT:
				return skillIconManager.getSkillImage(Skill.HERBLORE);
			case LINUM_TIRINUM:
				return skillIconManager.getSkillImage(Skill.FARMING);
			default:
				throw new IllegalArgumentException("Unsupported resource type");
		}
	}

	public enum ResourceType
	{
		ORE_DEPOSIT,
		PHREN_ROOTS,
		GRYM_ROOT,
		LINUM_TIRINUM,
		FISHING_SPOT
	}
}
