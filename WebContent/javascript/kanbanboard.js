$(document).ready(main());
// an array list with users boards.
var boards = new Array();
var eventsocket;
// the current board.
var currentBoard = null;
// var eventStack = new Array();
var onlyreporterstasks = false;
var onlyassigneetasks = false;
var notificationStack = [];
var eventHistory = [];
function main() {
	updateBoards();
	startEventService();
	setupShortCuts();

}
function setupShortCuts() {
	$(document)
			.keypress(
					function(e) {
						if (e.originalEvent.ctrlKey && e.originalEvent.altKey) {
							switch (e.key) {
							case "b":
								createBoard("", "", 1);
								break;
							case "s":
								createStation("", "", 0);
								break;
							case "t":
								var assignee_id = document
										.getElementById("new_task_assignee_list").options[document
										.getElementById("new_task_assignee_list").selectedIndex].id
										.replace("u", "");
								createTask("", "", assignee_id, "#ffffff");
								break;
							default:
								break;
							}
						}

					});
}
function updateBoards() {
	jQuery.support.cors = true;
	$.ajaxSetup({
		cache : false
	});
	$
			.getJSON(
					"http://" + window.location.host
							+ "/kanbanmanagement?get_user_boards")
			.done(
					function(data) {

						boards = new Array();
						var currentBoardisDeleted = true;
						// store boards as arraylist
						$.each(data, function(i, board) {

							boards[i] = new Board(board);
							// check if current board is deleted
							if (currentBoard != null
									&& boards[i].id == currentBoard.id) {
								currentBoardisDeleted = false;
							}
						});

						if (((currentBoard == null || currentBoardisDeleted) && boards.length > 0)) {
							setCurrentBoard(boards[0].id);

						} else if (boards.length > 0) {
							setCurrentBoard(currentBoard.id);
						} else {
							setCurrentBoard(null);
						}

					});
}
function startEventService() {

	// ws
	// establish the communication channel over a websocket
	eventsocket = new WebSocket("ws://" + window.location.host + "/eventsocket");
	// called when socket connection established
	eventsocket.onopen = function() {

		console.log("Connected ...");
		eventsocket.send(username);

	};

	// called when a message received from server
	eventsocket.onmessage = function(evt) {
		var event = new BoardEvent(JSON.parse(evt.data));
		notificationStack.push(event);
		if (event.type == "DELETE_BOARD" && currentBoard.id == event.board.id) {
			currentBoard = boards[0] | null;
		} else if (event.type == "CREATE_BOARD") {
			currentBoard = event.board;
		}
		// delay because of lazy database connection...
		setTimeout(function() {
			updateBoards();
			setUpRightNaviItems();
			updateHistory();
			messagePopup("INFO", "Aktualisierung", event.eventToString());
		}, 250);

	};

	// called when socket connection closed
	eventsocket.onclose = function() {
		console.log("Disconnected ...!");
		startEventService();

	};

	// called in case of an error
	eventsocket.onerror = function(err) {
		console.log("ERROR!", err);
		startEventService();
	};
}
/**
 * 
 * @param {String}
 *            title
 * @param {String}
 *            description
 * @param {String}
 *            templatenumber
 */
function createBoard(title, description, templatenumber) {
	var params = "create_board=" + title + "&description=" + description
			+ "&template_number=" + templatenumber;
	sendRequest(params);
}
function editBoard(id, title, description) {
	var params = "edit_board=" + title + "&board_id=" + id + "&description="
			+ description;
	sendRequest(params);
}
function deleteBoard(id) {
	var sure = confirm("Wenn Sie diese Tafel löschen werden auch alle Aufgaben gelöscht. Diese Aktion ist unwiderruflich!");
	if (sure) {
		var params = "delete_board=" + id;
		sendRequest(params);
	}

}
function deleteStation(id) {
	var sure = confirm("Das Löschen einer Station kann nicht rückgängig gemacht werden. Sind Sie sicher?");
	if (sure) {
		var params = "delete_station=" + id + "&board_id=" + currentBoard.id;
		sendRequest(params);
	}

}
function deleteTask(id) {
	var sure = confirm("Das Löschen eines Tasks kann nicht rückgängig gemacht werden. Sind Sie sicher?");
	if (sure) {
		var params = "delete_task=" + id + "&board_id=" + currentBoard.id;
		sendRequest(params);
	}
}
function createStation(title, description, position) {
	var params = "create_station=" + title + "&description=" + description
			+ "&position=" + position + "&board_id=" + currentBoard.id;
	sendRequest(params);

}
function editStation(id, title, description) {
	var params = "edit_station=" + id + "&title=" + title + "&description="
			+ description + "&board_id=" + currentBoard.id;
	sendRequest(params);
}

function createTask(title, description, assignee_id, color) {
	var trimmedColor = color.replace("#", "");
	var params = "create_task=" + title + "&description=" + description
			+ "&assignee_id=" + assignee_id + "&color=" + trimmedColor
			+ "&board_id=" + currentBoard.id;
	sendRequest(params);

}
function editTask(id, title, description, assignee_id, color) {
	var trimmedColor = color.replace("#", "");
	var params = "edit_task=" + id + "&title=" + title + "&description="
			+ description + "&assignee_id=" + assignee_id + "&color="
			+ trimmedColor + "&board_id=" + currentBoard.id;
	sendRequest(params);
}
function moveTask(task_id, station_id, board_id) {
	var params = "move_task=" + task_id + "&station_id=" + station_id
			+ "&board_id=" + board_id;
	sendRequest(params);
}
function inviteUserToBoard(username) {
	var params = "invite_user_to_board=" + username + "&board_id="
			+ currentBoard.id;
	sendRequest(params);
}
function uninviteUserFromBoard(user_id) {
	var params = "uninvite_user_from_board=" + user_id + "&board_id="
			+ currentBoard.id;
	sendRequest(params);
}
function handleJsonError(data) {
	messagePopup("ERROR", "Serverfehler", JSON.stringify(data));
}
// sets the current board into the board view
function setCurrentBoard(board_id) {

	// create new boardViewElement
	var boardViewContainer = $('#board_view');
	// if id is null
	if (board_id == null) {
		boardViewContainer.contents().empty();
		updateDropDownList();
		setUpRightNaviItems();
		return;
	}
	// choose the board with the given board_id
	for (var i = 0; i < boards.length; i++) {
		if (boards[i].id == board_id) {
			currentBoard = boards[i];
			break;
		}
	}

	var board_container = new BoardWidget(currentBoard);

	boardViewContainer.contents().replaceWith(board_container);
	// create task form (users to assign)
	updateUsersToAssignList();
	updateDropDownList();
	setUpRightNaviItems();
	updateHistory();
	filterTasksByReporter(onlyreporterstasks);

	// set click listener for info buttons.

}
// updates the drop down menu

function updateDropDownList() {
	// dropdown list
	var dropdownlist = $('#board_list').empty();
	// if no boards in boards found
	if (boards.length == 0) {
		$('#sectiontopbar').hide();
		return;
	} else {
		$('#sectiontopbar').show();
	}

	// iterate over the users boards and create a new option for eny board
	for (var i = 0; i < boards.length; i++) {

		var newOption = document.createElement("option");
		newOption.setAttribute("id", "b" + boards[i].id);
		newOption.setAttribute("onclick", "setCurrentBoard(" + boards[i].id
				+ ");");
		// if currentBoard set
		if (currentBoard != null && currentBoard.id == boards[i].id) {
			newOption.setAttribute("selected", "selected");
		}

		newOption.innerHTML = boards[i].title;
		dropdownlist.append(newOption);

	}

}
// fetch boards as JSON Objects from server
function updateUsersToAssignList() {

	// dropdown list
	var dropdownlist = $('#new_task_assignee_list').empty();
	var manageUserList = $('#board_user_list').empty();
	// iterate over the users boards and create a new option for eny board
	for (var i = 0; i < currentBoard.users.length; i++) {
		var newOption = document.createElement("option");
		var newUserListItem = document.createElement("li");
		var uninviteButton = document.createElement("button");

		newUserListItem.innerHTML = currentBoard.users[i].name
		if (currentBoard.owner.id !== currentBoard.users[i].id) {
			uninviteButton.setAttribute("class", "delete_button fa fa-remove");

			uninviteButton.setAttribute("type", "button");
			uninviteButton.setAttribute("onclick", "uninviteUserFromBoard("
					+ currentBoard.users[i].id + ")");
			newUserListItem.appendChild(uninviteButton);

		}

		newOption.setAttribute("id", "u" + currentBoard.users[i].id);
		if (currentBoard.users[i].name == username) {
			newOption.setAttribute("selected", "selected");
		}
		newOption.innerHTML = currentBoard.users[i].name;
		newUserListItem.setAttribute("id", "u" + currentBoard.users[i].id);
		manageUserList.append(newUserListItem);
		dropdownlist.append(newOption);
	}

}

/**
 * drag and drop functions
 * 
 * @param {MouseEvent}
 *            ev
 */
function dragTask(ev) {
	ev.dataTransfer.setData("text", ev.target.id);

}
function dragAndMoveTask(ev) {

}

function dragStation(ev) {
	ev.dataTransfer.setData("text", ev.target.id);
}
// handles drop event of a task
function dropTask(ev) {

	// prevent dropping into other tasks...
	if (ev.target.getAttribute("class") != "station_tasks") {
		return;
	}

	// target is a station..
	ev.preventDefault();
	var data = ev.dataTransfer.getData("text");
	var task_container = document.getElementById(data);
	var station_container = ev.target;

	// identify station and task for board update purpose
	var station_id = station_container.getAttribute("id").replace("s", "");
	var task_id = task_container.getAttribute("id").replace("t", "");
	var board_id = currentBoard.id;
	// drop it.
	moveTask(task_id, station_id, board_id);
	ev.target.appendChild(task_container);

}
// handles drop event of a station
function dropStation(ev) {
	// prevent dropping into other tasks...
	if (ev.target.getAttribute("id") != "board_stations")
		return;
	// target is the board.
	ev.preventDefault();
	var data = ev.dataTransfer.getData("text");
	var station_container = document.getElementById(data);
	var board_container = ev.target;
	// identify for update purposes
	var board_id = board_container.getAttribute("id");
	var station_id = station_container.getAttribute("id");
	// drop it.
	console.log("station '" + station_id + "' dropped into board '" + board_id
			+ "'.");
	ev.target.appendChild(task_container);

}
function allowDropTask(ev) {
	if (ev.target.getAttribute("class") != "station_tasks") {
		return;
	}
	ev.preventDefault();
}
function allowDropStation(ev) {
	if (ev.target.getAttribute("id") != "board_stations") {
		return;
	}
	ev.preventDefault();
}

function setUpRightNaviItems() {

	/**
	 * @type {Boolean}
	 */
	var hasNoBoards = boards.length == 0;
	var isOwner = false;
	var hasStations = false;
	if (currentBoard) {
		isOwner = (currentBoard.owner.name == username);
		hasStations = currentBoard.stations.length == 0;
	}
	if (!hasNoBoards && isOwner == true) {
		$('#create_station_item').show();
		$('#manage_users_item').show();
	} else {
		$('#create_station_item').hide();
		$('#manage_users_item').hide();
	}
	if (!hasNoBoards) {
		$('#create_task_item').show();
		$('#activities_item').show();
	} else {
		$('#create_task_item').hide();
		$('#activities_item').hide();
	}

}
/**
 * @param {Number}
 *            id
 */
function editBoardForm(id) {
	var title = null;
	var description = null;
	var id = currentBoard.id;

	showEditForm("Tafel bearbeiten", function() {
		title = document.getElementById("edit_title").value;
		description = document.getElementById("edit_description").value;
		if (title != null && description != null && id != null) {
			editBoard(id, title, description);
			destroyEditForm();
		}
	});
	var edit_popup = document.getElementById("edit_popup");

	document.getElementById("edit_title").value = currentBoard.title;
	document.getElementById("edit_description").value = currentBoard.description;
}
function editStationForm(id) {
	var station = currentBoard.getStationById(id);

	var title = null;
	var description = null;

	showEditForm("Station bearbeiten", function() {
		title = document.getElementById("edit_title").value;
		description = document.getElementById("edit_description").value;
		if (title != null && description != null && station.id != null) {
			editStation(station.id, title, description);
			destroyEditForm();
		}
	});
	var edit_popup = document.getElementById("edit_popup");
	document.getElementById("edit_title").value = station.title;
	document.getElementById("edit_description").value = station.description;
}
function editTaskForm(id) {
	var task = currentBoard.getTaskById(id);
	var title = null;
	var description = null;
	var assignee_id = null;
	var color = null;
	showEditForm(
			"Task bearbeiten",
			function() {
				title = document.getElementById("edit_title").value;
				description = document.getElementById("edit_description").value;
				assignee_id = document.getElementById("assignee_edit_select").options[document
						.getElementById("assignee_edit_select").selectedIndex].id
						.replace("u", "");

				color = document.getElementById("edit_color").value;

				if (title != null && description != null && task.id != null
						&& assignee_id != null && color != null) {
					editTask(task.id, title, description, assignee_id, color);
					destroyEditForm();
				}
			});
	var edit_popup = document.getElementById("edit_popup");

	document.getElementById("edit_title").value = task.title;
	document.getElementById("edit_description").value = task.description;
	var edit_form = document.getElementById("edit_form");
	var assignee_select = document.createElement("select");
	assignee_select.setAttribute("id", "assignee_edit_select");
	var assignee_select_label = document.createElement("label");
	assignee_select_label.setAttribute("for", "assignee_edit_select");
	assignee_select_label.innerHTML = "Zuweisen";
	for (var i = 0; i < currentBoard.users.length; i++) {
		var newOption = document.createElement("option");
		newOption.id = "u" + currentBoard.users[i].id;
		if (currentBoard.users[i].id == task.assignee.id) {
			newOption.setAttribute("selected", "selected");
		}
		newOption.innerHTML = currentBoard.users[i].name;

		assignee_select.appendChild(newOption);

	}
	var color_lbl = document.createElement("label");
	color_lbl.setAttribute("for", "edit_color");
	color_lbl.innerHTML = "Farbe";
	var color_picker = document.createElement("input");
	color_picker.setAttribute("type", "color");
	color_picker.setAttribute("id", "edit_color");
	color_picker.setAttribute("value", "#" + task.color);
	edit_form.insertBefore(color_picker, document
			.getElementById("edit_description").nextSibling);
	edit_form.insertBefore(color_lbl, document
			.getElementById("edit_description").nextSibling);
	edit_form.insertBefore(assignee_select, document
			.getElementById("edit_description").nextSibling);
	edit_form.insertBefore(assignee_select_label, document
			.getElementById("edit_description").nextSibling);
}
function showEditForm(formTitle, editFunction) {

	destroyEditForm();
	var popup = document.createElement("div");
	popup.setAttribute("id", "edit_popup");

	var header1 = document.createElement("h3");
	header1.innerHTML = unescape(formTitle);
	var form = document.createElement("form");
	form.setAttribute("id", "edit_form");
	$(form).submit(function(e) {
		e.preventDefault();
		editFunction();
	});
	var titleLabel = document.createElement("label");
	titleLabel.setAttribute("for", "edit_title");
	titleLabel.innerHTML = "Titel";
	var titleField = document.createElement("input");
	titleField.setAttribute("id", "edit_title");
	titleField.setAttribute("type", "text");
	titleField.setAttribute("autofocus", "autofocus");

	var descriptionLabel = document.createElement("label");
	descriptionLabel.setAttribute("for", "edit_description");
	descriptionLabel.innerHTML = "Beschreibung";
	var descriptionFiel = document.createElement("textarea");
	descriptionFiel.setAttribute("id", "edit_description");
	descriptionFiel.setAttribute("type", "text");
	form.appendChild(titleLabel);
	form.appendChild(titleField);
	form.appendChild(descriptionLabel);
	form.appendChild(descriptionFiel);
	var submitButton = document.createElement("button");
	submitButton.setAttribute("type", "submit");
	submitButton.setAttribute("class", "edit_button fa fa-edit");
	submitButton.addEventListener("click", editFunction, false);
	var closeButton = document.createElement("button");
	closeButton.setAttribute("class", "close_button fa fa-remove");
	closeButton.addEventListener("click", function() {
		destroyEditForm();
	}, false);

	popup.appendChild(header1);
	popup.appendChild(form);
	popup.appendChild(submitButton);
	popup.appendChild(closeButton);
	document.body.insertBefore(popup, document.getElementById("main_section"));
	$(titleField).focus();
}

function destroyEditForm() {
	if (document.getElementById("edit_popup")) {
		document.body.removeChild(document.getElementById("edit_popup"));
	}
}
function filterTasksByReporter(onlyreporterstasks) {

	for (var i = 0; i < currentBoard.stations.length; i++) {
		for (var j = 0; j < currentBoard.stations[i].tasks.length; j++) {
			var task = currentBoard.stations[i].tasks[j];
			if (task.reporter.name != username) {
				if (task.assignee.name === username
						&& onlyassigneetasks === true
						&& onlyreporterstasks === true) {
					continue;
				} else {
					document.getElementById("t" + task.id).style.display = onlyreporterstasks ? "none"
							: "";
				}

			}
		}
	}

	if (onlyreporterstasks === true) {
		document.getElementById("filter_by_reporter").setAttribute("class",
				"active_filter");
	} else {
		document.getElementById("filter_by_reporter").removeAttribute("class");
	}

}
function filterTasksByAssignee(onlyassigneetasks) {

	for (var i = 0; i < currentBoard.stations.length; i++) {
		for (var j = 0; j < currentBoard.stations[i].tasks.length; j++) {
			var task = currentBoard.stations[i].tasks[j];

			if (task.assignee.name != username) {
				if (task.reporter.name === username
						&& onlyreporterstasks === true
						&& onlyassigneetasks === true) {
					continue;
				} else {
					document.getElementById("t" + task.id).style.display = onlyassigneetasks ? "none"
							: "";
				}
			}
		}
	}

	if (onlyassigneetasks === true) {
		document.getElementById("filter_by_assignee").setAttribute("class",
				"active_filter");
	} else {
		document.getElementById("filter_by_assignee").removeAttribute("class");
	}
}
/**
 * 
 * @param {Date}
 *            date
 */

/**
 * 
 * @param {Event}
 *            event
 */
function touchHandler(event) {

	var touches = event.changedTouches, first = touches[0], type = "";
	switch (event.type) {
	case "touchstart":
		type = "dragstart";
		break;
	case "touchmove":
		type = "drag";
		break;
	case "touchend":
		type = "dragend";
		break;
	default:
		return;
	}
	var simulatedEvent = document.createEvent("MouseEvent");
	simulatedEvent.initMouseEvent(type, true, true, window, 1, first.screenX,
			first.screenY, first.clientX, first.clientY, false, false, false,
			false, 0/* left */, null);
	first.target.dispatchEvent(simulatedEvent);

	event.preventDefault();
}
/**
 * @param {String}
 *            params
 */
function sendRequest(params) {
	$.getJSON("http://" + window.location.host + "/kanbanmanagement?" + params)
			.done(function(msg) {
				if (msg.error) {
					messagePopup("ERROR", "Fehler", msg.message);
				}
				// updateBoards();
			});
}
function undo(board_id) {
	var params = "undo=" + board_id;
	sendRequest(params);
}
function redo(board_id) {
	var params = "redo=" + board_id;
	sendRequest(params);
}
function updateHistory() {
	if (currentBoard) {
		$.getJSON(
				"http://" + window.location.host
						+ "/kanbanmanagement?get_history=" + currentBoard.id)
				.done(
						function(msg) {
							if (msg.error) {
								messagePopup("ERROR", "Fehler", msg.message);
							} else {
								$('#board_activity_list').empty();
								// eventHistory = [];
								$.each(msg, function(i, bEvent) {
									eventHistory[i] = new BoardEvent(bEvent);

								})
								$.when.apply($, eventHistory).then(
										function() {

											$('#board_activity_list').html(
													new HistoryList(
															eventHistory));
										});

							}

						});
	}

}
