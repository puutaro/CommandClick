# FileApis

`CommandClick` is managed by files data  
So, each file data can use in CommandClick's development  
Here introduce feature by file path 

- "~" -> `/storage/emulated/0/Documents/cmdclick`

Table
-----------------
* [~](#root_dir_path)
* [`~/AppDir/${app directory}/system/url/cmdclickUrlHistory`](#url_history)
* [`~/AppDir/../system/scroll/scrollPosi.tsv`](#scroll_position)
* [`~/AppDir/${app directory}/${fannel_dir}/longPressMenuDir/srcAnchorLongPressMenu.txt`](#src_anchor_long_press_menu)
* [`~/AppDir/${app directory}/${fannel_dir}/longPressMenuDir/srcImageAnchorLongPressMenu.txt`](#src_image_anchor_long_press_menu)
* [`~/AppDir/${app directory}/${fannel_dir}/longPressMenuDir/imageAnchorLongPressMenu.txt`](#image_long_press_menu)
* [`~/AppDir/${app directory}/${fannel_dir}/settings/homeScriptUrlsPath.txt`](#home_script_urls_path)
* [`~/AppDir/${app directory}/${fannel_dir}/settings/settingButtonConfig.js`](#setting_button_config)
* [`~/AppDir/${app directory}/${fannel_dir}/settings/qrDialogConfig.js`](#qr_dialog_config)
* [`~/AppDir/${app directory}/${fannel_dir}/settingVariables/replaceVariablesTable.tsv`](#replace_variables_table)
* [`~/AppDir/${app directory}/${fannel_dir}/systemJs/urlHistoryClick.js`](#url_history_click)
* [`~/AppDir/${app directory}/${fannel_dir}/systemJs/appHistoryClick.js`](#app_history_click)
* [`~/AppDir/${app directory}/${fannel_dir}/systemJs/onAutoExec.js`](#on_auto_exec)
* [`~/AppDir/${app directory}/${fannel_dir}/systemJs/noArg.js`](#no_arg)
* [`~/conf/monitor/term_[1-4]`](#output_monitor)
* [`~/ubuntu/backup/rootfs.tar.gz`](#ubuntu_backup_rootfs)
* [`~/temp/cmd/cmd.sh`](#foreground_cmd_script)
* [`~/temp/monitor/updateMonitor.txt`](#update_monitor)
* [`~/temp/ubuntuService/isActiveUbuntuService.txt`](#is_active_ubuntu_service)
  

## `~` <a id="root_dir_path"></a>

Root directory for cmdclick

~ is `/storage/emulated/0/Documents/cmdclick` here


CommandClick automaticaly create files in App directory/system/url. This is used by system, alse is userinterface for app signal.

## `~/AppDir/${app directory}/system/url/cmdclickUrlHistory.tsv` <a id="url_history"></a>

Url history tsv file that you have ever visited

ex)  

```
title1\turl1
title2\turl2
.
.
.
```

## `~/AppDir/../system/scroll/scrollPosi.tsv` <a id="scroll_position"></a>

Save scroll psition

ex)

```
url\tscroll y-position1
ur2\tscroll y-position2
.
.
.
```

## `~/AppDir/${app directory}/${fannel_dir}/settingVariables/replaceVariablesTable.tsv` <a id="replace_variables_table"></a>

Tsv to convert [replace variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md) to tsv like key to value

```
variable name1\tvariable value1
variable name2\tvariable value2
variable name3\tvariable value3
.
.
.
```

- Auto create & convert [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md) in js use

## `~/AppDir/${app directory}/${fannel_dir}/settings/homeScriptUrlsPath.txt` <a id="home_script_urls_path"></a>

fannel list for [homeScriptUrlsPath](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#homescripturlspath) in [setting variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md)

```
fannel path 1
fannel path 2
.
.
.
```
- [settings directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/directory_structure.md#settings)


## `~/AppDir/${app directory}/${fannel_dir}/settings/settingButtonConfig.js` <a id="setting_button_config"></a>

setting button config file

## `~/AppDir/${app directory}/${fannel_dir}/settings/qrDialogConfig.js` <a id="qr_dialog_config"></a>

qr dialog config file

## `~/AppDir/${app directory}/${fannel_dir}/longPressMenuDir/srcAnchorLongPressMenu.txt` <a id="src_anchor_long_press_menu"></a>

fannel list for [srcAnchorLongPressMenu](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#srcanchorlongpressmenufilepath)

```
fannel name 1
fannel name 2
.
.
.
```

- [longPressMenu directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/directory_structure.md#long_press_menu_dir)

## `~/AppDir/${app directory}/${fannel_dir}/longPressMenuDir/srcImageAnchorLongPressMenu.txt` <a id="src_image_anchor_long_press_menu"></a>

fannel list for [srcImageAnchorLongPressMenu](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#srcimageanchorlongpressmenufilepath)

```
fannel name 1
fannel name 2
.
.
.
```

- [longPressMenu directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/directory_structure.md#long_press_menu_dir)

## `~/AppDir/${app directory}/${fannel_dir}/longPressMenuDir/imageAnchorLongPressMenu.txt` <a id="image_long_press_menu"></a>

fannel list for [imageAnchorLongPressMenu](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#imagelongpressmenufilepath)

```
fannel name 1
fannel name 2
.
.
.
```

- [longPressMenu directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/directory_structure.md#long_press_menu_dir)

## `~/AppDir/${app directory}/${fannel_dir}/systemJs/urlHistoryClick.js` <a id="url_history_click"></a>

Trigger this js script primarlily on click [url history](https://github.com/puutaro/CommandClick/blob/master/md/developer/system_js_args.md#urlhistoryclick) than [`urlHistoryClick` js args](https://github.com/puutaro/CommandClick/blob/master/md/developer/system_js_args.md#urlhistoryclick)  

- [systemJs directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/directory_structure.md#system_js)


## `~/AppDir/${app directory}/${fannel_dir}/systemJs/appHistoryClick.js` <a id="app_history_click"></a>

Trigger this js script on click [app history](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#history) 

- [systemJs directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/directory_structure.md#system_js)


## `~/AppDir/${app directory}/${fannel_dir}/systemJs/onAutoExec.js` <a id="on_auto_exec"></a>

Trigger this js script primarlily than no js args

- [systemJs directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/directory_structure.md#system_js)


## `~/AppDir/${app directory}/${fannel_dir}/systemJs/noArg.js` <a id="no_arg"></a>

Trigger this js script primarlily than [`onAutoExec` js args](https://github.com/puutaro/CommandClick/blob/master/md/developer/system_js_args.md#onautoexec) when set `ON` to [onAutoExec](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#onautoexec) in [setting variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#setting-variable)   

- [systemJs directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/directory_structure.md#system_js)


## `~/conf/monitor/monitor_[1-4]` <a id="output_monitor"></a>

Output file for js and shell    

- term type table

| type        | description |
|-------------| --------- |
| `monitor_1` | starndard output |
| `monitor_2`    | error and system output |
| `monitor_3`    | free |
| `monitor_4`    | /dev/null |

### `~/ubuntu/backup/rootfs.tar.gz` <a id="ubuntu_backup_rootfs"></a>

Ubuntu' backup rootfs compressed tar  

### `~/temp/cmd/cmd.sh` <a id="foreground_cmd_script"></a> 

Ubuntu's foreground exec shell file

### `~/temp/monitor/updateMonitor.txt` <a id="update_monitor"></a>  

temp monitor update contents file 

### `~/temp/ubuntuService/isActiveUbuntuService.txt` <a id="is_active_ubuntu_service"></a>  

Ubuntu service active check. On active, put  

