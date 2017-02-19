package autoenroller;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.*;

import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;

/**
 * The runnable main class for the SPIRE auto-enroller.
 *
 * Conditions for {@link Action}s should be carefully considered.
 * They should individually check for all cases and make
 * no assumptions based on other {@link Class}es or Actions.
 * For example, an {@link Edit} Action should check if the
 * {@link Lecture} to edit is in the current schedule.
 *
 * This program does not work when SPIRE asks to select
 * a semester. Functionality for this will be added during
 * a period of time when SPIRE asks to select a semester
 * so that it may be implemented and tested.
 *
 * This program is open-source and free to use by anyone.
 * It may be modified and distributed.
 * Use this program at your own risk. Any damages are
 * the responsibility of the user and not the author.
 * This program does not perform any action that
 * may not be manually performed by the user.
 */
public class SpireAutomator {
    public static void main(String[] args) {
        File propertiesFile = new File(".autoenroller.properties");
        Properties properties = new Properties();
        WebDriver driver;
        Map<String, Lecture> currentSchedule = new HashMap<>();
        Map<String, Lecture> shoppingCart = new HashMap<>();
        ArrayList<Action> actions = new ArrayList<>();

        if(propertiesFile.exists()) {
            try {
                properties.load(new FileInputStream(propertiesFile));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        driver = getWebDriver(properties, propertiesFile);

        Spire spire = new Spire(driver, currentSchedule, shoppingCart, actions);
        if(!properties.isEmpty()) {
            spire.setProperties(properties, propertiesFile);
        }
        spire.run();
    }

    protected static WebDriver getWebDriver(Properties properties, File propertiesFile) {
        WebDriver driver = null;
        int browserNum;
        if (properties.getProperty("browser") != null) {
            browserNum = Integer.valueOf(properties.getProperty("browser"));
        } else {
            do {
                System.out.println("Web browser?\n1: Google Chrome\n2: Mozilla Firefox");
                browserNum = new Scanner(System.in).nextInt();
                if (!(browserNum == 1 || browserNum == 2)) {
                    browserNum = -1;
                }
            } while (browserNum == -1);
            System.out.println("Save browser choice? (y/n)");
            if (new Scanner(System.in).nextLine().equals("y")) {
                properties.setProperty("browser", "" + browserNum);
                storeProperties(properties, propertiesFile);
            }
        }
        if (browserNum == 1) {
            if (IS_OS_WINDOWS) {
                System.setProperty("webdriver.chrome.driver", WebDriverExecutable.CHROME_WIN32);
            } else if (IS_OS_MAC) {
                System.setProperty("webdriver.chrome.driver", WebDriverExecutable.CHROME_MAC64);
            } else if (IS_OS_LINUX) {
                System.setProperty("webdriver.chrome.driver", WebDriverExecutable.CHROME_LINUX64);
            }
            driver = new ChromeDriver();
        } else if (browserNum == 2) {
            if (IS_OS_WINDOWS) {
                System.setProperty("webdriver.gecko.driver", WebDriverExecutable.FIREFOX_WIN64);
            } else if (IS_OS_MAC) {
                System.setProperty("webdriver.gecko.driver", WebDriverExecutable.FIREFOX_MACOS);
            } else if (IS_OS_LINUX) {
                System.setProperty("webdriver.gecko.driver", WebDriverExecutable.FIREFOX_LINUX64);
            }
            driver = new FirefoxDriver();
        }
        return driver;
    }

    private static void storeProperties(Properties properties, File propertiesFile) {
        if(!propertiesFile.exists()) {
            try {
                if(propertiesFile.createNewFile()) {
                    Files.setAttribute(propertiesFile.toPath(), "dos:hidden", true);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        try {
            properties.store(new FileOutputStream(propertiesFile), "");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
