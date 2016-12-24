package fischerautoenroll;

import java.util.*;

import autoenroller.Class;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;

public class Main
{
	// Higher sleepFactor = shorter sleep times between page loads. Default is 1.
	// Does not affect refresh for open classes rate, which is 5 seconds.
	private static final int SLEEP_FACTOR = 1;
	private static String semester = "";
	private static int semesterNumber = -1;
	private static ArrayList<Class> enrolledClasses = new ArrayList<>();
	private static ArrayList<Class> shoppingCartClasses = new ArrayList<>();
	private static Map<Class, Class> swapClassesWith = new HashMap<>();
	private static Map<Class, Class> classDiscussions = new HashMap<>();

	public static void main(String[] args)
	{
		WebDriver driver = null;
		Scanner s = new Scanner(System.in);
		System.out.println("Web browser?\n1: Google Chrome\n2: Mozilla Firefox (Gecko driver)");
		int browser = s.nextInt();
		if(browser == 1) {
			if(IS_OS_WINDOWS) {
				System.setProperty("webdriver.chrome.driver", WebDriverExecutable.CHROME_WIN32);
			} else if(IS_OS_MAC) {
				System.setProperty("webdriver.chrome.driver", WebDriverExecutable.CHROME_MAC64);
			} else if (IS_OS_LINUX) {
				System.setProperty("webdriver.chrome.driver", WebDriverExecutable.CHROME_LINUX64);
			}
			driver = new ChromeDriver();
		} else if(browser == 2) {
			if(IS_OS_WINDOWS) {
				System.setProperty("webdriver.gecko.driver", WebDriverExecutable.GECKO_WIN64);
			} else if(IS_OS_MAC) {
				System.setProperty("webdriver.gecko.driver", WebDriverExecutable.GECKO_MACOS);
			} else if (IS_OS_LINUX) {
				System.setProperty("webdriver.gecko.driver", WebDriverExecutable.GECKO_LINUX64);
			}
			driver = new FirefoxDriver();
		}
		if(driver == null) {
			System.out.println("WebDriver not initialized.");
			System.exit(1);
		}

		School school = School.UMASS; // just umass for now;

		// Texting.setUpSMS(); // put this on hold for now because email is shitty

        /*collect the username and password, in a console if possible
	    nvm console thing is dumb
	    java.io.Console in = System.console();
	    if (in != null)
		{
			System.out.println("Username: ");
			username = in.readLine();
			System.out.println("Password: ");
			password = new String(in.readPassword()); // doesn't type the password on the screen as you type it
		} else
		{
		}*/
		String username, password;
		s = new Scanner(System.in);
		System.out.println("Username: ");
		username = s.nextLine();
		System.out.println("Password: ");
		password = s.nextLine();

		switch(school) {
			case UMASS:
				umassEnroller(driver, username, password);
				break;
			case CORNELL:
				cornellEnroller(driver, username, password);
				break;
			default:
				break;
		}
		s.close();
	}

	private static void umassEnroller(WebDriver driver, String username, String password) {
		Scanner s = new Scanner(System.in);
		driver.get(UMassWebPage.PAGE_URL);

		WebElement loginInput = driver.findElement(By.cssSelector(UMassWebPage.LOC_USERNAME_INPUT));
		WebElement passwordInput = driver.findElement(By.cssSelector(UMassWebPage.LOC_PASSWORD_INPUT));
		WebElement submitButton = driver.findElement(By.cssSelector(UMassWebPage.LOC_SUBMIT_BUTTON));

		// type in the appropriate usernames and passwords
		loginInput.sendKeys(username);
		passwordInput.sendKeys(password);
		//System.out.print("Logging in... ");
		submitButton.click();
		sleep(5000);

		// wait until the page is loaded
//		if (driver.getTitle().equals(UMassWebPage.EXPECTED_LOGIN_TITLE)) {
//			System.out.println("logged in successfully.");
//		} else {
//			System.out.println("Error: could not log in successfully. New page title is \"" + driver.getTitle() + "\". Exiting with code 1.");
//			System.exit(1);
//		}

		// load the main iframe so that Selenium can find the enrollment button
		WebElement iframe = driver.findElement(By.tagName("iframe"));
		driver.get(iframe.getAttribute("src"));

		// go to the enroll screen
		WebElement searchForClassesButton = driver.findElement(By.cssSelector(UMassWebPage.ENROLLMENT_BUTTON));
		searchForClassesButton.click();
		sleep(1000);

		// Checks if current page asks to select term
		if(driver.findElements(By.xpath(UMassWebPage.SELECT_TERM_HEADER)).size() > 0) {
			selectSemester(driver, s);
		}
//		if (driver.getTitle().equals(UMassWebPage.EXPECTED_SHOPPING_CART_TITLE)) {
//			System.out.println("Successfully loaded shopping cart");
//		} else {
//			System.out.println("Error: could not load shopping cart. New page title is \"" + driver.getTitle() + "\". Exiting with code 1.");
//			System.exit(1);
//		}

		// Find all of the enrolled classes
		List<WebElement> enrolledRows = driver.findElement(By.cssSelector(UMassWebPage.ENROLLED_CLASSES_TABLE)).findElements(By.tagName("tr"));
		for(WebElement row : enrolledRows) {
			// Finds enrolled classes if image is enrolled icon, and if name is a hyperlink or not (discussions don't have hyperlinks).
			if(row.getAttribute("innerHTML").contains(UMassWebPage.ENROLLED_IMAGE) && row.findElements(By.cssSelector(UMassWebPage.CLASS_LINK)).size() > 0) {
				//enrolledClasses.add(getClassFromLink(row));
			}
		}
		System.out.println("Found " + enrolledClasses.size() + " enrolled classes.");
		for (int i = 0; i < enrolledClasses.size(); i++) {
			System.out.println(enrolledClasses.get(i).getName());
		}

		// Find all of the shopping cart classes
		List<WebElement> shoppingCartRows = driver.findElement(By.cssSelector(UMassWebPage.SHOPPING_CART_CLASSES_TABLE)).findElements(By.tagName("tr"));
		for (WebElement row : shoppingCartRows) {
			// Finds enrollable shopping cart classes if they have a checkbox.
			if (row.getAttribute("innerHTML").contains("<input")) {
				//shoppingCartClasses.add(getClassFromLink(row));
			}
		}
		// Print out the classes we found and select them
		System.out.println("Found " + shoppingCartClasses.size() + " shopping cart classes.");
		for (int i = 0; i < shoppingCartClasses.size(); i++) {
			System.out.println(i + ": " + shoppingCartClasses.get(i).getName());
		}

		boolean hasClasses = false;
		classesLoop:while (!hasClasses) {
			System.out.println("Type a number (or comma-separated numbers) to indicate the course(s) you wish to track:" );
			s = new Scanner(System.in);
			String line = s.nextLine();
			for (String num : line.split(",")) {
				num = num.replaceAll(" ", "");
				int classNum;
				try {
					classNum = Integer.parseInt(num);
				} catch (NumberFormatException e) {
					System.out.println("Error: enter valid class number(s).");
					continue classesLoop;
				}
				if (classNum < 0 && classNum > shoppingCartClasses.size()) {
					System.out.println("Error: enter valid class number(s).");
					continue classesLoop;
				} else {
					Class className = shoppingCartClasses.get(classNum);
					System.out.println("When available, just enroll in "+className.getName()+" or swap with a currently enrolled class?");
					System.out.println("0: Just enroll in the class.");
					System.out.println("1: Swap this class with a currently enrolled class.");
					int enrollOrSwap = s.nextInt();
					if(enrollOrSwap == 1) {
						System.out.println("What class do you want to swap out with "+className.getName()+"?");
						for(int i = 0; i < enrolledClasses.size(); i++) {
							System.out.println(i+": "+enrolledClasses.get(i).getName());
						}
						int i = s.nextInt();
						swapClassesWith.put(className, enrolledClasses.get(i));
					} else {
						swapClassesWith.put(className, null);
					}
				}
			}
			hasClasses = true;
		}

		printQueuedActions();
		// Now that we have the selected classes, refresh every 5 seconds
		while(!swapClassesWith.keySet().isEmpty()) {
			driver.get(driver.getCurrentUrl());
			sleep(5000*SLEEP_FACTOR);
			WebElement classesTable = driver.findElement(By.cssSelector(UMassWebPage.SHOPPING_CART_CLASSES_TABLE));
			List<WebElement> rows = classesTable.findElements(By.tagName("tr"));
			for(WebElement row : rows) {
				boolean openFound = false;
				String html = row.getAttribute("innerHTML");
				if(html.contains("<input")) {
					for(Class className : swapClassesWith.keySet()) {
						if(html.contains(className.getName())) {
							WebElement img = null;
							try {
								img = row.findElement(By.tagName("img"));
							} catch(NoSuchElementException e) {
								System.out.println("Error: unable to find open/closed image icon. Exiting with code 1.");
								System.exit(1);
							}
							if(img.getAttribute("alt").equals(UMassWebPage.OPEN_CLASS_ALT_TEXT)) {
								// It's open!
								System.out.println("Found open class: " + className.getName());
								openFound = true;
								if(swapClassesWith.get(className) == null) {
									enrollClass(driver, row);
								} else {
									swapClass(driver, s, className);
								}
								// Removes class from watchlist even if enrollment failed.
								// Assuming if failed once then will fail every time.
								swapClassesWith.remove(className);
								printQueuedActions();
							} else {
								// System.out.println(className + " is not open. Alt text is " + img.getAttribute("alt"));
							}
						}
					}
					if(openFound) {
						break;
					}
				}
			}
		}
		System.out.println("All selected classes enrolled/swapped in.");
		driver.close();
	}

	private static void cornellEnroller(WebDriver driver, String username, String password) {
		WebElement loginInput = driver.findElement(By.cssSelector(CornellWebPage.LOC_USERNAME_INPUT));
		WebElement passwordInput = driver.findElement(By.cssSelector(CornellWebPage.LOC_PASSWORD_INPUT));
		WebElement submitButton = driver.findElement(By.cssSelector(CornellWebPage.LOC_SUBMIT_BUTTON));

		// type in the appropriate usernames and passwords
		loginInput.sendKeys(username);
		passwordInput.sendKeys(password);
		System.out.println("Logging in.");
		submitButton.click();
		sleep(10000);

		if (driver.getTitle().equals(CornellWebPage.EXPECTED_LOGIN_TITLE))
		{
			System.out.println("Logged in Successfully");
		} else
		{
			System.out.println("Error: could not log in successfully. New page title is \"" + driver.getTitle() + "\". Exiting with code 1.");
			System.exit(1);
		}
	}

	private static void selectSemester(WebDriver driver, Scanner s) {
		// Makes a list of all the rows in the terms table, including label rows.
		List<WebElement> selectTermsTable = driver.findElement(By.cssSelector(UMassWebPage.SELECT_TERM_TABLE)).findElements(By.tagName("tr"));
		ArrayList<WebElement> terms = new ArrayList<>();
		for(WebElement selectTermsRow : selectTermsTable) {
			// Checks if this row in the terms table actually displays a semester, and not just labels.
			if(selectTermsRow.findElements(By.className(UMassWebPage.SELECT_TERMS_ROW_CLASS)).size() > 0) {
				terms.add(selectTermsRow);
			}
		}
		System.out.println("Found " + terms.size() + " terms.");
		for (int i = 0; i < terms.size(); i++)
		{
			System.out.println(i + ": " + terms.get(i).findElement(By.id(UMassWebPage.TERM_TABLE_ROW_NAME+i)).getText());
		}
		// Save semester so as to only ask the user for input once. Assumes user wants to work on one semester throughout.
		if(semester.equals("")) {
			System.out.println("Type a number to indicate the semester you wish to track:");
			semesterNumber = s.nextInt();
			semester = terms.get(semesterNumber).findElement(By.id(UMassWebPage.TERM_TABLE_ROW_NAME+semesterNumber)).getText();
		}
		System.out.println("Selected term is "+semester);
		terms.get(semesterNumber).findElement(By.className(UMassWebPage.SELECT_TERM_RADIO)).click();
		driver.findElement(By.id(UMassWebPage.SEMESTER_CONTINUE)).click();
		sleep(1000);
	}

    private static void selectDiscussion(WebDriver driver) {
        ArrayList<WebElement> discs = new ArrayList<>();
        // Makes a list of all the rows in the discs table, including label rows, and iterate over the list.
        for(WebElement selectDiscRow : driver.findElement(By.cssSelector(UMassWebPage.SELECT_DISC_TABLE)).findElements(By.tagName("tr"))) {
            // Checks if this row in the discussions table actually displays a discussion section, and not just labels.
            // Also checks if the discussion section is open. Does not add to list if it is not open.
            if(selectDiscRow.findElements(By.className(UMassWebPage.SELECT_DISC_ROW_CLASS)).size() > 0 &&
                    selectDiscRow.findElement(By.tagName("img")).getAttribute("alt").equals(UMassWebPage.SELECT_DISC_ALT_OPEN)) {
                discs.add(selectDiscRow);
            }
        }
        System.out.println("Found " + discs.size() + " open discussion section(s).");
        for (int i = 0; i < discs.size(); i++) {
            System.out.println(i + ": " + discs.get(i).findElement(By.id(UMassWebPage.SELECT_DISC_SECTION_NAME+i)).getText()+" "+discs.get(i).findElement(By.id(UMassWebPage.SELECT_DISC_SECTION_TIME+i)).getText());
        }
		discs.get(discs.size()).findElement(By.className(UMassWebPage.SELECT_DISC_RADIO_BUTTON)).click();
        driver.findElement(By.className(UMassWebPage.SELECT_DISC_NEXT_BUTTON));
        sleep(5000);
    }

	private static boolean enrollClass(WebDriver driver, WebElement row) {
		boolean enrollSuccess = false;
		WebElement hiddenInput = null;
		try {
			hiddenInput = row.findElement(By.cssSelector(UMassWebPage.HIDDEN_INPUT));
		} catch (NoSuchElementException e) {
			System.out.println("Error: unable to find hidden input to select open class. Exiting with code 1.");
			System.exit(1);
		}
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("document.getElementById(\"" + hiddenInput.getAttribute("id") + "\").setAttribute(\"value\", \"Y\");");
		// now go to the enroll page
		try {
			driver.findElement(By.cssSelector(UMassWebPage.ENROLL_BUTTON)).click();
		} catch (NoSuchElementException e) {
			System.out.println("Error: can't find enroll button. Exiting with code 1.");
			System.exit(1);
		}
		sleep(1000);
		// now we are on finish enrolling page
		try {
			driver.findElement(By.cssSelector(UMassWebPage.FINISH_ENROLLING_BUTTON)).click();
		} catch (NoSuchElementException e) {
			System.out.println("Error: could not find finish enrolling button. Exiting with code 1.");
			System.exit(1);
		}
		sleep(10000);
		// Find the icon that indicates success or failure.
		try {
			WebElement statusTable = driver.findElement(By.cssSelector(UMassWebPage.ENROLLED_TABLE));
			List<WebElement> statusTableRows = statusTable.findElements(By.tagName("tr"));
			for (int i = 1; i < statusTableRows.size(); i++) { // Zeroth row is just headers.
				// Find class name & enrollment status
				String enrolledClassName = statusTableRows.get(i).findElement(By.cssSelector(UMassWebPage.ENROLLED_CLASS_NAME)).getAttribute("innerHTML").replace("&nbsp;", " ").replace("  ", " ");
				if (statusTableRows.get(i).findElement(By.tagName("img")).getAttribute("alt").equals(UMassWebPage.ENROLL_IMG_SUCCESS_ALT_TEXT)) {
					System.out.println("Successfully enrolled in a class: " + enrolledClassName);
					enrollSuccess = true;
				} else {
					String errorMessage = statusTableRows.get(i).findElement(By.cssSelector(UMassWebPage.ENROLLMENT_ERROR_MESSAGE)).getAttribute("innerHTML");
					errorMessage = errorMessage.replace("<!-- Begin HTML Area Name Undisclosed -->", "");
					errorMessage = errorMessage.replace("<!-- End HTML Area -->", "");
					errorMessage = errorMessage.replace("<b>", "");
					errorMessage = errorMessage.replace("</b>", "");
					errorMessage = errorMessage.replace("\n", "");
					errorMessage = errorMessage.replace("\r", "");
					System.out.println("Could not enroll in " + enrolledClassName + ". Message: " + errorMessage);
				}
			}
		} catch (NoSuchElementException e) {
			System.out.println("Error: unable to determine whether enrolling was successful. Exiting with code 1.");
			System.exit(1);
		}
		driver.findElement(By.cssSelector(UMassWebPage.ADD_ANOTHER_CLASS_BUTTON)).click();
		sleep(1000);
		return enrollSuccess;
	}

	private static boolean swapClass(WebDriver driver, Scanner s, Class toClass) {
		//Go to SWAP tab.
		driver.findElement(By.xpath(UMassWebPage.SWAP_BUTTON)).click();
		sleep(1000);
		// Checks if current page asks to select term
		if(driver.findElements(By.xpath(UMassWebPage.SELECT_TERM_HEADER)).size() > 0) {
			selectSemester(driver, s);
		}
		// Select from-class and to-class in dropdown menus by class ID values, then click Select button.
		new Select(driver.findElement(By.cssSelector(UMassWebPage.SWAP_FROM_SCHEDULE))).selectByValue(swapClassesWith.get(toClass).getClassId());
		new Select(driver.findElement(By.cssSelector(UMassWebPage.SWAP_WITH_CART))).selectByValue(toClass.getClassId());
		driver.findElement(By.cssSelector(UMassWebPage.SWAP_SELECT_BUTTON)).click();

		return false;
	}

//	private static Class getClassFromLink(WebElement row) {
//		WebElement classLink = row.findElement(By.cssSelector(UMassWebPage.CLASS_LINK));
//		String[] split = classLink.getAttribute("innerHTML").split("<br>"); // the name is before the <br>tag
//		Class newClass = new Class();
//		newClass.setName(split[0]);
//		newClass.setClassId(split[1].replace("(", "").replace(")", "").replace("\n", "").replace(" ", ""));
//		return newClass;
//	}

	private static void printQueuedActions() {
		System.out.println("Waiting to perform the following actions:");
		for(Class enrollClass : swapClassesWith.keySet()) {
			if(swapClassesWith.get(enrollClass) == null) {
				System.out.println("    Enroll in "+enrollClass.getName());
			} else {
				System.out.println("    Swap "+enrollClass.getName()+" with "+swapClassesWith.get(enrollClass).getName());
			}
		}
	}

	private static void sleep(int millis) {
		try{
			Thread.sleep(millis/SLEEP_FACTOR);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}
