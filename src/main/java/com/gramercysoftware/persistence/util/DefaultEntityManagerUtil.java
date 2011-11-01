/*
 * Copyright (C) 2011 David Harcombe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
