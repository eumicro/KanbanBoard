/**
 * @param {Object}
 *            board
 */
function Board(board) {
	this.id = board.id;
	this.title = board.title;
	this.description = board.description;

	this.stations = new Array();
	for (var i = 0; i < board.stations.length; i++) {
		this.stations.push(new Station(board.stations[i]));
	}

	this.users = new Array();
	for (var i = 0; i < board.users.length; i++) {
		this.users.push(new User(board.users[i]));
	}
	this.owner = new User(board.owner);
	/**
	 * @returns {Task}
	 */
	this.getTaskById = function(task_id) {
		for (var i = 0; i < this.stations.length; i++) {
			var tasks = this.stations[i].tasks;
			for (var j = 0; j < tasks.length; j++) {

				if (tasks[j].id == task_id) {
					return tasks[j];
				}
			}
		}
		return null;
	}
	/**
	 * @param {Number}
	 *            station_id
	 * @returns {Station}
	 */
	this.getStationById = function(station_id) {
		for (var i = 0; i < this.stations.length; i++) {
			if (this.stations[i].id == station_id) {
				return this.stations[i];
			}
		}
		return null;
	}

}
/**
 * @param {Object}
 *            station
 */
function Station(station) {
	this.id = station.id;
	this.title = station.title;
	this.description = station.description;
	this.position = station.position;
	this.tasks = new Array();

	for (var i = 0; i < station.tasks.length; i++) {
		this.tasks.push(new Task(station.tasks[i]));
	}
	/**
	 * @returns {Task}
	 */
	this.getTaskById = function(task_id) {
		for (var i = 0; i < this.tasks[i]; i++) {
			if (this.tasks[i].id == task_id) {
				return this.tasks[i];
			}
		}
		return null;
	}

}
/**
 * @param {Object}
 *            task
 */
function Task(task) {
	this.id = task.id;
	this.title = task.title;
	this.description = task.description;
	this.color = task.color;
	this.lastChange = task.lastChange;
	this.assignee = new User(task.assignee);
	this.reporter = new User(task.reporter);
}
/**
 * @param {JSON}
 */
function User(user) {
	this.id = user.id;
	this.name = user.name;
	this.getName = function() {
		return this.name;
	}
	this.getId = function() {
		return this.id;
	}
}
/**
 * @param {Object}
 *            event
 */
function BoardEvent(event) {
	this.type = event.type;
	this.actor = new User(event.actor);
	this.board = new Board(event.board);
	this.affected = null;
	this.date = event.date;
	switch (this.type) {
	case "CREATE_BOARD":
		/**
		 * @type {Board} affectedObject
		 */
		this.affected = new Board(event.affected);
		break;
	case "CREATE_STATION":
		this.affected = new Station(event.affected);
		break;
	case "CREATE_TASK":
		this.affected = new Task(event.affected);
		break;
	case "EDIT_BOARD":
		this.affected = new Board(event.affected);
		break;
	case "EDIT_STATION":
		this.affected = new Station(event.affected);
		break;
	case "EDIT_TASK":
		this.affected = new Task(event.affected);
		break;
	case "DELETE_BOARD":
		this.affected = new Board(event.affected);
		break;
	case "DELETE_STATION":
		this.affected = new Station(event.affected);
		break;
	case "DELETE_TASK":
		this.affected = new Task(event.affected);
		break;
	case "INVITE_USER":
		this.affected = new User(event.affected);
		break;
	case "UNINVITE_USER":
		this.affected = new User(event.affected);
		break;
	case "MOVE_TASK":
		this.affected = new Task(event.affected);
		break;
	case "MOVE_STATION":
		this.affected = new Station(event.affected);
		break;
	case "ASSIGN_USER_TO_TASK":
		this.affected = new User(event.affected[2]);
		break;
	default:
		break;
	}
	this.eventToString = function() {
		var message = event.actor.name == username ? "Sie haben "
				: event.actor.name + " hat ";
		switch (event.type) {
		case "CREATE_TASK":
			message += "die Aufgabe '" + event.affected.title + "' erstellt";
			break;
		case "EDIT_TASK":
			message += "die Aufgabe '" + event.affected.title + "' bearbeitet";
			break;
		case "DELETE_TASK":
			message += "die Aufgabe '" + event.affected.title + "' gelöscht";
			break;
		case "CREATE_STATION":
			message += "die Station '" + event.affected.title + "' erstellt";
			break;
		case "EDIT_STATION":
			message += "die Station '" + event.affected.title + "' bearbeitet";
			break;
		case "DELETE_STATION":
			message += "die Station '" + event.affected.title + "' gelöscht";
			break;
		case "CREATE_BOARD":
			message += "die Tafel '" + event.affected.title + "' erstellt";
			break;
		case "EDIT_BOARD":
			message += "die Tafel '" + event.affected.title + "' bearbeitet";
			break;
		case "DELETE_BOARD":
			message += "die Tafel '" + event.affected.title + "' gelöscht";
			break;
		case "INVITE_USER":
			message += event.affected.name == username ? "Sie "
					: "den Benutzer '" + event.affected.name + "'";
			message += " eingeladen";
			break;
		case "UNINVITE_USER":
			message += "den Benutzer '" + event.affected.name + "' ausgeladen";
			break;
		case "MOVE_TASK":
			message += "die Aufgabe '" + event.affected.title + "' verschoben";
			break;
		case "MOVE_STATION":
			message += "die Station '" + event.affected.title + "' verschoben";
			break;
		case "ASSIGN_USER_TO_TASK":
			message += "die Aufgabe '" + event.affected[0].title
					+ "' dem Nutzer '" + data.affected[1].name + "' zugewiesen";
			break;
		default:
			break;
		}
		message += ".";
		return message;
	}
}