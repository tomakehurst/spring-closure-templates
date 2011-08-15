var APP = {}

APP.words = []

APP.pushNewWord = function() {
	var newWord = $('#newWordTextField').val();
	APP.words.push(newWord);
	$('#clientWords').html(com.tomakehurst.clientWords({ words: APP.words }));
}

$(document).ready(function() {
	$('#getServerTimeLink').click(function() {
		$.ajax({
		  url: "/spring-closure-templates-example/app/server-time",
		  context: document.body,
		  success: function(data) {
		    $('#serverTime').html(data);
		  }
		});
	});
	
	$('#submitButton').click(APP.pushNewWord);
	
	$('#wordsForm').submit(function() {
		APP.pushNewWord();
		return false;
	});
});