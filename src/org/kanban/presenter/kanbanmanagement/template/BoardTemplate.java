package org.kanban.presenter.kanbanmanagement.template;

import java.util.List;

public abstract class BoardTemplate {
	public abstract String getBoardTitle();

	public abstract String getBoardDescription();

	public abstract List<String> getStationTitles();
}
