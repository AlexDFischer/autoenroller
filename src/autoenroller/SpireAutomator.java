package autoenroller;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;

/**
 * The runnable main class for the SPIRE auto-enroller.
 * Includes example of hardcoded class structure.
 */
public class SpireAutomator {
    public static void main(String[] args) {
        WebDriver driver = getWebDriver();
        ArrayList<Class> currentSchedule = new ArrayList<>();
        ArrayList<Class> shoppingCart = new ArrayList<>();
        ArrayList<Action> actions = new ArrayList<>();

        // Start current schedule.
        Lecture compsci311_01 = new Lecture("COMPSCI 311-01", "Introduction to Algorithms", "14784");
        Discussion compsci311_01aa = new Discussion("COMPSCI 311-01AA", "14785");
        compsci311_01.addEnrolledDiscussion(compsci311_01aa);
        Lecture compsci320_01 = new Lecture("COMPSCI 320-01", "Software Engineering", "14786");
        Discussion compsci320_01aa = new Discussion("COMPSCI 320-01AA", "14809");
        compsci320_01.addEnrolledDiscussion(compsci320_01aa);
        Lecture math235_02 = new Lecture("MATH 235-02", "Intro Linear Algebra", "15157");
        currentSchedule.addAll(Arrays.asList(compsci311_01, compsci320_01, math235_02));
        // End current schedule.

        // Start shopping cart.
        Discussion compsci311_01ab = new Discussion("COMPSCI 311-01AB", "20764");
        compsci311_01.addDiscussion(compsci311_01ab);
        Lecture compsci326_01 = new Lecture("COMPSCI 326-01", "Web Programming", "14842");
        Lecture compsci240_01 = new Lecture("COMPSCI 240-01", "Reasoning Under Uncertainty", "14812");
        Discussion compsci240_01aa = new Discussion("COMPSCI 240-01AA", "14836");
        Discussion compsci240_01ab = new Discussion("COMPSCI 240-01AB", "14841");
        Discussion compsci240_01ac = new Discussion("COMPSCI 240-01AC", "14851");
        Discussion compsci240_01ad = new Discussion("COMPSCI 240-01AD", "14864");
        compsci240_01.addDiscussions(compsci240_01aa, compsci240_01ab, compsci240_01ac, compsci240_01ad);
        Lecture math235_07 = new Lecture("MATH 235-07", "Intro Linear Algebra", "15184");
        shoppingCart.addAll(Arrays.asList(compsci326_01, compsci240_01, math235_07));
        // End shopping cart.

        // Start actions.
        actions.add(new Swap(math235_07, math235_02).addCondition(new Condition() {
            @Override
            public boolean isMet() {
                return math235_07.isOpen();
            }
            @Override
            public String toString() {
                return math235_07.getNameAndSection()+" is open";
            }
        }));
        actions.add(new Swap(compsci326_01, compsci320_01).addCondition(new Condition() {
            @Override
            public boolean isMet() {
                return compsci326_01.isOpen();
            }
            @Override
            public String toString() {
                return compsci326_01.getNameAndSection()+" is open";
            }
        }));
        actions.add(new Edit(compsci311_01, compsci311_01ab).addCondition(new Condition() {
            @Override
            public boolean isMet() {
                return compsci311_01ab.isOpen();
            }
            @Override
            public String toString() {
                return compsci311_01ab.getNameAndSection()+" is open";
            }
        }));
        actions.add(new Edit(compsci240_01, compsci240_01aa).addCondition(new Condition() {
            @Override
            public boolean isMet() {
                return currentSchedule.contains(compsci240_01) &&
                        !compsci240_01.getEnrolledDiscussion().equals(compsci240_01aa) && compsci240_01aa.isOpen();
            }
            @Override
            public String toString() {
                return "Enrolled in "+compsci240_01.getNameAndSection()+", not enrolled in "+
                        compsci240_01aa.getNameAndSection()+", and "+compsci240_01aa.getNameAndSection()+" is open";
            }
        }));
        // Examples of creating conditions as method references. Also possible to use statement/expression lambdas.
        //actions.add(new Swap(math235_07, math235_02).addCondition(math235_07::isOpen, math235_07.getNameAndSection()+" is open"));
        //actions.add(new Swap(compsci326_01, compsci320_01).addCondition(compsci326_01::isOpen, compsci326_01.getNameAndSection()+" is open"));
        //actions.add(new Edit(compsci311_01, compsci311_01ab).addCondition(compsci311_01ab::isOpen, compsci311_01ab.getNameAndSection()+" is open"));
        // End actions

        Spire spire = new Spire(driver, currentSchedule, shoppingCart, actions);
        spire.run();
    }

    private static WebDriver getWebDriver() {
        WebDriver driver = null;
        System.out.println("Web browser?\n1: Google Chrome\n2: Mozilla Firefox (Gecko driver)");
        do {
            int browser = new Scanner(System.in).nextInt();
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
            } else {
                System.out.println("Invalid entry.");
            }
        } while(driver == null);
        return driver;
    }
}
