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

module.exports = new ContactCreator();
