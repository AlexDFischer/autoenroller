package autoenroller;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * The main controller for SPIRE activity throughout runtime.
 * Though the fischerautoenroll package assumes future support
 * for other schools, it cannot be assumed that their class
 * structure is the same as UMass' structure. Other schools
 * also do not use SPIRE. This package is therefore geared
 * toward SPIRE at UMass exclusively.
 *
 * The current schedule and shopping cart are stored as
 * {@link Map}s in order to prevent duplicate Lectures
 * that may come up when combining hardcoded and parsed
 * Lectures. Keys are class IDs and return Lectures.
 */
public class Spire {
    private WebDriver driver;
    private File propertiesFile;
    private Properties properties;
    private Map<String, Lecture> currentSchedule;
    private Map<String, Lecture> shoppingCart;
    private ArrayList<Action> actions;

    public Spire(WebDriver driver) {
        this.driver = driver;
        this.propertiesFile = null;
        this.properties = new Properties();
        this.currentSchedule = new HashMap<>();
        this.shoppingCart = new HashMap<>();
        this.actions = new ArrayList<>();
    }

    public Spire(WebDriver driver, ArrayList<Action> actions) {
        this(driver);
        this.actions = actions;
    }

    public Spire(WebDriver driver, Map<String, Lecture> currentSchedule, Map<String, Lecture> shoppingCart, ArrayList<Action> actions) {
        this(driver);
        this.currentSchedule = currentSchedule;
        this.shoppingCart = shoppingCart;
        this.actions = actions;
    }

    /**
     * This function runs the entire Spire automation
     * from login to conclusion. It is called by the
     * Spire controller after instantiation and adding
     * the schedule, shopping cart, and actions.
     */
    public void run() {
        spireLogon(UMass.LOGIN_URL);

        // SPIRE is normally shown as a webpage within a webpage.
        // The subwebpage's code is hard to access while it is nested.
        // This line explicitly waits until the internal frame is present
        // and then loads it into the driver as the main webpage.
        driver.get(UMass.waitForElement(driver, By.tagName("iframe")).getAttribute("src"));
        // Wait in case there is an error popup (seen on Firefox, not Chrome).
        UMass.sleep(500);
        // Click on the link that goes to enrollment.
        UMass.waitForElement(driver, By.cssSelector(UMass.ENROLLMENT_LINK_SELECTOR)).click();

        // Currently in the shopping cart. There may be some hardcoded Classes
        // used to create Actions, but this parses the actual current schedule and
        // shopping cart to prevent discrepancies. Duplicate Classes are prevented
        // with the use of a Map with keys of class IDs.
        currentSchedule.putAll(parseCurrentSchedule());
        shoppingCart.putAll(parseShoppingCart());
        printCurrentSchedule();
        printShoppingCart();
        if(actions.isEmpty()) {
            // Create Actions based on current schedule and shopping cart.
            actions = createActions();
        }
        printActions();

        System.out.println("Beginning automated refresh.");
        long previousTime = System.currentTimeMillis();
        while(!actions.isEmpty()) {
            // Reload current shopping cart page at least every 5 seconds.
            // If it has been less than 5 seconds since the last refresh, wait an extra 5 seconds.
            if((System.currentTimeMillis()-previousTime)/1000 < 5) {
                UMass.sleep(5000);
            }
            // Uncomment this line to show the number of seconds since the last refresh, on every refresh.
            // System.out.println("Refreshing "+(System.currentTimeMillis()-previousTime)/1000+" seconds later...");
            previousTime = System.currentTimeMillis();
            driver.get(driver.getCurrentUrl());
            // For each Action, check Conditions, meet if able, perform if able.
            for(Action action : actions) {
                if(action.allConditionsMet()) {
                    System.out.print("All conditions met for action:\n"+action.toString()+"\nPerforming action... ");
                    if(action.perform(this)) {
                        System.out.println("Successfully performed action.");
                        // If the successful performance of this Action satisfies other Actions, mark them as such.
                        action.setSatisfied(true);
                        action.satisfyOtherActions();
                        // After every successful action performance, refresh the schedule and shopping cart.
                        // May remove hardcoded Classes if they do not exist in actual schedule or cart.
                        currentSchedule = parseCurrentSchedule();
                        shoppingCart = parseShoppingCart();
                        printCurrentSchedule();
                        printShoppingCart();
                        printActions();
                    } else {
                        System.out.println("Failed to perform action.");
                    }
                }
            }
            // Remove all satisfied actions.
            for(Action action: actions) {
                if(action.isSatisfied()) {
                    actions.remove(action);
                }
            }
        }
        System.out.println("All actions performed.");
        printCurrentSchedule();
    }

    private String getUsername() {
        if(properties.getProperty("username") != null) {
            return properties.getProperty("username");
        } else {
            System.out.println("Username:");
            String username = new Scanner(System.in).nextLine();
            System.out.println("Save username? (y/n)");
            if(new Scanner(System.in).nextLine().equals("y")) {
                properties.setProperty("username", username);
                storeProperties(properties, propertiesFile);
            }
            return username;
        }

    }

    private String getPassword() {
        if(properties.getProperty("password") != null) {
            return properties.getProperty("password");
        } else {
            System.out.println("Password:");
            String password;
            Console console = System.console();
            if(console != null) {
                password = new String(console.readPassword());
            } else {
                password = new Scanner(System.in).nextLine();
            }
            System.out.println("Save password? (y/n)");
            if(new Scanner(System.in).nextLine().equals("y")) {
                properties.setProperty("password", password);
                storeProperties(properties, propertiesFile);
            }
            return password;
        }
    }

    public void printCurrentSchedule() {
        System.out.println("Current schedule:");
        for(Class c : currentSchedule.values()) {
            System.out.println(c.toString());
        }
        System.out.println();
    }

    public void printShoppingCart() {
        System.out.println("Shopping cart:");
        for(Lecture l : shoppingCart.values()) {
            System.out.println(l.toString());
        }
        System.out.println();
    }

    public void printActions() {
        System.out.println("Actions:");
        for(Action a : actions.toArray(new Action[0])) {
            System.out.println(a.toString());
        }
        System.out.println();
    }

    private void spireLogon(String url) {
        driver.get(url);
        do {
            // Explicitly waits for the Username field to load and types username.
            UMass.waitForElement(driver, By.id(UMass.USERNAME_ID)).sendKeys(getUsername());
            // Presence of Username means Password and Go button are loaded too.
            driver.findElement(By.id(UMass.PASSWORD_ID)).sendKeys(getPassword());
            driver.findElement(By.cssSelector(UMass.LOGIN_BUTTON_SELECTOR)).click();
            UMass.sleep(1000);
        // The page will be "SPIRE Logon" as long as the user is not logged in.
        } while(driver.getTitle().equals("SPIRE Logon"));
    }

    private Map<String, Lecture> parseCurrentSchedule() {
        Map<String, Lecture> schedule = new HashMap<>();
        // Gets the size of the current schedule table on the shopping cart page
        // and iterates over each row. Skips the first row; it's just header labels.
        for(int row = 1; row < UMass.waitForElement(driver, By.cssSelector(UMass.CART_SCHEDULE_SELECTOR)).findElements(By.tagName("tr")).size(); row++) {
            // Assume enrollment status is always in 7th column of each row.
            if(UMass.findElementCartSchedule(driver, row, 7).getAttribute("innerHTML").contains(UMass.ENROLLED_IMAGE)) {
                // Lecture rows' names are hyperlink classes. Discussions are hyperlink-disabled classes.
                if(UMass.findElementCartSchedule(driver, row, 1).getAttribute("innerHTML").contains(UMass.HYPERLINK_CLASS_HTML)) {
                    // Create new Lecture by parsing text from this row.
                    String[] lectureInfo = UMass.findElementCartSchedule(driver, row, 1).getText().split("\n");
                    Lecture scheduleLecture = new Lecture(lectureInfo[0], lectureInfo[1]);
                    scheduleLecture.setDescription(UMass.findElementCartSchedule(driver, row, 2).getText());
                    schedule.put(scheduleLecture.getClassId(), scheduleLecture);
                } else if(UMass.findElementCartSchedule(driver, row, 1).getAttribute("innerHTML").contains(UMass.HYPERLINKDISABLED_CLASS_HTML)) {
                    String[] discussionInfo = UMass.findElementCartSchedule(driver, row, 1).getText().split("\n");
                    Discussion scheduleDiscussion = new Discussion(discussionInfo[0], discussionInfo[1]);
                    scheduleDiscussion.setDescription(UMass.findElementCartSchedule(driver, row, 2).getText());
                    // Finds the Lecture of the same name as Discussion and sets as enrolled Discussion.
                    for(Lecture l : schedule.values()) {
                        if(l.getName().equals(scheduleDiscussion.getName())) {
                            l.setEnrolledDiscussion(scheduleDiscussion);
                            break;
                        }
                    }
                }
            }
        }
        // Goes to each Lecture's edit tab and parses the other Discussion sections.
        // A Lecture that is supposed to have Discussions will already contain one.
        // A Lecture with no Discussions has none at all.
        for(Lecture l : schedule.values()) {
            if(l.hasDiscussions()) {
                l.addDiscussions(getOtherScheduleDiscussions(l));
            }
        }
        return schedule;
    }

    private ArrayList<Discussion> getOtherScheduleDiscussions(Lecture lecture) {
        ArrayList<Discussion> otherDiscussions = new ArrayList<>();
        // Clicks on the "edit" tab at the top of SPIRE.
        UMass.findElementTab(driver, "edit").click();
        // Waits and finds enrolled Lectures dropdown list and selects Lecture with its class ID.
        new Select(UMass.waitForElement(driver, By.cssSelector(UMass.ENROLLED_DROPDOWN_SELECTOR))).selectByValue(lecture.getClassId());
        driver.findElement(By.cssSelector(UMass.EDIT_CONFIRM_STEP_1_SELECTOR)).click();
        // Waits for the discussions table to load, then iterates over all Discussions, regardless of open/closed.
        for(int i = 1; i < UMass.waitForElement(driver, By.cssSelector(UMass.DISCUSSIONS_TABLE_SELECTOR))
                .findElements(By.tagName("tr")).size(); i++) {
            Discussion otherDiscussion = new Discussion();
            otherDiscussion.setName(lecture.getName());
            otherDiscussion.setDescription(lecture.getDescription());
            otherDiscussion.setClassId(UMass.findElementDiscussionTable(driver, i, 2).getText());
            otherDiscussion.setSection(UMass.findElementDiscussionTable(driver, i, 3).getText());
            otherDiscussions.add(otherDiscussion);
        }
        // Goes back to the shopping cart after all Discussions are parsed in.
        UMass.findElementTab(driver, "add").click();
        return otherDiscussions;
    }

    private Map<String, Lecture> parseShoppingCart() {
        Map<String, Lecture> cart = new HashMap<>();
        // Subtract 2 from table length because first 2 rows are headers and labels.
        for(int row = 1; row < UMass.waitForElement(driver, By.cssSelector(UMass.CART_SHOPPING_SELECTOR)).findElements(By.tagName("tr")).size()-2; row++) {
            // In the shopping cart, Lectures have checkboxes and Discussions do not.
            if(UMass.findElementShoppingCart(driver, row, 1).getAttribute("innerHTML").contains(UMass.CART_CHECKBOX_HTML)) {
                String[] lectureInfo = UMass.findElementShoppingCart(driver, row, 2).getText().split("\n");
                Lecture cartLecture = new Lecture(lectureInfo[0], lectureInfo[1]);
                //Goes into the Lecture to fetch the description.
                UMass.findElementShoppingCart(driver, row, 2).findElement(By.className(UMass.HYPERLINK_CLASS)).click();
                // Gets the whole Lecture name and splits by the dash (with spaces) for description.
                cartLecture.setDescription(UMass.waitForElement(driver, By.cssSelector(UMass.LECTURE_DESC_SELECTOR)).getText().split(" - ")[1]);
                // While we're on this Lecture's page, check if there are Discussions to add.
                if (driver.findElements(By.cssSelector(UMass.DISCUSSIONS_TABLE_SELECTOR)).size() > 0) {
                    for (int i = 1; i < driver.findElement(By.cssSelector(UMass.DISCUSSIONS_TABLE_SELECTOR)).findElements(By.tagName("tr")).size(); i++) {
                        Discussion discussion = new Discussion();
                        discussion.setName(cartLecture.getName());
                        discussion.setDescription(cartLecture.getDescription());
                        discussion.setClassId(UMass.findElementDiscussionTable(driver, i, 2).getText());
                        discussion.setSection(UMass.findElementDiscussionTable(driver, i, 3).getText());
                        cartLecture.addDiscussion(discussion);
                    }
                }
                UMass.findElementTab(driver, "add").click();
                cart.put(cartLecture.getClassId(),cartLecture);
            }
        }
        return cart;
    }

    private ArrayList<Action> createActions() {
        ArrayList<Action> actions = new ArrayList<>();
        Scanner s = new Scanner(System.in);
        String input;
        System.out.println("Create basic Actions (Actions with Conditions must be hardcoded):");
        do {
            System.out.println("What kind of action do you want to create? Enter one of the following:");
            System.out.println("\"add\", \"drop\", \"edit\", \"swap\", \"done\"");
            input = s.nextLine();
            switch(input) {
                case "add":     Add add = new Add();
                                System.out.println("Select the class to add:");
                                add.setLectureToAdd(selectLecture(getShoppingCart()));
                                System.out.println("Select the discussion to add:");
                                add.setDiscussionToAdd(selectDiscussion(add.getLectureToAdd().getDiscussions()));
                                getActions().add(add);
                                break;
                case "drop":    Drop drop = new Drop();
                                System.out.println("Select the class to drop:");
                                drop.setLectureToDrop(selectLecture(getCurrentSchedule()));
                                getActions().add(drop);
                                break;
                case "edit":    Edit edit = new Edit();
                                System.out.println("Select the class to edit:");
                                edit.setLectureToEdit(selectLecture(getCurrentSchedule()));
                                do {
                                    System.out.println("Select the discussion to edit into:");
                                    Discussion d = selectDiscussion(edit.getLectureToEdit().getDiscussions());
                                    if(!d.equals(edit.getLectureToEdit().getEnrolledDiscussion())) {
                                        edit.setDiscussionToAdd(d);
                                    } else {
                                        System.out.println("Already enrolled in this discussion.");
                                    }
                                } while(edit.getDiscussionToAdd() == null);
                                getActions().add(edit);
                                break;
                case "swap":    Swap swap = new Swap();
                                System.out.println("Select the class to drop:");
                                swap.setLectureToDrop(selectLecture(getCurrentSchedule()));
                                System.out.println("Select the class to add:");
                                swap.setLectureToAdd(selectLecture(getShoppingCart()));
                                System.out.println("Select the discussion to add:");
                                swap.setDiscussionToAdd(selectDiscussion(swap.getLectureToAdd().getDiscussions()));
                                getActions().add(swap);
                                break;
                case "done":    break;
                default:        System.out.println("Invalid input.");
            }
        } while(!input.equals("done"));
        return actions;
    }

    public Lecture selectLecture(Map<String, Lecture> lectures) {
        for(Lecture l : lectures.values()) {
            System.out.println(l.getClassId()+": "+l.getNameAndDescription());
        }
        Lecture result;
        do {
            System.out.println("Enter the class ID:");
            result = lectures.get(new Scanner(System.in).nextLine());
        } while(result == null);
        return result;
    }

    public Discussion selectDiscussion(Map<String, Discussion> discussions) {
        for(Discussion d : discussions.values()) {
            System.out.println(d.getClassId()+"+ "+d.getNameAndDescription());
        }
        Discussion result;
        do {
            System.out.println("Enter the class ID:");
            result = discussions.get(new Scanner(System.in).nextLine());
        } while(result == null);
        return result;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties, File propertiesFile) {
        this.properties = properties;
        this.propertiesFile = propertiesFile;
    }

    private void storeProperties(Properties properties, File propertiesFile) {
        if(!propertiesFile.exists()) {
            try {
                propertiesFile.createNewFile();
                Path propertiesNioPath = propertiesFile.toPath();
                Files.setAttribute(propertiesNioPath, "dos:hidden", true);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        try {
            properties.store(new FileOutputStream(propertiesFile), "");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Lecture> getCurrentSchedule() {
        return currentSchedule;
    }

    public Map<String, Lecture> getShoppingCart() {
        return shoppingCart;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }
}
