package autoenroller;

import java.util.ArrayList;

/**
 * An Action describes a change that the user wishes
 * to perform on his or her SPIRE schedule. An Action
 * contains a list of {@link Condition}s that must be
 * met before the Action occurs. Possible Actions
 * include {@link Add}, {@link Drop}, {@link Swap},
 * and {@link Edit}. Each Action requires different
 * information to describe it.
 */
public abstract class Action {
    protected ArrayList<Condition> conditions = new ArrayList<>();

    public boolean allConditionsMet() {
        boolean met = true;
        for(Condition c : conditions) {
            if(!c.isMet()) {
                met = false;
            }
        }
        return met;
    }

    public Action addCondition(Condition c) {
        conditions.add(c);
        return this;
    }

    public boolean hasConditions() {
        return conditions.size() > 0;
    }

    public abstract boolean perform();

    @Override
    public String toString() {
        String string = "";
        for(Condition c : conditions.toArray(new Condition[0])) {
            string += "\n   "+c.toString();
        }
        return string;
    }
}
