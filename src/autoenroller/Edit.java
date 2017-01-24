package autoenroller;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

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

    @Override
    public boolean perform(Spire spire) {
        boolean result = false;
        WebDriver driver = spire.getDriver();
        // Go to the "edit" SPIRE tab.
        UMass.findElementTab(spire.getDriver(), "edit").click();
        // Wait, then select the Lecture in the dropdown menu by its class ID.
        new Select(UMass.waitForElement(driver, By.cssSelector(UMass.ENROLLED_DROPDOWN_SELECTOR))).selectByValue(lectureToEdit.getClassId());
        // Click the "Proceed To Step 2 Of 3" button.
        driver.findElement(By.cssSelector(UMass.EDIT_CONFIRM_STEP_1_SELECTOR)).click();
        // If there is no desired Discussion and no currently enrolled Discussion, this class doesn't need them.
        // It's also useless to edit a class with no Discussions.
        if(discussionToAdd != null && lectureToEdit.getEnrolledDiscussion() != null) {
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
            UMass.waitForElement(driver, By.cssSelector(UMass.NEXT_BUTTON_SELECTOR)).click();
            UMass.waitForElement(driver, 30,  By.cssSelector(UMass.FINISH_BUTTON_SELECTOR)).click();
            // If the result icon says successful, return true.
            if(UMass.waitForElement(driver, By.cssSelector(UMass.RESULT_ICON_SELECTOR))
                    .getAttribute("innerHTML").contains(UMass.SUCCESS_ICON_HTML)) {
                result = true;
            }
        }
        // Go back to the shopping cart.
        UMass.findElementTab(driver, "add").click();
        this.setSatisfied(result);
        return result;
    }

    public Lecture getLectureToEdit() {
        return lectureToEdit;
    }

    public void setLectureToEdit(Lecture lectureToEdit) {
        this.lectureToEdit = lectureToEdit;
    }

    public Discussion getDiscussionToAdd() {
        return discussionToAdd;
    }

    public void setDiscussionToAdd(Discussion discussionToAdd) {
        this.discussionToAdd = discussionToAdd;
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
