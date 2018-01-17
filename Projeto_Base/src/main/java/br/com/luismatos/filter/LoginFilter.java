package br.com.luismatos.filter;
import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import br.com.luismatos.model.Usuario;
public class LoginFilter implements Filter {
	
	
	private final static String FILTER_APPLIED = "_security_filter_applied";
	
	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	
	
	public LoginFilter() {
			
	}
	
	public void init(FilterConfig arg0) throws ServletException {
			
	}
	
	public void destroy() {
			
	}
	
	@SuppressWarnings("deprecation")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		@SuppressWarnings("unused")
		HttpServletResponse servletResponse = (HttpServletResponse) response;
		HttpSession session = servletRequest.getSession();
		String uri = ((HttpServletRequest) request).getRequestURI();
		Usuario user = (Usuario) session.getAttribute("info_user");
		try {
			if ((uri.indexOf("aplicacao") > 0) && (user == null)) {
				//String url = "/login.xhtml";
				String contextPath = ((HttpServletRequest) request).getContextPath();
				
				((HttpServletResponse) response).sendRedirect(contextPath);
				return;
			}else{
				HttpSession sess = ((HttpServletRequest) request).getSession(true);

				String newCurrentPage = ((HttpServletRequest) request).getServletPath();

				if (sess.getAttribute("currentPage") == null) {
					sess.setAttribute("lastPage", newCurrentPage);
					sess.setAttribute("currentPage", newCurrentPage);
				} else {

					String oldCurrentPage = sess.getAttribute("currentPage").toString();
					if (!oldCurrentPage.equals(newCurrentPage)) {
						sess.setAttribute("lastPage", oldCurrentPage);
						sess.setAttribute("currentPage", newCurrentPage);
					}
				}
				System.out.println(sess.getAttribute("lastPage"));
				request.setCharacterEncoding("UTF-8");
				chain.doFilter(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setCharacterEncoding("UTF-8");
			chain.doFilter(request, response);
		}

		
	}
}