# settingButtonMenuConfig.js

Table
-----------------
<!-- vim-markdown-toc GFM -->

* [Overview](#overview)
* [Config key](#config-key)
	* [jsPath](#jspath)
	* [Setting key](#setting-key)
* [Example](#example)


## Overview

Config for [Setting button](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#settings) menu

- Use default config in no set

## Config key 

| Key name | Description | 
| --------- | --------- | 
| `name` | menu name | 
| `icon` | [Optional] [pre reserved icon names](https://github.com/puutaro/CommandClick/blob/master/md/developer/collection/icons.md) |
| `jsPath` | [Optional] js file path |
| `parentName` | [Optional] parent menu name when sub menu |
| `extra` | [Optional] setting for long click |

- Concat by `,`

### jsPath

Js path that you want to execute.  
Also use [`JS_PATH_MACRO`](https://github.com/puutaro/CommandClick/blob/master/md/developer/collection/JsPathMacro.md)


### Setting key 

Config key include this keys

| Key name | Description | 
| --------- | --------- | 
| `parentDirPath` | parent dir path | 
| `compPrefix` | complete prefix string |
| `compSuffix` | complete suffix string |
| `broadcastAction` | -> [broadcast action name](https://github.com/puutaro/CommandClick/blob/master/md/developer/broadcastActoins.md) |
| `broadcastSchemas` | -> [broadcast shemas](https://github.com/puutaro/CommandClick/blob/master/md/developer/broadcastActoins.md) |

- Concat by `!`
- braadcast scheme concat by `!` like `broadcastSchemas=${schema1}=value1!${schema2}=value2`



## Example

- settingButtonConfig.js

```js.js
name=sync
|icon=update
|jsPath=SYNC
|extra=
	broadcastAction=`${UPDATE_LIST_INDEX_BROADCAST_ACTION}`,
name=add app dir
|icon=plus
|jsPath=ADD_APP_DIR
|extra=
	!broadcastAction=`${UPDATE_LIST_INDEX_BROADCAST_ACTION}`,

```
