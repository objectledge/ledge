jQuery(document).ready(function() {
	EmailTool.encodeSafe(document);
});

EmailTool = function() { };

EmailTool.encodeSafe = function ( context ) {

	jQuery("span[class=emailEncode]",jQuery(context)).each(function() {
		var data_encoded = $(this).attr("data-encoded");
		if (data_encoded) {
			$(this).text(unescape(data_encoded));
			$(this).attr("data-encoded", "");
		}
	});

};