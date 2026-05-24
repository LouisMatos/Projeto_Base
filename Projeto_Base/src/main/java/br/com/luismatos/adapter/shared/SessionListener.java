package br.com.luismatos.adapter.shared;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class SessionListener implements HttpSessionListener {

    private static final Logger logger = Logger.getLogger(SessionListener.class.getName());

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        logger.info("Sessão criada: " + event.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        String ultimoAcesso = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            .format(new Date(event.getSession().getLastAccessedTime()));
        logger.info("Sessão encerrada: " + event.getSession().getId() + " — último acesso: " + ultimoAcesso);
    }
}
