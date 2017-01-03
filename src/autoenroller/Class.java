package autoenroller;

/**
 * Classes may be {@link Lecture}s or {@link Discussion}s.
 * All classes have a name that consists of the class'
 * abbreviation and class level number, section number,
 * a description stating the class' long-form name,
 * and a class ID that uniquely identifies it.
 * Does not support Labs. In the future, Labs should
 * be treated like discussions.
 */
public abstract class Class {
    protected String name;
    protected String section;
    protected String description;
    protected String classId;

    public Class() {
        this.name = "";
        this.section = "";
        this.description = "";
        this.classId = "";
    }

    public Class(String name) {
        this();
        String[] split = name.split("-");
        this.name = split[0];
        this.section = split[1];
    }

    public Class(String name, String classId) {
        this(name);
        this.classId = classId;
    }

    public Class(String name, String description, String classId) {
        this(name, classId);
        this.description = description;
    }

    public Class(String name, String section, String description, String classId) {
        this(name, description, classId);
        this.name = name;
        this.section = section;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNameAndSection() {
        return name+"-"+section;
    }

    public String getNameAndDescription() {
        return name+": "+description;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public abstract boolean isOpen();

    @Override
    public String toString() {
        if(!description.equals("")) {
            return getNameAndSection()+": "+description+" ("+classId+")";
        } else {
            return getNameAndSection()+" ("+classId+")";
        }
    }
}
