sso = {
	/**
	 * Attempt migrating existing SSO realm session into current domain.
	 * 
	 * @param ticketUrl
	 *            the URL of sso.Ticket view, as returned by
	 *            $ssoTool.ticketUrl()
	 * @param callback
	 *            a function that will be invoked with single boolean parameter
	 *            indicating if session migration was successful or not. Values are
	 *            'success', 'not_logged_id', 'invalid_request', 'internal_error'
	 */
	migrateSession : function(ticketUrl, callback) {
		$.ajax({
			url : ticketUrl,
			dataType : "jsonp",
			success : function(data) {
				if (data && data.status && data.status == "success") {
					document.cookie = "org.objectledge.web.sso.ticket="
							+ data.ticket;
					callback(data.status);
					return;
				} else {
					if(data && data.status) {
						callback(data.status);
						return;
					}
				}
				callback("internal_error");
			},
			error : function(jqXHR, textStatus, errorThrown) {
				callback("internal_error");
			}
		});
	},

	/**
	 * Attempt a login into SSO realm.
	 * 
	 * @param loginUrl
	 *            the URL of sso.Login view, as returned by $ssoTool.loginUrl()
	 * @param callback
	 *            a function that will be invoked with single string parameter
	 *            indicating if login attempt was successful or not. Values are
	 *            'success', 'invalid_credentials', 'invalid_request', 'internal_error'
	 */
	login : function(loginUrl, vlogin, vpassword, callback) {
		$.ajax({
			url : loginUrl,
			dataType : "jsonp",
			data : {
				login : vlogin,
				password : vpassword
			},
			success : function(data) {
				if (data && data.status && data.status == "success") {
					document.cookie = "org.objectledge.web.sso.ticket="
							+ data.ticket;
					callback(data.status);
					return;
				} else {
					if(data && data.status) {
						callback(data.status);
						return;
					}					
				}
				callback("internal_error");
			},
			error : function(jqXHR, textStatus, errorThrown) {
				callback("internal_error");
			}
		});
	}
};