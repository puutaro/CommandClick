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
* [`~/AppDir/${app directory}/${fannel_dir}/settingVariables/replaceVariablesTable.tsv`](#replace_variables_table)
* [`~/AppDir/${app directory}/${fannel_dir}/systemJs/urlHistoryClick.js`](#url_history_click)
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

## `~/AppDir/${app directory}/${fannel_dir}/systemJs/urlHistoryClick.js` <a id="url_history_click"></a>

Trigger this js script primarlily on click [url history](https://github.com/puutaro/CommandClick/blob/master/md/developer/system_js_args.md#urlhistoryclick) than [`urlHistoryClick` js args](https://github.com/puutaro/CommandClick/blob/master/md/developer/system_js_args.md#urlhistoryclick)  


## `~/conf/monitor/term_[1-4]` <a id="output_monitor"></a>

Output file for js and shell    

- term type table

| type | description |
| --------- | --------- |
| `term_1` | starndard output |
| `term_2` | error and system output |
| `term_3` | free |
| `term_4` | /dev/null |

### `~/ubuntu/backup/rootfs.tar.gz` <a id="ubuntu_backup_rootfs"></a>

Ubuntu' backup rootfs compressed tar  

### `~/temp/cmd/cmd.sh` <a id="foreground_cmd_script"></a> 

Ubuntu's foreground exec shell file

### `~/temp/monitor/updateMonitor.txt` <a id="update_monitor"></a>  

temp monitor update contents file 

### `~/temp/ubuntuService/isActiveUbuntuService.txt` <a id="is_active_ubuntu_service"></a>  

Ubuntu service active check. On active, put  

