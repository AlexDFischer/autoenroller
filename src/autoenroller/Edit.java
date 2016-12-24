package autoenroller;

/**
 * The Edit {@link Action} is used to change the discussion section
 * the user is enrolled in without dropping and re-adding the lecture.
 * The user must already be enrolled in the lecture to edit it.
 *
 * This Action can fail if you have a class conflict.
 * Class conflicts should be predicted by the user and taken
 * care of as {@link Condition}s required to perform this Action.
 */
public class Edit extends Action {
    private Lecture lectureToEdit;
    private Discussion discussionToAdd;

    public Edit() {
        this.lectureToEdit = null;
        this.discussionToAdd = null;
    }

    public Edit(Lecture lectureToEdit, Discussion discussionToAdd) {
        this();
        this.lectureToEdit = lectureToEdit;
        this.discussionToAdd = discussionToAdd;
    }

    //TODO: Implement actual SPIRE execution to perform Edit function.
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
        String string = "Edit "+lectureToEdit.getNameAndSection()+" into "+discussionToAdd.getNameAndSection();
        if(hasConditions()) {
            string += " under conditions:"+super.toString();
        }
        return string;
    }
}
