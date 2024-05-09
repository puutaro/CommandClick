# settingButtonConfig.js

Table
-----------------
<!-- vim-markdown-toc GFM -->

* [Overview](#overview)
* [Config key](#config-key)
	* [Setting key](#config-key)
* [Example](#example)


## Overview

Config for [Setting button](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#settings) feature

- Use default config in no set

## Config key 

| Key name | Description | 
| --------- | --------- | 
| `click` | setting for click | 
| `longClick` | setting for long click |
| `icon` | [Optional] [pre reserved icon names](https://github.com/puutaro/CommandClick/blob/master/md/developer/collection/icons.md) |

- `${Config key}=` -> Mean disable this click.
- Concat by `,`

### Setting key 

| Key name | Description | 
| --------- | --------- | 
| `func` | execute js path macro | 
| `menuPath` | menu config path |
| `onHideFooter` | hide footer in menu |

- Concat by `|`


## Example

- settingButtonConfig.js

```js.js
longClick=
	func=MENU
	|menuPath=`${setting button menu config js path1}`
	|onHideFooter=,
click=
	func=MENU
	|menuPath=`${setting button menu config js path1}`
	|onHideFooter=,
icon=plus,

```
