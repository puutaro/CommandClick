# Directory structure

`CommandClick` main data is located share storage on android
Here introduce feature by directory 

- "~" -> `/storage/emulated/0/Documents/cmdclick`


Table
-----------------
* [~](#root_dir_path)
* [`~/AppDir`](#app_dir)
  * [`~/AppDir/../system`](#app_dir_system_dir)
    * [`~/AppDir/../system/url`](#app_dir_system_dir_url_dir)
    * [`~/AppDir/../system/scroll`](#app_dir_system_dir_scroll_dir)
  * [`~/AppDir/../{fannelDir}`](#fannel_dir)
* [`~/conf`](#conf)  
  * [`~/conf/AppDirAdmin`](#conf_app_dir_admin)
  * [`~/conf/AppHistoryDir`](#conf_app_history_dir)
  * [`~/conf/ccimport`](#conf_ccimport)
  * [`~/conf/monitor`](#conf_monitor)
  * [`~/conf/repository`](#conf_repository)
* [`~/ubuntu`](#ubuntu)  
  * [`~/ubuntu/backup`](#ubuntu_backup)  
* [`~/temp`](#temp)
  * [`~/temp/cmd`](#temp_cmd)
  * [`~/temp/download`](#temp_download)
  * [`~/temp/monitor`](#temp_monitor)
  * [`~/temp/ubuntuService`](#temp_ubuntu_service)


## `~` <a id="root_dir_path"></a>

Root directory for cmdclick

~ is `/storage/emulated/0/Documents/cmdclick` here


## `~/AppDir` <a id="app_dir"></a>

This dir include [app directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory)

### `~/AppDir/../system` <a id="app_dir_system_dir"></a>

This direcotory include system dir of [app directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory)

#### `~/AppDir/../system/url` <a id="app_dir_system_dir_url_dir"></a>

This dir include url history [app directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory)
[Url history](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#url-history) is used to bookmark [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel)

#### `~/AppDir/../system/scroll` <a id="app_dir_system_dir_scroll_dir"></a>

This dir include y-scroll history [app directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory)
to resotre y-position when you visit site in url history, 

- [No scrill url](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#no-scroll-save-url)

## `~/AppDir/../{fannelDir}`  <a id="fannel_dir"></a>

[Fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md) directory path

All fannel main direcotry must be `${fannel name without extend} + "Dir"`, otherwise cannot controll by `CommandClick`


## `~/conf` <a id="conf"></a>

`CommandClick` administorator dir.

### `~/conf/AppDirAdmin` <a id="conf_app_dir_admin"></a>

Include [app directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory) configuration.

- [App directory manager](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#change-app-dir)

### `~/conf/AppHistoryDir` <a id="conf_app_history_dir"></a>

Include [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) historys

[App history](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#history)

### `~/conf/ccimport` <a id="conf_ccimport"></a>

Include [cc imported](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#import-library) js library.

### `~/conf/monitor` <a id="conf_monitor"></a>

Include monitor files

- [Select term](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#select-term)

### `~/conf/repository` <a id="conf_repository"></a>

Include [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) repository

- [Install fannel](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#install-fannel)

### `~/ubuntu` <a id="ubuntu"></a>

Include about ubuntu

### `~/ubuntu/backup` <a id="ubuntu_backup"></a>

Include ubuntu' backup rootfs

### `~/temp` <a id="temp"></a> 

Include temp use directorys

### `~/temp/cmd` <a id="temp_cmd"></a> 

Include ubuntu's foreground exec shell file

- [ubuntu's foreground exec shell file](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#foreground_cmd_script)  

### `~/temp/download` <a id="temp_download"></a> 

temp download file to be removed in next download


### `~/temp/monitor` <a id="temp_monitor"></a>  

Include temp monitor update contents file 

- [updateMonitor.txt](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#update_monitor)

### `~/temp/ubuntuService` <a id="temp_ubuntu_service"></a>  

Include ubuntu service temp files 

- [isActiveUbuntuService.txt](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#is_active_ubuntu_service)

