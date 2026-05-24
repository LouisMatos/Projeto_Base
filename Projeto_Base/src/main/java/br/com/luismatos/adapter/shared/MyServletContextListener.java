package br.com.luismatos.adapter.shared;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.getProperties().put("org.apache.el.parser.COERCE_TO_ZERO", "false");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
