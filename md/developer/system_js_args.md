# System js args


Add arg on CommandClick's certain action for [js](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsArgs/get.md).  
For example, `urlHistoryClick`, `onAutoExec` action.  



Table
-----------------
* [urlHistoryClick](#urlhistoryclick)
* [onAutoExec](#onautoexec)
  

## `urlHistoryClick

On click [url history](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#url-history), add this arg.  


- usecase
  
```js.js

let args = jsArgs.get().split("\t");
const firstArgs = args.at(0); // urlHistoryClick

switch(firstArgs){
  case "urlHistoryClick"
    alert("url history click");
    break
};

```


## `onAutoExec

When set `ON` to [oAutoExec](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#onautoexec) in [setting variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#setting-variable), add this arg.  



- usecase
  
```js.js

let args = jsArgs.get().split("\t");
const firstArgs = args.at(0); // onAutoExec
switch(firstArgs){
  case "onAutoExec"
    alert("on auto exec");
    break
};

```

- Mainly, to be used for preprocessing 

