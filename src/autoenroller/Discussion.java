package autoenroller;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * A Discussion is a secondary part of a {@link Lecture}.
 * The user may be enrolled in one Discussion per Lecture.
 * If a Lecture offers Discussions, the user must enroll in one
 * to enroll in the Lecture. Lectures in the shopping cart
 * have a selected Discussion for the user to enroll into.
 * A Lecture may offer multiple Discussions or none at all.
 * Labs are treated like Discussions.
 */
public class Discussion extends Class {

    public Discussion() {
        super();
    }

    public Discussion(String name) {
        this();
        String[] split = name.split("-");
        setName(split[0]);
        setSection(split[1]);
    }

    public Discussion(String name, String classId) {
        this(name);
        setClassId(classId);
    }

    public Discussion(String name, String classId, String description) {
        this(name, classId);
        setDescription(description);
    }

    @Override
    public int isOpen(WebDriver driver) {
        // First, search for the Discussion in the shopping cart, like a Lecture.
        int result = super.isOpen(driver);
        // If the Discussion was not found in the shopping cart, find it inside of its Lecture.
        if(result == UMass.NOT_FOUND) {
            // For each Class in the shopping cart. Size-2 because driver finds top 2 header rows.
            for(int rowSC = 1; rowSC <= driver.findElement(By.cssSelector(UMass.CART_SHOPPING_SELECTOR))
                    .findElements(By.tagName("tr")).size()-2; rowSC++) {
                // If this shopping cart Class has the same name as this Discussion, and is clickable.
                // Implies that this Class is the Lecture associated with this Discussion.
                WebElement shoppingCartClassName = UMass.findElementShoppingCart(driver, rowSC, 2);
                if(shoppingCartClassName.getText().contains(this.getName())
                        && shoppingCartClassName.getAttribute("innerHTML").contains(UMass.HYPERLINK_CLASS_HTML)) {
                    // Click that Lecture to see all of its Discussions.
                    shoppingCartClassName.findElement(By.className(UMass.HYPERLINK_CLASS)).click();
                    // For each Discussion in the discussion table. Size-1 because driver finds top header row.
                    for(int rowDT = 1; rowDT <= UMass.waitForElement(driver, By.cssSelector(UMass.DISCUSSIONS_TABLE_SELECTOR))
                            .findElements(By.tagName("tr")).size()-1; rowDT++) {
                        // If this Discussion matches the class ID.
                        if(UMass.findElementDiscussionTable(driver, rowDT, 1).getText().contains(this.getClassId())) {
                            // If it has an open icon, return true. If not, return false.
                            if(UMass.findElementDiscussionTable(driver, rowDT, 7).getAttribute("innerHTML").contains((UMass.OPEN_ICON_HTML))) {
                                result = UMass.TRUE;
                            } else {
                                result = UMass.FALSE;
                            }
                            // Whether open or closed, break the discussion loop.
                            break;
                        }
                    }
                    // Go back to the shopping cart.
                    UMass.findElementTab(driver, "add").click();
                    // Since the associated Lecture was found, break the shopping cart loop.
                    break;
                }
            }
        }

        return result;
    }
}
