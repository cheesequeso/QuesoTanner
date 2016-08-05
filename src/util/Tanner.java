package util;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;

/**
 * Handles the tanner actions
 */
public class Tanner {

    public AbstractScript script;
    NPC desertTanner = null;
    WidgetChild tannerWindow;

    public Tanner(AbstractScript script) {
        this.script = script;
    }

    public boolean tradeAndTan(int child_widget_int) {

        desertTanner = script.getNpcs().closest(tanner -> tanner != null && tanner.getName().contains("Ellis"));

        if (desertTanner!=null) {

            desertTanner.interact("Trade");

            if (script.sleepUntil(() -> script.getWidgets().getWidgetChild(324, child_widget_int) != null, 900000)) {
                tannerWindow = script.getWidgets().getWidgetChild(324, child_widget_int);
                tannerWindow.interact("Tan All");
            }
        }

        if (script.sleepUntil(() -> script.getInventory().contains(leather -> leather != null && leather.getName().contains("eather")), 12000)) {
            return true;
        } else {
            return false;
        }
    }
}