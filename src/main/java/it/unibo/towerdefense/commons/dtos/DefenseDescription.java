package it.unibo.towerdefense.commons.dtos;

import it.unibo.towerdefense.models.defenses.Defense;

/**Contains data that can be visualized in the build menu about defenses.*/
public class DefenseDescription {
    /**Description of defense.*/
    private String description;
    /**name.*/
    private String name;
    /**Cost.*/
    private int cost;

    /**simple constructor with all fields.
     * @param description
     * @param name
     * @param cost
    */
    public DefenseDescription(final String description, final String name, final int cost) {
        this.description = description;
        this.name = name;
        this.cost = cost;
    }

    /**getter for description.
     * @return the description.
    */
    public String getDescription() {
        return this.description;
    }

    /**getter for name.
     * @return the name.
    */
    public String getName() {
        return this.name;
    }

    /**getter for description.
     * @return the description.
    */
    public int getCost() {
        return this.cost;
    }

    /**Returns the description for empty zones.*/
    public static DefenseDescription nonBuiltDefense() {
        final String desc = "Nothing is here";
        final String name = "empty zone";
        return new DefenseDescription(desc, name, 0);
    }
}
