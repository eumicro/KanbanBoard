package org.kanban.presenter.usermanagement;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kanban.model.User;
import org.kanban.presenter.database.exceptions.DataBaseErrorException;
import org.kanban.presenter.database.exceptions.DataBaseNotAvailableException;
import org.kanban.presenter.usermanagement.exceptions.UserDoesNotExistException;
import org.kanban.view.LoginView;
import org.kanban.view.MainView;

/**
 * Servlet Filter implementation class UserManagementFilter
 */
public class UserManagementFilter implements Filter {
	private static final String LOGIN_HEAD_TITLE = "Willkommen auf Kanban-Tafel!";
	private Logger log;
	private UserManagement userManagement;

	/**
	 * Default constructor.
	 */
	public UserManagementFilter() {
		// do not delete it.
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		String username = UserManagementUtil.getValueByCookieName(
				((HttpServletRequest) request).getCookies(),
				UsermanagementCookieNames.USERNAME);
		User user = null;
		try {

			if (username == null) {
				log.info("Cookie not found! Redirecting to Log In View");
				response.getWriter().write(
						new LoginView(LOGIN_HEAD_TITLE).write());
				return;
			} else {
				user = userManagement.getUserByName(username);

				if (userManagement.isLoggedIn(user)) {
					log.info("User is logged in and will be redirected to the board view.");
					// show main view if user is logged in
					response.getWriter()
							.write(new MainView("Willkommen auf Kanban-Tafel")
									.write());
					return;
				} else {

					log.info("User is not logged in and will be redirected to the login view.");
					Cookie loggedOutCookie = UserManagementUtil.getUnsetCookie(
							((HttpServletRequest) request).getCookies(),
							UsermanagementCookieNames.USERNAME);
					((HttpServletResponse) response).addCookie(loggedOutCookie);
					response.getWriter().write(
							new LoginView(LOGIN_HEAD_TITLE).write());
					return;
				}
			}

		} catch (UserDoesNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataBaseNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataBaseErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		log = Logger.getAnonymousLogger();
		log.info("User management filter initialization...");
		try {
			userManagement = UserManagement.getInstance();
		} catch (DataBaseNotAvailableException e) {
			log.info("Schwerwiegender Datenbankfehler.");
			e.printStackTrace();
		} catch (DataBaseErrorException e) {
			log.info("Schwerwiegender Datenbankfehler.");
			e.printStackTrace();
		}
		log.info("User management filter initialization done.");

	}

}
