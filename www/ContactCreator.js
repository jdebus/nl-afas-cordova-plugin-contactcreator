cordova.define("nl-afas-cordova-plugin-contactcreator.ContactCreator", function(require, exports, module) {
/**
 * @constructor
 */
var ContactCreator = function() {};

ContactCreator.prototype.addContact = function(contact, success, failure) {		
	cordova.exec(
	    success, 
	    failure, 
	    "ContactCreator",
	    "addContact", 
	    [contact]);
};

ContactCreator.prototype.getAccount = function(success, failure) {		
	cordova.exec(
	    success, 
	    failure, 
	    "ContactCreator",
	    "getAccount");
};


module.exports = new ContactCreator();

});
