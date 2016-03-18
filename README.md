Contact creator for Android
==========================
This plugin allows you to insert a new contact into your Android contactlist by starting a new intent and opening the contactlist application.
The regular cordova-plugin-contacts does not start a new intent so you can't edit the information you're saving. With this plugin you can.

Requirements
-------------
- Android 4.3 or higher
- Cordova 3.0 or higher

    Installation
-------------
    cordova plugin add nl-afas-cordova-plugin-contactcreator

Usage
------

	You can pass the following contact JSON object to the plugin:
	
		{ 
			displayName: '<display name>',
			firstName: '<first name>',
			lastName: '...',
			phoneWork: '',
			phoneHome: '',
			mobileWork: '',
			mobileHome: '',
			emailWork: '',
			emailHome: '',
			street: '',
			city: '',
			postCode: '',
			houseNumber: '',
			country: ''
		}

	You can then start the activity by using:
	
		cordova.plugins.ContactCreator.addContact(contact, success, failed)


LICENSE
--------
The MIT License (MIT)

Copyright (c) 2016 steffanmitrovic@live.nl Steffan Mitrovic AFAS Software BV - s.mitrovic@afas.nl


Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
