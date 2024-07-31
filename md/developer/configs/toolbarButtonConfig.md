# toolbarButtonConfig.js

Table
-----------------
<!-- vim-markdown-toc GFM -->

* [Overview](#overview)
* [How to specify](#how-to-specify)
* [Config key ](#config-key-)
	* [click](#click)
		* [Ex for click](#ex-for-click)
		* [Js action macro](#js-action-macro)
	* [Setting key ](#setting-key-)
* [Example](#example)
* [settingimport](#settingimport)

## Overview

Config for toolbar button feature

- Use default config in no set

## How to specify


Specify by setting variables([playButtonConfig]((https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#playbuttonconfig)),[editButtonConfig]((https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#editbuttonconfig)), [settingButtonConfig]((https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#settingbuttonconfig)), [extraButtonConfig]((https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#extrabuttonconfig))) in fannel


```js.js
/// SETTING_SECTION_START
playButtonConfig="file://${toolbar button config path1}"
editButtonConfig="file://${toolbar button config path2}"
settingButtonConfig="file://${toolbar button config path3}"
extraButtonConfig="file://${toolbar button config path4}"
/// SETTING_SECTION_END
```

- `${toolbar button config path1}` con

```js.js
color=darkGreen,
disable=OFF,

click=
    func=D_MENU
        ?args=
            menuPath=
                `${cmdTtsPlayerTableSettingMenuConfigPath}`
                &title="Setting menu",

```
- `${toolbar button config path2}` ex

```js.js
visible=ON,

disable=OFF,
    color=darkGreen,

click=
    acVar=runToTableState
        ?importPath=`${cmdMusicPlayerChangeStateAction}`
        ?replace=
            STATE=`${TABLE}`,

```

- `${toolbar button config path3}` ex

```js.js
.  
.  
.  
```

- `${toolbar button config path4}` ex

```js.js
.  
.  
.  
```


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


## settingimport

Import enable to this config, -> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/settingImport.md)

