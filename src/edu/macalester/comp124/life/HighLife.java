package edu.macalester.comp124.life;

/**
 * Created by needa_000 on 2/5/14.
 */
public class HighLife extends RuleSet{
    @Override
    public String getName() {
        return "High Life Rules";
    }

    @Override
    public boolean applyRules(boolean isAlive, int neighborCount) {
        if(isAlive && (neighborCount == 2 || neighborCount == 3))
            return true;
        if(!isAlive && (neighborCount == 3 || neighborCount == 6))
            return true;
        return false;
    }
}
