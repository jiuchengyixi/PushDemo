package com.common.android.utils;

import android.util.Log;

import org.apache.log4j.Level;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Log utilities that can control whether print the log.
 */
public class Logger {
	private static int LOG_LEVEL = 6;
	
	private static final int VERBOSE = 5;
	private static final int DEBUG = 4;
	private static final int INFO = 3;
	private static final int WARN = 2;
	private static final int ERROR = 1;

	private static boolean SAVE_ALL_LOG = false;
	private static String TAG = "Logger";

	/**
	 * 初始化Logger，请在Application onCreate()时初始化
	 * */
	public static void initLogger(File logDir) {
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		LogConfigurator logConfigurator = new LogConfigurator();
		logConfigurator.setFileName(logDir.getAbsolutePath() + File.separator
				+ "system.log");
		logConfigurator.setRootLevel(Level.DEBUG);
		logConfigurator.setLevel("org.apache", Level.ERROR);
		logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
		logConfigurator.setMaxFileSize(1024 * 1024 * 5);
		logConfigurator.setImmediateFlush(true);
		logConfigurator.configure();
	}

	public static void setTAG(String TAG) {
		Logger.TAG = TAG;
	}

	public static void setDebug (boolean debug) {
		LOG_LEVEL = debug ? 6 : 1;
	}

	 public static void saveAllLog (boolean save) {
		SAVE_ALL_LOG = save;
	}
	
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger("SytemLog");

	public static void v(String tag, String msg) {
		if (LOG_LEVEL > VERBOSE) {
			if (SAVE_ALL_LOG) {
				log.debug(tag + ", [" + getFileLineMethod()+ "]" + msg);
			} else {
				Log.v(tag, "-->>[" + getFileLineMethod()+ "]" + msg);
			}
		}

	}

	public static void d(String tag, String msg) {
		if (LOG_LEVEL > DEBUG) {
			if (SAVE_ALL_LOG) {
				log.debug(tag + ", [" + getFileLineMethod()+ "]" + msg);
			} else {
				Log.d(tag, "-->>[" + getFileLineMethod()+ "]" + msg);
			}
		}
	}

	public static void i(String tag, String msg) {
		if (LOG_LEVEL > INFO) {
			if (SAVE_ALL_LOG) {
				log.info(tag + ", [" + getFileLineMethod()+ "]" + msg);
			} else {
				Log.i(tag, "-->>[" + getFileLineMethod()+ "]" + msg);
			}
		}
	}

	public static void w(String tag, String msg) {
		if (LOG_LEVEL > WARN) {
			if (SAVE_ALL_LOG) {
				log.warn(tag + ", [" + getFileLineMethod()+ "]" + msg);
			} else {
				Log.w(tag, "-->>[" + getFileLineMethod()+ "]" + msg);
			}
		}
	}

	public static void e(String tag, String msg) {
		if (LOG_LEVEL > ERROR) {
			if (SAVE_ALL_LOG) {
				log.error(tag + ", [" + getFileLineMethod()+ "]" + msg);
			} else {
				Log.e(tag, "-->>[" + getFileLineMethod()+ "]" + msg);
			}
		}
	}

	public static void d(Object msg) {
		if (LOG_LEVEL > DEBUG) {
			if (SAVE_ALL_LOG) {
				log.debug(TAG + ", [" + getFileLineMethod()+ "]" + msg);
			} else {
				Log.d(TAG, "-->>[" + getFileLineMethod()+ "]" + msg);
			}
		}
	}

	public static void i(Object msg) {
		if (LOG_LEVEL > DEBUG) {
			if (SAVE_ALL_LOG) {
				log.info(TAG + ", [" + getFileLineMethod()+ "]" + msg);
			} else {
				Log.i(TAG, "-->>[" + getFileLineMethod()+ "]" + msg);
			}
		}
	}

	public static void e(Object msg) {
		if (LOG_LEVEL > ERROR) {
			if (SAVE_ALL_LOG) {
				log.error(TAG + ", [" + getFileLineMethod()+ "]" + msg);
			} else {
				Log.e(TAG, "-->>[" + getFileLineMethod()+ "]" + msg);
			}
		}
	}
	public static void w(Object msg) {
		if (LOG_LEVEL > WARN) {
			if (SAVE_ALL_LOG) {
				log.warn(TAG + ", [" + getFileLineMethod()+ "]" + msg);
			} else {
				Log.w(TAG, "-->>[" + getFileLineMethod()+ "]" + msg);
			}
		}
	}

	public static void printStackTrace () {
		try {
			throw new Exception("printStackTrace");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getFileLineMethod() {
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[2];
		StringBuffer toStringBuffer = new StringBuffer("[").append(traceElement.getFileName()).append(" | ").append(traceElement.getLineNumber()).append(" | ").append(traceElement.getMethodName()).append("]");
		return toStringBuffer.toString();
	}
}
