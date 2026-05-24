package br.com.luismatos.adapter.filter;

import br.com.luismatos.adapter.shared.SessionContext;
import br.com.luismatos.domain.model.Usuario;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtro de autenticação: protege todas as URLs sob /aplicacao/*.
 *
 * Comportamento por tipo de requisição quando a sessão não está autenticada:
 *  - Requisição normal  → redireciona para a página de login (HTTP 302)
 *  - Requisição AJAX    → devolve uma resposta JSF partial-response com <redirect>,
 *                         que o runtime JSF/PrimeFaces processa e redireciona o browser
 *                         sem quebrar o ciclo de vida AJAX no cliente.
 */
public class LoginFilter implements Filter {

    private static final String HEADER_FACES_REQUEST        = "Faces-Request";
    private static final String VALOR_AJAX_PARCIAL          = "partial/ajax";
    private static final String PARAMETRO_JSF_AJAX          = "javax.faces.partial.ajax";
    private static final String CHAVE_PAGINA_ATUAL          = "currentPage";
    private static final String CHAVE_PAGINA_ANTERIOR       = "lastPage";

    @Override
    public void init(FilterConfig config) throws ServletException {}

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession         session      = httpRequest.getSession(false);

        String  uri           = httpRequest.getRequestURI();
        Usuario usuarioLogado = (session != null)
            ? (Usuario) session.getAttribute(SessionContext.CHAVE_USUARIO)
            : null;

        if (uri.contains("/aplicacao/") && usuarioLogado == null) {
            String loginUrl = httpRequest.getContextPath() + "/index.xhtml";

            if (isRequisicaoAjax(httpRequest)) {
                enviarRedirectAjax(httpResponse, loginUrl);
            } else {
                httpResponse.sendRedirect(loginUrl);
            }
            return;
        }

        rastrearNavegacao(httpRequest);
        request.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Detecta se a requisição é um AJAX parcial do JSF/PrimeFaces.
     * JSF define o header "Faces-Request: partial/ajax" em todas as requisições AJAX.
     */
    private boolean isRequisicaoAjax(HttpServletRequest request) {
        return VALOR_AJAX_PARCIAL.equals(request.getHeader(HEADER_FACES_REQUEST))
            || "true".equals(request.getParameter(PARAMETRO_JSF_AJAX));
    }

    /**
     * Escreve uma resposta XML no formato esperado pelo runtime JSF para AJAX.
     * O elemento <redirect> instrui o jsf.ajax do cliente a fazer um redirect real.
     */
    private void enviarRedirectAjax(HttpServletResponse response, String loginUrl)
            throws IOException {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().printf(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<partial-response><redirect url=\"%s\"/></partial-response>",
            loginUrl
        );
    }

    private void rastrearNavegacao(HttpServletRequest request) {
        HttpSession session          = request.getSession(true);
        String      paginaAtual      = request.getServletPath();
        String      paginaAnterior   = (String) session.getAttribute(CHAVE_PAGINA_ATUAL);

        if (paginaAnterior == null) {
            session.setAttribute(CHAVE_PAGINA_ANTERIOR, paginaAtual);
            session.setAttribute(CHAVE_PAGINA_ATUAL,    paginaAtual);
        } else if (!paginaAnterior.equals(paginaAtual)) {
            session.setAttribute(CHAVE_PAGINA_ANTERIOR, paginaAnterior);
            session.setAttribute(CHAVE_PAGINA_ATUAL,    paginaAtual);
        }
    }
}
