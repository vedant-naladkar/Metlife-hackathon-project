package Tests;

import org.openqa.selenium.By;

import Elements.AutomationExercise;
import Utilities.ExcelReader;
import Utilities.ManagerDriver;

public class VerifyLoginandRegistration {
	String sheetName = "Login";
	AutomationExercise locators = new AutomationExercise();

	public boolean verifyLogin(String tcID) {
		boolean flag = false;
		// Step 1: Click on Signup / Login
		ManagerDriver.getDriver().findElement(locators.getObject("LoginOrLogout")).click();
		String tDID = ExcelReader.getInstance().getCellData("TestCases", tcID, "Login");
		// Step 2: Enter name and existing email
		ManagerDriver.getDriver().findElement(By.name("name"))
				.sendKeys(ExcelReader.getInstance().getCellData(sheetName, tDID, "Name"));
		ManagerDriver.getDriver().findElement(By.xpath("//*[@id=\"form\"]/div/div/div[3]/div/form/input[3]"))
				.sendKeys(ExcelReader.getInstance().getCellData(sheetName, tDID, "Email"));
		ManagerDriver.getDriver().findElement(By.xpath("//*[@id='form']/div/div/div[3]/div/form/button")).click();

		return flag;
	}
}
