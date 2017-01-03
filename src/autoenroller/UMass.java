package autoenroller;

/**
 * The many constants that identify {@link org.openqa.selenium.WebElement}s
 * on each page of SPIRE.
 */
public class UMass {
    // SPIRE Logon
    public static final String LOGIN_URL = "https://spire.umass.edu/";
    public static final String USERNAME_ID = "userid";
    public static final String PASSWORD_ID = "pwd";
    public static final String LOGIN_BUTTON_SELECTOR = "#login > p:nth-child(5) > input[type=\"submit\"]";

    // Student Center
    public static final String STUDENT_ENROLLMENT_ID = "DERIVED_SSS_SCR_SSS_LINK_ANCHOR1";
}
