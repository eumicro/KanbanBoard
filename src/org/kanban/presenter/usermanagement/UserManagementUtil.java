package org.kanban.presenter.usermanagement;

import javax.servlet.http.Cookie;

public class UserManagementUtil {
	public static String getValueByCookieName(Cookie[] cookies,
			String cookieName) {

		if (cookies == null || cookieName == null) {
			return null;

		}
		Cookie result = getCookieByName(cookies, cookieName);
		if (result == null) {
			return null;
		}
		return result.getValue();
	}

	public static Cookie getCookieByName(Cookie[] cookies, String cookieName) {
		Cookie result = null;
		if (cookies == null || cookieName == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieName)) {
				result = cookie;
				break;
			}
		}
		return result;
	}

	public static Cookie getUnsetCookie(Cookie[] cookies, String name) {
		Cookie result = getCookieByName(cookies, name);
		result.setMaxAge(0);
		result.setValue("");
		return result;
	}
}
