package br.com.luismatos.adapter.shared;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @deprecated Substituído por {@link ElParserContextListener}.
 *             Mantenha esta classe apenas enquanto existirem referências externas.
 *             O arquivo web.xml já foi atualizado para usar a nova classe.
 */
@Deprecated
public class MyServletContextListener implements ServletContextListener {

    private final ElParserContextListener delegate = new ElParserContextListener();

    @Override
    public void contextInitialized(ServletContextEvent event) {
        delegate.contextInitialized(event);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        delegate.contextDestroyed(event);
    }
}
