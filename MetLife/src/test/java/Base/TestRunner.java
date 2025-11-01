package Base;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.List;

import Utilities.ManagerDriver;
import Utilities.ConfigReader;
import Utilities.ExcelReader;

public class TestRunner {
	static {

	}

	public static void main(String[] args)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, MalformedURLException {
		try {
			List<String> testCases = ExcelReader.getInstance().getExecutableTestIDs();
			System.out.println(testCases);
			for (String tcID : testCases) {
				// Initialize driver
				ManagerDriver.initDriver();
				ManagerDriver.getDriver().get(ConfigReader.getProperty("URL"));

				// Read test metadata from Excel
				String className = ExcelReader.getInstance().getCellData("TestCases", tcID, "ClassName");
				String methodName = ExcelReader.getInstance().getCellData("TestCases", tcID, "MethodName");

				System.out.println("Started Running: " + tcID);

				// Step 1: Load the class dynamically
				Class<?> clazz = Class.forName("Tests." + className);

				// Step 2: Create an instance of that class
				Object obj = clazz.getDeclaredConstructor().newInstance();

				// Step 3: Get the method reference (with 1 String parameter)
				java.lang.reflect.Method method = clazz.getDeclaredMethod(methodName, String.class);

				// Step 4: Invoke the method with tcID as argument
				Object result = method.invoke(obj, tcID);

				// Step 5: Handle boolean return type
				if (result instanceof Boolean) {
					boolean success = (Boolean) result;
					System.out.println("Method returned boolean: " + success);

					if (success) {
						System.out.println("✅ Test passed!");
					} else {
						System.out.println("❌ Test failed!");
					}
				} else {
					System.out.println("Method result: " + result);
				}

			}
		} catch (Exception e) {

		} finally {
			tearDown();
		}
	}

	private static void tearDown() {
		ManagerDriver.getDriver().quit();

	}

}
