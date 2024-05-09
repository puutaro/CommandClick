# listIndexConfig.jd


Table
-----------------
<!-- vim-markdown-toc GFM -->

* [Overview](#overview)
* [Config key](#config-key)
	* [ListIndexFileNameKey](#listindexfilenamekey)
	* [ListIndexDescKey](#listindexdesckey)
  * [shellPath example](#shellPath-example) 
* [Example](#example)


## Overview

Config for [list index mode](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types/list_index.md) in edit's [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md)



## Config key 

| Key name | Description | 
| --------- | --------- | 
| `name` | file name | 
| `desc` | description setting in cardview |
| `installFannel` | only for install fannel repository |


- Concat by `,`


### ListIndexFileNameKey

| Key name | Description | 
| --------- | --------- | 
| `onHide` | hide file name | 
| `removeExtend` | remove extend |
| `compPrefix` | comp prefix |
| `compSuffix` | comp suffix |
| `shellPath` | make file name by shell |


### ListIndexDescKey


| Key name | Description | 
| --------- | --------- | 
| `length` | hide file name | 
| `shellPath` | remove extend |
| `compPrefix` | comp prefix |
| `compSuffix` | comp suffix |
| `shellPath` | make file name by shell: `FILE_NAME_CON_MARK` is file name value |

- Concat by `!`


### shellPath example

```sh.sh
echo "${EDIT_TARGET_CONTENTS}" | ${b} awk '{print $0}'
```

- `${EDIT_TARGET_CONTENTS}` -> Auto replace with edit target contents
- `${b}` -> busybox symlink path

## Example

- settingButtonConfig.js

```js.js
name=sync
|icon=update
|func=SYNC
|args=
	broadcastAction=`${UPDATE_LIST_INDEX_BROADCAST_ACTION}`,
name=add app dir
|icon=plus
|func=ADD_APP_DIR
|args=
	!broadcastAction=`${UPDATE_LIST_INDEX_BROADCAST_ACTION}`,

```
