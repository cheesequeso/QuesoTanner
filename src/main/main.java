package main;

import org.dreambot.api.Client;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;

import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;

import org.dreambot.api.wrappers.interactive.NPC;
import util.CurrentStatus;
import util.LocationValidator;
import util.Banker;
import util.LeatherType;
import util.Tanner;
import util.PaintComputations;


/**********************************************************
 * Banks, Tans and Travels. The Ultimate F2P Bot Tanner
 **********************************************************/

/**
 *
 ____ _  _ ____ ____ ____ _  _ ___    ___  _  _ ____ ____
 |    |  | |__/ |__/ |___ |\ |  |     |__] |  | | __ [__
 |___ |__| |  \ |  \ |___ | \|  |     |__] |__| |__] ___]


 */

@ScriptManifest(author = "CheeseQueso", category = Category.MONEYMAKING, description = "Tans hides (soft or hard) in AlKharid, then walks to GE", name = "QuesoTanner", version = 1.0)
public class main extends AbstractScript {

    /** Paint declaration stuff */
    private final URL PAINT_PICTURE = new URL("http://i.imgur.com/wYGWnNd.png");
    private final URL BACKSPLASH_PICTURE = new URL("http://i.imgur.com/AhsBmq1.png");
    private final Image BACKGROUND = ImageIO.read(PAINT_PICTURE.openStream());
    private final Image SPLASH = ImageIO.read(BACKSPLASH_PICTURE.openStream());
    private long TIME_BEGIN;
    private long TIME_RAN;
    private Color BLUE = new Color(40, 40, 40);

    /** Location definitions */
    private final Area ALKHARID_BANK = new Area(3270, 3164, 3271, 3170, 0);
    private final Area TANNER_AREA = new Area(3271, 3189, 3275, 3193, 0);
    private final Tile GRAND_EXCHANGE = new Tile(3166, 3486, 0);

    /** Status ENUM control variables */
    public LeatherType tanStatus;
    public CurrentStatus currentStatus;

    /** Logic control variables */
    private int goldWithdrawAction = 0;
    public static String hideCount = "0";
    public static String hidesLeft = "0";

    /** GUI Tracking */
    private int logoutWhenDone = -1;
    private int walkToGEWhenDone = -1;
    private int guiSaved = 0;

    NPC desertTanner = null;
    LocationValidator initializer = new LocationValidator(this);
    Banker bank = new Banker(this);
    Tanner tanner = new Tanner(this);
    MainGUITanner javaGUI = new MainGUITanner(this);
    PaintComputations paint = new PaintComputations(this);

    public main() throws IOException {

    }

    /** Sets script vars based on the GUI input */
    public void setUIVars(boolean dataArray[], String leatherType) {
        tanStatus = leatherType.contains("Hard") ? tanStatus = tanStatus.HARD_LEATHER:tanStatus.LEATHER;
        logoutWhenDone = dataArray[0] ? 1 : 0;
        walkToGEWhenDone = dataArray[1] ? 1 : 0;
        guiSaved = 1;

    }

    /** Set the initial values */
    @Override
    public void onStart() {
        javaGUI.setVisible(true);
        currentStatus = CurrentStatus.INITIALIZING;
        TIME_BEGIN = System.currentTimeMillis();
        super.onStart();
    }

    /** Do this over and over again until base condition is met */
    @Override
    public int onLoop() {

        if (guiSaved != 0) {
            switch (initializer.determineCurrentAction()) {
                case "bank":
                    currentStatus = CurrentStatus.BANKING;
                    handleBanking();
                    break;
                case "tanner":
                    currentStatus = CurrentStatus.TAN;
                    handleTanning();
                    break;
                case "travelTanner":
                    currentStatus = CurrentStatus.TRAVEL;
                    handleTravelTanner();
                    break;
                default:
                    break;
            }
        }
        return Calculations.random(300, 1000);
    }

    /** Draw the paint for the script */
    public void onPaint(Graphics g)
    {
        if (guiSaved != 0) {

            //TO:DO Fix static member vars freezing paint in PaintComputations class
            String profit = Integer.toString(Integer.parseInt(hideCount) * tanStatus.getLeatherCost());
            hideCount = paint.checkHideCount();
            hidesLeft = paint.getHidesLeft();
            TIME_RAN = System.currentTimeMillis() - this.TIME_BEGIN;

            g.setColor(BLUE);
            g.drawImage(BACKGROUND, 10, 346, null);
            g.drawImage(SPLASH, 55, 48, null);
            g.drawString(hidesLeft, 107, 392);
            g.drawString(hideCount, 149, 412);
            g.drawString(profit, 146, 430);
            g.drawString(paint.timeConversion(TIME_RAN), 161, 449);
            g.drawString(getCurrentStatus(), 381, 390);
        }
    }

    /** Handle GE travel */
    private void handleGE() {
        log("Walking GE Method");
    }

    /** Handle all the logic for withdrawing and depositing goods */
    private void handleBanking() {

        //We need to walk to bank
        if (ALKHARID_BANK.contains(getLocalPlayer()) || getBank().isOpen()) {

            log("In bank");

            bank.bankBanker();

            if (bank.outOfItems() && walkToGEWhenDone == 1) {

                while (getLocalPlayer().distance(GRAND_EXCHANGE) >= 30) {
                    getWalking().walk(GRAND_EXCHANGE);
                    sleepTime(2000, 4000);
                }

                if (logoutWhenDone == 1) {
                    //logout here
                    stop();
                }

            } else if (bank.outOfItems() && logoutWhenDone == 1) {
                //logout
                stop();

            } else {
                    bank.depositAll();
                    if (bank.handleWithdraw(tanStatus.getLeatherCost())) {
                        currentStatus = CurrentStatus.TRAVEL;
                    }
                }
        } else {
            log("walking to bank");
            getWalking().walk(ALKHARID_BANK.getRandomTile());

            if (!ALKHARID_BANK.contains(getLocalPlayer())) {sleepTime(2000, 3999);}
        }
    }

    /** Handle all the logic for traveling to and from the tanner NPC */
    private void handleTravelTanner() {

        log("Travelling to tanner!");

        if (TANNER_AREA.contains(getLocalPlayer())) {

            log("player in tanning area");
            desertTanner = getNpcs().closest(tanner -> tanner != null && tanner.hasAction("Trade"));

            if (desertTanner != null) {
                currentStatus = CurrentStatus.TAN;
            }

        } else {
            getWalking().setRunThreshold(10);
            getWalking().walk(TANNER_AREA.getRandomTile());
            sleepTime(2000, 3999);
        }
    }

    /** Handle all the logic for tanning the inventory hides */
    private void handleTanning() {

        log("Should currently be tanning!");

        currentStatus = CurrentStatus.TAN;

        if (tanStatus.getLeatherType().contains("soft")) {
            tanner.tradeAndTan(124);
        } else {
            tanner.tradeAndTan(125);
        }
    }

    /** Sleeps for designated randomized ms */
    public void sleepTime(int from, int to) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(from, to));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** Get a string version of the current status in the game */
    private String getCurrentStatus() {

        switch (this.currentStatus) {
            case INITIALIZING:
                return "Initializing State";
            case BANKING:
                return "Banking Data";
            case TAN:
                return "Currently Tanning";
            case TRAVEL:
                return "Travelling";
            case GE:
                return "Grand Exchange";
            default:
                return "Error";
        }
    }
}