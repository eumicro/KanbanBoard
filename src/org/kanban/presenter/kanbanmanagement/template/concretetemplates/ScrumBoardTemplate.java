package org.kanban.presenter.kanbanmanagement.template.concretetemplates;

import java.util.ArrayList;
import java.util.List;

import org.kanban.presenter.kanbanmanagement.template.BoardTemplate;

public class ScrumBoardTemplate extends BoardTemplate {

	@Override
	public String getBoardTitle() {
		return "Scrum Board";
	}

	@Override
	public String getBoardDescription() {
		return "Scrum-Tafel als Hilfswerkzeug bei agilen Softwareentwicklung.";
	}

	@Override
	public List<String> getStationTitles() {
		List<String> stationNames = new ArrayList<String>();
		// stations
		stationNames.add("Zutun");
		stationNames.add("Wird bearbeitet");
		stationNames.add("Wird geprüft");
		stationNames.add("Erledigt");
		return stationNames;
	}

}
