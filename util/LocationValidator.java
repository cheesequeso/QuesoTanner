package util;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.wrappers.interactive.Player;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Validates player location
 */
public class LocationValidator{

    public AbstractScript script;
    Tile doorTile = new Tile(3278, 3191, 0);

    public LocationValidator(AbstractScript script) {
        this.script = script;
    }

    /** Check to see if you're in the bank */
    public boolean insideBankingArea(Area bankArea, Player currentPlayerLocation) {

        if (!bankArea.contains(currentPlayerLocation)) {
            walkToBank(bankArea);
            return false;
        } else {
            return true;
        }
    }

    public void walkToGrandExchange() {
        script.getWalking().walk(BankLocation.GRAND_EXCHANGE.getCenter());


        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 3999));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (script.getLocalPlayer().distance(BankLocation.GRAND_EXCHANGE.getCenter()) <= 50) {
            script.stop();
        }
    }

    /** Check to see if you're currently inside the tanning area */
    public boolean insideTanningArea(Area tanArea, Player currentPlayerLocation) {

        if (tanArea.contains(currentPlayerLocation)) {
            return true;
        } else {
            return false;
        }
    }

    /** Go to the bank */
    public void walkToBank(Area bankLocation) {

        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(500, 2500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        script.getWalking().walk(bankLocation.getRandomTile());
    }

    /** Go to the tanner */
    public void walkToTanner(Area tannerLocation) {


        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(500, 2500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (script.getLocalPlayer().distance(doorTile) >= 10) {
            script.getWalking().walk(tannerLocation.getRandomTile());
        }

    }

    /** TO-DO: Integrate method that walks to the grand exchange */
}