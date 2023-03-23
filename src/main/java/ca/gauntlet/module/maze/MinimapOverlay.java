package ca.gauntlet.module.maze;

import ca.gauntlet.TheGauntletConfig;
import ca.gauntlet.TheGauntletPlugin;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

class MinimapOverlay extends Overlay
{
	private final TheGauntletConfig config;
	private final MazeModule mazeModule;
	private final ResourceManager resourceManager;

	@Inject
	MinimapOverlay(
		final TheGauntletPlugin plugin,
		final TheGauntletConfig config,
		final MazeModule mazeModule,
		final ResourceManager resourceManager)
	{
		super(plugin);

		this.config = config;
		this.mazeModule = mazeModule;
		this.resourceManager = resourceManager;

		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}


	@Override
	public Dimension render(final Graphics2D graphics2D)
	{
		if (config.minimapResourceOverlay())
		{
			renderMinimapResourceIcons(graphics2D, mazeModule.getResourceEntities());
		}

		if (config.minimapDemibossOverlay())
		{
			renderMinimapNPCIcons(graphics2D, mazeModule.getDemiBosses());
		}

		return null;
	}

	private void renderMinimapNPCIcons(final Graphics2D graphics2D, final Set<Demiboss> demiBosses)
	{
		if (demiBosses.isEmpty())
		{
			return;
		}

		for (final Demiboss demiboss : demiBosses)
		{
			final Point point = demiboss.getMinimapPoint();

			if (point == null)
			{
				continue;
			}

			OverlayUtil.renderImageLocation(graphics2D, point, demiboss.getMinimapIcon());
		}
	}

	private void renderMinimapResourceIcons(final Graphics2D graphics2D, final Collection<ResourceEntity> resources)
	{
		if (resources.isEmpty())
		{
			return;
		}

		for (final ResourceEntity resource : resources)
		{
			if (config.resourceTracker() &&
				config.resourceRemoveOutlineOnceAcquired() &&
				resourceManager.hasAcquiredResource(resource))
			{
				continue;
			}

			final Point point = resource.getMinimapPoint();

			if (point == null)
			{
				continue;
			}

			OverlayUtil.renderImageLocation(graphics2D, point, resource.getMinimapIcon());
		}
	}


}
