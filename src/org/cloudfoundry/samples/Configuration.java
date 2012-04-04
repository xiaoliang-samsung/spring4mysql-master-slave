package org.cloudfoundry.samples;

import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	static Properties props = new Properties();
	static String fileName = "system.properties";

	public static void init() {
		try {
			InputStream in = Configuration.class.getClassLoader()
					.getResourceAsStream(fileName);
			props.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getValue(String key) {
		return props.getProperty(key);
	}

}
