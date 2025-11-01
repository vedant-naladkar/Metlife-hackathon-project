package Elements;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;

public class AutomationExercise {
	public static Map<String , By> hm = new HashMap<>();
	public AutomationExercise() {
		hm.put("Username", By.xpath(""));
		hm.put("Email", By.xpath(""));
		hm.put("LoginOrLogout", By.xpath("//*[@id='header']/div/div/div/div[2]/div/ul/li[4]/a"));
	}
	public By getObject(String key) {
		return hm.get(key);
	}
}
