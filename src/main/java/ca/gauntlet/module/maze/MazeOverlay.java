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

import ca.gauntlet.TheGauntletConfig;
import ca.gauntlet.TheGauntletPlugin;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectID;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

@Singleton
public class MazeOverlay extends Overlay
{
	private final Client client;
	private final TheGauntletConfig config;
	private final MazeModule mazeModule;
	private final ModelOutlineRenderer modelOutlineRenderer;
	private final ResourceManager resourceManager;

	@Inject
	public MazeOverlay(
		final Client client,
		final TheGauntletPlugin plugin,
		final TheGauntletConfig config,
		final MazeModule mazeModule,
		final ModelOutlineRenderer modelOutlineRenderer,
		final ResourceManager resourceManager)
	{
		super(plugin);

		this.client = client;
		this.config = config;
		this.mazeModule = mazeModule;
		this.modelOutlineRenderer = modelOutlineRenderer;
		this.resourceManager = resourceManager;

		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
		setLayer(OverlayLayer.UNDER_WIDGETS);
	}

	@Override
	public Dimension render(final Graphics2D graphics2D)
	{
		renderResources(graphics2D);
		renderUtilities();

		return null;
	}

	private void renderResources(final Graphics2D graphics2D)
	{
		if (!config.overlayResources() || mazeModule.getResourceEntities().isEmpty())
		{
			return;
		}

		for (final ResourceEntity resourceEntity : mazeModule.getResourceEntities())
		{
			if (!isOverlayEnabled(resourceEntity))
			{
				continue;
			}

			if (config.resourceTracker() &&
				config.resourceRemoveOutlineOnceAcquired() &&
				resourceManager.hasAcquiredResource(resourceEntity))
			{
				continue;
			}

			final GameObject gameObject = resourceEntity.getGameObject();

			final LocalPoint lp = gameObject.getLocalLocation();

			if (config.resourceHullOutlineWidth() > 0)
			{
				modelOutlineRenderer.drawOutline(gameObject, config.resourceHullOutlineWidth(),
					resourceEntity.getOutlineColor(), 1);
			}

			if (config.resourceTileOutlineWidth() > 0)
			{
				final Polygon polygon = Perspective.getCanvasTilePoly(client, lp);

				if (polygon != null)
				{
					OverlayUtil.renderPolygon(graphics2D, polygon, resourceEntity.getOutlineColor(),
						resourceEntity.getFillColor(), new BasicStroke(config.resourceTileOutlineWidth()));
				}
			}

			if (config.resourceIconSize() > 0)
			{
				OverlayUtil.renderImageLocation(client, graphics2D, lp, resourceEntity.getIcon(), 0);
			}
		}
	}

	private void renderUtilities()
	{
		if (!config.utilitiesOutline() || mazeModule.getUtilities().isEmpty())
		{
			return;
		}

		for (final GameObject gameObject : mazeModule.getUtilities())
		{
			modelOutlineRenderer.drawOutline(gameObject, config.utilitiesOutlineWidth(),
				config.utilitiesOutlineColor(), 1);
		}
	}

	private boolean isOverlayEnabled(final ResourceEntity resourceEntity)
	{
		switch (resourceEntity.getGameObject().getId())
		{
			case ObjectID.CRYSTAL_DEPOSIT:
			case ObjectID.CORRUPT_DEPOSIT:
				return config.overlayOreDeposit();
			case ObjectID.PHREN_ROOTS:
			case ObjectID.PHREN_ROOTS_36066:
				return config.overlayPhrenRoots();
			case ObjectID.LINUM_TIRINUM:
			case ObjectID.LINUM_TIRINUM_36072:
				return config.overlayLinumTirinum();
			case ObjectID.GRYM_ROOT:
			case ObjectID.GRYM_ROOT_36070:
				return config.overlayGrymRoot();
			case ObjectID.FISHING_SPOT_36068:
			case ObjectID.FISHING_SPOT_35971:
				return config.overlayFishingSpot();
			default:
				return false;
		}
	}
}
