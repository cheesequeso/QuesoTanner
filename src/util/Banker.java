package util;

import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.wrappers.interactive.NPC;


/**
 * Class responsible for handling all banking related activities during the tanning process
 */
public class Banker {

    private AbstractScript script;

    public Banker(AbstractScript script) {
        this.script = script;
    }

    /** Open the bank */
    public void bankBanker() {
        NPC desertBanker = script.getNpcs().closest(banker -> banker != null && banker.hasAction("Bank"));

        //Open bank
        if (desertBanker != null) {
            desertBanker.interact("Bank");
            script.sleepUntil(() -> script.getBank().isOpen(), 45000);
        }
    }

    /** Handle out of resources */
    public boolean outOfItems() {
        if ((script.getBank().count("Coins") <= 2 && script.getInventory().count("Coins") == 0) || script.getBank().count("Cowhide") == 0) {
            script.log("Out of items");
            return true;
        } else {
            script.log("Still have items");
            return false;
        }
    }

    /** Logically determine what to withdraw */
    public boolean handleWithdraw(int tanningCosts) {

        if (script.getInventory().count("Coins") > 2) {

            //Withdraw all your hides if you have enough money
            script.getBank().withdrawAll(hides -> hides != null && hides.getName().contains("Cowhide"));
            script.sleepUntil(() -> script.getInventory().contains("Cowhide"), 3000);

            return true;

        } else if (script.getInventory().contains("Cowhide") && script.getInventory().count("Coins") <= 2) {

            //Withdraw the amount of coins it would cost to tan X amount of hides
            script.getBank().withdraw(coins -> coins != null && coins.getName().contains("Coins"), script.getBank().count("Cowhide")*tanningCosts);
            script.sleepUntil(() -> script.getInventory().contains("Coins"), 3000);

            return true;

        } else {

            //If you had none of the items in your inventory, then withdraw both coins and cowhides
            script.getBank().withdraw(coins -> coins != null && coins.getName().contains("Coins"), script.getBank().count("Cowhide")*tanningCosts);
            script.getBank().withdrawAll(hides -> hides != null && hides.getName().contains("Cowhide"));
            script.sleepUntil(() -> script.getInventory().contains("Coins") && script.getInventory().contains("Cowhide"), 3000);

            return true;
        }
    }

    /** Deposits all items in inventory into the player bank */
    public void depositAll() {
        script.log("Depositing Items - [KEEPING GOLD IN INVENTORY]");

        if (script.sleepUntil(() -> script.getBank().isOpen(), 9000)) {

            script.log("Bank is open");
            script.getBank().depositAllExcept(coins -> coins != null && coins.getName().contains("Coins"));
        }
    }
}