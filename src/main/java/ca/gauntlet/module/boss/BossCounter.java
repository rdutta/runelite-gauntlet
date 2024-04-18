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
    @Getter
    private String bossAttackStyle = "RANGE";
    @Getter @Setter
    private String bossCurrentPrayer = "";

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
        String reqPrayer = "You need to pray: ";

        net.runelite.api.Point hunlleffPoint = new net.runelite.api.Point(10, 160); // Adjust the position as needed
        net.runelite.api.Point playerPoint = new net.runelite.api.Point(10, 170); // Adjust the position as needed
        net.runelite.api.Point reqPoint = new net.runelite.api.Point(10, 200); // Adjust the position as needed
        net.runelite.api.Point prayPoint = new net.runelite.api.Point(100, 200);

        OverlayUtil.renderTextLocation(graphics, hunlleffPoint, hunlleffAttacks, Color.WHITE);
        OverlayUtil.renderTextLocation(graphics, playerPoint, playerAttacks, Color.WHITE);
        OverlayUtil.renderTextLocation(graphics, reqPoint, reqPrayer, Color.WHITE);

        if(bossAttackStyle.equals("RANGE"))
            OverlayUtil.renderTextLocation(graphics, prayPoint, bossAttackStyle, Color.GREEN);
        else if(bossAttackStyle.equals("MAGIC"))
            OverlayUtil.renderTextLocation(graphics, prayPoint, bossAttackStyle, Color.BLUE);

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

    void updateBossAttackStyle(String prayer) { bossAttackStyle = prayer; }
    void resetPrayer() { bossAttackStyle = "RANGE"; }
    void resetPlayerAttackCount() { playerAttackCount = 0; }
    void resetBossAttackCount() { bossAttackCount = 0; }

}
