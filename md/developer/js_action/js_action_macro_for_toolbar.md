# js action macro for toolbar


js action macro is certain js process macro for list logo

Table
-----------------
<!-- vim-markdown-toc GFM -->

* [ADD](#add)
  * [args for ADD](#args-for-add)
    * [titleArgs in args in ADD](#titleargs-in-args-in-add)
    * [marco table in titleArgs in ADD](#marco-table-in-titleargs-in-add)
  * [js interface supporting ADD](#js-interface-supporting-add)
  * [Ex for ADD](#ex-for-add)
* [ADD_URL](#add_url)
  * [type support table for ADD_URL](#type-support-table-for-add_url)
  * [args for ADD_URL](#args-for-add_url)
    * [url marco table in ADD_URL](#url-marco-table-in-add_url)
  * [js interface supporting ADD_URL](#js-interface-supporting-add_url)
  * [Ex for ADD_URL](#ex-for-add_url)
* [ADD_URL_CON](#add_url_con)
  * [args for ADD_URL_CON](#args-for-add_url_con)
    * [url marco table in ADD_URL_CON](#url-marco-table-in-add_url_con)
  * [js interface supporting ADD_URL_CON](#js-interface-supporting-add_url_con)
  * [Ex for ADD_URL_CON](#ex-for-add_url_con)
* [ADD_GMAIL_CON](#add_gmail_con)
  * [args for ADD_GMAIL_CON](#args-for-add_gmail_con)
    * [Key for extraMap in ADD_GMAIL_CON](#key-for-extramap-in-add_gmail_con)
  * [js interface supporting ADD_GMAIL_CON](#js-interface-supporting-add_gmail_con)
  * [Ex for ADD_GMAIL_CON](#ex-for-add_gmail_con)
* [ADD_URL_HISTORY](#add_url_history)
  * [js interface supporting ADD_URL_HISTORY](#js-interface-supporting-add_url_history)
  * [Ex for ADD_URL_HISTORY](#ex-for-add_url_history)
* [EDIT](#edit)
  * [type support table for EDIT](#type-support-table-for-edit)
  * [js interface supporting EDIT](#js-interface-supporting-edit)
  * [Ex for EDIT](#ex-for-edit)
* [GET_FILE](#get_file)
  * [args for GET_FILE](#args-for-get_file)
  * [macro for GET_FILE](#macro-for-get_file)
  * [js interface supporting GET_FILE](#js-interface-supporting-get_file)
  * [Ex for GET_FILE](#ex-for-get_file)
* [GET_DIR](#get_dir)
  * [type support table for GET_DIR](#type-support-table-for-get_dir)
  * [args for GET_DIR](#args-for-get_dir)
  * [macro for GET_DIR](#macro-for-get_dir)
  * [js interface supporting GET_DIR](#js-interface-supporting-get_dir)
  * [Ex for GET_DIR](#ex-for-get_dir)
* [GET_FILES](#get_files)
  * [args for GET_FILES](#args-for-get_files)
  * [macro for GET_FILES](#macro-for-get_files)
  * [js interface supporting GET_FILES](#js-interface-supporting-get_files)
  * [Ex for GET_FILES](#ex-for-get_files)
* [GET_DIRS](#get_dirs)
  * [args for GET_DIRS](#args-for-get_dirs)
  * [macro for GET_DIRS](#macro-for-get_dirs)
  * [js interface supporting GET_DIRS](#js-interface-supporting-get_dirs)
  * [Ex for GET_DIRS](#ex-for-get_dirs)
* [GET_QR_CON](#get_qr_con)
  * [args for GET_QR_CON](#args-for-get_qr_con)
  * [js interface supporting GET_QR_CON](#js-interface-supporting-get_qr_con)
  * [Ex for GET_QR_CON](#ex-for-get_qr_con)
* [KILL](#kill)
  * [js interface supporting KILL](#js-interface-supporting-kill)
  * [Ex for KILL](#ex-for-kill)
* [MENU](#menu)
  * [MENU args](#menu-args)
  * [js interface supporting MENU](#js-interface-supporting-menu)
  * [MENU ex](#menu-ex)
* [D_MENU](#d_menu)
  * [D_MENU args](#d_menu-args)
  * [D_MENU ex](#d_menu-ex)
* [NORMAL](#normal)
  * [NORMAL ex](#normal-ex)
* [PAGE_SEARCH](#page_search)
  * [PAGE_SEARCH ex](#page_search-ex)
* [WEB_SEARCH](#web_search)
  * [WEB_SEARCH ex](#web_search-ex)
* [NO_SCROLL_SAVE_URL](#no_scroll_save_url)
  * [NO_SCROLL_SAVE_URL ex](#no_scroll_save_url-ex)
* [OK](#ok)
  * [OK ex](#ok-ex)
* [QR_SCAN](#qr_scan)
  * [js interface supporting QR_SCAN](#js-interface-supporting-qr_scan)
  * [QR_SCAN ex](#qr_scan-ex)
* [REFRESH_MONITOR](#refresh_monitor)
  * [js interface supporting REFRESH_MONITOR](#js-interface-supporting-refresh_monitor)
  * [REFRESH_MONITOR ex](#refresh_monitor-ex)
* [RESTART_UBUNTU](#restart_ubuntu)
  * [js interface supporting RESTART_UBUNTU](#js-interface-supporting-restart_ubuntu)
  * [RESTART_UBUNTU ex](#restart_ubuntu-ex)
* [SIZING](#sizing)
  * [SIZING ex](#sizing-ex)
* [SELECT_MONITOR](#select_monitor)
  * [js interface supporting SELECT_MONITOR](#js-interface-supporting-select_monitor)
  * [SELECT_MONITOR ex](#select_monitor-ex)
* [SHORTCUT](#shortcut)
  * [SHORTCUT ex](#shortcut-ex)
* [SYNC](#sync)
  * [SYNC ex](#sync-ex)
* [TERMUX_SETUP](#termux_setup)
  * [TERMUX_SETUP ex](#termux_setup-ex)
* [USAGE](#usage)
  * [USAGE ex](#usage-ex)

## ADD

Add item

### args for ADD

| Arg            | Type                     | Description                                                                                                                                                                                               |
|----------------|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `removeSuffix` | string separated by `&`  | Erase in specified suffix order                                                                                                                                                                           |
| `removePrefix` | string separated by `&`  | Erase in specified prefix order                                                                                                                                                                           |
| `compPrefix`   | string                   | comp prefix                                                                                                                                                                                               |
| `compSuffix`   | string                   | comp suffix                                                                                                                                                                                               |
| `shellPath`    | shell script path string | pre reserved word: <br> `${FILE_NAME}` -> file name or title name                                                                                                                                         |
| `dirPath`      | dir path to prepend      | complete dir path to prepend to file name <br> Enable when [type is tsvEdit]([tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type)) |
| `titleArgs`    | title args               | title setting args                                                                                                                                                                                        |

- Concat by `?`

#### titleArgs in args in ADD

| Arg            | Type                     | Description                                                        |
|----------------|--------------------------|-------------------------------------------------------------------|
| `removeSuffix` | string separated by `&`  | Erase in specified suffix order                                   |
| `removePrefix` | string separated by `&`  | Erase in specified prefix order                                   |
| `compPrefix`   | string                   | comp prefix                                                       |
| `compSuffix`   | string                   | comp suffix                                                       |
| `shellPath`    | shell script path string | pre reserved word: <br> `${FILE_NAME}` -> file name or title name |
| `macro`        | Macro                    | Macro to format title                                             |

#### marco table in titleArgs in ADD

| Macro                  | Description                             |
|------------------------|-----------------------------------------|
| `CAMEL_TO_BLANK_SNAKE` | Convert cammel case to blank snake case |


### js interface supporting ADD

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/toolbar/JsFileAdder/add.md)


### Ex for ADD

```js.js
func=ADD
?args=
    compPrefix=${TTS_PREFIX}
    &compSuffix=PlayList${TSV_SUFFIX}
    &dirPath=${cmdTtsPlayerPlayListTableDirPath}
    &titleArgs=
        "macro=CAMEL_TO_BLANK_SNAKE
        ?removeSuffix=PlayList${TSV_SUFFIX}&${TSV_SUFFIX}&List&Play
        ?compSuffix=List
        ",
```

## ADD_URL

Add title url line

### type support table for ADD_URL

| Type                                                                                                                        | Description                |
|-----------------------------------------------------------------------------------------------------------------------------|---------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type)  | None                      |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Add title url line | 

### args for ADD_URL

| Arg           | Type               | Description         |
|---------------|--------------------|---------------------|
| `url`         | marco / url string | First load site url |
| `onSearchBtn` | `OFF` / other      | Disable search btn  |

- Concat by `?`

#### url marco table in ADD_URL

| Macro       | Description        |
|-------------|-------------------|
| `RECENT`    | Recent visit url  |
| `FREQUENCY` | Most frequent url |


### js interface supporting ADD_URL

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/toolbar/JsUrlAdder/add_S.md)


### Ex for ADD_URL

```js.js
func=ADD_URL
    ?args=
        url="https://www.youtube.com/"
        &onSearchBtn=OFF
    ,
```

## ADD_URL_CON

Add url contents

### args for ADD_URL_CON

| Arg                       | Type               | Description                        |
|---------------------------|--------------------|------------------------------------|
| `url`                     | marco / url string | First load site url                |
| `onSearchBtn`             | `OFF` / other      | Disable search btn                 |
| `urlConSaveParentDirPath` | dir path           | Parent dir path for save contents  |
| `compSuffix`              | `OFF` / suffix     | Suffix to complete. ex) extend etc |
| `onSaveUrlHistory`        | `ON` / other       | Switch for saving url history      |

- Concat by `?`

#### url marco table in ADD_URL_CON

| Macro       | Description |
|-------------|------------|
| `RECENT`    | Recent visit url  |
| `FREQUENCY` | Most frequent url |


### js interface supporting ADD_URL_CON

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/toolbar/JsAddUrlCon/add_S.md)


### Ex for ADD_URL_CON

```js.js
func=ADD_URL_CON
  ?args=
    extraMapCon="
      url=RECENT
      |onSearchBtn=ON
      |urlConSaveParentDirPath=${cmdTtsPlayerSaveUrlConDirPath}
      |compSuffix=${TXT_SUFFIX}
      |onSaveUrlHistory=ON
      "
```

## ADD_GMAIL_CON

Add gmail contents

### args for ADD_GMAIL_CON

| Arg                       | Type             | Description                |
|---------------------------|------------------|----------------------------|
| `gmailAd`                     | gmail url string | First load gmail url       |
| `extraMap`                     | kye-value con    | Extra setting map contents |

- Concat by `?`

#### Key for extraMap in ADD_GMAIL_CON

| Macro       | Description      |
|-------------|------------------|
| `urlConSaveParentDirPath`    | Recent visit url |
| `compSuffix` | Most frequent url |


### js interface supporting ADD_GMAIL_CON

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/toolbar/JsAddGmailCon/add.md)


### Ex for ADD_GMAIL_CON

```js.js
func=ADD_GMAIL_CON
  ?func=jsAddGmailCon.add
  ?args=
      gmailAd=${gmailAd}
      &extraMap="
          urlConSaveParentDirPath=${cmdTtsPlayerSaveUrlConDirPath}
          |compSuffix=${TXT_SUFFIX}
```

## ADD_URL_HISTORY

Add url history to tsv

### js interface supporting ADD_URL_HISTORY

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/toolbar/JsAddToUrlHistory/add_S.md)


### Ex for ADD_URL_HISTORY

```js.jsf
func=ADD_URL_HISTORY
```

## EDIT

Edit original setting variable in [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel)

- default action in editButtonConfig

### type support table for EDIT

| Type                                                                                                                        | Description                |
|-----------------------------------------------------------------------------------------------------------------------------|---------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type)  | None                      |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Add title url line |

### js interface supporting EDIT

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/toolbar/JsAddToUrlHistory/add_S.md)


### Ex for EDIT

```js.js
func=EDIT
```

## GET_FILE

Add file from directory

### args for GET_FILE

| Arg      | Type             | Description                               |
|----------|------------------|-------------------------------------------|
| `prefix` | string separated by `&` | OR filter file or dif name by prefix list |
| `suffix` | string separated by `&`    | OR filter file or dif name                |
| `initialPath` | string separated by `&`    | Default picker directory path             |
| `macro` | string separated by `&`    | Macro for picker directory path type      |
| `tag` | string separated by `&`    | Tag for dest dir save file                |

### macro for GET_FILE

Macro for dest dir path

| type        | Description                               |
|-------------|------------------------------------------|
| `FROM_RECENT_DIR` | Get dest dir from recent select dest dir |

### js interface supporting GET_FILE

-> [detail](https://github.com/puutaro/CommandClick/blob/master/blob/master/md/developer/js_interface/functions/toolbar/JsFileOrDirGetter/get_S.md)


### Ex for GET_FILE

```js.js
func=GET_FILE
?args=
  suffix="${M4A_SUFFIX}&${MP3_SUFFIX}"
  &initialPath="${STORAGE}/Music"
  &macro=FROM_RECENT_DIR
  &tag=addByOne
```

## GET_DIR

Add dir in other directory

### type support table for GET_DIR

| Type                                                                                                                        | Description             |
|-----------------------------------------------------------------------------------------------------------------------------|-------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type)  | None                    |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Add dir to tsv |

### args for GET_DIR

| Arg      | Type             | Description                               |
|----------|------------------|-------------------------------------------|
| `prefix` | string separated by `&` | OR filter file or dif name by prefix list |
| `suffix` | string separated by `&`    | OR filter file or dif name                |
| `initialPath` | string separated by `&`    | Default picker directory path             |
| `macro` | string separated by `&`    | Macro for picker directory path type      |
| `tag` | string separated by `&`    | Tag for dest dir save file                |

### macro for GET_DIR

Macro for dest dir path

| type        | Description                               |
|-------------|------------------------------------------|
| `FROM_RECENT_DIR` | Get dest dir from recent select dest dir |

### js interface supporting GET_DIR

-> [detail](https://github.com/puutaro/CommandClick/blob/master/blob/master/md/developer/js_interface/functions/toolbar/JsFileOrDirGetter/get_S.md)


### Ex for GET_DIR

```js.js
func=GET_DIR
?args=
  suffix="addDirSuffix"
  &initialPath="${STORAGE}/Music"
  &macro=FROM_RECENT_DIR
  &tag=addByOne
```

## GET_FILES

Add files by bulk

### args for GET_FILES

| Arg      | Type             | Description                               |
|----------|------------------|-------------------------------------------|
| `prefix` | string separated by `&` | OR filter file or dif name by prefix list |
| `suffix` | string separated by `&`    | OR filter file or dif name                |
| `initialPath` | string separated by `&`    | Default picker directory path             |
| `macro` | string separated by `&`    | Macro for picker directory path type      |
| `tag` | string separated by `&`    | Tag for dest dir save file                |

### macro for GET_FILES

Macro for dest dir path

| type        | Description                               |
|-------------|------------------------------------------|
| `FROM_RECENT_DIR` | Get dest dir from recent select dest dir |

### js interface supporting GET_FILES

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/toolbar/JsFileOrDirListGetter/get_S.md)


### Ex for GET_FILES

```js.js
func=GET_FILES
  ?args=
    suffix="${M4A_SUFFIX}&${MP3_SUFFIX}"
    &initialPath="${STORAGE}/Music"
    &macro=FROM_RECENT_DIR
    &tag=addByBulk
```

## GET_DIRS

Add dirs by bulk

### args for GET_DIRS

| Arg      | Type             | Description                               |
|----------|------------------|-------------------------------------------|
| `prefix` | string separated by `&` | OR filter file or dif name by prefix list |
| `suffix` | string separated by `&`    | OR filter file or dif name                |
| `initialPath` | string separated by `&`    | Default picker directory path             |
| `macro` | string separated by `&`    | Macro for picker directory path type      |
| `tag` | string separated by `&`    | Tag for dest dir save file                |

### macro for GET_DIRS

Macro for dest dir path

| type        | Description                               |
|-------------|------------------------------------------|
| `FROM_RECENT_DIR` | Get dest dir from recent select dest dir |

### js interface supporting GET_DIRS

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/toolbar/JsFileOrDirListGetter/get_S.md)


### Ex for GET_DIRS

```js.js
func=GET_DIRS
  ?args=
    &initialPath="${STORAGE}/Music"
    &macro=FROM_RECENT_DIR
    &tag=addByBulk
```

## GET_QR_CON

Add QR con by scanner

### args for GET_QR_CON

| Arg          | Type                    | Description                                                               |
|--------------|-------------------------|---------------------------------------------------------------------------|
| `compPrefix` | string separated by `&` | Comp prefix                                                               |
| `compSuffix` | string separated by `&` | Comp suffix                                                               |
| `parentDirPath`     | dir path                | Parent dir path for qr con <br> If not specify, this is currentAppDirPath |


### js interface supporting GET_QR_CON

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/qr/JsQrGetter/get_S.md)


### Ex for GET_QR_CON

```js.js
func=GET_QR_CON
  ?args=
    &compSuffix=".txt"
```

## KILL

Kill process

### js interface supporting KILL

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/toolbar/JsProcessKiller/kill_S.md)


### Ex for KILL

```js.js
func=KILL
```

## MENU

Launch menu

- default action in settingButtonConfig

### MENU args

| arg        | value  | Description                                                                                                 |
|------------|--------|------------------------------------------------------------------------------------------------------------|
| `menuPath` | path   | [Menu config path](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/menuConfig.md) |
| `title`    | string | Title                                                                                                      |
| `onHideFooter` | -      | Hide footer on setting this key                                                                            |

### js interface supporting MENU

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/toolbar/JsProcessKiller/kill_S.md)


### MENU ex

```js.js
|func=MENU
  ?args=
    menuPath=`${MENU_PATH}`
    &onHideFooter=,
```

## D_MENU

Launch menu by dialog

### D_MENU args

| arg        | value        | Description                                                                                                 |
|------------|--------------|------------------------------------------------------------------------------------------------------------|
| `menuPath` | path         | [Menu config path](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/menuConfig.md) |
| `title`    | string       | Title                                                                                                      |

### D_MENU ex


```js.js
|func=MENU
  ?args=
    menuPath=`${MENU_PATH}`
    &title="dialog menu title",
```

## NORMAL

Switch toolbar to normal


### js interface supporting NORMAL

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/toolbar/JsToolbarSwitcher/switch_S.md)

### NORMAL ex

```js.js
|func=NORMAL
```

## PAGE_SEARCH

Switch toolbar to page search box


### js interface supporting PAGE_SEARCH

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/toolbar/JsToolbarSwitcher/switch_S.md)

### PAGE_SEARCH ex

```js.js
|func=PAGE_SEARCH
```

## WEB_SEARCH

Switch toolbar to web search box


### js interface supporting WEB_SEARCH

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/toolbar/JsToolbarSwitcher/switch_S.md)


### WEB_SEARCH ex

```js.js
|func=WEB_SEARCH
```

## NO_SCROLL_SAVE_URL

Register [no scroll save url]([register no scroll url](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#no-scroll-save-url))

### NO_SCROLL_SAVE_URL ex

```js.js
|func=NO_SCROLL_SAVE_URL
```

## OK

Save current fannel contents and change [index mode](https://github.com/puutaro/CommandClick/blob/master/USAGE.md)

- default action in playButtonConfig

### OK ex

```js.js
|func=OK
```

## QR_SCAN

Scan QR code and execute

### js interface supporting QR_SCAN

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/toolbar/JsQrScanner/scan_S.md)

### QR_SCAN ex

```js.js
|func=QR_SCAN
```

## REFRESH_MONITOR

Refresh monitor

### js interface supporting REFRESH_MONITOR

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/toolbar/JsMonitorRefresh/refresh.md)

### REFRESH_MONITOR ex

```js.js
|func=REFRESH_MONITOR
```

## RESTART_UBUNTU

Restart ubuntu service

### js interface supporting RESTART_UBUNTU

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsUbuntu/boot.md)

### RESTART_UBUNTU ex

```js.js
|func=RESTART_UBUNTU
``` 

## SIZING

Monitor sizing

- long <-> short

### SIZING ex

```js.js
|func=SIZING
```

## SELECT_MONITOR

Restart ubuntu service

### js interface supporting SELECT_MONITOR

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/system/JsSelectTerm/launch.md)

### SELECT_MONITOR ex

```js.js
|func=SELECT_MONITOR
``` 

## SHORTCUT

Restart ubuntu service

### SHORTCUT ex

```js.js
|func=SHORTCUT
``` 

## SYNC

Sync list index

### SYNC ex

```js.js
|func=SYNC
``` 

## TERMUX_SETUP

Setup termux for CommandClick

### TERMUX_SETUP ex

```js.js
|func=TERMUX_SETUP
``` 

## USAGE

Launch CommandClick's usage README 

### USAGE ex

```js.js
|func=USAGE
``` 
