package main;

import org.dreambot.api.methods.container.impl.bank.BankLocation;

import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.methods.container.impl.bank.BankLocation;

import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.dreambot.api.wrappers.interactive.NPC;
import util.CurrentStatus;
import util.LocationValidator;
import util.Banker;
import util.LeatherType;
import util.Tanner;


/**********************************************************
 * Banks, Tans and Travels. The Ultimate F2P Bot Tanner
 **********************************************************/

/**
 *
 ____ _  _ ____ ____ ____ _  _ ___    ___  _  _ ____ ____
 |    |  | |__/ |__/ |___ |\ |  |     |__] |  | | __ [__
 |___ |__| |  \ |  \ |___ | \|  |     |__] |__| |__] ___]

 **** Tanner goes null in console on initial trade
 **** Banking has a slight delay delay
 **** Handling door closed/open issue could be quicker
 **** Doesn't handle if you have more hides but you're out of money
 */

@ScriptManifest(author = "CheeseQueso", category = Category.MONEYMAKING, description = "Tans hides (soft or hard) in AlKharid, then walks to GE", name = "QuesoTanner", version = 1.0)
public class main extends AbstractScript {



    //PAINT VARIABLE DECLARATIONS
    URL url_pic = new URL("http://i.imgur.com/wYGWnNd.png");
    URL backsplash_pic = new URL("http://i.imgur.com/AhsBmq1.png");
    Image bg = ImageIO.read(url_pic.openStream());
    Image splash = ImageIO.read(backsplash_pic.openStream());

    //X, Y, Z DIAGONAL COORDINATES OF BANK AND TANNING
    Area alkharidBank = new Area(3269, 3161, 3271, 3170, 0);
    Area tannerArea = new Area(3271, 3189, 3275, 3193, 0);

    //TO:D0 MAKE A GE SELECTION AREA AND WALK WHEN HIDES ARE GONE

    //Object validating different player locations in-game
    LocationValidator initializer = new LocationValidator(this);

    //Bank instantiation for bank interaction
    Banker bank = new Banker(this);

    //Tanner instantiation for tanner interaction
    Tanner tanner = new Tanner(this);

    //Determines the type of leather you want and the cost
    private LeatherType tanStatus;

    //Current status in script traversal
    private CurrentStatus currentStatus;

    //Boolean to track initial gold withdraw
    private int goldWithdrawAction = 0;

    // PAINT VARIABLE DECLARATIONS
    private long timeBegan;
    private long timeRan;
    private Color blue = new Color(40, 40, 40);

    NPC desertTanner = null;

    private String hideCount = "0";
    private String hidesLeft = "0";

    public main() throws IOException {
    }



    /** Set the initial values */
    @Override
    public void onStart() {

        log("Initializing Hide Tanner");

        //Type of leather you want to tan
        tanStatus = LeatherType.HARD_LEATHER;

        //Current script status
        currentStatus = CurrentStatus.INITIALIZING;

        timeBegan = System.currentTimeMillis();

        super.onStart();
    }

    /** Do this over and over again until base condition is met */
    @Override
    public int onLoop() {

        switch (currentStatus) {

            case INITIALIZING:
                log("We are initializing");

                boolean insideBank = initializer.insideBankingArea(alkharidBank, getLocalPlayer());

                if (insideBank) {
                    currentStatus = CurrentStatus.BANKING;
                }
                break;
            case BANKING:
                log("We are banking");
                handleBanking();
                break;
            case TRAVEL:
                log("We are travelling");
                handleTravel();
                break;
            case TAN:
                log("We are tanning");
                handleTanning();
                break;
            case GE:
                handleGE();
                break;
            default:
                log("Default case");
                break;
        }

        if (currentStatus == currentStatus.TRAVEL || currentStatus == currentStatus.INITIALIZING) {
            log("Click Travel");
            return Calculations.random(1000, 2500);
        } else {
            log("Click not travel");
            return Calculations.random(300, 1000);
        }

    }

    /** Get current running time for the paint */
    private String timeConversion(long duration)
    {
        String res = "";
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                .toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                .toMinutes(duration));
        if (days == 0) {
            res = (hours + ":" + minutes + ":" + seconds);
        } else {
            res = (days + ":" + hours + ":" + minutes + ":" + seconds);
        }
        return res;
    }


    /** Draw the paint for the script */
    public void onPaint(Graphics g)
    {
        //Get the bot running time
        timeRan = System.currentTimeMillis() - this.timeBegan;

        //Set paint text color
        g.setColor(blue);

        //Draw the bg image on-screen
        g.drawImage(bg, 10, 346, null);

        //Draw the bg image on-screen
        g.drawImage(splash, 55, 48, null);

        //Hides Left
        //To-DO Move this down a little bit
        g.drawString(getHidesLeft(), 107, 392);

        //Hides Completed
        g.drawString(checkHideCount(), 149, 412);

        //Estimated Profit
        g.drawString(checkProfit(), 146, 430);

        //Total Running Time
        g.drawString(timeConversion(timeRan), 161, 449);

        //Current Status
        g.drawString(getCurrentStatus(), 381, 390);
    }

    /** Get a string version of the current status in the game */
    private String getCurrentStatus() {
        if (currentStatus == currentStatus.INITIALIZING) {
            return "Initializing State";
        } else if (currentStatus == currentStatus.BANKING) {
            return "Banking Data";
        } else if (currentStatus == currentStatus.TAN) {
            return "Currently Tanning";
        } else if (currentStatus == currentStatus.TRAVEL) {
            return "Travelling";
        } else if (currentStatus == currentStatus.GE) {
            return "Grand Exchange";
        } else {
            return "SCRIPT ERROR! Restart!";
        }
    }

    /** Paint update method  for hide counts */
    private String checkHideCount() {
        if (getBank().isOpen()) {
            int newHideCount = getBank().count(leather -> leather != null && leather.getName().contains("Hard leather"));
            hideCount = Integer.toString(newHideCount);
            return hideCount;
        }
        else {
            return hideCount;
        }
    }

    /** Paint update method  for hide counts */
    private String getHidesLeft() {
        if (getBank().isOpen()) {
            int newHideCount = getBank().count(leather -> leather != null && leather.getName().contains("Cow"));
             hidesLeft = Integer.toString(newHideCount);
            return hidesLeft;
        }
        else {
            return hidesLeft;
        }
    }

    /** Gets the current profit that is made utilizing real-time GE prices */
    private String checkProfit() {

        return Integer.toString(Integer.parseInt(hideCount) * 140);
    }


    /** Handle go to the GE */
    public void handleGE() {

        log("Going to grand exchange");
        initializer.walkToGrandExchange();

    }
    /** Handle all the logic for withdrawing and depositing goods */
    private void handleBanking() {

        bank.bankBanker();

        sleepUntil(() -> getBank().isOpen(), 45000);

        if (!bank.checkHidesExistInBank() && currentStatus != CurrentStatus.GE){
            getBank().depositAllItems();
            currentStatus = CurrentStatus.GE;
        }

        if (goldWithdrawAction == 0 && getBank().isOpen() && bank.checkHidesExistInBank() && currentStatus != CurrentStatus.GE) {

            if (!bank.checkSufficientFunds(tanStatus)) {
                log("Not enough funds!");
                currentStatus = CurrentStatus.GE;
            } else {
                log("We have enough funds");
                if (goldWithdrawAction == 0) {
                    bank.withdrawGold(tanStatus);
                }
                log("Withdrew gold!");
            }

        }

        if (getBank().isOpen() && getInventory().contains(coins -> coins != null && coins.getName().contains("Coins")) && bank.checkHidesExistInBank() && currentStatus != CurrentStatus.GE) {

            //Only withdraw gold on the initial run
            goldWithdrawAction = 1;

            //Deposit everything but gold
            bank.depositAll();

            //Withdraw your hides
            bank.withdrawHides();

            //Avoid the initial random sleep timer for initial movement
            getWalking().walk(tannerArea.getRandomTile());

            //Change status
            currentStatus = CurrentStatus.TRAVEL;
        }
    }

    /** Handle all the logic for traveling to and from the tanner NPC */
    private void handleTravel() {

        //Travel to tanner
        initializer.walkToTanner(tannerArea);

        if (getNpcs().closest(tanner -> tanner != null && tanner.hasAction("Trade")) != null) {
            desertTanner = getNpcs().closest(tanner -> tanner != null && tanner.hasAction("Trade"));
        }

        //Checks to see if the door to tanner is present and open, if not open it
        tanner.handleDoorOutside(tannerArea);

        //Ensure the tanner is present and the player is in the tanning area
        if ( desertTanner != null && desertTanner.isOnScreen() && initializer.insideTanningArea(tannerArea, getLocalPlayer())) {
            currentStatus = CurrentStatus.TAN;
        }
    }

    /** Handle all the logic for tanning the inventory hides */
    private void handleTanning() {

        //Tan all the hides based on widget-type
        if (tanStatus.getLeatherType().contains("soft")) {

            log("Tanning soft leather");

            //Trade the dude
            tanner.initiateTrade(124);
            tanner.tanAllHides(124);

        } else if (tanStatus.getLeatherType().contains("hard")) {

            log("Tanning hard leather");

            //Trade the dude
            tanner.initiateTrade(125);
            tanner.tanAllHides(125);
        }


        //See if the process completed successfully
        if (tanner.checkHidesTanned()) {

            log("All hides tanned sucessfully!");

            getWalking().walk(alkharidBank.getRandomTile());

            //Handle if the door is closed from the inside
//            if (tanner.handleDoorInside()) {

                //Restart the work-flow
                currentStatus = CurrentStatus.INITIALIZING;
//            }
        }
    }
}