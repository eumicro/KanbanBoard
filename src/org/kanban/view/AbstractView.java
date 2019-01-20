package org.kanban.view;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.kanban.shared.Paths;

public abstract class AbstractView {
	private Document document;
	private Element topnavi, content, header, footer;

	public AbstractView(String title) {
		Paths layoutPath = new Paths();
		header = new Element(Tag.valueOf("header"), "");
		content = new Element(Tag.valueOf("section"), "");
		footer = new Element(Tag.valueOf("footer"), "");

		try {
			document = Jsoup.parse(layoutPath.getLayoutFile("main.html"),
					"UTF-8");

			document.title(title);
			topnavi = Jsoup
					.parse(layoutPath.getLayoutFile("topnavi.html"), "UTF-8")
					.getElementsByTag("nav").first();
			addCSSToHead("default.css");
			addJavaScriptToHead("jQuery.js");
			addJavaScriptToHead("usermanagement.js");
			addJavaScriptToHead("util.js");
			build();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	protected void addToTopnavi(Node loginLogout) {
		topnavi.getElementById("login_register_info").appendChild(loginLogout);
	}

	protected void addToContent(Node content) {
		this.content.appendChild(content);
	}

	protected void addToFooter(Node content) {
		this.footer.appendChild(content);
	}

	protected void addJavaScriptToHead(String javaScriptFileName) {
		Element jsRes = new Element(Tag.valueOf("script"), "");
		jsRes.attr("type", "text/javascript");
		jsRes.attr("src", new Paths().getJavaScriptFilePath(javaScriptFileName));
		document.head().appendChild(jsRes);

	}

	protected void addCSSToHead(String cssFileName) {
		Element cssRes = new Element(Tag.valueOf("link"), "");
		cssRes.attr("rel", "Stylesheet");
		cssRes.attr("type", "text/css");
		cssRes.attr("href", new Paths().getCSSFilePath(cssFileName));
		document.head().appendChild(cssRes);
	}

	protected void addToHead(Node child) {
		this.document.head().appendChild(child);
	}

	protected void addRightNavi(Node node) {

		document.body().after(content).appendChild(node);
	}

	public String write() {

		header.appendChild(topnavi);
		// build...
		document.body().appendChild(header);
		document.body().appendChild(content);
		// QUICKFIX can be deleted later...
		// document.body().appendChild(footer);
		return document == null ? "<h1>Fehler beim Laden der Benutzerschnittstelle!<h1>"
				: document.toString();

	}

	protected abstract void build() throws IOException;
}
