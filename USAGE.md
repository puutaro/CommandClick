
# Command Click Usage 
<img src="https://github.com/puutaro/CommandClick/assets/55217593/e4e6f75b-a35e-47f1-bb41-144d8ea88185" width="500"> 

[![Android: 8+](https://img.shields.io/badge/Android-8+-blueviolet.svg?style=popout&logo=android)]((https://opensource.org/licenses/MIT))
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)]((https://opensource.org/licenses/MIT))
![GitHub release (with filter)](https://img.shields.io/github/v/release/puutaro/CommandClick)
![GitHub repo size](https://img.shields.io/github/repo-size/puutaro/CommandClick)
<img src="https://img.shields.io/endpoint?url=https://apt.izzysoft.de/fdroid/api/v1/shield/com.mirfatif.permissionmanagerx&label=IzzyOnDroid&cacheSeconds=86400">  

Table of Contents
-----------------
<!-- vim-markdown-toc GFM -->

* [Usage](#usage)
  * [History](#history)
  * [Url history](#url-history)
  * [Change term size](#change-term-size)
  * [Run](#run)
  * [Edit](#edit)
  * [Write](#write)
  * [Kill](#kill)
  * [Description](#description)
  * [Copy file](#copy-file)
  * [Copy file path](#copy-file-path)
  * [Add](#add)
  * [Change app dir](#change-app-dir)
  * [Create shortcut](#create-shortcut)
  * [Install fannel](#install-fannel)
  * [Config](#config)
  * [Termux setting](#termux-setting)
  * [Edit startup](#edit-startup)
  * [No scroll save url](#no-scroll-save-url)
  * [Term reflesh](#term-reflesh)
  * [Forward](#forward)
  * [Search mode](#search-mode)
    * [Terminal filter](#terminal-filter)
    * [Terminal search](#terminal-search)
    * [Launch web menu](#launch-web-menu)
    * [Highlight search](#highlight-search)
    * [Highlight search dialog feature](#highlight-search-dialog-feature)
  * [Auto exec script](#auto-exec-script)
    * [Startup script](#startup-script)
  * [Internet Button exec script](#internet-button-exec-script)
  * [Button exec script](#button-exec-script)
* [Developer page](#developer-page)
* [CommandClick repository](#commandclick-repository)
* [Generally TroubleShooting](#generally-troubleshooting)
* [Ubuntu debian or widnows version](#ubuntu-debian-or-widnows-version)



Usage
-----

### Index mode

This mode is main mode. Top is `web terminal view`, down is `script name list`, bottom is toolbar.  
Main usage is executoin script by net surfing and list script clicking, other usage is maintenance script or app by longpress or toolbar.  
  
  
<img src="https://github.com/puutaro/CommandClick/assets/55217593/12ebcad4-b447-4c5f-ad4c-3ad1a685d606" width="600">  



### History

This feature is basic and great feature in `Command Click`.   
This always allow you to select current directory and mode which used, as if you look in Android's backstack feature's history.

[Procedure]  
1. Click left bottom history button.

<img src="https://github.com/puutaro/CommandClick/assets/55217593/3814775f-0eaf-4163-8e14-3d358b7dff4e" width="400">  

### Url history

Above same.


1. Long press left bottom history button.

<img src="https://github.com/puutaro/CommandClick/assets/55217593/e27ca648-e436-4b8d-a95f-3e17e5c06914" width="400">  


### Change term size

Terminal size change.  
  
[Procedure]  
1. Click toolbar right setting button

<img src="https://github.com/puutaro/CommandClick/assets/55217593/7a7eed05-c669-499a-b7c4-7b59f2947e09" width="400">  


### Run

Run script or launch app mode (when editExecute variable is `Always`)  

[Procedure]  
1. Click script item

<img src="https://github.com/puutaro/CommandClick/assets/55217593/bfa4ba53-faa8-4b3b-88ef-1c978e8495f5" width="400">  


### Edit

Edit script.  

[Procedure] 
1. Long press list item
2. Click `edit` in menu

<img src="https://github.com/puutaro/CommandClick/assets/55217593/e5ac7e04-a506-4c3d-b7f7-5505ac6f7b03" width="400">  

### Write

Edit script by editor  

[Procedure]  
1. Long press list item
2. Click `write` in menu
  
### Delete

Delete script  

[Procedure]  
1. Long press list item
2. Click `utility` -> `delete` in menu


### Kill
  
(Shell only) Kill shellscript proccess  
  
[Procedure]  
1. Long press list item
2. Click `utility` -> `kill` in menu

### Description

Display description for script 

[Procedure]  
1. Long press list item
2. Click `utility` -> `description` in menu 

or


1. Click thumbnail

<img src="https://github.com/puutaro/CommandClick/assets/55217593/8e9fd681-0b66-48bc-bb24-e82cb5b95f85" width="400">  
  

### Copy file

Copy file for script   
  
[Procedure]  
1. Long press list item
2. Click `copy` -> `copy file` in menu 
  
  
### Copy file path

Copy file path for script 

[Procedure]  
1. Long press list item
2. Click `copy` -> `copy file path` in menu


### Add

Add new script.  
At the same time, if you installed code editor, edit new file.    
  
More detail ref [add-DEVELOPER.md](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#add)
  
[Procedure]  
1. Long press right buttom gear button  
2. Click `add` in popup menu 

<img src="https://github.com/puutaro/CommandClick/assets/55217593/2e73099f-b2f7-4241-80e9-1a28a4a4a100" width="400">  

### Change app dir
  
Start `App directory` Manager 
  

[Procedure]  
1. Long press right buttom gear button  
2. Click `setting` -> "change_app_dir" in popup menu
  
`App directory` is directory which index mode retreive
- when item long press, poupu `add`, `delete`, `copy` and `rename` menu 
    - `add`: add `App directory` 
    - `delete`: delete `App directory`
    - `rename`: rename `App directory` name
    - `copy`: copy `App directory`


### Create shortcut
 
You can create shortcut for current `App directory` or `script`  

[Procedure]  
1. Long press right buttom gear button  
2. Click `setting` -> "create_short_cut" in popup menu
   

### Install fannel

`fannel` is bellow meaning    
 
>  fannel is ComamndClick using script (javascript, and shellscript)
> For instance, your click script in CommandClick. One thing I can say that CommandClick is developed for the purpose of changing javaxcript and shellscript to gui appication. That applies to click script. It's so called Gui application. We can say so. I call the gui application fannel.
  
  
[Procedure]    
1. Long press right bottom gear button  
2. click `install_fannel`   
3. You can install `fannel` by clicking.   

When you wont to sync [git repository](https://github.com/puutaro/commandclick-repository), `sync` by left bottom sync button  and wait until `fannel` list update.   
  
<img src="https://github.com/puutaro/CommandClick/assets/55217593/4589a003-3eb5-46d9-a981-ad00930923ca" width="600">  


### Config

You can setting `CommandClick` Configration
- detail setting reference [add-DEVELOPER.md](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#add)

[Procedure]  
1. Long press right buttom gear button  
2. Click `setting` -> "config" in popup menu


### Termux Setting

Command Click is use [`RUN_COMMAND` Intent](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent) in termux  
and, require termux storage setting.
You can set by onley this menu press.


[Procedure]  
1. long press right bottom setting button  
2. `setting` -> `termux setup`   
3. Long press on termux
4. Click paste popup on termux  
5. Continue pressing `Enter` on termux
- clipboard contents:
   ```sh.sh
   pkg update -y && pkg upgrade -y \
   && yes | termux-setup-storage \
   && sed -r 's/^\#\s(allow-external-apps.*)/\1/' -i "$HOME/.termux/termux.properties" 
   ```

   - reference
      - Enable `allow-external-apps` [detail](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent#allow-external-apps-property-mandatory)
      - Add Storage permission. [detail](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent#storage-permission-optional)
      - Execute `termux-setup-storage` on termux

4. Set strage access again in `android 11` (Optional)

> You may get "Permission denied" error when trying to access shared storage, even though the permission has been granted.
>  
> Workaround:
>
> Go to Android Settings --> Applications --> Termux --> Permissions
> Revoke Storage permission
> Grant Storage permission again

[detail](https://wiki.termux.com/wiki/Termux-setup-storage)

5. Set `Draw Over Apps permission` in `android 11+` (Optinal)

> You can grant Termux the Draw Over Apps permission from its App Info activity:
> `Android Settings` -> `Apps` -> `Termux` -> `Advanced` -> `Draw over other apps`.

[detail](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent/06f1de1b262d7612497e76463d8cc34ba7f49832#draw-over-apps-permission-optional)

- When above method cannot settle down, `CommandClick` or `Termux` restart, and system reboot.

### Edit startup

Edit `statup script` contenst.  
`statup script` is setting script for `current app directory`. So this setting directly link to usability.    
[ref](#startup-script)
  
[Procedure]  
1. Long press right buttom gear button  
2. Click `edit_startup` in popup menu
  
### No scroll save url

Register scroll y position ignore domain 

[Procedure]  
1. Long press right buttom gear button  
2. Click `no scroll save url` in popup menu


### Term reflesh

You can reflesh `web terminal view`.
  
[Procedure]  
1. Long press right buttom gear button  
2. Click `setting` -> "term_reflesh" in popup menu


### Forward

You can forward `web terminal view` history.

[Procedure]  
1. Long press right buttom gear button  
2. Click `left allow button`
   

### Search mode

You can search `web terminal view` by toolbar search item.
  
[Procedure]  
1. Input bottom search bar.

<img src="https://github.com/puutaro/CommandClick/assets/55217593/0fe0a998-ae2d-4c8e-bff5-ceddd49a7988" width="400">  



#### Terminal filter

It's default setting in terminal short size. If you type string, realtime filter start.

<img src="https://github.com/puutaro/CommandClick/assets/55217593/944434ae-ec25-4bcf-a99a-c1bc6797ee10" width="400">  
<img src="https://github.com/puutaro/CommandClick/assets/55217593/d468feac-62c9-43c4-a15b-b44282467c60" width="400">  


#### Terminal search

Search typing word.
  
[Procedure]  
1. Long press terminal mark or web mark long press

<img src="https://github.com/puutaro/CommandClick/assets/55217593/ac7ec457-005e-48ac-83d5-872bce60c269" width="400">  
<img src="https://github.com/puutaro/CommandClick/assets/55217593/7919ebe1-12d2-4955-b5ec-debfa73da564" width="400">  


#### Launch web menu

This is shortcut menu with internet button.
When you look website, press internet button no highlight, launch menu by inernet button script
  
[Procedure]    
1. Click internet button.

<img src="https://github.com/puutaro/CommandClick/assets/55217593/c6e61be7-bfb2-4b8d-a13f-3d985d42d052" width="400">  


#### Highlight search

This mode is web browser search revolution.
Existing browser don't have this feature.  
This mode is `CommmandClick`'s web search big advantage.
This mode enable speedy search and derailment search.  
  
[Procedure]  
1. Highlight text in website
2. Click internet button


<img src="https://github.com/puutaro/CommandClick/assets/55217593/bb4824ea-da17-43a8-bfc9-c1443d031b92" width="400">  

#### Highlight search dialog feature

Highlight search inpact source is this feature. 
More precisely, thanks to shortcut button power. 

  
- click
  
<img src="https://github.com/puutaro/CommandClick/assets/55217593/a2c2cf37-fbeb-49d4-8c5b-80324d2ffffc" width="400">  

- long press
  
<img src="https://github.com/puutaro/CommandClick/assets/55217593/b8a9dc77-f533-469a-8d63-59cb5b4ecfd1" width="400">  




### Auto exec script

`Command Click` have auto exec script. This is used when `index mode` startup or end.

### Startup script
  
This script is automaticaly executed when `index mode` startup.
    
Override `config setting variable`, if you are change default value with your set value.



### Internet Button exec script

This script is executed, click when internet buton is grey globle mark and long terminal mode is active.


### Button exec script

This script is executed when history buton click or long click, if you select  `urlHistoryOrButtonExec` to `BUTTON_EXEC` in setting variable.
Also whether click or long click torigger, due to `historySwitch` setting  (reference to [add-DEVELOPER.md](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#add)
).


### Developer page

-> [DEVELOPER.md](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md)

`CommandClick` is powered by javascript mainly. For that, This has many javascript interface and option.
These enforce browser feature like addon. If you had made nice script, `Commandclick` is more suitable browser for you. 
I mean, `CommandClick` is customize browser.
And, `CommandClick` is multi application. Beyond addon field, `CommandClick` is multiple standalone application(I call `fannel`).  
I mean, `CommandClick` is low code tool. You can make andorid application by less effort.  
Welcome to above developer page that expands depending on you.  


### Commandclick-repository

-> [CommandClick's fannel repository](https://github.com/puutaro/commandclick-repository)

`fannel` is ComamndClick using script (javascript, and shellscript)
For instance, your click script in CommandClick. One thing I can say that CommandClick is developed for the purpose of changing javaxcript and shellscript to gui appication. That applies to click script. It's so called Gui application. We can say so. I call the gui application `fannel`




### Generally TroubleShooting  

- When url load slow in different than before, probably due to cache, so click it's url from `url history`.
    - In many cases, occur in google search result page.


- Ocationaly first start proccess crush, try, don't worry, just reboot.  
    - Becuase app resoruce prefetch is busy, it's occur. Therefore, it' s instant problem. Rarely happens after the second time.

- When `websearch suggest` is not working, press space. if suggest  exist, fly.  
    - Prabably due to `AutovompleteTextView`'s specification.  　　
    
- When frequently crush, your smartphone reboot, or `onAdBlock` setting variable set `OFF`  
    - Becuase of low memory, as a side note when `onAdBlock` is `ON`, `CommandClick` make block list on activiry reboot. Therefore 
Crashes easily when memory is low    

- When `fannel` suddenly not working, update latest `CommandClick` and `fannel`.
    - `CommandClick` and `fannel` is new baby. Therefre, these is frequetly updated without backwards compatible.　I continuly enforce usability and fanctionality. Before long, if these grow up adult, I weight stability. But, please feel at ease, most often even now, latest version works fine.  




### Ubuntu debian or widnows version

Reference to [url](https://github.com/puutaro/cmdclick)


