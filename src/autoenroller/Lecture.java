package autoenroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A Lecture is a {@link Class} that may have some number of
 * {@link Discussion} sections associated with it. The Lecture
 * contains a {@link Map} where class ID's are keys and the
 * Discussions are values. If the user is enrolled in this
 * Lecture, they must also be enrolled in a Discussion,
 * which must be in the Map of discussions for this Lecture.
 * The enrolled discussion is a key to the Discussion in the Map.
 */
public class Lecture extends Class {
    private String enrolledDiscussion;
    private Map<String, Discussion> discussions;

    public Lecture() {
        super();
        this.enrolledDiscussion = "";
        this.discussions = new HashMap<>();
    }

    public Lecture(String name, String classId) {
        super(name, classId);
        this.enrolledDiscussion = "";
        this.discussions = new HashMap<>();
    }

    public Lecture(String name, String description, String classId) {
        super(name, description, classId);
        this.enrolledDiscussion = "";
        this.discussions = new HashMap<>();
    }

    public Lecture(String name, String classId, Discussion[] discussions) {
        super(name, classId);
        this.enrolledDiscussion = "";
        this.discussions = new HashMap<>();
        for(Discussion d : discussions) {
            this.discussions.put(d.getClassId(), d);
        }
    }

    public Lecture(String name, String classId, ArrayList<Discussion> discussions) {
        super(name, classId);
        this.enrolledDiscussion = "";
        this.discussions = new HashMap<>();
        for(Discussion d : discussions.toArray(new Discussion[0])) {
            this.discussions.put(d.getClassId(), d);
        }
    }

    public Lecture(String name, String description, String classId, Discussion enrolledDiscussion) {
        super(name, description, classId);
        this.enrolledDiscussion = enrolledDiscussion.getClassId();
        this.discussions = new HashMap<>();
        this.discussions.put(this.enrolledDiscussion, enrolledDiscussion);
    }

    public Lecture(String name, String description, String classId, Discussion... discussions) {
        super(name, description, classId);
        this.description = description;
        this.discussions = new HashMap<>();
        for(Discussion d : discussions) {
            this.discussions.put(d.getClassId(), d);
        }
    }

    public Lecture(String name, String description, String classId, ArrayList<Discussion> discussions) {
        super(name, description, classId);
        this.description = description;
        this.discussions = new HashMap<>();
        for(Discussion d : discussions.toArray(new Discussion[0])) {
            this.discussions.put(d.getClassId(), d);
        }
    }

    public Lecture(String name, String description, String classId, Discussion enrolledDiscussion, Discussion... discussions) {
        super(name, description, classId);
        this.enrolledDiscussion = enrolledDiscussion.getClassId();
        this.discussions = new HashMap<>();
        this.discussions.put(this.enrolledDiscussion, enrolledDiscussion);
        for(Discussion d : discussions) {
            this.discussions.put(d.getClassId(), d);
        }
    }

    public Lecture(String name, String description, String classId, Discussion enrolledDiscussion, ArrayList<Discussion> discussions) {
        super(name, description, classId);
        this.enrolledDiscussion = enrolledDiscussion.getClassId();
        this.discussions = new HashMap<>();
        this.discussions.put(this.enrolledDiscussion, enrolledDiscussion);
        for(Discussion d : discussions.toArray(new Discussion[0])) {
            this.discussions.put(d.getClassId(), d);
        }
    }

    public Lecture setEnrolledDiscussion(Discussion enrolledDiscussion) {
        this.enrolledDiscussion = enrolledDiscussion.getClassId();
        discussions.put(this.enrolledDiscussion, enrolledDiscussion);
        return this;
    }

    public Discussion getEnrolledDiscussion() {
        return discussions.get(enrolledDiscussion);
    }

    public Discussion getDiscussion(String key) {
        return discussions.get(key);
    }

    public Map<String, Discussion> getDiscussions() {
        return discussions;
    }

    public ArrayList<Discussion> getDiscussionsList() {
        return new ArrayList<>(discussions.values());
    }

    public Lecture addDiscussion(Discussion discussion) {
        discussions.put(discussion.getClassId(), discussion);
        return this;
    }

    public Lecture addDiscussions(Discussion... discussions) {
        for(Discussion d : discussions) {
            this.discussions.put(d.getClassId(), d);
        }
        return this;
    }

    public Lecture addDiscussions(ArrayList<Discussion> discussions) {
        for(Discussion d : discussions) {
            this.discussions.put(d.getClassId(), d);
        }
        return this;
    }

    public Lecture removeDiscussion(Discussion discussion) {
        discussions.remove(discussion.getClassId());
        return this;
    }

    public boolean hasDiscussions() {
        return !discussions.isEmpty();
    }

    @Override
    public String toString() {
        if(discussions == null) {
            return super.toString();
        } else {
            if(discussions.size() == 0) {
                return super.toString();
            } else {
                String string = super.toString()+" with discussions:";
                for(Discussion d : discussions.values()) {
                    string += "\n   "+d.toString();
                    if(d.getClassId().equals(enrolledDiscussion)) {
                        string += " (enrolled)";
                    }
                }
                return string;
            }
        }
    }
}
