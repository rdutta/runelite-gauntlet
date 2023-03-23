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

package ca.gauntlet.module.maze;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.util.ImageUtil;

class ResourceEntity
{
	private static final int DEFAULT_ICON_SIZE = 14;

	@Getter(AccessLevel.PACKAGE)
	private final ResourceType resourceType;
	@Getter(AccessLevel.PACKAGE)
	private final GameObject gameObject;
	private final BufferedImage originalIcon;
	@Getter(AccessLevel.PACKAGE)
	private final BufferedImage minimapIcon;
	private BufferedImage icon;
	private int iconSize;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private Color outlineColor;
	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private Color fillColor;

	ResourceEntity(
		final ResourceType resourceType,
		final GameObject gameObject,
		final SkillIconManager skillIconManager,
		final int iconSize,
		final Color outlineColor,
		final Color fillColor)
	{
		this.resourceType = resourceType;
		this.gameObject = gameObject;
		this.iconSize = iconSize;
		this.outlineColor = outlineColor;
		this.fillColor = fillColor;

		originalIcon = getOriginalIcon(skillIconManager, resourceType, false);
		minimapIcon = getOriginalIcon(skillIconManager, resourceType, true);
	}

	boolean isResourceType(final ResourceType resourceType)
	{
		return this.resourceType == resourceType;
	}

	void setIconSize(final int iconSize)
	{
		this.iconSize = iconSize;
		final int size = iconSize <= 0 ? DEFAULT_ICON_SIZE : iconSize;
		icon = ImageUtil.resizeImage(originalIcon, size, size);
	}

	BufferedImage getIcon()
	{
		if (icon == null)
		{
			final int size = iconSize <= 0 ? DEFAULT_ICON_SIZE : iconSize;
			icon = ImageUtil.resizeImage(originalIcon, size, size);
		}

		return icon;
	}

	private static BufferedImage getOriginalIcon(final SkillIconManager skillIconManager,
												 final ResourceType resourceType, final boolean small)
	{
		switch (resourceType)
		{
			case ORE_DEPOSIT:
				return skillIconManager.getSkillImage(Skill.MINING, small);
			case PHREN_ROOTS:
				return skillIconManager.getSkillImage(Skill.WOODCUTTING, small);
			case FISHING_SPOT:
				return skillIconManager.getSkillImage(Skill.FISHING, small);
			case GRYM_ROOT:
				return skillIconManager.getSkillImage(Skill.HERBLORE, small);
			case LINUM_TIRINUM:
				return skillIconManager.getSkillImage(Skill.FARMING, small);
			default:
				throw new IllegalArgumentException("Unsupported resource type");
		}
	}

	@Nullable
	Point getMinimapPoint()
	{
		final Point point = gameObject.getMinimapLocation();

		if (point == null)
		{
			return null;
		}

		return new Point(point.getX() - minimapIcon.getHeight() / 2, point.getY() - minimapIcon.getWidth() / 2);
	}

	enum ResourceType
	{
		ORE_DEPOSIT,
		PHREN_ROOTS,
		GRYM_ROOT,
		LINUM_TIRINUM,
		FISHING_SPOT
	}
}
