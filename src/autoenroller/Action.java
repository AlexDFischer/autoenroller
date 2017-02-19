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
    private ArrayList<Condition> conditions = new ArrayList<>();
    private ArrayList<Action> satisfiableActions = new ArrayList<>();
    private boolean satisfied = false;

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

    /**
     * Performs the specific Action. Each kind of Action
     * overrides this abstract function to perform its
     * specific task. This function must only be called
     * of all of the Conditions for this Action are met.
     * Condition check occurs in the Spire controller.
     * @param spire     The Spire controller with cart, schedule, and driver.
     * @return          True if successfully performed, false if not.
     */
    public abstract boolean perform(Spire spire);

    // Recursively satisfies a list of satisfiable actions.
    // This should execute on all of this Action's satisfiable Actions
    // in the case that this Action performs successfully.
    public Action satisfyOtherActions() {
        for(Action action : satisfiableActions) {
            action.setSatisfied(true);
            action.satisfyOtherActions();
        }
        return this;
    }

    public ArrayList<Action> getSatisfiableActions() {
        return satisfiableActions;
    }

    public boolean isSatisfied() {
        return this.satisfied;
    }

    public Action setSatisfiableActions(ArrayList<Action> satisfiableActions) {
        this.satisfiableActions = satisfiableActions;
        return this;
    }

    public Action setSatisfiableActions(Action... actions) {
        for(Action action : actions) {
            this.satisfiableActions.add(action);
        }
        return this;
    }

    // May be set by another Action in the case that this Action is
    // no longer necessary due to the successful performance of the
    // other action.
    public Action setSatisfied(boolean satisfied) {
        this.satisfied = satisfied;
        return this;
    }

    @Override
    public String toString() {
        String string = "";
        for(Condition c : conditions.toArray(new Condition[0])) {
            string += "\n   "+c.toString();
        }
        return string;
    }
}
