
let args = jsArgs.get().split("\t");
var ITEM_NAME = args.at(2);

if(!ITEM_NAME) exitZero();
jsFannelInstaller.install(ITEM_NAME);
