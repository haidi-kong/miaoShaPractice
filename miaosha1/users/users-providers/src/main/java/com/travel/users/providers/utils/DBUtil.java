package com.travel.users.providers.utils;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBUtil {
	
	private static Properties props;
	
	static {
		try {
			YamlPropertiesFactoryBean yamlProFb = new YamlPropertiesFactoryBean();
			yamlProFb.setResources(new ClassPathResource("application-dev.yml"));
			props = yamlProFb.getObject();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConn() throws Exception{
		String url = props.getProperty("spring.datasource.url");
		String username = props.getProperty("spring.datasource.username");
		String password = props.getProperty("spring.datasource.password");
		String driver = props.getProperty("spring.datasource.driver-class-name");
		Class.forName(driver);
		return DriverManager.getConnection(url,username, password);
	}
}
