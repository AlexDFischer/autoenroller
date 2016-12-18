package fischerautoenroll;

public class Class {
    private String name;
    private String classId;

    public Class() {
        this.name = "";
        this.classId = "";
    }

    public Class(String name, String section, String classId) {
        this();
        this.name = name;
        this.classId = classId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    @Override
    public String toString() {
        return "Class{" +
                "name='" + name + '\'' +
                ", classId='" + classId + '\'' +
                '}';
    }
}
