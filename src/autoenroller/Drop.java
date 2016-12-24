package autoenroller;

/**
 * The Drop {@link Action} drops a particular {@link Lecture}
 * when the appropriate {@link Condition}s are met.
 */
public class Drop extends Action {
    private Lecture lectureToDrop;

    public Drop() {
        this.lectureToDrop = null;
    }

    public Drop(Lecture lectureToDrop) {
        this();
        this.lectureToDrop = lectureToDrop;
    }

    //TODO: Implement actual SPIRE execution to perform Drop function.
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
        String string = "Drop "+lectureToDrop.getNameAndSection();
        if(hasConditions()) {
            string += " under conditions:"+super.toString();
        }
        return string;
    }
}
