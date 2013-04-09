jQuery(document).ready(function() {

	jQuery("span.emailEncode").each(function(i, v) {
		var data_encoded = $(this).attr("data-encoded");
		if (data_encoded) {
			$(this).text(unescape(data_encoded));
			$(this).attr("data-encoded", "");
		}
	});
});