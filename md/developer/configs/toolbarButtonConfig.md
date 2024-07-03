# toolbarButtonConfig.js

Table
-----------------
<!-- vim-markdown-toc GFM -->

* [Overview](#overview)
* [Config key ](#config-key-)
	* [click](#click)
		* [Ex for click](#ex-for-click)
		* [Js action macro](#js-action-macro)
	* [Setting key ](#setting-key-)
* [Example](#example)


## Overview

Config for toolbar button feature

- Use default config in no set

## Config key 

| Key | Value            | Description                                                                                                     | 
|-------------|------------------|-----------------------------------------------------------------------------------------------------------------| 
| `click`     | js action        | setting for click                                                                                               | 
| `longClick` | js action        | setting for long click                                                                                          |
| `icon`      | icon name macro  | [pre reserved icon names](https://github.com/puutaro/CommandClick/blob/master/md/developer/collection/icons.md) |
| `visible`   | `ON` / `OFF`     | Switch for visiblity                  |
| `disable`   | `ON` / `OFF`     | Disable switch               |
| `color`     | color name macro | [color name](https://github.com/puutaro/CommandClick/blob/master/md/developer/collection/color.md)              |

- `${Config key}=` -> Mean disable this click.
- Concat by `,`



### click

Trigger on toolbar certain button

#### Ex for click

##### `OK` macro case

```js.js
  click=
      func=COPY_FILE_SIMPLE
          ?args=
              copyDestiTsvPathCon=`
                      $COPY_DEST_DIR_PATH1}\ttsv\n
                      $COPY_DEST_DIR_PATH1}\ttsv\n
                  `
              &extra=
                withFile="ON"
```

##### Js action case

```js.js

click=
    |var=ttsFileName
        ?func=jsPath.basename
        ?args=
            path="${ITEM_NAME}"
    |var=palyInfo
        ?func=jsFileSystem.read
        ?args=
            path="${cmdTtsPlayerPlayInfoPath}"
    |acVar=runCurRecordPlay
        ?importPath=
            `${cmdTtsPlayerTtsAction}`
        ?replace=
            TEMP_PLAY_CON=
                `${ITEM_NAME}`
            &EXTRA_CONTENT=
                `${palyInfo} ${ttsFileName}`
```


#### Js action macro

-> [Js action macro for toolbar](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md)



### Setting key 

| Key name        | Description            | 
|-----------------|------------------------| 
| `func`          | execute js path macro  | 
| `menuPath`      | menu config path       |
| `onHideFooter`  | hide footer in menu    |

- Concat by `|`


## Example

- settingButtonConfig.js

```js.js
longClick=
	func=MENU
		?args=
			menuPath=`${setting button menu config js path1}`
	|onHideFooter=,
click=
	func=MENU
		?args=
			menuPath=`${setting button menu config js path1}`
	|onHideFooter=,
icon=plus,

```
