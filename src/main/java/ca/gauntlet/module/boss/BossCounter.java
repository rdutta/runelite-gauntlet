package ca.gauntlet.module.boss;

import ca.gauntlet.TheGauntletConfig;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class BossCounter extends Overlay
{
    private final TheGauntletConfig config;

    @Getter
    private int bossAttackCount = 0;
    @Getter
    private int playerAttackCount = 0;

    @Inject
    private BossCounter(final TheGauntletConfig config)
    {
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if(!config.bossCounterOverlay())
            return null;

        String playerAttacks = "Player Attacks: " + playerAttackCount;
        String hunlleffAttacks = "Boss Attacks: " + bossAttackCount;

        net.runelite.api.Point hunlleffPoint = new net.runelite.api.Point(10, 160);
        net.runelite.api.Point playerPoint = new net.runelite.api.Point(10, 170);
        OverlayUtil.renderTextLocation(graphics, hunlleffPoint, hunlleffAttacks, Color.WHITE);
        OverlayUtil.renderTextLocation(graphics, playerPoint, playerAttacks, Color.WHITE);

        return null;
    }


    void incrementBossAttack()
    {
        bossAttackCount++;
        if(bossAttackCount == 4)
            resetBossAttackCount();
    }

    void incrementPlayerAttackCount()
    {
        playerAttackCount++;
        if (playerAttackCount == 6)
            resetPlayerAttackCount();
    }

    void resetPlayerAttackCount() { playerAttackCount = 0; }
    void resetBossAttackCount() { bossAttackCount = 0; }

}
