package br.com.luismatos.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
public class MyServletContextListener implements ServletContextListener {
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.getProperties().put("org.apache.el.parser.COERCE_TO_ZERO", "false");
	}
}