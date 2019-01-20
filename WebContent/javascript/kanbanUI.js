function BoardWidget(board) {
	/**
	 * @type {Board}
	 */
	this.board = board;
	this.board_container = document.createElement("div");
	this.board_container.setAttribute("id", "b" + this.board.id);
	this.board_container.setAttribute("class", "board");

	// set title
	/**
	 * @type {Element}
	 */
	this.board_title = document.createElement("h2");
	this.board_title.innerHTML = this.board.title;

	// set description
	/**
	 * @type {Element}
	 */
	this.board_description = document.createElement("div");
	this.board_description.setAttribute("class", "info_slide");
	this.board_description.innerHTML = this.board.description;
	this.board_description.style.display = "none";
	// info board button
	this.infoBoardButton = document.createElement("button");
	this.infoBoardButton.setAttribute("class", "info_button fa fa-info");
	this.infoBoardButton.setAttribute("id", "board_info_button");
	this.infoBoardButton.addEventListener("click", function() {
		$('.board').find('.info_slide').slideToggle();
	}, false);
	this.infoBoardButton.innerHTML = " ";
	this.board_title.appendChild(this.infoBoardButton);
	// for owners
	if (username == this.board.owner.name) {
		// edit board button
		this.editBoardButton = document.createElement("button");
		this.editBoardButton.setAttribute("class", "edit_button fa fa-edit");
		this.editBoardButton.setAttribute("onclick", "editBoardForm("
				+ this.board.id + ")");
		this.editBoardButton.innerHTML = " ";
		this.board_title.appendChild(this.editBoardButton);

		// delete board button
		this.deleteBoardButton = document.createElement("button");
		this.deleteBoardButton.setAttribute("class",
				"delete_button fa fa-remove");
		this.deleteBoardButton.setAttribute("onclick", "deleteBoard("
				+ this.board.id + ")");
		this.deleteBoardButton.innerHTML = " ";
		this.board_title.appendChild(this.deleteBoardButton);

	}

	// set stations
	this.boardStationsContainer = document.createElement("div");
	this.boardStationsContainer.setAttribute("id", "board_stations");
	// drag & drop functionality for the board
	this.boardStationsContainer.setAttribute("ondrop", "dropStation(event)");
	this.boardStationsContainer.setAttribute("ondragover",
			"allowDropStation(event)");
	for (var i = 0; i < this.board.stations.length; i++) {
		var station = this.board.stations[i];

		// create station container
		var station_container = new StationWidget(new Station(station));

		this.boardStationsContainer.appendChild(station_container);

	}
	// build container
	this.board_container.appendChild(this.board_title);
	this.board_container.appendChild(this.board_description);
	this.board_container.appendChild(this.boardStationsContainer);
	/**
	 * 
	 * @returns {Array}
	 */
	this.getStationWidgets = function() {
		var list = new Array();
		for (var i = 0; i < this.boardStationsContainer.childNodes.length; i++) {
			list.push(this.boardStationsContainer.childNodes[i]);
		}
		return list;
	}

	return this.board_container;
}

/**
 * @param {Station}
 *            station
 */
function StationWidget(station) {
	/**
	 * type {Station}
	 */
	this.station = station;
	this.station_container = document.createElement("div");

	this.station_container.setAttribute("class", "station");
	this.station_container.setAttribute("draggable", "true");
	this.station_container.setAttribute("ondragstart", "dragStation(event)");

	// title
	this.title = document.createElement("h3");
	this.title.innerHTML = this.station.title;

	// info station button
	this.infoStationButton = document.createElement("button");
	this.infoStationButton.setAttribute("class", "info_button fa fa-info");
	this.infoStationButton.addEventListener("click", function() {
		$(this).parent().next('.station_info').slideToggle();
	}, false);
	this.infoStationButton.innerHTML = " ";
	this.title.appendChild(this.infoStationButton);

	// for owners
	if (username == currentBoard.owner.name) {
		// edit station button
		this.editButton = document.createElement("button");
		this.editButton.setAttribute("class", "edit_button fa fa-edit");
		this.editButton.setAttribute("onclick", "editStationForm("
				+ this.station.id + ")");
		this.editButton.innerHTML = " ";
		this.title.appendChild(this.editButton);

		// delete station button
		this.deleteButton = document.createElement("button");
		this.deleteButton.setAttribute("class", "delete_button fa fa-remove");
		this.deleteButton.setAttribute("onclick", "deleteStation("
				+ this.station.id + ")");
		this.deleteButton.innerHTML = " ";
		this.title.appendChild(this.deleteButton);
	}
	this.station_container.appendChild(this.title);
	this.station_info = document.createElement("div");
	this.station_info.setAttribute("class", "station_info");
	this.station_info.innerHTML = this.station.description;
	this.station_info.style.display = "none";
	this.station_container.appendChild(this.station_info);
	// tasks
	this.tasks_container = document.createElement("div");
	this.tasks_container.setAttribute("id", "s" + this.station.id);
	this.tasks_container.setAttribute("class", "station_tasks");
	// drag & drop functionality for the station
	this.tasks_container.setAttribute("ondrop", "dropTask(event)");
	this.tasks_container.setAttribute("ondragover", "allowDropTask(event)");

	// create tasks container
	for (var j = 0; j < this.station.tasks.length; j++) {
		// task object
		var task = this.station.tasks[j];
		// task container
		var task_container = new TaskWidget(new Task(task));
		this.tasks_container.appendChild(task_container);
	}
	this.station_container.appendChild(this.tasks_container);

	return this.station_container;
}
/**
 * @param {Task}
 *            task
 */
function TaskWidget(task) {
	/**
	 * @type {Task}
	 */
	this.task = task;
	/**
	 * @type {Boolean}
	 */
	this.isSlideDown = false;
	// task container
	this.task_container = document.createElement("div");
	this.task_container.setAttribute("id", "t" + this.task.id);
	this.task_container.setAttribute("class", "task");
	// drag & drop functionality
	this.task_container.setAttribute("draggable", "true");
	this.task_container.setAttribute("ondragstart", "dragTask(event)");
	this.task_container.setAttribute("ondrag", "dragAndMoveTask(event)");
	this.task_container.setAttribute("style", "background-color:#"
			+ this.task.color + ";");
	// content of the task
	this.title = document.createElement("h4");
	this.title.innerHTML = unescape(this.task.title);

	// info task button
	this.infoTaskButton = document.createElement("button");
	this.infoTaskButton.setAttribute("class", "info_button fa fa-info");
	this.infoTaskButton.addEventListener("click", function() {
		$(this).parent().next('.task_info').slideToggle();
	}, false);
	this.infoTaskButton.innerHTML = " ";
	this.title.appendChild(this.infoTaskButton);
	// edit task button
	/**
	 * @type {Element}
	 */
	this.editTaskButton = document.createElement("button");
	this.editTaskButton.setAttribute("class", "edit_button fa fa-edit");

	this.editTaskButton.addEventListener("click", function() {
		editTaskForm(task.id);
	}, false);
	this.editTaskButton.innerHTML = " ";
	this.title.appendChild(this.editTaskButton);
	// delete task action for user
	this.deleteTask = document.createElement("button");
	this.deleteTask.setAttribute("class", "delete_button fa fa-remove");
	this.deleteTask.setAttribute("onclick", "deleteTask(" + this.task.id + ")");
	this.deleteTask.innerHTML = " ";
	this.title.appendChild(this.deleteTask);

	this.description = document.createElement("p");
	this.description.innerHTML = unescape(this.task.description);
	this.horizontal_line = document.createElement("hr");
	this.reporter_lbl = document.createElement("b");
	this.reporter_lbl.innerHTML = "Ersteller:";
	this.reporter = document.createElement("p");
	this.reporter.innerHTML = this.task.reporter.name;
	this.assignee_lbl = document.createElement("b");
	this.assignee_lbl.innerHTML = "Zugewiesen:";
	this.assignee = document.createElement("p");
	this.assignee.innerHTML = this.task.assignee.name;
	this.lastchanged_lbl = document.createElement("b");
	this.lastchanged_lbl.innerHTML = "Letztes Update:";
	this.lastchanged = document.createElement("p");
	/**
	 * 
	 * @param {Date}
	 *            date
	 */
	this.getDateString = function(date) {
		return "am " + (date.getDate() + 1) + "." + (date.getMonth() + 1) + "."
				+ date.getFullYear() + " um " + date.getHours() + ":"
				+ date.getMinutes() + " Uhr";

	}
	this.lastchanged.innerHTML = this.getDateString(new Date(
			this.task.lastChange));
	// build task_container
	this.task_container.appendChild(this.title);

	this.task_info = document.createElement("div");
	this.task_info.setAttribute("class", 'task_info');
	this.task_info.style.display = "none";
	this.task_info.appendChild(this.horizontal_line);
	this.task_info.appendChild(this.reporter_lbl);
	this.task_info.appendChild(this.reporter);
	this.task_info.appendChild(this.assignee_lbl);
	this.task_info.appendChild(this.assignee);
	this.task_info.appendChild(this.lastchanged_lbl);
	this.task_info.appendChild(this.lastchanged);
	this.task_container.appendChild(this.task_info);
	this.slideToggle = function() {
		this.task_info.slideToggle();
	}
	this.task_container.appendChild(this.description);
	return this.task_container;

}
/**
 * @param {String}
 *            title
 * @param {Function}
 *            onSubmit
 */
function AbstractEditForm(title, onSubmit) {
	this.editProcedure = onSubmit;
	destroyEditForm();
	this.popup = document.createElement("div");
	this.popup.setAttribute("id", "edit_popup");

	this.header1 = document.createElement("h3");
	this.header1.innerHTML = unescape(title);

	this.form = document.createElement("form");
	this.form.setAttribute("id", "edit_form");
	$(this.form).submit(function(e) {
		e.preventDefault();
		this.editProcedure();
	});

	this.titleLabel = document.createElement("label");
	this.titleLabel.setAttribute("for", "edit_title");
	this.titleLabel.innerHTML = "Titel";
	this.titleField = document.createElement("input");
	this.titleField.setAttribute("id", "edit_title");
	this.titleField.setAttribute("type", "text");
	this.titleField.setAttribute("autofocus", "autofocus");
	this.descriptionLabel = document.createElement("label");
	this.descriptionLabel.setAttribute("for", "edit_description");
	this.descriptionLabel.innerHTML = "Beschreibung";
	this.descriptionField = document.createElement("textarea");
	this.descriptionField.setAttribute("id", "edit_description");
	this.descriptionField.setAttribute("type", "text");
	this.form.appendChild(this.titleLabel);
	this.form.appendChild(this.titleField);
	this.form.appendChild(this.descriptionLabel);
	this.form.appendChild(this.descriptionField);
	this.submitButton = document.createElement("button");
	this.submitButton.setAttribute("type", "submit");
	this.submitButton.setAttribute("class", "edit_button fa fa-edit");
	this.submitButton.addEventListener("click", this.editProcedure, false);
	this.closeButton = document.createElement("button");
	this.closeButton.setAttribute("class", "close_button fa fa-remove");
	this.closeButton.addEventListener("click", function() {
		destroyEditForm();
	}, false);

	this.popup.appendChild(this.header1);
	this.popup.appendChild(this.form);
	this.popup.appendChild(this.submitButton);
	this.popup.appendChild(this.closeButton);
	return this.popup;

}
AbstractEditForm.prototype.destroyEditForm = function() {
	console.log("called..");
	$().remove(this.popup);
}
AbstractEditForm.prototype.setTitle = function(value) {
	$(this.popup).find('#edit_title').html(value);
}
AbstractEditForm.prototype.setDescription = function(value) {
	$(this.popup).find('#edit_description').html(value);
}
/**
 * 
 * @param {BoardEvent}
 *            event
 */
function HistoryItem(event) {
	var date = new Date(event.date)
	this.tr = document.createElement("li");

	var entry = document.createElement("div");
	var title = document.createElement("h5");
	title.innerHTML = getDateString() + ": " + event.actor.name;
	var desc = document.createElement("p");
	desc.innerHTML = event.eventToString();
	entry.appendChild(title);
	entry.appendChild(desc);
	this.tr.appendChild(entry);
	function getActionToString() {
		return event.affected.title;
	}

	function getDateString() {
		return date.getDate() + "." + date.getMonth() + "."
				+ date.getFullYear() + "-" + date.getHours() + ":"
				+ date.getMinutes();
	}

	return this.tr;
}
/**
 * 
 * @param {Array}
 *            events
 */
function HistoryList(events) {
	/**
	 * @param {Array}
	 */
	this.events = events;
	this.table = document.createElement("ul");
	for (var i = 0; i < this.events.length; i++) {
		var event = this.events[i];
		this.table.appendChild(new HistoryItem(event));
	}
	$(this.table).find('h5').click(function() {
		$(this).next('p').slideToggle();
		$(this.table).find('h5').not($(this)).next('p').slideUp();
	});
	return this.table;
}

/**
 * @param {String}
 *            title
 * @param {EditFormWidget}
 *            form
 */
function EditPopup(title, form) {
	this.editPopup = new Popup("EDIT", title, form);
	this.editPopup.id = "edit_popup";

	return this.editPopup;
}

function EditFormWidget() {
	this.form = document.createElement("form");
	return this.form;
}
function ColorPickerWidget() {
	var form = document.createElement("form");
	for (var i = 0; i < 255; i++) {
		var button = document.createElement("button");

		button.style.backgroundColor = "#000000";
		button.style.display = "inline";
		form.appendChild(button);
	}
	$(form).submit(function(e) {
		e.preventDefault();
	})
	this.popup = new EditPopup("Farbauswahl", form);
	return this.popup;
}