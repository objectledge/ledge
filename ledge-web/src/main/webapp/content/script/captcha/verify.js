function initCaptchaVerify(fieldName, url, form, msg) {
    $("#" + fieldName).click(function() {
        captchaVerify(url, form, msg);
    });
}

function captchaResponseCallback(responce) {
    if (jQuery("#recaptcha_response_field").size() > 0) {
        jQuery("#recaptcha_response_field").val(responce);
    }
}

function captchaVerify(url, form, msg) {
    var challenge = jQuery("#recaptcha_challenge_field").val() || "";
    var response = jQuery("#recaptcha_response_field").val() || "";
    var api_version = jQuery("#recaptcha_api_version").val() || "";
    jQuery.get(url, {
        'recaptcha_challenge_field' : challenge,
        'recaptcha_response_field' : response,
        'recaptcha_api_version' : api_version
    }, function(data) {
        captchaCallback(data, form, msg);
    }, "json");
}

function captchaCallback(data, form, msg) {
    if (data.result) {
        form.submit();
    } else {
        if (jQuery("#add_comment_error").size() > 0) {
            jQuery("#add_comment_error").html(msg);
        } else {
            alert(msg);
        }
        if (Recaptcha) {
            Recaptcha.reload();
        }
        if (grecaptcha) {
            grecaptcha.reset();
        }
    }
}