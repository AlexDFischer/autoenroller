package autoenroller;

/**
 * A Condition only exists to be checked if it is met.
 * Each instance of a class that implements Condition
 * must define how it is met. The class should also
 * override toString() to describe the Condition's
 * terms in a human-readable format.
 * Examples of anonymous Conditions can be found in
 * {@link SpireAutomator}.
 */
public interface Condition {
    boolean isMet();
}
