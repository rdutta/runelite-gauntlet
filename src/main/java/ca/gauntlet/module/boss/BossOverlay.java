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
public class BossOverlay extends Overlay
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
