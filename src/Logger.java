
public class Logger {
	Class type = null;
	
	public Logger (Class type) {
		this.type = type;
	}
	
	public void info(String message) {
		System.out.println("[INFO] (" + type.getCanonicalName() + "): " + message);
	}
	
	public void err(String message) {
		System.out.println("[ERROR] (" + type.getCanonicalName() + "): " + message);
	}
	
	public void info(int message) {
		System.out.println("[INFO] (" + type.getCanonicalName() + "): " + message);
	}
	
	public void err(int message) {
		System.out.println("[ERROR] (" + type.getCanonicalName() + "): " + message);
	}
}
