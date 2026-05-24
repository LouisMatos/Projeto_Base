package br.com.luismatos.adapter.shared;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Listener de inicialização da aplicação.
 *
 * Define {@code org.apache.el.parser.COERCE_TO_ZERO=false} para evitar que
 * o parser de expressões EL converta automaticamente {@code null} para zero
 * em campos numéricos, preservando o comportamento esperado nos formulários JSF.
 */
public class ElParserContextListener implements ServletContextListener {

    private static final String PROPRIEDADE_EL_COERCE = "org.apache.el.parser.COERCE_TO_ZERO";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.getProperties().setProperty(PROPRIEDADE_EL_COERCE, "false");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {}
}
