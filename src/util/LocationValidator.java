package util;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.AbstractScript;

/**
 * Validates player location
 */
public class LocationValidator{

    public AbstractScript script;
    private final Area TANNER_AREA = new Area(3271, 3189, 3275, 3193, 0);

    public LocationValidator(AbstractScript script) {
        this.script = script;
    }

    /** Function that will determine where to walk based on inventory **/
    public String determineCurrentAction() {

        Inventory userInventory = script.getInventory();

        if (userInventory.contains(hides -> hides != null && hides.getName().contains("leather"))) {
            script.log("WALKING TO BANK NOW!");
            return "bank";
        } else if (userInventory.count("Coins") > 2 && userInventory.count("Cowhide") > 1 && TANNER_AREA.contains(script.getLocalPlayer())) {
            script.log("WALKING TO TANNER");
            return "tanner";
        } else if (userInventory.contains(hides -> hides != null && hides.getName().contains("eather")) && userInventory.count("Coins") <= 2) {
            script.log("WALKING TO BANK");
            return "bank";
        } else if (userInventory.count("Coins") > 2 && userInventory.count("Cowhide") == 0) {
            script.log("WALKING TO BANK");
            return "bank";
        }  else if (userInventory.count("Coins") == 0) {
            return "bank";
        } else {
            script.log("TRAVEL TANNER");
            return "travelTanner";
        }
    }
}