/**
 * Single Sign On client.
 * 
 * @param baseUrl
 *            base URL of the SSO realm controller.
 */
function SSO(baseUrl) {
    var iframe = document.createElement("iframe");
    iframe.src = baseUrl + "/view/sso.Frame";
    var deferred = $.Deferred();
    
    if (iframe.attachEvent){
        iframe.attachEvent("onload", function() {
            deferred.resolve(iframe.contentWindow);
        });
    } else {
        iframe.onload = function() {
            deferred.resolve(iframe.contentWindow);
        };
    }
    document.body.appendChild(iframe);
    
    this.invoke = function(message, callback) {
        deferred.done(function(contentWindow) {
            var handler = function(event) {
                var data = JSON.parse(event.data);
                if(data.status === "success") {
                    callback(data.response);
                } else {
                    callback({
                        status : "internal_error"
                    });
                }
                window.removeEventListener("message", handler);
            };
            window.addEventListener("message", handler);
            contentWindow.postMessage(JSON.stringify(message), baseUrl); 
        });
    };
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
    this.invoke({
        action : "Ticket"
    }, function(response) {
        if(response.status === "success") {
            document.cookie = "org.objectledge.web.sso.ticket=" + response.ticket + '; path=/';
            callback(response.status, response.uid);
        } else {
            callback(response.status);
        }
    });
}

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
SSO.prototype.login = function(login, password, callback) {
    this.invoke({
        action : "Login",
        login : login,
        password : password
    }, function(response) {
        if(response.status === "success") {
            document.cookie = "org.objectledge.web.sso.ticket=" + response.ticket + '; path=/';
            callback(response.status, login);
        } else {
            callback(response.status);
        }
    });    
}