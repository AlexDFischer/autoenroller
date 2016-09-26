package fischerautoenroll;

public class UmassWebPage
{
	// login page
	public static final String PAGE_URL = "https://www.spire.umass.edu/psp/heproda/?cmd=login&languageCd=ENG";
	public static final String LOC_USERNAME_INPUT = "input#userid";
	public static final String LOC_PASSWORD_INPUT = "input#pwd";
	public static final String LOC_SUBMIT_BUTTON = "input[name=Submit]";
	public static final String EXPECTED_LOGIN_TITLE = "Student Center";
	
	// main page
	public static final String ENROLLMENT_BUTTON = "a#DERIVED_SSS_SCR_SSS_LINK_ANCHOR1";
	
	// shopping cart page
	public static final String EXPECTED_SHOPING_CART_TITLE = "Add Classes";
	public static final String CLASSES_TABLE = "table#SSR_REGFORM_VW\\$scroll\\$0";
	public static final String CLASS_LINK = "a.PSHYPERLINK";
	public static final String OPEN_CLASS_ALT_TEXT = "Open";
	public static final String HIDDEN_INPUT = "input[type=\"hidden\"";
	public static final String ENROLL_BUTTON = "a#DERIVED_REGFRM1_LINK_ADD_ENRL\\$291\\$";
	
	// finish enrolling page
	public static final String FINISH_ENROLLING_BUTTON = "a#DERIVED_REGFRM1_SSR_PB_SUBMIT";
	
	// enrolled page
	public static final String ENROLLED_TABLE = "table#SSR_SS_ERD_ER\\$scroll\\$0";
	public static final String ENROLLED_CLASS_NAME = "span.PSLONGEDITBOX";
	public static final String ENROLLMENT_ERROR_MESSAGE = "td.PSLEVEL1GRIDODDROW div div";
	public static final String ENROLL_IMG_SUCCESS_ALT_TEXT = "Success";
}
