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

package ca.gauntlet.overlay;

import ca.gauntlet.TheGauntletConfig;
import ca.gauntlet.TheGauntletPlugin;
import ca.gauntlet.entity.ResourceEntity;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import javax.inject.Inject;
import javax.inject.Singleton;

import ca.gauntlet.resource.ResourceManager;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.ObjectID;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

@Singleton
public class SceneOverlay extends Overlay
{
	private final Client client;
	private final TheGauntletPlugin plugin;
	private final TheGauntletConfig config;
	private final ModelOutlineRenderer modelOutlineRenderer;
	private final ResourceManager resourceManager;

	private Player player;

	@Inject
	public SceneOverlay(
		final Client client,
		final TheGauntletPlugin plugin,
		final TheGauntletConfig config,
		final ModelOutlineRenderer modelOutlineRenderer,
		final ResourceManager resourceManager)
	{
		super(plugin);

		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.modelOutlineRenderer = modelOutlineRenderer;
		this.resourceManager = resourceManager;

		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
		setLayer(OverlayLayer.UNDER_WIDGETS);
	}

	@Override
	public Dimension render(final Graphics2D graphics2D)
	{
		player = client.getLocalPlayer();

		if (player == null)
		{
			return null;
		}

		if (plugin.isInHunllef())
		{
			renderTornadoes(graphics2D);
		}
		else
		{
			renderResources(graphics2D);
			renderUtilities();
		}

		return null;
	}

	private void renderTornadoes(final Graphics2D graphics2D)
	{
		if (config.tornadoTileOutline() == TheGauntletConfig.TileOutline.OFF || plugin.getTornadoes().isEmpty())
		{
			return;
		}

		final boolean trueTile = config.tornadoTileOutline() == TheGauntletConfig.TileOutline.TRUE_TILE;

		for (final NPC tornado : plugin.getTornadoes())
		{
			Polygon polygon;

			if (trueTile)
			{
				final WorldPoint worldPoint = tornado.getWorldLocation();

				if (worldPoint == null)
				{
					continue;
				}

				final LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);

				if (localPoint == null)
				{
					continue;
				}

				polygon = Perspective.getCanvasTilePoly(client, localPoint);
			}
			else
			{
				polygon = Perspective.getCanvasTilePoly(client, tornado.getLocalLocation());
			}

			if (polygon == null)
			{
				continue;
			}

			drawOutlineAndFill(graphics2D, config.tornadoOutlineColor(), config.tornadoFillColor(),
				config.tornadoTileOutlineWidth(), polygon);
		}
	}

	private void renderResources(final Graphics2D graphics2D)
	{
		if (!config.overlayResources() || plugin.getResourceEntities().isEmpty())
		{
			return;
		}

		final LocalPoint localPointPlayer = player.getLocalLocation();

		for (final ResourceEntity resourceEntity : plugin.getResourceEntities())
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

			final LocalPoint localPointGameObject = gameObject.getLocalLocation();

			if (isOutsideRenderDistance(localPointGameObject, localPointPlayer))
			{
				continue;
			}

			if (config.resourceHullOutlineWidth() > 0)
			{
				modelOutlineRenderer.drawOutline(gameObject, config.resourceHullOutlineWidth(), resourceEntity.getOutlineColor(), 1);
			}

			if (config.resourceTileOutlineWidth() > 0)
			{
				final Polygon polygon = Perspective.getCanvasTilePoly(client, localPointGameObject);

				if (polygon != null)
				{
					drawOutlineAndFill(graphics2D, resourceEntity.getOutlineColor(), resourceEntity.getFillColor(),
						config.resourceTileOutlineWidth(), polygon);
				}
			}

			if (config.resourceIconSize() > 0)
			{
				OverlayUtil.renderImageLocation(client, graphics2D, localPointGameObject, resourceEntity.getIcon(), 0);
			}
		}
	}

	private void renderUtilities()
	{
		if (!config.utilitiesOutline() || plugin.getUtilities().isEmpty())
		{
			return;
		}

		final LocalPoint localPointPlayer = player.getLocalLocation();

		for (final GameObject gameObject : plugin.getUtilities())
		{
			if (isOutsideRenderDistance(gameObject.getLocalLocation(), localPointPlayer))
			{
				continue;
			}

			modelOutlineRenderer.drawOutline(gameObject, config.utilitiesOutlineWidth(), config.utilitiesOutlineColor(), 1);
		}
	}

	private boolean isOutsideRenderDistance(final LocalPoint localPoint, final LocalPoint playerLocation)
	{
		return localPoint.distanceTo(playerLocation) >= config.renderDistance().getDistance();
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

	private static void drawOutlineAndFill(
		final Graphics2D graphics2D,
		final Color outlineColor,
		final Color fillColor,
		final float strokeWidth,
		final Shape shape)
	{
		final Color originalColor = graphics2D.getColor();
		final Stroke originalStroke = graphics2D.getStroke();

		graphics2D.setStroke(new BasicStroke(strokeWidth));
		graphics2D.setColor(outlineColor);
		graphics2D.draw(shape);

		graphics2D.setColor(fillColor);
		graphics2D.fill(shape);

		graphics2D.setColor(originalColor);
		graphics2D.setStroke(originalStroke);
	}
}
