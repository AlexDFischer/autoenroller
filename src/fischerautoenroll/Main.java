package fischerautoenroll;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/*
 * NOTE: I needed to put this file in the project folder: https://github.com/mozilla/geckodriver/releases
 */
public class Main
{
	public static final String DRIVER_LOC = "/home/alex/src/Autoenroll/geckodriver";

	public static void main(String...args)
	{
		School school = School.UMASS; // just umass for now;
		
		// Texting.setUpSMS(); // put this on hold for now because email is shitty
		
		System.setProperty("webdriver.gecko.driver", DRIVER_LOC);
		WebDriver driver = new FirefoxDriver();
		driver.get(UmassWebPage.PAGE_URL);
		System.out.println(driver.getTitle());

		WebElement loginInput, passwordInput, submitButton;
		if (school == School.UMASS)
		{
			loginInput = driver.findElement(By.cssSelector(UmassWebPage.LOC_USERNAME_INPUT));
			passwordInput = driver.findElement(By.cssSelector(UmassWebPage.LOC_PASSWORD_INPUT));
			submitButton = driver.findElement(By.cssSelector(UmassWebPage.LOC_SUBMIT_BUTTON));
		} else //if (school == School.CORNELL)
		{
			loginInput = driver.findElement(By.cssSelector(CornellWebPage.LOC_USERNAME_INPUT));
			passwordInput = driver.findElement(By.cssSelector(CornellWebPage.LOC_PASSWORD_INPUT));
			submitButton = driver.findElement(By.cssSelector(CornellWebPage.LOC_SUBMIT_BUTTON));
		}
		
		// collect the username and password, in a console if possible
		// nvm console thing is dumb
		String username, password;
		/*
		java.io.Console in = System.console();
		if (in != null)
		{
			System.out.println("Username: ");
			username = in.readLine();
			System.out.println("Password: ");
			password = new String(in.readPassword()); // doesn't type the password on the screen as you type it
		} else
		{*/
			java.util.Scanner s = new java.util.Scanner(System.in);
			System.out.println("Username: ");
			username = s.nextLine();
			System.out.println("Password: ");
			password = s.nextLine();
		/*}*/
		
		// type in the appropriate usernames and passwords
		loginInput.sendKeys(username);
		passwordInput.sendKeys(password);
		System.out.println("Logging in.");
		submitButton.click();
		try
		{
			Thread.sleep(10000);
		} catch (InterruptedException e1)
		{
			// won't happen
			e1.printStackTrace();
		}
		
		if (school == School.UMASS)
		{
			// wait until the page is loaded
			System.out.println(((JavascriptExecutor) driver).executeScript("return document.readyState").toString());
			if (driver.getTitle().equals(UmassWebPage.EXPECTED_LOGIN_TITLE))
			{
				System.out.println("Logged in Successfully");
			} else
			{
				System.out.println("Error: could not log in successfully. New page title is \"" + driver.getTitle() + "\". Exiting with code 1.");
				System.exit(1);
			}
			
			// load the main iframe so that Selenium can find the enrollment button
			WebElement iframe = driver.findElement(By.tagName("iframe"));
			driver.get(iframe.getAttribute("src"));
			
			// go to the enroll screen
			WebElement searchForClassesButton = driver.findElement(By.cssSelector(UmassWebPage.ENROLLMENT_BUTTON));
			searchForClassesButton.click();
			try
			{
				Thread.sleep(5000);
			} catch (InterruptedException e)
			{
				// won't happen
				e.printStackTrace();
			}
			if (driver.getTitle().equals(UmassWebPage.EXPECTED_SHOPING_CART_TITLE))
			{
				System.out.println("Successfully loaded shopping cart");
			} else
			{
				System.out.println("Error: could not load shopping cart. New page title is \"" + driver.getTitle() + "\". Exiting with code 1.");
				System.exit(1);
			}
			
			// find all of the classes
			WebElement classesTable = driver.findElement(By.cssSelector(UmassWebPage.CLASSES_TABLE));
			List<WebElement> rows = classesTable.findElements(By.tagName("tr"));
			ArrayList<String> classes = new ArrayList<>();
			for (WebElement row : rows)
			{
				if (row.getAttribute("innerHTML").contains("<input")) // if it has a checkbox
				{
					WebElement classLink = row.findElement(By.cssSelector(UmassWebPage.CLASS_LINK));
					classes.add(classLink.getAttribute("innerHTML").split("<br>")[0]); // the name is before the <br>tag
				}
			}
			// print out the classes we found and select them
			System.out.println("Found " + classes.size() + " classes.");
			for (int i = 0; i < classes.size(); i++)
			{
				System.out.println(i + ": " + classes.get(i));
			}
			
			ArrayList<String> selectedClasses = new ArrayList<>();
			boolean hasClasses = false;
classesLoop:while (!hasClasses)
			{
				System.out.println("Type a number (or comma-separated numbers) to indicate the course(s) you wish to track:" );
				String line = s.nextLine();
				for (String num : line.split(","))
				{
					num = num.replaceAll(" ", "");
					int classNum;
					try
					{
						classNum = Integer.parseInt(num);
					} catch (NumberFormatException e)
					{
						System.out.println("Error: enter valid class number(s).");
						continue classesLoop; // I'm so sorry
					}
					if (classNum >= 0 && classNum < classes.size())
					{
						selectedClasses.add(classes.get(classNum));
					} else
					{
						System.out.println("Error: enter valid class number(s).");
						continue classesLoop; // I'm so sorry
					}
				}
				hasClasses = true;
			}
			System.out.println("Selected the following classes:");
			for (String className : selectedClasses)
			{
				System.out.println(className);
			}
			// now that we have the selected classes, refresh every 5 seconds
			while (true)
			{
				driver.get(driver.getCurrentUrl());
				try
				{
					Thread.sleep(5000);
				} catch (InterruptedException e)
				{
					// won't happen
					e.printStackTrace();
				}
				// check if any of the selected classes are open
				boolean anyClassesOpen = false;
				classesTable = driver.findElement(By.cssSelector(UmassWebPage.CLASSES_TABLE));
				rows = classesTable.findElements(By.tagName("tr"));
				for (WebElement row : rows)
				{
					String html = row.getAttribute("innerHTML");
					if (html.contains("<input")) // if it has a checkbox
					{
						// check if the row contains the name of any of the selected classes
						for (String className : selectedClasses)
						{
							if (html.contains(className))
							{
								// we've found a selected class, now see if it's open
								WebElement img = null;
								try
								{
									img = row.findElement(By.tagName("img"));
								} catch (NoSuchElementException e)
								{
									System.out.println("Error: unable to find open/closed image icon. Exiting with code 1.");
									System.exit(1);
								}
								if (img.getAttribute("alt").equals(UmassWebPage.OPEN_CLASS_ALT_TEXT))
								{
									// it's open! Let's select it
									System.out.println("Found open class: " + className);
									WebElement hiddenInput = null;
									try
									{
										hiddenInput = row.findElement(By.cssSelector(UmassWebPage.HIDDEN_INPUT));
									} catch (NoSuchElementException e)
									{
										System.out.println("Error: unable to find hidden input to select open class. Exiting with code 1.");
										System.exit(1);
									}
									JavascriptExecutor js = (JavascriptExecutor)driver;
									js.executeScript("document.getElementById(\"" + hiddenInput.getAttribute("id") + "\").setAttribute(\"value\", \"Y\");");
									anyClassesOpen = true;
								} else
								{
									// System.out.println(className + " is not open. Alt text is " + img.getAttribute("alt"));
								}
							}
						}
					}
				}
				if (anyClassesOpen)
				{
					// now go to the enroll page
					try
					{
						driver.findElement(By.cssSelector(UmassWebPage.ENROLL_BUTTON)).click();
						Thread.sleep(5000);
					} catch (InterruptedException e)
					{
						// won't happen
						e.printStackTrace();
					} catch (NoSuchElementException e)
					{
						System.out.println("Error: can't find enroll button. Exiting with code 1.");
						System.exit(1);
					}
					// now we are on finish enrolling page
					try
					{
						driver.findElement(By.cssSelector(UmassWebPage.FINISH_ENROLLING_BUTTON)).click();
						Thread.sleep(10000);
					} catch (NoSuchElementException e)
					{
						System.out.println("Error: could not find finish enrolling button. Exiting with code 1.");
					} catch (InterruptedException e)
					{
						// won't happen
						e.printStackTrace();
					}
					// find the icon that indicates success or failure
					try
					{
						WebElement statusTable = driver.findElement(By.cssSelector(UmassWebPage.ENROLLED_TABLE));
						List<WebElement> statusTableRows = statusTable.findElements(By.tagName("tr"));
						for (int i = 1; i < statusTableRows.size(); i++) // skip the zeroth row; it's just headers
						{
							// find class name & enrolled status
							String enrolledClassName = statusTableRows.get(i).findElement(By.cssSelector(UmassWebPage.ENROLLED_CLASS_NAME)).getAttribute("innerHTML").replace("&nbsp;", " ").replace("  ", " ");
							WebElement img = statusTableRows.get(i).findElement(By.tagName("img"));
							if (img.getAttribute("alt").equals(UmassWebPage.ENROLL_IMG_SUCCESS_ALT_TEXT))
							{
								// successfully enrolled!!!
								System.out.println("Successfully enrolled in a class: " + enrolledClassName);
								System.exit(0);
								// remove it from the list of classes we're trying to enroll in
								for (int j = 0; j < selectedClasses.size(); j++)
								{
									if (selectedClasses.get(j).contains(enrolledClassName))
									{
										System.out.println("Successfully enrolled in " + selectedClasses.get(j));
										selectedClasses.remove(j);
									}
								}
							} else
							{
								String errorMessage = statusTableRows.get(i).findElement(By.cssSelector(UmassWebPage.ENROLLMENT_ERROR_MESSAGE)).getAttribute("innerHTML");
								errorMessage = errorMessage.replace("<!-- Begin HTML Area Name Undisclosed -->", "");
								errorMessage = errorMessage.replace("<!-- End HTML Area -->", "");
								errorMessage = errorMessage.replace("<b>", "");
								errorMessage = errorMessage.replace("</b>", "");
								errorMessage = errorMessage.replace("\n", "");
								errorMessage = errorMessage.replace("\r", "");
								System.out.println("Could not enroll in " + enrolledClassName + ". Message: " + errorMessage);
							}
						}
					} catch (NoSuchElementException e)
					{
						System.out.println("Error: unable to determine whether enrolling was successful. Exiting with code 1.");
						//e.printStackTrace();
					}
					
				}
			}
			
		} else // if (school == School.Cornell)
		{
			if (driver.getTitle().equals(CornellWebPage.EXPECTED_LOGIN_TITLE))
			{
				System.out.println("Logged in Successfully");
			} else
			{
				System.out.println("Error: could not log in successfully. New page title is \"" + driver.getTitle() + "\". Exiting with code 1.");
				System.exit(1);
			}
		}
		s.close();
	}
}
