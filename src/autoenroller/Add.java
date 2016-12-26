package autoenroller;
//TODO: What does Action Add do if Lecture is in cart but wrong Discussion?
/**
 * To add a lecture, the lecture must be in your shopping cart.
 * When a lecture is in your shopping cart, it must have a discussion
 * section attached to it (if the lecture requires a discussion).
 * This {@link Action} checks if the lecture and the specific discussion
 * are already in the shopping cart. If both are true, the Action
 * enrolls you in them both. If the lecture is in the shopping cart
 * but with a different discussion, the action
 *
 * This action can fail if you have a class conflict or do not meet
 * the lecture's prerequisites. Class conflicts should be predicted
 * by the user and taken care of as {@link Condition}s required
 * to perform this Action. This program obviously cannot meet your
 * prerequisites for you.
 */
public class Add extends Action {
    private Lecture lectureToAdd;
    private Discussion discussionToAdd;

    public Add() {
        this.lectureToAdd = null;
        this.discussionToAdd = null;
    }

    public Add(Lecture lectureToAdd) {
        this();
        this.lectureToAdd = lectureToAdd;
    }

    public Add(Lecture lectureToAdd, Discussion discussionToAdd) {
        this(lectureToAdd);
        this.discussionToAdd = discussionToAdd;
    }

    //TODO: Implement actual SPIRE execution to perform Add function.
    @Override
    public boolean perform() {
        if(allConditionsMet()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String string = "Add "+lectureToAdd.getNameAndSection();
        if(discussionToAdd != null) {
            string += " with "+discussionToAdd.getNameAndSection();
        }
        if(hasConditions()) {
            string += " under conditions:"+super.toString();
        }
        return string;
    }
}
