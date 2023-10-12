/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2023, rdutta <https://github.com/rdutta>
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

@ConfigGroup(TheGauntletConfig.CONFIG_GROUP)
public interface TheGauntletConfig extends Config
{
	String CONFIG_GROUP = "thegauntlet";

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

	// Resource Tracking

	@ConfigItem(
		name = "Track resources",
		description = "Track resources in counter infoboxes.",
		position = 0,
		keyName = "resourceTracker",
		section = resourceTrackingSection
	)
	default boolean resourceTracker()
	{
		return false;
	}

	@ConfigItem(
		name = "Tracking Mode",
		description = "Increment or decrement resource counters." +
			"<br>Disable a counter by setting value to 0.",
		position = 1,
		keyName = "resourceTrackingMode",
		section = resourceTrackingSection
	)
	default TrackingMode resourceTrackingMode()
	{
		return TrackingMode.DECREMENT;
	}

	@ConfigItem(
		name = "Remove acquired resources",
		description = "Remove counters when acquired amount reached.",
		position = 2,
		keyName = "resourceRemoveAcquired",
		section = resourceTrackingSection
	)
	default boolean resourceRemoveAcquired()
	{
		return false;
	}

	@ConfigItem(
		name = "Ore",
		description = "The desired number of ores to acquire.",
		position = 3,
		keyName = "resourceOre",
		section = resourceTrackingSection
	)
	default int resourceOre()
	{
		return 3;
	}

	@ConfigItem(
		name = "Phren bark",
		description = "The desired number of phren barks to acquire.",
		position = 4,
		keyName = "resourceBark",
		section = resourceTrackingSection
	)
	default int resourceBark()
	{
		return 3;
	}

	@ConfigItem(
		name = "Linum tirinum",
		description = "The desired number of linum tirinums to acquire.",
		position = 5,
		keyName = "resourceTirinum",
		section = resourceTrackingSection
	)
	default int resourceTirinum()
	{
		return 3;
	}

	@ConfigItem(
		name = "Grym leaf",
		description = "The desired number of grym leaves to acquire.",
		position = 6,
		keyName = "resourceGrym",
		section = resourceTrackingSection
	)
	default int resourceGrym()
	{
		return 2;
	}

	@ConfigItem(
		name = "Weapon frames",
		description = "The desired number of weapon frames to acquire.",
		position = 7,
		keyName = "resourceFrame",
		section = resourceTrackingSection
	)
	default int resourceFrame()
	{
		return 2;
	}

	@ConfigItem(
		name = "Paddlefish",
		description = "The desired number of paddlefish to acquire.",
		position = 8,
		keyName = "resourcePaddlefish",
		section = resourceTrackingSection
	)
	default int resourcePaddlefish()
	{
		return 20;
	}

	@ConfigItem(
		name = "Crystal shards",
		description = "The desired number of crystal shards to acquire.",
		position = 9,
		keyName = "resourceShard",
		section = resourceTrackingSection
	)
	default int resourceShard()
	{
		return 320;
	}

	@ConfigItem(
		name = "Bowstring",
		description = "Whether or not to acquire the crystalline or corrupted bowstring.",
		position = 10,
		keyName = "resourceBowstring",
		section = resourceTrackingSection
	)
	default boolean resourceBowstring()
	{
		return false;
	}

	@ConfigItem(
		name = "Spike",
		description = "Whether or not to acquire the crystal or corrupted spike.",
		position = 11,
		keyName = "resourceSpike",
		section = resourceTrackingSection
	)
	default boolean resourceSpike()
	{
		return false;
	}

	@ConfigItem(
		name = "Orb",
		description = "Whether or not to acquire the crystal or corrupted orb.",
		position = 12,
		keyName = "resourceOrb",
		section = resourceTrackingSection
	)
	default boolean resourceOrb()
	{
		return false;
	}

	// Resource Overlay Section

	@ConfigItem(
		name = "Overlay resources",
		description = "Toggle enabling/disabling resource overlays.",
		position = 0,
		keyName = "overlayResources",
		section = resourceOverlaySection
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
		section = resourceOverlaySection
	)
	default boolean overlayOreDeposit()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		name = "Ore outline color",
		description = "Change the outline color of ore deposits.",
		position = 2,
		keyName = "oreDepositOutlineColor",
		section = resourceOverlaySection
	)
	default Color oreDepositOutlineColor()
	{
		return new Color(255, 0, 0, 255);
	}

	@Alpha
	@ConfigItem(
		name = "Ore fill color",
		description = "Change the fill color of ore deposits.",
		position = 3,
		keyName = "oreDepositFillColor",
		section = resourceOverlaySection
	)
	default Color oreDepositFillColor()
	{
		return new Color(255, 0, 0, 50);
	}

	@ConfigItem(
		name = "Phren Roots",
		description = "Toggle overlaying phren roots.",
		position = 4,
		keyName = "overlayPhrenRoots",
		section = resourceOverlaySection
	)
	default boolean overlayPhrenRoots()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		name = "Phren outline color",
		description = "Change the outline color of phren roots.",
		position = 5,
		keyName = "phrenRootsOutlineColor",
		section = resourceOverlaySection
	)
	default Color phrenRootsOutlineColor()
	{
		return new Color(0, 255, 0, 255);
	}

	@Alpha
	@ConfigItem(
		name = "Phren fill color",
		description = "Change the fill color of phren roots.",
		position = 6,
		keyName = "phrenRootsFillColor",
		section = resourceOverlaySection
	)
	default Color phrenRootsFillColor()
	{
		return new Color(0, 255, 0, 50);
	}

	@ConfigItem(
		name = "Linum Tirinum",
		description = "Toggle overlaying linum tirinum.",
		position = 7,
		keyName = "overlayLinumTirinum",
		section = resourceOverlaySection
	)
	default boolean overlayLinumTirinum()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		name = "Linum outline color",
		description = "Change the outline color of linum tirinum.",
		position = 8,
		keyName = "linumTirinumOutlineColor",
		section = resourceOverlaySection
	)
	default Color linumTirinumOutlineColor()
	{
		return new Color(255, 255, 255, 255);
	}

	@Alpha
	@ConfigItem(
		name = "Linum fill color",
		description = "Change the fill color of linum tirinum.",
		position = 9,
		keyName = "linumTirinumFillColor",
		section = resourceOverlaySection
	)
	default Color linumTirinumFillColor()
	{
		return new Color(255, 255, 255, 50);
	}

	@ConfigItem(
		name = "Grym Root",
		description = "Toggle overlaying grym roots.",
		position = 10,
		keyName = "overlayGrymRoot",
		section = resourceOverlaySection
	)
	default boolean overlayGrymRoot()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		name = "Grym outline color",
		description = "Change the outline color of grym roots.",
		position = 11,
		keyName = "grymRootOutlineColor",
		section = resourceOverlaySection
	)
	default Color grymRootOutlineColor()
	{
		return new Color(255, 255, 0, 255);
	}

	@Alpha
	@ConfigItem(
		name = "Grym fill color",
		description = "Change the fill color of grym roots.",
		position = 12,
		keyName = "grymRootFillColor",
		section = resourceOverlaySection
	)
	default Color grymRootFillColor()
	{
		return new Color(255, 255, 0, 50);
	}

	@ConfigItem(
		name = "Fishing Spot",
		description = "Toggle overlaying fishing spots.",
		position = 13,
		keyName = "overlayFishingSpot",
		section = resourceOverlaySection
	)
	default boolean overlayFishingSpot()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		name = "Fishing outline color",
		description = "Change the outline color of fishing spots.",
		position = 14,
		keyName = "fishingSpotOutlineColor",
		section = resourceOverlaySection
	)
	default Color fishingSpotOutlineColor()
	{
		return new Color(0, 255, 255, 255);
	}

	@Alpha
	@ConfigItem(
		name = "Fishing fill color",
		description = "Change the fill color of fishing spots.",
		position = 15,
		keyName = "fishingSpotFillColor",
		section = resourceOverlaySection
	)
	default Color fishingSpotFillColor()
	{
		return new Color(0, 255, 255, 50);
	}

	@Range(
		max = 64
	)
	@ConfigItem(
		name = "Icon size",
		description = "Change the size of the resource icons.<br>0px width = disabled",
		position = 16,
		keyName = "resourceIconSize",
		section = resourceOverlaySection
	)
	@Units(Units.PIXELS)
	default int resourceIconSize()
	{
		return 14;
	}

	@Range(
		max = 2
	)
	@ConfigItem(
		name = "Hull outline width",
		description = "Change the width of the resource hull outline.<br>0px width = disabled",
		position = 17,
		keyName = "resourceHullOutlineWidth",
		section = resourceOverlaySection
	)
	@Units(Units.PIXELS)
	default int resourceHullOutlineWidth()
	{
		return 1;
	}

	@Range(
		max = 2
	)
	@ConfigItem(
		name = "Tile outline width",
		description = "Change the width of the resource tile outline.<br>0px width = disabled",
		position = 18,
		keyName = "resourceTileOutlineWidth",
		section = resourceOverlaySection
	)
	@Units(Units.PIXELS)
	default int resourceTileOutlineWidth()
	{
		return 1;
	}

	@ConfigItem(
		name = "Minimap overlays",
		description = "Overlay the minimap with icons for resources.",
		position = 19,
		keyName = "minimapResourceOverlay",
		section = resourceOverlaySection
	)
	default boolean minimapResourceOverlay()
	{
		return false;
	}

	@ConfigItem(
		name = "Dynamically remove overlays",
		description = "Remove overlays for acquired tracked resources." +
			"<br>Disabled if incrementally tracking resources.",
		position = 20,
		keyName = "resourceRemoveOutlineOnceAcquired",
		section = resourceOverlaySection
	)
	default boolean resourceRemoveOutlineOnceAcquired()
	{
		return false;
	}

	// Utilities Section

	@ConfigItem(
		name = "Outline starting room utilities",
		description = "Outline various utilities in the starting room.",
		position = 0,
		keyName = "utilitiesOutline",
		section = utilitiesSection
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
		section = utilitiesSection
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
		section = utilitiesSection
	)
	default Color utilitiesOutlineColor()
	{
		return Color.MAGENTA;
	}

	@ConfigItem(
		name = "Prevent entry with uncooked fish",
		description = "Removes \"Pass\" and \"Quick-Pass\" while carrying uncooked paddlefish.",
		position = 3,
		keyName = "utilitiesFishCheck",
		section = utilitiesSection
	)
	default boolean utilitiesFishCheck()
	{
		return false;
	}


	// Hunllef Section

	@ConfigItem(
		name = "Outline Hunllef tile",
		description = "Outline the Hunllef's tile.",
		position = 0,
		keyName = "hunllefTileOutline",
		section = hunllefSection
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
		section = hunllefSection
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
		section = hunllefSection
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
		section = hunllefSection
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
		section = hunllefSection
	)
	default TileOutline tornadoTileOutline()
	{
		return TileOutline.OFF;
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
		section = hunllefSection
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
		section = hunllefSection
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
		section = hunllefSection
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
		section = npcsSection
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
		section = npcsSection
	)
	@Units(Units.PIXELS)
	default int demibossOutlineWidth()
	{
		return 1;
	}

	@Alpha
	@ConfigItem(
		name = "Dark beast color",
		description = "Change the outline color of dark beasts.",
		position = 2,
		keyName = "darkBeastOutlineColor",
		section = npcsSection
	)
	default Color darkBeastOutlineColor()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		name = "Dragon color",
		description = "Change the outline color of dragons.",
		position = 3,
		keyName = "dragonOutlineColor",
		section = npcsSection
	)
	default Color dragonOutlineColor()
	{
		return Color.BLUE;
	}

	@Alpha
	@ConfigItem(
		name = "Bear color",
		description = "Change the outline color of bears.",
		position = 4,
		keyName = "bearOutlineColor",
		section = npcsSection
	)
	default Color bearOutlineColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		name = "Outline strong npcs",
		description = "Overlay strong npcs with a colored outline.",
		position = 5,
		keyName = "strongNpcOutline",
		section = npcsSection
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
		position = 6,
		keyName = "strongNpcOutlineWidth",
		section = npcsSection
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
		position = 7,
		keyName = "strongNpcOutlineColor",
		section = npcsSection
	)
	default Color strongNpcOutlineColor()
	{
		return Color.ORANGE;
	}

	@ConfigItem(
		name = "Outline weak npcs",
		description = "Overlay weak npcs with a colored outline.",
		position = 8,
		keyName = "weakNpcOutline",
		section = npcsSection
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
		position = 9,
		keyName = "weakNpcOutlineWidth",
		section = npcsSection
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
		position = 10,
		keyName = "weakNpcOutlineColor",
		section = npcsSection
	)
	default Color weakNpcOutlineColor()
	{
		return Color.ORANGE;
	}

	@ConfigItem(
		name = "Minimap overlays",
		description = "Overlay the minimap with icons for demi-bosses.",
		position = 11,
		keyName = "minimapDemibossOverlay",
		section = npcsSection
	)
	default boolean minimapDemibossOverlay()
	{
		return false;
	}

	// Timer Section

	@ConfigItem(
		position = 0,
		keyName = "timerOverlay",
		name = "Overlay timer",
		description = "Display an overlay that tracks your gauntlet time.",
		section = timerSection
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
		section = timerSection
	)
	default boolean timerChatMessage()
	{
		return false;
	}

	// Constants

	@Getter
	@AllArgsConstructor
	enum TileOutline
	{
		OFF("Off"),
		ON("On"),
		TRUE_TILE("True Tile");

		private final String name;

		@Override
		public String toString()
		{
			return name;
		}
	}

	@Getter
	@AllArgsConstructor
	enum TrackingMode
	{
		DECREMENT("Decrement"),
		INCREMENT("Increment");

		private final String name;

		@Override
		public String toString()
		{
			return name;
		}
	}
}
