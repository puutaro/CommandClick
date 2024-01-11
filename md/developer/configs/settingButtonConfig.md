# settingButtonConfig.js

Table
-----------------
<!-- vim-markdown-toc GFM -->

* [Overview](#overview)
* [Config key](#config-key)
* [Setting key](#config-key)
* [Config key](#config-key)
* [Example](#example)


## Overview

Config for [Setting button](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#settings) feature

- Use default config in no set

## Config key 

| Key name | Description | 
| --------- | --------- | 
| `click` | setting for click | 
| `longClick` | setting for long click |

- `${Config key}=` -> Mean disable this click.


## Setting key 

| Key name | Description | 
| --------- | --------- | 
| `jsPath` | execute js path macro | 
| `menuPath` | menu config path |
| `onHideFooter` | hide footer in menu |

- concat by `|`


## Example

- settingButtonConfig.js

```js.js
longClick=
	jsPath=MENU
	|menuPath=`${setting button menu config js path1}`
	|onHideFooter=,
click=
	jsPath=MENU
	|menuPath=`${setting button menu config js path1}`
	|onHideFooter=,

```
