
var page = require('webpage').create()
    system = require('system');

if (system.args.length === 1) {
    console.log('Usage: loadPage.js <some URL>');
    // TODO does this indicate an error?
    phantom.exit(1);
}

address = system.args[1];
page.open(address, function() {
    page.render('todomvc.png');
    phantom.exit();
});
console.log('Cannot open address ' + address);

