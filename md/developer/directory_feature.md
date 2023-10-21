# Directory_feature

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
* [`~/conf`](#conf)(#conf)  
  * [`~/conf/AppDirAdmin`](#conf_app_dir_admin)
  * [`~/conf/AppHistoryDir`](#conf_app_history_dir)
  * [`~/conf/ccimport`](#conf_ccimport)
  * [`~/conf/monitor`](#conf_monitor)
  * [`~/conf/repository`](#conf_repository)



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

- 


