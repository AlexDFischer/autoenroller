package autoenroller;

import org.openqa.selenium.WebDriver;

import java.util.ArrayList;

/**
 * The main controller for SPIRE activity throughout runtime.
 */
public class Spire {
    private WebDriver driver;
    private ArrayList<Class> currentSchedule;
    private ArrayList<Class> shoppingCart;
    private ArrayList<Action> actions;

    public Spire(WebDriver driver) {
        this.driver = driver;
    }

    public Spire(WebDriver driver, ArrayList<Action> actions) {
        this.driver = driver;
        this.actions = actions;
    }

    public Spire(WebDriver driver, ArrayList<Class> currentSchedule, ArrayList<Class> shoppingCart, ArrayList<Action> actions) {
        this.driver = driver;
        this.currentSchedule = currentSchedule;
        this.shoppingCart = shoppingCart;
        this.actions = actions;
    }

    public void run() {
        //TODO: Implement actual SPIRE execution based on fischerautoenroll.
        printClassStatus();
    }

    public void printClassStatus() {
        System.out.println("Current schedule:");
        for(Class c : currentSchedule.toArray(new Class[0])) {
            System.out.println(c.toString());
        }
        System.out.println();
        System.out.println("Shopping cart:");
        for(Class c : shoppingCart.toArray(new Class[0])) {
            System.out.println(c.toString());
        }
        System.out.println();
        System.out.println("Actions:");
        for(Action a : actions.toArray(new Action[0])) {
            System.out.println(a.toString());
        }
        System.out.println();
    }
}
