/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2023, rdutta <https://github.com/rdutta>
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

package ca.gauntlet.module.boss;

import ca.gauntlet.TheGauntletConfig;
import ca.gauntlet.TheGauntletConfig.TileOutline;
import ca.gauntlet.TheGauntletPlugin;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

@Singleton
class BossOverlay extends Overlay
{
	private final Client client;
	private final TheGauntletConfig config;
	private final BossModule bossModule;

	@Inject
	public BossOverlay(
		final Client client,
		final TheGauntletPlugin plugin,
		final TheGauntletConfig config,
		final BossModule bossModule)
	{
		super(plugin);

		this.client = client;
		this.config = config;
		this.bossModule = bossModule;

		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
		setLayer(OverlayLayer.UNDER_WIDGETS);
	}

	@Override
	public Dimension render(final Graphics2D graphics2D)
	{
		renderTornadoes(graphics2D);
		return null;
	}

	private void renderTornadoes(final Graphics2D graphics2D)
	{
		if (config.tornadoTileOutline() == TileOutline.OFF || bossModule.getTornadoes().isEmpty())
		{
			return;
		}

		final boolean trueTile = config.tornadoTileOutline() == TileOutline.TRUE_TILE;

		for (final NPC tornado : bossModule.getTornadoes())
		{
			final Polygon polygon;

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

			OverlayUtil.renderPolygon(graphics2D, polygon, config.tornadoOutlineColor(), config.tornadoFillColor(),
				new BasicStroke(config.tornadoTileOutlineWidth()));
		}
	}
}
