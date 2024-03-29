
var AliPush = function() {};

AliPush.prototype.init = function(options,success, fail) {
	if (!options) {
		options = {account:''};
	}
	var params = {
		account: options.account,
	};

	return cordova.exec(success, fail, "aliPushClient", "init", [params]);
};

AliPush.prototype.initstate = function(success, fail) {
	return cordova.exec(success, fail, "aliPushClient", "initstate", [{}]);
};

AliPush.prototype.registerNotify = function(success, fail) {
	return cordova.exec(success, fail, "aliPushClient", "registerNotify", [{}]);
};
AliPush.prototype.getMessage = function(success, fail) {
	return cordova.exec(success, fail, "aliPushClient", "getMessage", [{}]);
};
AliPush.prototype.unbind = function(success, fail) {
	return cordova.exec(success, fail, "aliPushClient", "unbind", [{}]);
};

window.AliPush = new AliPush();