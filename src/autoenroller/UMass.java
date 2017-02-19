package autoenroller;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * The constants that identify {@link org.openqa.selenium.WebElement}s
 * throughout SPIRE.
 */
public class UMass {
    // General
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    public static final int NOT_FOUND = -1;

    public static final String CHECKBOX_CLASS = "PSCHECKBOX";
    public static final String RADIO_BUTTON_CLASS = "PSRADIOBUTTON";
    public static final String CONFIRM_BUTTON_CLASS = "SSSBUTTON_CONFIRMLINK";
    public static final String HYPERLINK_CLASS_HTML = "class=\"PSHYPERLINK\"";
    public static final String HYPERLINKDISABLED_CLASS_HTML = "class=\"PSHYPERLINKDISABLED\"";
    public static final String HYPERLINK_CLASS = "PSHYPERLINK";
    public static final String NEXT_BUTTON_SELECTOR = "#DERIVED_CLS_DTL_NEXT_PB";
    public static final String FINISH_BUTTON_SELECTOR = "#DERIVED_REGFRM1_SSR_PB_SUBMIT";
    public static final String RESULT_ICON_SELECTOR = "#trSSR_SS_ERD_ER\\24 0_row1 > td:nth-child(3)";
    public static final String SUCCESS_ICON_HTML = "alt=\"Success\"";
    public static final String OPEN_ICON_HTML = "alt=\"Open\"";

    // SPIRE Logon
    public static final String LOGIN_URL = "https://spire.umass.edu/";
    public static final String USERNAME_ID = "userid";
    public static final String PASSWORD_ID = "pwd";
    public static final String LOGIN_BUTTON_SELECTOR = "#login > p:nth-child(5) > input[type=\"submit\"]";

    // Student Center
    public static final String ENROLLMENT_LINK_SELECTOR = "#DERIVED_SSS_SCR_SSS_LINK_ANCHOR1";

    // Shopping Cart
    public static final String CART_SCHEDULE_SELECTOR = "#STDNT_ENRL_SSVW\\24 scroll\\24 0";
    public static final String CART_SHOPPING_SELECTOR = "#SSR_REGFORM_VW\\24 scroll\\24 0";
    public static final String ENROLLED_IMAGE = "UM_PS_ENROLLED_ICN_1.gif";
    public static final String CART_CHECKBOX_HTML = "class=\"PSCHECKBOX\"";
    public static final String LECTURE_DESC_SELECTOR = "#DERIVED_CLS_DTL_DESCR50";
    public static final String DISCUSSIONS_TABLE_SELECTOR = "#SSR_CLS_TBL_R1\\24 scroll\\24 0 > tbody > tr:nth-child(2) > td > table";

    // Add Classes
    public static final String ADD_CART_FIELD_SELECTOR = "#DERIVED_REGFRM1_CLASS_NBR";
    public static final String ADD_CART_ENTER_SELECTOR = "#DERIVED_REGFRM1_SSR_PB_ADDTOLIST2\\24 9\\24";
    public static final String ADD_DISC_TABLE_SELECTOR = "#SSR_CLS_TBL_R1\\24 scroll\\24 0";
    public static final String CONFIRM_ADD_CART_SELECTOR = "#DERIVED_CLS_DTL_NEXT_PB\\24 280\\24";
    public static final String ENROLL_BUTTON_SELECTOR = "#DERIVED_REGFRM1_LINK_ADD_ENRL\\24 291\\24";
    public static final String ADD_MORE_CLASS_SELECTOR = "#DERIVED_REGFRM1_SSR_LINK_STARTOVER";

    // Drop Classes
    public static final String DROP_TABLE_SELECTOR = "#STDNT_ENRL_SSV1\\24 scroll\\24 0";

    // Swap Classes
    public static final String SWAP_SCHEDULE_MENU_SELECTOR = "#DERIVED_REGFRM1_DESCR50\\24 225\\24";
    public static final String SWAP_CART_ID_SELECTOR = "#DERIVED_REGFRM1_CLASS_NBR";
    public static final String SWAP_ENTER_ID_SELECTOR = "#DERIVED_REGFRM1_SSR_PB_ADDTOLIST2\\24 106\\24";

    // Edit Classes
    public static final String ENROLLED_DROPDOWN_SELECTOR = "#DERIVED_REGFRM1_DESCR50\\24 225\\24";
    public static final String EDIT_CONFIRM_STEP_1_SELECTOR = "#DERIVED_REGFRM1_LINK_UPDATE_ENRL";

    // Returns elements of the shopping cart table on the shopping cart page.
    public static WebElement findElementShoppingCart(WebDriver driver, int row, int col) {
        return waitForElement(driver, By.cssSelector("#trSSR_REGFORM_VW\\24 0_row"+row+" > td:nth-child("+col+")"));
    }

    // Returns elements of the current schedule table on the shopping cart page.
    public static WebElement findElementCartSchedule(WebDriver driver, int row, int col) {
        return waitForElement(driver, By.cssSelector("#trSTDNT_ENRL_SSVW\\24 0_row"+row+" > td:nth-child("+col+")"));
    }

    // Returns elements of the current Lecture's table of all existing Discussions.
    public static WebElement findElementDiscussionTable(WebDriver driver, int row, int col) {
        return waitForElement(driver, By.cssSelector("#trSSR_CLS_TBL_R1\\24 0_row"+row+" > td:nth-child("+col+")"));
    }

    public static WebElement findElementDropTable(WebDriver driver, int row, int col) {
        return waitForElement(driver, By.cssSelector("#trSTDNT_ENRL_SSV1\\24 0_row"+row+" > td:nth-child("+col+")"));
    }

    public static WebElement findElementAddTable(WebDriver driver, int row, int col) {
        return waitForElement(driver, By.cssSelector("#trSSR_CLS_TBL_R1\\24 0_row"+row+" > td:nth-child("+col+")"));
    }

    public static WebElement findElementTab(WebDriver driver, String tabName) {
        WebElement tabFound = null;
        // This table has many inactive/invisible rows, but they will not match any text so they can be ignored.
        for(WebElement tab : waitForElement(driver, By.cssSelector(
                "#win0divDERIVED_SSTSNAV_SSTS_NAV_SUBTABS > div > table > tbody > tr:nth-child(2)")).findElements(By.tagName("td"))) {
            if(tab.getText().toLowerCase().equals(tabName.toLowerCase())) {
                tabFound = tab;
                break;
            }
        }
        return tabFound;
    }

    /**
     * Wait for a {@link WebElement} to load.
     * Refreshes every 200 milliseconds and times out after 10 seconds.
     * @param driver    {@link WebDriver} running the browser.
     * @param by        The element being checked for.
     * @return          The {@link WebElement} once it has been found.
     */
    public static WebElement waitForElement(WebDriver driver, By by) {
        // This may be a good place to check if SPIRE asks to select a semester.
        // Debatable as to whether this is too low-level and should be handled
        // situationally rather than for every Wait instance.
        // When they happen, semester selections come up for very many actions.
        return (new WebDriverWait(driver, 10, 200)).until(ExpectedConditions.presenceOfElementLocated(by));
    }

    /**
     * Wait for a {@link WebElement} to load.
     * Refreshes every 200 milliseconds and times out after a given number of seconds.
     * @param driver            {@link WebDriver} running the browser.
     * @param timeoutSeconds    Number of seconds to spend refreshing before timing out.
     * @param by                The element being checked for.
     * @return                  The {@link WebElement} once it has been found.
     */
    public static WebElement waitForElement(WebDriver driver, int timeoutSeconds, By by) {
        return (new WebDriverWait(driver, timeoutSeconds, 200)).until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
