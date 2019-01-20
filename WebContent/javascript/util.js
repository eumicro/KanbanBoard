/**
 * 
 */
function getCookie(cname) {
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ')
			c = c.substring(1);
		if (c.indexOf(name) == 0)
			return c.substring(name.length, c.length);
	}
	return "";
}

/**
 * drag and drop functions
 * 
 * @param ev
 */
function drag(ev) {
	ev.dataTransfer.setData("text", ev.target.id);
}

function drop(ev) {
	ev.preventDefault();
	var data = ev.dataTransfer.getData("text");
	ev.target.appendChild(document.getElementById(data));
}
function allowDrop(ev) {
	ev.preventDefault();
}
/**
 * shows a new popup.
 * 
 * @param {String}
 *            type (INFO,ERROR,WARNING)
 * @param msg
 * 
 */
function messagePopup(type, title, msg) {
	var m = document.createElement("p");
	m.innerHTML = msg;

	var p = new Popup(type, title, m);
	$(p).hide();
	$(document.body).append($(p));
	$(p).show({
		direction : "top"
	}, 1000);

}

function readCookie(name) {
	var nameEQ;
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ')
			c = c.substring(1, c.length);
		if (c.indexOf(nameEQ) == 0) {
			return c.substring(nameEQ.length, c.length);
		}
	}
	return null;
}
/**
 * 
 * @param element
 * @returns {Object}
 */
function getPosition(element) {
	var xPosition = 0;
	var yPosition = 0;

	while (element) {
		xPosition += (element.offsetLeft - element.scrollLeft + element.clientLeft);
		yPosition += (element.offsetTop - element.scrollTop + element.clientTop);
		element = element.offsetParent;
	}
	return {
		x : xPosition,
		y : yPosition
	};
}
/**
 * @param {Element}
 *            elem
 * @param {String}
 *            type
 * @param {String}
 *            title
 */
function Popup(type, title, elem) {
	this.popup = document.createElement("div");
	this.popUpId = "popup_" + Math.round(Math.random() * 1000);
	this.popup.setAttribute("id", this.popUpId);
	var header1 = document.createElement("b");
	header1.innerHTML = unescape(title + ": ");
	var elemWrapper = document.createElement("div");
	elemWrapper.appendChild(elem)
	elemWrapper.style.display = "block";
	elemWrapper.style.clear = "both";
	this.closeButton = document.createElement("button");
	this.closeButton.setAttribute("class", "close_button fa fa-remove");
	$(this.closeButton).click(function() {

		var p = $(this).parent();
		p.fadeOut("slow", function() {
			p.remove();
		});
	});

	if (type == "INFO") {
		this.popup.setAttribute("class", "popup_info");
		setTimeout(function() {

			$(".popup_info").fadeOut("slow", function() {
				$(".popup_info").remove();
			});
		}, 4000);
	} else if (type == "ERROR") {
		this.popup.setAttribute("class", "popup_error");
	} else if (type == "EDIT") {
		this.popup.setAttribute("class", "edit_popup");
	}

	this.popup.appendChild(header1);
	this.popup.appendChild(elemWrapper);
	this.popup.appendChild(this.closeButton);

	return this.popup;
}