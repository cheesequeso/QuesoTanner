package util;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.methods.map.Area;

/**
 * Handles the tanner actions
 */
public class Tanner {

    public AbstractScript script;
    NPC desertTanner = null;

    public Tanner(AbstractScript script) {
        this.script = script;
    }

    /** Handles the closed door from outside the tanning salon */
    public boolean handleDoorOutside(Area tannerArea) {

        Tile doorTile = new Tile(3278, 3191, 0);

        GameObject tannerDoor = script.getGameObjects().closest(door -> door != null && door.getID() == 1536 && door.distance(doorTile) <= 2);

        if (tannerDoor.hasAction("Close")) {
            script.getWalking().walk(tannerArea.getRandomTile());
        } else if (tannerDoor.hasAction("Open")) {
            tannerDoor.interact("Open");
            script.sleepUntil(() -> tannerDoor.hasAction("Close"), 5000);
            script.getWalking().walk(tannerArea.getRandomTile());
        }

        if (tannerArea.contains(script.getLocalPlayer())) {
            return true;
        } else {
            return false;
        }
    }

    /** Handles the closed door from inside the tanning salon */
    public boolean handleDoorInside() {

        Tile doorTile = new Tile(3278, 3191, 0);
        Area bankArea = new Area(3269, 3161, 3271, 3170, 0);
        Area tannerArea = new Area(3271, 3189, 3275, 3193, 0);

        GameObject tannerDoor = script.getGameObjects().closest(door -> door != null && door.getID() == 1536 && script.getLocalPlayer().distance(doorTile) <= 9);

        if (tannerDoor == null) {
            script.log("Not in range of the door");

            if (tannerDoor.hasAction("Open")) {
                script.getWalking().walk(doorTile);
                script.sleepUntil(() -> script.getLocalPlayer().distance(doorTile) <= 2, 3000);
            }
            script.getWalking().walk(bankArea.getRandomTile());
            script.sleepUntil(() -> bankArea.contains(script.getLocalPlayer()), 4000);
        } else {
            script.log("In range of door");
        }

        if (!tannerArea.contains(script.getLocalPlayer())) {
            return true;
        } else {
            return false;
        }
    }

    /** Function to ensure tanning is complete */
    public boolean checkHidesTanned() {

        if (script.getInventory().contains(hides -> hides != null && hides.getName().contains("Hard leather"))) {
            script.log("The hides were tanned!");
            return true;
        } else {
            script.log("The hides were NOT tanned!");
            return false;
        }
    }

    /** Initiate leather trading with the dude */
    public void initiateTrade(int child_widget_int) {

        //Get the desert tanner dude
        //TO:DO add this into a try-catch block

        if (script.getNpcs().closest(tanner -> tanner != null && tanner.hasAction("Trade") && tanner.getName().contains("Ellis")) != null) {
            desertTanner = script.getNpcs().closest(tanner -> tanner != null && tanner.hasAction("Trade") && tanner.getName().contains("Ellis"));
        }

        //Tan Tanner
        if (desertTanner != null) {
            try {
                script.log("Attempting to trade");
                desertTanner.interact("Trade");
            } catch (Exception e) {
                script.log(e.toString());
            }
        } else {
            script.log("Tanner is null ERROR");
        }
    }

    /** Tan all the hides in the player inventory */
    public void tanAllHides(int child_widget_int) {

        script.sleepUntil(() -> script.getWidgets().getWidgetChild(324, child_widget_int).isVisible(), 8000);

        if (script.getWidgets().getWidgetChild(324, child_widget_int) !=null){
            script.getWidgets().getWidgetChild(324, child_widget_int).interact("Tan All");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            script.log("making");
        } else {
            script.log("widget is null");
        }
    }

}