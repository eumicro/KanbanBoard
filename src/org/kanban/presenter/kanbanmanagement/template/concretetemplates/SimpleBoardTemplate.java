package org.kanban.presenter.kanbanmanagement.template.concretetemplates;

import java.util.ArrayList;
import java.util.List;

import org.kanban.presenter.kanbanmanagement.template.BoardTemplate;

public class SimpleBoardTemplate extends BoardTemplate {

	@Override
	public String getBoardTitle() {
		return "Einfache Tafel";
	}

	@Override
	public String getBoardDescription() {

		return "Einfachste Tafel.";
	}

	@Override
	public List<String> getStationTitles() {
		List<String> stationNames = new ArrayList<String>();
		// stations
		stationNames.add("Zu erledigen");
		stationNames.add("Wird erledigt");
		stationNames.add("Ist erledigt");
		return stationNames;

	}

}
