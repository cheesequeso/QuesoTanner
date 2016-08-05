package util;

/**
 * Simple classifiers for the type of leather you want to tan
 */
public enum LeatherType {

    LEATHER("soft", 1),
    HARD_LEATHER("hard", 3);

    private String leatherType;
    private int leatherCost;


    LeatherType(String leatherType, int leatherCost) {
        setLeatherType(leatherType);
        setLeatherCost(leatherCost);
    }

    public String getLeatherType() {
        return leatherType;
    }

    public void setLeatherType(String leatherType) {
        this.leatherType = leatherType;
    }

    public int getLeatherCost() {
        return leatherCost;
    }

    public void setLeatherCost(int leatherCost) {
        this.leatherCost = leatherCost;
    }
}
