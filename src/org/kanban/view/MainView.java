package org.kanban.view;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.kanban.shared.Paths;

public class MainView extends AbstractView {

	public MainView(String title) {
		super(title);
	}

	private Element rightnavi, createtask, createstation, createboard, adduser, logout, board;

	@Override
	protected void build() throws IOException {
		Paths layoutPath = new Paths();
		try {

			logout = Jsoup.parse(layoutPath.getLayoutFile("logout.html"), "UTF-8").getElementsByTag("nav").first();
			board = Jsoup.parse(layoutPath.getLayoutFile("board.html"), "UTF-8").getElementsByTag("section").first();
			rightnavi = Jsoup.parse(layoutPath.getLayoutFile("rightnavi.html"), "UTF-8").getElementsByTag("aside")
					.first();
			createtask = Jsoup.parse(layoutPath.getLayoutFile("createtaskform.html"), "UTF-8").getElementsByTag("form")
					.first();
			createstation = Jsoup.parse(layoutPath.getLayoutFile("createstepform.html"), "UTF-8")
					.getElementsByTag("form").first();
			createboard = Jsoup.parse(layoutPath.getLayoutFile("createboardform.html"), "UTF-8")
					.getElementsByTag("form").first();
			adduser = Jsoup.parse(layoutPath.getLayoutFile("adduserform.html"), "UTF-8").getElementsByTag("form")
					.first();

			rightnavi.getElementById("add_station_form").appendChild(createstation);
			rightnavi.getElementById("create_board_form").appendChild(createboard);
			rightnavi.getElementById("add_task_form").appendChild(createtask);
			rightnavi.getElementById("add_user_form").appendChild(adduser);

		} catch (IOException e) {

			e.printStackTrace();
		}
		addJavaScriptToHead("model.js");
		addJavaScriptToHead("kanbanUI.js");
		addJavaScriptToHead("kanbanboard.js");
		addToTopnavi(logout);
		addToContent(board);
		addRightNavi(rightnavi);

	}

}
