package com.gramercysoftware.persistence.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.persistence.Persistence;

import org.apache.log4j.Logger;

/**
 * Default utility class for managing entity manager
 */
public class DefaultEntityManagerUtil extends EntityManagerUtil {
	private static Logger log = Logger.getLogger(DefaultEntityManagerUtil.class);
	private static EntityManagerUtil instance = new DefaultEntityManagerUtil();

	public static EntityManagerUtil getInstance() {
		return instance;
	}

	private DefaultEntityManagerUtil() {
		try {
			Properties properties = new Properties();
			InputStream resourceAsStream = DefaultEntityManagerUtil.class.getResourceAsStream("/persistence.properties");

			try {
				properties.load(resourceAsStream);
			} catch (IOException e) {
				log.error("unable to load persistence.properties file", e);
			}

			try {
				entityManagerFactory = Persistence.createEntityManagerFactory(properties.getProperty(PERSISTENCE_UNIT_KEY), properties);
			} catch (RuntimeException t) {
				log.fatal(t);
				throw t;
			}

			log.debug("DefaultEntityManagerUtil: created EntityManagerFactory successfully with properties: " + properties);
		} catch (Throwable x) {
			log.error("ERROR", x);
		}
	}
}
