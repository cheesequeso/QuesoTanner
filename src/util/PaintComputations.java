package util;

import org.dreambot.api.script.AbstractScript;

import java.util.concurrent.TimeUnit;

import main.main;

/**
 * Functions used to update the paint data
 */
public class PaintComputations {

    AbstractScript script;
    private String hideCount = "";

    public PaintComputations(AbstractScript script) {
        this.script = script;
    }

    /** Paint update method  for hide counts */
    public String checkHideCount() {
        hideCount = script.getBank().isOpen() ? Integer.toString(script.getBank().count(leather -> leather != null && leather.getName().contains("Hard leather"))) : main.hideCount;
        return hideCount;
    }

    /** Paint update method  for hide counts */
    public String getHidesLeft() {
        String hidesLeft = script.getBank().isOpen() ? Integer.toString(script.getBank().count(leather -> leather != null && leather.getName().contains("Cow"))) : main.hidesLeft;
        return hidesLeft;
    }

    /** Get current running time for the paint */
    public String timeConversion(long duration)
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
}
