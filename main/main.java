package main;

import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;

import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.dreambot.api.wrappers.interactive.NPC;
import util.CurrentStatus;
import util.LocationValidator;
import util.Banker;
import util.LeatherType;
import util.Tanner;


/**
 * Banks, Tans and Travels. The Ultimate F2P Bot Tanner
 */

@ScriptManifest(author = "CheeseQueso", category = Category.MONEYMAKING, description = "Tans hides (soft or hard) in AlKharid, then walks to GE", name = "QuesoTanner", version = 1.0)
public class main extends AbstractScript {



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

    private int goldWithdrawAction = 0;

    // PAINT VARIABLE DECLARATIONS
    private long timeBegan;
    private long timeRan;
    private Color blue = new Color(40, 40, 40);
    private int hideCount = 0;


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
                } else {
                    log("You have no more hides, we need to stop or walk to GE based on GUI selection");
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

    // PAINT VARIABLE DECLARATIONS
    URL url_pic = new URL("http://i.imgur.com/wYGWnNd.png");
    Image bg = ImageIO.read(url_pic.openStream());

    /** Draw the paint for the script */
    public void onPaint(Graphics g)
    {

        //Get the bot running time
        timeRan = System.currentTimeMillis() - this.timeBegan;

        //Set paint text color
        g.setColor(blue);

        //Draw the bg image on-screen
        g.drawImage(bg, 5, 346, null);

        //Hides Left (101, 389)
        g.drawString("Hides Left", 101, 389);

        //Hides Completed(140, 411)
        g.drawString(checkHideCount(), 140, 411);

        //Estimated Profit(140, 426)
        g.drawString(checkProfit(), 140, 426);

        //Total Running Time(156, 446)
        g.drawString(timeConversion(timeRan), 156, 446);

        //Current Status(373, 390)
        g.drawString(getCurrentStatus(), 373, 390);

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
            return "Travelling to Destination";
        } else {
            return "SCRIPT ERROR! Restart!";
        }
    }

    /** Paint update method  for hide counts */
    private String checkHideCount() {
        if (getBank().isOpen()) {
            int updatedHideCount = getBank().count(leather -> leather != null && leather.getName().contains("Hard"));
            hideCount = updatedHideCount;
            return Integer.toString(updatedHideCount);
        }
        else {
            return Integer.toString(hideCount);
        }
    }

    /** Gets the current profit that is made utilizing real-time GE prices */
    private String checkProfit() {

        //To:DO - Integrate method to grab real-time GE prices from the Jagex API
        return Integer.toString(hideCount* 140);
    }

    /** Handle all the logic for withdrawing and depositing goods */
    private void handleBanking() {

        bank.bankBanker();

        sleepUntil(() -> getBank().isOpen(), 3500);

        if (!bank.checkHidesExistInBank()){
            //stop();
        }

        if (goldWithdrawAction == 0) {
            bank.withdrawGold(tanStatus);
            log("Withdrew gold!");
        }

        if (getInventory().contains(coins -> coins != null && coins.getName().contains("Coins"))) {
            goldWithdrawAction = 1;
            bank.depositAll();

            //TO-DO: This function is probably more annoying than useful.
            //bank.checkSufficientFunds(tanStatus);

            bank.withdrawHides();
            currentStatus = CurrentStatus.TRAVEL;
        }
    }

    /** Handle all the logic for traveling to and from the tanner NPC */
    private void handleTravel() {

        //Travel to tanner
        initializer.walkToTanner(tannerArea);

        NPC desertTanner = getNpcs().closest(tanner -> tanner != null && tanner.hasAction("Trade"));

        //Checks to see if the door to tanner is present and open, if not open it
        tanner.handleDoorOutside(tannerArea);

        //Ensure the tanner is present and the player is in the tanning area
        if (desertTanner.isOnScreen() && initializer.insideTanningArea(tannerArea, getLocalPlayer())) {
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

            //Handle if the door is closed from the inside
            if (tanner.handleDoorInside()) {

                log("Handled the door and exited the tanner.");

                //Restart the work-flow
                currentStatus = CurrentStatus.INITIALIZING;
            }
        }
    }
}