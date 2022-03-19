/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2020, dutta64 <https://github.com/dutta64>
 * Copyright (c) 2019, kThisIsCvpv <https://github.com/kThisIsCvpv>
 * Copyright (c) 2019, ganom <https://github.com/Ganom>
 * Copyright (c) 2019, kyle <https://github.com/Kyleeld>
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

package ca.gauntlet;

import java.awt.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup("thegauntlet")
public interface TheGauntletConfig extends Config
{
	// Sections

	@ConfigSection(
		name = "Resource Tracking",
		description = "Resource tracking section.",
		position = 0,
		closedByDefault = true
	)
	String resourceTrackingSection = "resourceTracking";

	@ConfigSection(
		name = "Resource Overlay",
		description = "Resource overlay section.",
		position = 1,
		closedByDefault = true
	)
	String resourceOverlaySection = "resourceOverlay";

	@ConfigSection(
		name = "Utilities",
		description = "Utilities section.",
		position = 2,
		closedByDefault = true
	)
	String utilitiesSection = "utilities";

	@ConfigSection(
		name = "Npcs",
		description = "Npcs section.",
		position = 3,
		closedByDefault = true
	)
	String npcsSection = "npcs";

	@ConfigSection(
		name = "Hunllef",
		description = "Hunllef section.",
		position = 4,
		closedByDefault = true
	)
	String hunllefSection = "hunllef";

	@ConfigSection(
		name = "Timer",
		description = "Timer section.",
		position = 5,
		closedByDefault = true
	)
	String timerSection = "timer";

	@ConfigSection(
		name = "Other",
		description = "Other section.",
		position = 6,
		closedByDefault = true
	)
	String otherSection = "other";

	// Resource Tracking

	@ConfigItem(
		name = "Track resources",
		description = "Track resources in counter infoboxes.",
		position = 0,
		keyName = "resourceTracker",
		section = "resourceTracking"
	)
	default boolean resourceTracker()
	{
		return false;
	}

	@ConfigItem(
		name = "Ore",
		description = "The desired number of ores to acquire.",
		position = 1,
		keyName = "resourceOre",
		section = "resourceTracking"
	)
	default int resourceOre()
	{
		return 3;
	}

	@ConfigItem(
		name = "Phren bark",
		description = "The desired number of phren barks to acquire.",
		position = 2,
		keyName = "resourceBark",
		section = "resourceTracking"
	)
	default int resourceBark()
	{
		return 3;
	}

	@ConfigItem(
		name = "Linum tirinum",
		description = "The desired number of linum tirinums to acquire.",
		position = 3,
		keyName = "resourceTirinum",
		section = "resourceTracking"
	)
	default int resourceTirinum()
	{
		return 3;
	}

	@ConfigItem(
		name = "Grym leaf",
		description = "The desired number of grym leaves to acquire.",
		position = 4,
		keyName = "resourceGrym",
		section = "resourceTracking"
	)
	default int resourceGrym()
	{
		return 2;
	}

	@ConfigItem(
		name = "Weapon frames",
		description = "The desired number of weapon frames to acquire.",
		position = 5,
		keyName = "resourceFrame",
		section = "resourceTracking"
	)
	default int resourceFrame()
	{
		return 2;
	}

	@ConfigItem(
		name = "Paddlefish",
		description = "The desired number of paddlefish to acquire.",
		position = 6,
		keyName = "resourcePaddlefish",
		section = "resourceTracking"
	)
	default int resourcePaddlefish()
	{
		return 20;
	}

	@ConfigItem(
		name = "Crystal shards",
		description = "The desired number of crystal shards to acquire.",
		position = 7,
		keyName = "resourceShard",
		section = "resourceTracking"
	)
	default int resourceShard()
	{
		return 320;
	}

	@ConfigItem(
		name = "Bowstring",
		description = "Whether or not to acquire the crystalline or corrupted bowstring.",
		position = 8,
		keyName = "resourceBowstring",
		section = "resourceTracking"
	)
	default boolean resourceBowstring()
	{
		return false;
	}

	@ConfigItem(
		name = "Spike",
		description = "Whether or not to acquire the crystal or corrupted spike.",
		position = 9,
		keyName = "resourceSpike",
		section = "resourceTracking"
	)
	default boolean resourceSpike()
	{
		return false;
	}

	@ConfigItem(
		name = "Orb",
		description = "Whether or not to acquire the crystal or corrupted orb.",
		position = 10,
		keyName = "resourceOrb",
		section = "resourceTracking"
	)
	default boolean resourceOrb()
	{
		return false;
	}

	// Resource Overlay Section

	@ConfigItem(
		name = "Overlay resources",
		description = "Overlay resources with a respective icon and tile outline.",
		position = 0,
		keyName = "overlayResources",
		section = "resourceOverlay"
	)
	default boolean overlayResources()
	{
		return false;
	}

	@ConfigItem(
		name = "Ore Deposit",
		description = "Toggle overlaying ore deposits.",
		position = 1,
		keyName = "overlayOreDeposit",
		section = "resourceOverlay"
	)
	default boolean overlayOreDeposit()
	{
		return true;
	}

	@ConfigItem(
		name = "Phren Roots",
		description = "Toggle overlaying phren roots.",
		position = 2,
		keyName = "overlayPhrenRoots",
		section = "resourceOverlay"
	)
	default boolean overlayPhrenRoots()
	{
		return true;
	}

	@ConfigItem(
		name = "Linum Tirinum",
		description = "Toggle overlaying linum tirinum.",
		position = 3,
		keyName = "overlayLinumTirinum",
		section = "resourceOverlay"
	)
	default boolean overlayLinumTirinum()
	{
		return true;
	}

	@ConfigItem(
		name = "Grym Root",
		description = "Toggle overlaying grym roots.",
		position = 4,
		keyName = "overlayGrymRoot",
		section = "resourceOverlay"
	)
	default boolean overlayGrymRoot()
	{
		return true;
	}

	@ConfigItem(
		name = "Fishing Spot",
		description = "Toggle overlaying fishing spots.",
		position = 5,
		keyName = "overlayFishingSpot",
		section = "resourceOverlay"
	)
	default boolean overlayFishingSpot()
	{
		return true;
	}

	@Range(
		min = 12,
		max = 64
	)
	@ConfigItem(
		name = "Icon size",
		description = "Change the size of the resource icons.",
		position = 6,
		keyName = "resourceIconSize",
		section = "resourceOverlay"
	)
	@Units(Units.PIXELS)
	default int resourceIconSize()
	{
		return 14;
	}

	@Range(
		min = 0,
		max = 2
	)
	@ConfigItem(
		name = "Tile outline width",
		description = "Change the width of the resource tile outline.",
		position = 7,
		keyName = "resourceTileOutlineWidth",
		section = "resourceOverlay"
	)
	@Units(Units.PIXELS)
	default int resourceTileOutlineWidth()
	{
		return 1;
	}

	@Alpha
	@ConfigItem(
		name = "Tile outline color",
		description = "Change the tile outline color of resources.",
		position = 8,
		keyName = "resourceTileOutlineColor",
		section = "resourceOverlay"
	)
	default Color resourceTileOutlineColor()
	{
		return new Color(128, 128, 128, 255); // gray
	}

	@Alpha
	@ConfigItem(
		name = "Tile fill color",
		description = "Change the tile fill color of resources.",
		position = 9,
		keyName = "resourceTileFillColor",
		section = "resourceOverlay"
	)
	default Color resourceTileFillColor()
	{
		return new Color(255, 255, 255, 50);
	}

	// Utilities Section

	@ConfigItem(
		name = "Outline starting room utilities",
		description = "Outline various utilities in the starting room.",
		position = 0,
		keyName = "utilitiesOutline",
		section = "utilities"
	)
	default boolean utilitiesOutline()
	{
		return false;
	}

	@Range(
		min = 1,
		max = 2
	)
	@ConfigItem(
		name = "Outline width",
		description = "Change the width of the utilities outline.",
		position = 1,
		keyName = "utilitiesOutlineWidth",
		section = "utilities"
	)
	@Units(Units.PIXELS)
	default int utilitiesOutlineWidth()
	{
		return 1;
	}

	@Alpha
	@ConfigItem(
		name = "Outline color",
		description = "Change the color of the utilities outline.",
		position = 2,
		keyName = "utilitiesOutlineColor",
		section = "utilities"
	)
	default Color utilitiesOutlineColor()
	{
		return Color.MAGENTA;
	}

	// Hunllef Section

	@ConfigItem(
		name = "Outline Hunllef tile",
		description = "Outline the Hunllef's tile.",
		position = 0,
		keyName = "hunllefTileOutline",
		section = "hunllef"
	)
	default boolean hunllefTileOutline()
	{
		return false;
	}

	@Range(
		min = 1,
		max = 2
	)
	@ConfigItem(
		name = "Tile outline width",
		description = "Change the width of the Hunllef's tile outline.",
		position = 1,
		keyName = "hunllefTileOutlineWidth",
		section = "hunllef"
	)
	@Units(Units.PIXELS)
	default int hunllefTileOutlineWidth()
	{
		return 1;
	}

	@Alpha
	@ConfigItem(
		name = "Tile outline color",
		description = "Change the outline color of the Hunllef's tile.",
		position = 2,
		keyName = "hunllefOutlineColor",
		section = "hunllef"
	)
	default Color hunllefOutlineColor()
	{
		return Color.WHITE;
	}

	@Alpha
	@ConfigItem(
		name = "Tile fill color",
		description = "Change the fill color of the Hunllef's tile.",
		position = 3,
		keyName = "hunllefFillColor",
		section = "hunllef"
	)
	default Color hunllefFillColor()
	{
		return new Color(255, 255, 255, 0);
	}

	@ConfigItem(
		name = "Outline tornado tile",
		description = "Outline the tiles of tornadoes.",
		position = 4,
		keyName = "tornadoTileOutline",
		section = "hunllef"
	)
	default boolean tornadoTileOutline()
	{
		return false;
	}

	@Range(
		min = 1,
		max = 2
	)
	@ConfigItem(
		name = "Tile outline width",
		description = "Change tile outline width of tornadoes.",
		position = 5,
		keyName = "tornadoTileOutlineWidth",
		section = "hunllef"
	)
	@Units(Units.PIXELS)
	default int tornadoTileOutlineWidth()
	{
		return 1;
	}

	@Alpha
	@ConfigItem(
		name = "Tile outline color",
		description = "Color to outline the tile of a tornado.",
		position = 6,
		keyName = "tornadoOutlineColor",
		section = "hunllef"
	)
	default Color tornadoOutlineColor()
	{
		return Color.YELLOW;
	}

	@Alpha
	@ConfigItem(
		name = "Tile fill color",
		description = "Color to fill the tile of a tornado.",
		position = 7,
		keyName = "tornadoFillColor",
		section = "hunllef"
	)
	default Color tornadoFillColor()
	{
		return new Color(255, 255, 0, 50);
	}

	// Npcs Section

	@ConfigItem(
		name = "Outline demi-bosses",
		description = "Overlay demi-bosses with a colored outline.",
		position = 0,
		keyName = "demibossOutline",
		section = "npcs"
	)
	default boolean demibossOutline()
	{
		return false;
	}

	@Range(
		min = 1,
		max = 2
	)
	@ConfigItem(
		name = "Outline width",
		description = "Change the width of the demi-boss outline.",
		position = 1,
		keyName = "demibossOutlineWidth",
		section = "npcs"
	)
	@Units(Units.PIXELS)
	default int demibossOutlineWidth()
	{
		return 1;
	}

	@ConfigItem(
		name = "Outline strong npcs",
		description = "Overlay strong npcs with a colored outline.",
		position = 2,
		keyName = "strongNpcOutline",
		section = "npcs"
	)
	default boolean strongNpcOutline()
	{
		return false;
	}

	@Range(
		min = 1,
		max = 2
	)
	@ConfigItem(
		name = "Outline width",
		description = "Change the width of the strong npcs outline.",
		position = 3,
		keyName = "strongNpcOutlineWidth",
		section = "npcs"
	)
	@Units(Units.PIXELS)
	default int strongNpcOutlineWidth()
	{
		return 1;
	}

	@Alpha
	@ConfigItem(
		name = "Outline color",
		description = "Change the outline color of strong npcs.",
		position = 4,
		keyName = "strongNpcOutlineColor",
		section = "npcs"
	)
	default Color strongNpcOutlineColor()
	{
		return Color.ORANGE;
	}

	@ConfigItem(
		name = "Outline weak npcs",
		description = "Overlay weak npcs with a colored outline.",
		position = 5,
		keyName = "weakNpcOutline",
		section = "npcs"
	)
	default boolean weakNpcOutline()
	{
		return false;
	}

	@Range(
		min = 1,
		max = 2
	)
	@ConfigItem(
		name = "Outline width",
		description = "Change the width of the weak npcs outline.",
		position = 6,
		keyName = "weakNpcOutlineWidth",
		section = "npcs"
	)
	@Units(Units.PIXELS)
	default int weakNpcOutlineWidth()
	{
		return 1;
	}

	@Alpha
	@ConfigItem(
		name = "Outline color",
		description = "Change the outline color of weak npcs.",
		position = 7,
		keyName = "weakNpcOutlineColor",
		section = "npcs"
	)
	default Color weakNpcOutlineColor()
	{
		return Color.ORANGE;
	}

	// Timer Section

	@ConfigItem(
		position = 0,
		keyName = "timerOverlay",
		name = "Overlay timer",
		description = "Display an overlay that tracks your gauntlet time.",
		section = "timer"
	)
	default boolean timerOverlay()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "timerChatMessage",
		name = "Chat timer",
		description = "Display a chat message on-death with your gauntlet time.",
		section = "timer"
	)
	default boolean timerChatMessage()
	{
		return false;
	}

	// Other Section

	@ConfigItem(
		name = "Render distance",
		description = "Set render distance of various overlays.",
		position = 0,
		keyName = "renderDistance",
		section = "other"
	)
	default RenderDistance renderDistance()
	{
		return RenderDistance.FAR;
	}

	// Constants

	@Getter
	@AllArgsConstructor
	enum RenderDistance
	{
		SHORT("Short", 2350),
		MEDIUM("Medium", 3525),
		FAR("Far", 4700),
		UNCAPPED("Uncapped", Integer.MAX_VALUE);

		private final String name;
		private final int distance;

		@Override
		public String toString()
		{
			return name;
		}
	}
}
