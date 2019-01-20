package org.kanban.view;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.kanban.shared.Paths;

public class LoginView extends AbstractView {
	private Element login, register, welcome, rightnavi;

	public LoginView(String title) {
		super(title);
	}

	@Override
	protected void build() throws IOException {
		Paths layoutPath = new Paths();
		File loginFile = layoutPath.getLayoutFile("login.html");
		login = Jsoup.parse(loginFile, "UTF-8").getElementsByTag("form")
				.first();

		register = Jsoup
				.parse(layoutPath.getLayoutFile("registerform.html"), "UTF-8")
				.getElementsByTag("form").first();
		rightnavi = Jsoup
				.parse(layoutPath.getLayoutFile("rightnavi.html"), "UTF-8")
				.getElementsByTag("aside").first();
		welcome = Jsoup
				.parse(layoutPath.getLayoutFile("welcome.html"), "UTF-8")
				.getElementById("welcome");
		rightnavi.getAllElements().remove();
		rightnavi.appendChild(register);

		addToTopnavi(login);
		addToContent(welcome);
		addToContent(rightnavi);

	}

}
