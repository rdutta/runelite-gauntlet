package ca.gauntlet.module.maze;

import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;

class Demiboss
{
	@Getter(AccessLevel.PACKAGE)
	private final NPC npc;

	@Getter(AccessLevel.PACKAGE)
	private final BufferedImage minimapIcon;

	Demiboss(final NPC npc, final SkillIconManager skillIconManager)
	{
		this.npc = npc;
		minimapIcon = getIcon(skillIconManager, npc);
	}

	@Nullable
	Point getMinimapPoint()
	{
		final Point point = npc.getMinimapLocation();

		if (point == null)
		{
			return null;
		}

		return new Point(point.getX() - minimapIcon.getHeight() / 2, point.getY() - minimapIcon.getWidth() / 2);
	}

	private static BufferedImage getIcon(final SkillIconManager skillIconManager, final NPC npc)
	{
		switch (npc.getId())
		{
			case NpcID.CRYSTALLINE_BEAR:
			case NpcID.CORRUPTED_BEAR:
				return skillIconManager.getSkillImage(Skill.ATTACK, true);
			case NpcID.CRYSTALLINE_DARK_BEAST:
			case NpcID.CORRUPTED_DARK_BEAST:
				return skillIconManager.getSkillImage(Skill.RANGED, true);
			case NpcID.CRYSTALLINE_DRAGON:
			case NpcID.CORRUPTED_DRAGON:
				return skillIconManager.getSkillImage(Skill.MAGIC, true);
			default:
				throw new IllegalArgumentException("Unsupported npc");
		}
	}
}
