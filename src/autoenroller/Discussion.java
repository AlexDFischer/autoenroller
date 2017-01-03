package autoenroller;

public class Discussion extends Class {

    public Discussion() {
        super();
    }

    public Discussion(String name) {
        this();
        String[] split = name.split("-");
        this.name = split[0];
        this.section = split[1];
    }

    public Discussion(String name, String classId) {
        this(name);
        this.classId = classId;
    }

    public Discussion(String name, String classId, String description) {
        this(name, classId);
        this.description = description;
    }

    //TODO: Implement actual SPIRE execution to check if Discussion is open.
    @Override
    public boolean isOpen() {
        return true;
    }
}
