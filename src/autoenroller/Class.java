package autoenroller;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Classes may be {@link Lecture}s or {@link Discussion}s.
 * All classes have a name that consists of the class'
 * abbreviation and class level number, section number,
 * a description stating the class' long-form name,
 * and a class ID that uniquely identifies it.
 * Labs are treated like Discussions.
 */
public abstract class Class {
    protected String name;
    protected String section;
    protected String description;
    protected String classId;

    public Class() {
        setName("");
        setSection("");
        setDescription("");
        setClassId("'");
    }

    public Class(String name) {
        this();
        String[] split = name.split("-");
        setName(split[0]);
        if(split.length > 1) {
            setSection(split[1]);
        }
    }

    public Class(String name, String classId) {
        this(name);
        setClassId(classId);
    }

    public Class(String name, String description, String classId) {
        this(name, classId);
        setDescription(description);
    }

    public Class(String name, String section, String description, String classId) {
        this(name, description, classId);
        setName(name);
        setSection(section);
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
        this.description = description.replace(" (Lecture)", "").replace(" (Discussion)", "");
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
        this.classId = classId.replace("(", "").replace(")", "");
    }

    /**
     * Checks if the Class is open. Class must be in the shopping cart.
     * Also used to check Discussions. Overridden in Discussion class
     * to find Discussions inside of their Lectures in the shopping cart.
     * This is the default check if a Lecture is open.
     * @param driver    WebDriver to handle the clicking and parsing.
     * @return          Class is either open, closed, or not found.
     */
    public int isOpen(WebDriver driver) {
        int result = UMass.NOT_FOUND;
        // If this is not the shopping cart, go to the shopping cart.
        if(!driver.getTitle().contains("Shopping Cart")) {
            UMass.findElementTab(driver, "add").click();
        }
        // For each Class in the shopping cart. Size-2 because driver finds top 2 header rows.
        for(int row = 1; row <= UMass.waitForElement(driver, By.cssSelector(UMass.CART_SHOPPING_SELECTOR))
                .findElements(By.tagName("tr")).size()-2; row++) {
            // If this Class is the class ID of this Lecture.
            if(UMass.findElementShoppingCart(driver, row, 2).getText().contains(getClassId())) {
                // If this Class has the open icon.
                if(UMass.findElementShoppingCart(driver, row, 7).getAttribute("innerHTML").contains(UMass.OPEN_ICON_HTML)) {
                    result = UMass.TRUE;
                } else {
                    result = UMass.FALSE;
                }
                break;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        if(!description.equals("")) {
            return getNameAndSection()+": "+description+" ("+classId+")";
        } else {
            return getNameAndSection()+" ("+classId+")";
        }
    }
}
