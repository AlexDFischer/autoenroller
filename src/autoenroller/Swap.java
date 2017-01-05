package autoenroller;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

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
    private Discussion discussionToAdd;
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

    public Swap(Lecture lectureToAdd, Discussion discussionToAdd, Lecture lectureToDrop) {
        this();
        this.lectureToAdd = lectureToAdd;
        this.discussionToAdd = discussionToAdd;
        this.lectureToDrop = lectureToDrop;
    }

    public Swap(Add add, Drop drop) {
        this();
        this.lectureToAdd = add.getLectureToAdd();
        this.discussionToAdd = add.getDiscussionToAdd();
        this.lectureToDrop = drop.getLectureToDrop();
    }

    @Override
    public boolean perform(Spire spire) {
        boolean result = false;
        WebDriver driver = spire.getDriver();
        // Go to the "swap" SPIRE tab.
        UMass.findElementTab(spire.getDriver(), "swap").click();
        // Select the Lecture to drop.
        new Select(UMass.waitForElement(driver, By.cssSelector(UMass.SWAP_SCHEDULE_MENU_SELECTOR)))
                .selectByValue(lectureToDrop.getClassId());
        // Enter the class ID of the Lecture to add.
        driver.findElement(By.cssSelector(UMass.SWAP_CART_ID_SELECTOR)).sendKeys(lectureToAdd.getClassId());
        // Click the enter button next to the class ID field.
        driver.findElement(By.cssSelector(UMass.SWAP_ENTER_ID_SELECTOR)).click();
        // If this Swap has a Discussion that needs to be selected.
        if(discussionToAdd != null) {
            // Waits for the discussions table to load, then finds the desired discussion.
            for(int row = 1; row < UMass.waitForElement(driver, By.cssSelector(UMass.DISCUSSIONS_TABLE_SELECTOR))
                    .findElements(By.tagName("tr")).size(); row++) {
                // If this Discussion row is the same class ID as the Discussion to add.
                if(UMass.findElementDiscussionTable(driver, row, 2).getText().contains(discussionToAdd.getClassId())) {
                    // Click on the Discussion's radio button and break out of the discussion table loop.
                    UMass.findElementDiscussionTable(driver, row, 1).findElement(By.className(UMass.RADIO_BUTTON_CLASS)).click();
                    break;
                }
            }
            // The Next button on the next 2 pages have the same CSS selector. Must sleep and wait to distinguish.
            driver.findElement(By.cssSelector(UMass.NEXT_BUTTON_SELECTOR)).click();
            UMass.sleep(1000);
        }
        // Click the next button on the confirm swap into Lecture page.
        UMass.waitForElement(driver, By.cssSelector(UMass.NEXT_BUTTON_SELECTOR)).click();
        // Click the finish button on the confirm swap page.
        UMass.waitForElement(driver, By.cssSelector(UMass.FINISH_BUTTON_SELECTOR)).click();
        // If the result icon says successful, return true.
        if(UMass.waitForElement(driver, By.cssSelector(UMass.RESULT_ICON_SELECTOR))
                .getAttribute("innerHTML").contains(UMass.SUCCESS_ICON_HTML)) {
            result = true;
        }
        // Go back to the shopping cart.
        UMass.findElementTab(driver, "add").click();
        this.setSatisfied(result);
        return result;
    }

    public Lecture getLectureToAdd() {
        return lectureToAdd;
    }

    public void setLectureToAdd(Lecture lectureToAdd) {
        this.lectureToAdd = lectureToAdd;
    }

    public Discussion getDiscussionToAdd() {
        return discussionToAdd;
    }

    public void setDiscussionToAdd(Discussion discussionToAdd) {
        this.discussionToAdd = discussionToAdd;
    }

    public Lecture getLectureToDrop() {
        return lectureToDrop;
    }

    public void setLectureToDrop(Lecture lectureToDrop) {
        this.lectureToDrop = lectureToDrop;
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
