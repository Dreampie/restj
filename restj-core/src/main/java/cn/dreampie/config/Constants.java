package cn.dreampie.config;

import cn.dreampie.route.Const;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The constant for Restj runtime.
 */
final public class Constants {

	private boolean devMode = false;
	private String encoding = Const.DEFAULT_ENCODING;

	public boolean isDevMode() {
		return devMode;
	}

	public void setDevMode(boolean devMode) {
		this.devMode = devMode;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}







