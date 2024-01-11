
# List index option


Table
-----------------
* [Overview](#overview)
* [Usage](#usage)
* [Directory structrue (mvc version)](#directory-structrue-mvc)
* [Directory structrue (mvvm version)](#directory-structrue-mvvm)
* [Pre reserved menu name](#pre-reserved-menu-name)


## Overview

This option transform `command variable` into list index.


## Usage  

```js.js
setVariableTypes={variableName}:LI=listDir={target list dir path}|menu={menuName1}(&subMenuName1&subMenuName2..}!{menuName2}(&subMenuName21&subMenuName22..}(|prefix={grep prefix})(|suffix={grep suffix})
```

- example
  
<img src="https://github.com/puutaro/CommandClick/assets/55217593/7a73a987-a71a-461a-8f54-12eea684b162" width="500">  

- This option must be one and other component is exclude by this.  
- Above list is script name in `{target list dir path}`  
- Enable to use [file prefix](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md#specify-file-path): `file://`

## Directory structrue (mvc version) <a id="directory-structrue-mvc"></a>

```
${target fannel dirctory}
└── click
    ├── itemClick.js
    ├── menuClick.js
    └── subMenuClick.js
```

- `itemClick.js` is trigger when item click  
- `menuClick.js` is trigger when menu click  
- `subMenuClick.js` is trigger when subMenu click  
- `itemClick.js`, `menuClick.js`, and `subMenuClick.js` have bellow argument:
   
```js.js 
	     let args = jsArgs.get().split("\t");   
	     var PARENT_DIR = args.at(0);  
	     var LIST_VIEW_DIR = args.at(1);  
	     var ITEM_NAME = args.at(2);  
	     var MENU_NAME = args.at(3);
```


## Directory structrue (mvvm version) <a id="directory-structrue-mvvm"></a>

(setVariableTypes)

```js.js
setVariableTypes={variableName}:LI=listDir={target list dir path}|menu=mvvmMenu1!{menuName1}&mvvmMenu1&mvvmMenu2!menuName2&subMenuName2&subMenuName2
```

```
${target fannel dirctory}
└── click
    ├── itemClick.js
    ├── menuClick.js
    ├── subMenuClick.js
    ├
    menu
    ├── mvvmMenu1.js
    ├── subMvvmMenu1.js
    └── subMvvmMenu2.js
```

- `itemClick.js` is trigger when item click  
- `menuClick.js` is trigger when menu click  
- `subMenuClick.js` is trigger when subMenu click  
- `itemClick.js`, `menuClick.js`, and `subMenuClick.js` have bellow argument:
   
```js.js 
	     let args = jsArgs.get().split("\t");   
	     var PARENT_DIR = args.at(0);  
	     var LIST_VIEW_DIR = args.at(1);  
	     var ITEM_NAME = args.at(2);  
	     var MENU_NAME = args.at(3);
```


## Pre reserved menu name  

| name| description  |
| --------- | --------- |
| `sync` | sync list to current directory files |
| `delete` | delete selected script |
| `add` | add script |
| `write` | edit item by editor  |
| `cat` | show time file contents  |
| `desc` | how description |
| `copy_path` | copy item file path |
| `copy_file` | copy item file to other directory |
| `copy_file_here` | copy item file to current directory |
| `editC` | edit command variables |
| `editS` | edit setting variables |
| `scanQR` | scan qr code |
| `getQR` | get qr code contents |

