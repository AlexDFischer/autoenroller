## SPIRE Enrollment Automator

This program automates different actions on the UMass SPIRE enrollment system. 
It allows the user to add, drop, swap, and edit classes under customizable conditions.
The program refreshes SPIRE at least every five seconds and continuously checks the
conditions of each action. When all of the conditions of an action are met,
the action attempts to perform. The program concludes when all actions have been
successfully performed.

To use this program, the user must manually hardcode conditions and actions,
as well as the classes associated with them, and then build and run the program.
Actions and conditions should be carefully considered to work under any situation.
The user should consider potential class conflicts and outstanding prerequisites.
Examples are provided in the main class, `autoenroller.SpireAutomator`.
Actions may be created during runtime but will not have any conditions,
and will attempt to perform on every refresh cycle until successful.

This program also parses the user's current schedule and shopping cart from the SPIRE webpage.
It reparses the current schedule and shopping cart after an action has successfully performed.
Be aware that if a class has been hardcoded but is not in the SPIRE current schedule
or shopping cart, it may be lost during reparsing, which may cause errors.

This program requires the Selenium WebDriver to build and run. It is available to download from
the Maven Repository  at `org.seleniumhq.selenium:selenium-java:3.0.1` or online at the 
[Maven Repository website](https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-java/3.0.1)
or [Selenium website](http://www.seleniumhq.org/).

This program is open-source and free to use by anyone.
It may be viewed, modified, and distributed.
**Use this program at your own risk.**
Any damages (such as lost class seats) are the responsibility of the user and _not_ the author.
This program does not perform any action that the user would not normally be able to perform manually.