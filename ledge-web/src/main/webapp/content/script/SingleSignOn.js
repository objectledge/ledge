/**
 * Single Sign On client.
 * 
 * @param baseUrl
 *            base URL of the SSO realm controller.
 */
function SSO(baseUrl) {
	this.ticketUrl = baseUrl + "/view/sso.Ticket";
	this.loginUrl = baseUrl + "/view/sso.Login";
}

/**
 * Attempt migrating existing SSO realm session into current domain.
 * 
 * @param ticketUrl
 *            the URL of sso.Ticket view, as returned by $ssoTool.ticketUrl()
 * @param callback
 *            a function that will be invoked with single boolean parameter
 *            indicating if session migration was successful or not. Values are
 *            'success', 'not_logged_id', 'invalid_request', 'internal_error'
 */
SSO.prototype.migrateSession = function(callback) {
	$.ajax({
		url : this.ticketUrl,
		beforeSend: function(xhr){
		    xhr.withCredentials = true;
		},
		type : "POST",
		dataType : "json",
		success : function(data) {
			if (data && data.status && data.status == "success") {
				document.cookie = "org.objectledge.web.sso.ticket=" + data.ticket + '; path=/';
				callback(data.status, data.uid);
				return;
			} else {
				if (data && data.status) {
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
};

/**
 * Attempt a login into SSO realm.
 * 
 * @param loginUrl
 *            the URL of sso.Login view, as returned by $ssoTool.loginUrl()
 * @param callback
 *            a function that will be invoked with single string parameter
 *            indicating if login attempt was successful or not. Values are
 *            'success', 'invalid_credentials', 'invalid_request',
 *            'internal_error'
 */
SSO.prototype.login = function(vlogin, vpassword, callback) {
	$.ajax({
		url : this.loginUrl,
		dataType : "jsonp",
		data : {
			login : vlogin,
			password : vpassword
		},
		success : function(data) {
			if (data && data.status && data.status == "success") {
				document.cookie = "org.objectledge.web.sso.ticket=" + data.ticket + '; path=/';
				callback(data.status, vlogin);
				return;
			} else {
				if (data && data.status) {
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
};
