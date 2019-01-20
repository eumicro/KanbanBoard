(function() {
	// function naviklapp(navid, board) {
	// // Element definion: Navigationelement, Button, Menu
	// var nav = document.getElementById(navid);
	// var board = document.getElementById('baords_view');
	// var button;
	// var menu;
	//
	// // If no navigation, do nothing
	// if (!nav) {
	// return;
	// }
	//
	// // First button element inside the <aside>
	// button = nav.getElementsByTagName('button')[0];
	//
	// // First unorder list inside the menu
	// menu = nav.getElementsByTagName('ul')[0];
	// if (!button) {
	// return;
	// }
	//
	// // If we don't had this elements, do nothing
	// if (!menu || !menu.childNodes.length) {
	// button.style.display = 'none';
	// return;
	// }
	//
	// // Function to insert or delete the toggled-on class
	// button.onclick = function() {
	//
	// if (-1 != button.className.indexOf('toggled-on')) {
	// button.className = button.className.replace('toggled-on', '');
	// menu.className = menu.className.replace('toggled-on', '');
	// board.className = board.className.replace('toggled-on', '');
	// } else {
	// button.className += ' toggled-on';
	// menu.className += ' toggled-on';
	// board.className += ' toggled-on';
	// }
	// };
	// }
	// naviklapp('rightnavi');
	$('#menu_button').click(function() {
		$('#menu_ul').slideToggle();

	});
})();