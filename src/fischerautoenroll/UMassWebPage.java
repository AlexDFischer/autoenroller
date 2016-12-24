package fischerautoenroll;

public class UMassWebPage
{
	// login page
	public static final String PAGE_URL = "https://www.spire.umass.edu/psp/heproda/?cmd=login&languageCd=ENG";
	public static final String LOC_USERNAME_INPUT = "input#userid";
	public static final String LOC_PASSWORD_INPUT = "input#pwd";
	public static final String LOC_SUBMIT_BUTTON = "input[name=Submit]";
	public static final String EXPECTED_LOGIN_TITLE = "Student Center";

	// main page
	public static final String ENROLLMENT_BUTTON = "a#DERIVED_SSS_SCR_SSS_LINK_ANCHOR1";

	// select term page
	public static final String SELECT_TERM_HEADER = "//*[contains(text(), 'Select Term')]";
	public static final String SELECT_TERM_TABLE = "#SSR_DUMMY_RECV1\\24 scroll\\24 0";
	public static final String TERM_TABLE_ROW_NAME = "win0divTERM_CAR$";
	public static final String SELECT_TERM_RADIO = "PSRADIOBUTTON";
	public static final String SELECT_TERMS_ROW_CLASS = "PSLEVEL2GRIDROW";
	public static final String SEMESTER_CONTINUE = "DERIVED_SSS_SCT_SSR_PB_GO";

	// Select discussion page
    public static final String SELECT_DISC_TABLE = "#SSR_CLS_TBL_R1\\24 scroll\\24 0 > tbody > tr:nth-child(2) > td > table";
    public static final String SELECT_DISC_ROW_CLASS = "PSLEVEL1GRIDROW";
    public static final String SELECT_DISC_ALT_OPEN = "Open";
    public static final String SELECT_DISC_SECTION_NAME = "R1_SECTION$";
    public static final String SELECT_DISC_SECTION_TIME = "DERIVED_CLS_DTL_SSR_MTG_SCHED_LONG$190$$";
    public static final String SELECT_DISC_SECTION_NUMB = "PSEDITBOX_DISPONLY";
    public static final String SELECT_DISC_RADIO_BUTTON = "PSRADIOBUTTON";
    public static final String SELECT_DISC_NEXT_BUTTON = "SSSBUTTON_CONFIRMLINK";

    // Swap classes
	public static final String SWAP_BUTTON = "//*[@id=\"win0divDERIVED_SSTSNAV_SSTS_NAV_SUBTABS\"]/div/table/tbody/tr[2]/td[14]/a";
	public static final String SWAP_FROM_SCHEDULE = "#DERIVED_REGFRM1_DESCR50\\24 225\\24";
	public static final String SWAP_WITH_CART = "#DERIVED_REGFRM1_SSR_CLASSNAME_35\\24 183\\24";
	public static final String SWAP_SELECT_BUTTON = "#DERIVED_REGFRM1_SSR_PB_ADDTOLIST1\\24 184\\24";

	public static final String ENROLLED_CLASSES_TABLE = "#STDNT_ENRL_SSVW\\24 scroll\\24 0";
	public static final String ENROLLED_IMAGE = "/cs/heproda/cache/UM_PS_ENROLLED_ICN_1.gif";

	// shopping cart page
	public static final String EXPECTED_SHOPPING_CART_TITLE = "Add Classes";
	public static final String SHOPPING_CART_CLASSES_TABLE = "table#SSR_REGFORM_VW\\$scroll\\$0";
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
	public static final String ADD_ANOTHER_CLASS_BUTTON = "#DERIVED_REGFRM1_SSR_LINK_STARTOVER";
}
