
# List index option


Table
-----------------
* [Overview](#overview)
* [Usage](#usage)
* [Directory structrue](#directory-structrue)
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


## Directory structrue  

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

- `itemclick.js` option  
`const override = true;` -> when click, your handling when js and shell don't execute.  


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
| `editC` | edit command variables |
| `editS` | edit setting variables |

