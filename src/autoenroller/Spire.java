package autoenroller;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * The main controller for SPIRE activity throughout runtime.
 * Though the fischerautoenroll package assumes future support
 * for other schools, it cannot be assumed that their class
 * structure is the same as UMass' structure. Other schools
 * also do not use SPIRE. This package is therefore geared
 * toward SPIRE at UMass exclusively.
 */
public class Spire {
    private WebDriver driver;
    private ArrayList<Class> currentSchedule;
    private ArrayList<Class> shoppingCart;
    private ArrayList<Action> actions;

    public Spire(WebDriver driver) {
        this.driver = driver;
        this.currentSchedule = new ArrayList<>();
        this.shoppingCart = new ArrayList<>();
        this.actions = new ArrayList<>();
    }

    public Spire(WebDriver driver, ArrayList<Action> actions) {
        this(driver);
        this.actions = actions;
    }

    public Spire(WebDriver driver, ArrayList<Class> currentSchedule, ArrayList<Class> shoppingCart, ArrayList<Action> actions) {
        this(driver, actions);
        this.currentSchedule = currentSchedule;
        this.shoppingCart = shoppingCart;
    }

    public void run() {
        //TODO: Implement actual SPIRE execution based on fischerautoenroll.
        spireLogon();

        // SPIRE is normally shown as a webpage within a webpage.
        // The subwebpage's code is hard to access while it is nested.
        // This line explicitly waits until the internal frame is present
        // and then loads it into the driver as the main webpage.
        driver.get(waitForElement(By.tagName("iframe")).getAttribute("src"));
        // The load time for the iframe is negligible, so no wait is needed.
        driver.findElement(By.id(UMass.STUDENT_ENROLLMENT_ID)).click();

        // We are now in the shopping cart. If the current schedule,
        // shopping cart, and actions are not pre-hardcoded,
        // this page should be parsed and create Classes and Actions.
        if(currentSchedule.isEmpty()) {
            // Parse current schedule, create Classes.
        }
        if(shoppingCart.isEmpty()) {
            // Parse shopping cart, create Classes.
        }
        if(actions.isEmpty()) {
            // Create Actions based on current schedule and shopping cart.
        }

        printAllLists();
        System.out.println("Beginning automated check every 5 seconds.");
        while(!actions.isEmpty()) {
            // Reload current shopping cart page every 5 seconds.
            driver.get(driver.getCurrentUrl());
            sleep(5000);
            // For each Action, check Conditions, meet if able, perform if able.
            for(Action action : actions) {
                if(action.allConditionsMet()) {
                    System.out.println("All conditions meet for action:\n"+action.toString());
                    if(action.perform()) {
                        System.out.println("Successfully performed action:\n"+action.toString());
                        actions.remove(action);printActions();
                    } else {
                        System.out.println("Failed to perform action:\n"+action.toString());
                    }
                }
            }
        }
        System.out.println("All actions performed.");
        printCurrentSchedule();
    }

    private String getUsername() {
        System.out.println("Username?");
        return new Scanner(System.in).nextLine();
    }

    //TODO: Find a more secure way to get the password.
    private String getPassword() {
        System.out.println("Password?");
        return new Scanner(System.in).nextLine();
    }

    public void printCurrentSchedule() {
        System.out.println("Current schedule:");
        for(Class c : currentSchedule.toArray(new Class[0])) {
            System.out.println(c.toString());
        }
        System.out.println();
    }

    public void printShoppingCart() {
        System.out.println("Shopping cart:");
        for(Class c : shoppingCart.toArray(new Class[0])) {
            System.out.println(c.toString());
        }
        System.out.println();
    }

    public void printActions() {
        System.out.println("Actions:");
        for(Action a : actions.toArray(new Action[0])) {
            System.out.println(a.toString());
        }
        System.out.println();
    }

    public void printAllLists() {
        printCurrentSchedule();
        printShoppingCart();
        printActions();
    }

    private WebElement waitForElement(By by) {
        return (new WebDriverWait(driver, 10, 200)).until(ExpectedConditions.presenceOfElementLocated(by));
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void spireLogon() {
        driver.get(UMass.LOGIN_URL);
        do {
            // Explicitly waits for the Username field to load and types username.
            waitForElement(By.id(UMass.USERNAME_ID)).sendKeys(getUsername());
            // Presence of Username means Password and Go button are loaded too.
            driver.findElement(By.id(UMass.PASSWORD_ID)).sendKeys(getPassword());
            driver.findElement(By.cssSelector(UMass.LOGIN_BUTTON_SELECTOR)).click();
            //TODO: Need a less immediate way to check for logon failure.
        } while(driver.getTitle().equals("SPIRE Logon"));
        // The page will be "SPIRE Logon" as long as the user is not logged in.
    }
}
