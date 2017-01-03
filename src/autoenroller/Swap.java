package autoenroller;

/**
 * The Swap {@link Action} enrolls the user in one {@link Lecture}
 * and drops another at the same time.
 *
 * This Action can fail if you have a class conflict or do not meet
 * the adding lecture's prerequisites. Class conflicts should be
 * predicted by the user and taken care of as {@link Condition}s
 * required to perform this Action.
 */
public class Swap extends Action {
    private Lecture lectureToAdd;
    private Lecture lectureToDrop;

    public Swap() {
        this.lectureToAdd = null;
        this.lectureToDrop = null;
    }

    public Swap(Lecture lectureToAdd, Lecture lectureToDrop) {
        this();
        this.lectureToAdd = lectureToAdd;
        this.lectureToDrop = lectureToDrop;
    }

    //TODO: Implement actual SPIRE execution to perform Swap function.
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
        String string = "Swap in "+lectureToAdd.getNameAndSection()+" out "+lectureToDrop.getNameAndSection();
        if(hasConditions()) {
            string += " under conditions:"+super.toString();
        }
        return string;
    }
}
