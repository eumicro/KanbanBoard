package org.kanban.presenter.kanbanmanagement.template;

import java.util.ArrayList;
import java.util.List;

import org.kanban.model.Board;
import org.kanban.model.Station;
import org.kanban.presenter.kanbanmanagement.template.concretetemplates.ScrumBoardTemplate;
import org.kanban.presenter.kanbanmanagement.template.concretetemplates.SimpleBoardTemplate;

public class KanbanFromTemplateFactory extends KanbanFactory {
	List<BoardTemplate> templates;
	private static KanbanFromTemplateFactory instance;

	private KanbanFromTemplateFactory() {
		templates = new ArrayList<BoardTemplate>();
		templates.add(new ScrumBoardTemplate());
		templates.add(new SimpleBoardTemplate());

	}

	public static KanbanFromTemplateFactory getinstance() {
		return instance == null ? instance = new KanbanFromTemplateFactory()
				: instance;
	}

	@Override
	public Board createBoard(BoardTemplate t) {
		Board board = new Board();
		board.setTitle(t.getBoardTitle());
		board.setDescription(t.getBoardDescription());
		for (String stationTitle : t.getStationTitles()) {
			Station station = new Station();
			station.setTitle(stationTitle);
			board.addStation(station);
		}
		return board;
	}

	public Board createBoardByTemplateNumber(Integer n) {
		BoardTemplate t = templates.get(n);

		return createBoard(t);
	}

	public List<BoardTemplate> getTemplates() {
		return templates;
	}

	public void addTemplate(BoardTemplate t) {
		templates.add(t);
	}

	public void removeTemplate(BoardTemplate t) {
		templates.remove(t);
	}
}
