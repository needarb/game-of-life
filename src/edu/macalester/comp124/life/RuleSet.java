package edu.macalester.comp124.life;

import java.awt.*;

/**
 * This interface defines the interface between the Life engine and the set of
 * rules describing the automata.
 * 
 * @author Michael Ekstrand <ekstrand@cs.umn.edu>
 * 
 */
public abstract class RuleSet {


    public RuleSet()
    {
        setAliveColor(Color.BLACK);
    }
    /**
     * Obtain the name of a rule set.
     * @return The rule set's name
     */
    public abstract String getName();
    private Color aliveColor;
    public Color getAliveColor()
    {
        return aliveColor;
    }
    public void setAliveColor(Color c) {
 this.aliveColor = c;    }
    /**
     * Apply the rules described by this rule set.
     * @param value The current value of the cell.
     * @param neighborCount The number of living neighbors.
     * @return The value of the cell in the next generation.
     */
    public abstract boolean applyRules(boolean value, int neighborCount);


}