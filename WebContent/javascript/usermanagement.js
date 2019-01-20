var varIsUser = undefined;
var username = getUsername();
/**
 * 
 * @returns {String}
 */
function getUsername() {
	$
			.ajax({
				type : 'GET',
				url : "http://" + window.location.host
						+ "/usermanagement?get_username",
				dataType : 'json',
				success : function(msg) {
					username = msg.message;
				},
				data : {},
				async : false
			});
	return username;
}
function logout() {
	console.log("send logout request...");
	messagePopup("INFO", "Abmelden",
			"Abmeldung läuft, Sie werden weitergeleitet...");
	$.getJSON("http://" + window.location.host + "/usermanagement?logout_user")
			.done(function(msg) {
				messagePopup("INFO", "Abmelden", msg.message);
				if (!msg.error) {

				}
				// google sign out
				var auth2 = gapi.auth2.getAuthInstance();
				auth2.signOut().then(function() {
					console.log('Google-User signed out.');
				});
				// redirect to login view...
				console.log("reload content...");
				username = "";
				location.reload();
			});
}
function logIn(username, password) {
	$.getJSON(
			"http://" + window.location.host + "/usermanagement?login_user="
					+ username + "&password=" + password).done(function(msg) {

		if (!msg.error) {
			// redirect to main...
			location.reload();
			getUsername();
		} else {
			messagePopup("ERROR", "Login", msg.message);
		}
	});
}
/**
 * 
 * @param {String}
 *            username
 * @param {String}
 *            password
 */
function createUser(username, password) {
	$.getJSON(
			"http://" + window.location.host + "/usermanagement?create_user="
					+ username + "&password=" + password).done(
			function(msg) {

				if (msg.error) {
					messagePopup("ERROR", "Registrierung", msg.message);
				} else {
					messagePopup("INFO", "Anmelden",
							"Sie wedren weitergeleitet!");
					$.getJSON(
							"http://" + window.location.host
									+ "/usermanagement?login_user=" + username
									+ "&password=" + password).done(
							function(msgLogin) {
								if (!msgLogin.error) {
									location.reload();
								} else {
									messagePopup("ERROR", "Anmeldung",
											"Anmeldung fehlgeschlagen :-(");
								}
							});
				}
			});
}

function isUser(username) {
	console.log(username);
	$.ajax({
		type : 'GET',
		url : "http://" + window.location.host + "/usermanagement?is_user="
				+ username,
		dataType : 'json',
		success : function(msg) {
			varIsUser = msg.message == "true";
		},
		data : {},
		async : false
	});
	return varIsUser;
}

/**
 * 
 * @param {String}
 *            username
 * @param {String}
 *            password1
 * @param {String}
 *            password2
 * @returns Boolean
 * 
 */
function isValidInput(username, password1, password2) {
	if (username.length < 4 || username > 32) {
		return false;
	}
	if (password1 != password2) {
		return false;
	} else if (password1.length < 2 || password1.length > 64) {
		return false;
	}
	return true;
}
function deleteUser(password) {
	$.getJSON(
			"http://" + window.location.host + "/usermanagement?delete_user="
					+ password).done(function(msg) {
		messagePopup("INFO", "Abmelden", msg.message);
		if (!msg.error) {
			logout();
		}
	});
}
function deleteUserPrompt() {
	var submit = function() {
		deleteUser(input.value);
	};

	var prompt = document.createElement("div");
	prompt.setAttribute("id", "deleteuser_prompt");
	var title = document.createElement("h2");
	title.innerHTML = "Benutzerdaten löschen";
	prompt.appendChild(title);

	var warning = document.createElement("p");
	warning.innerHTML = "Durch das Löschen der Nutzerdaten werden alle von Ihnen erstellten Tafeln mitgelöscht. Diese Aktion ist unwiderruflich!";
	prompt.appendChild(warning);

	var label = document.createElement("label");
	label.setAttribute("id", "password_prompt_label");
	label.setAttribute("for", "password_prompt_input");
	label.innerHTML = "Passwort";
	prompt.appendChild(label);

	var input = document.createElement("input");
	input.setAttribute("id", "password_prompt_input");
	input.setAttribute("type", "password");
	input.addEventListener("keyup", function(e) {
		if (event.keyCode == 13)
			submit();
	}, false);
	prompt.appendChild(input);

	var button = document.createElement("button");
	button.innerHTML = "Benutzerdaten löschen!";
	button.addEventListener("click", submit, false);
	prompt.appendChild(button);

	var cancleButton = document.createElement("button");
	cancleButton.setAttribute("type", "reset");
	cancleButton.innerHTML = "Abbrechen";
	cancleButton.addEventListener("click", function() {
		document.body.removeChild(prompt);
	}, false);
	prompt.appendChild(cancleButton);

	document.body.appendChild(prompt);

};

