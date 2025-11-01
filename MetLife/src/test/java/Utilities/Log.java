package Utilities;	

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {

	private final Logger logger;

	// Private constructor
	private Log(Class<?> clazz) {
		this.logger = LogManager.getLogger(clazz);
	}

	// Factory method to get logger instance
	public static Log getLogger(Class<?> clazz) {
		return new Log(clazz);
	}

	public void info(String message) {
		logger.info(message);
	}

	public void debug(String message) {
		logger.debug(message);
	}

	public void warn(String message) {
		logger.warn(message);
	}

	public void error(String message) {
		logger.error(message);
	}

	public void error(String message, Throwable t) {
		logger.error(message, t);
	}
}
