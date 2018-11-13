var exec = require('cordova/exec');

exports.fastPrint = function(arg0, success, error) {
    exec(success, error, 'CAPrinter', 'fastPrint', [arg0]);
    window.alert('Hello');
};