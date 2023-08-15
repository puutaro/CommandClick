
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
  * [Add](#add)
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



#### History

<img src="https://github.com/puutaro/CommandClick/assets/55217593/e561bd88-167c-4acb-8cf9-991185c18be0" width="400">  


This feature is basic and great feature in `Command Click`. This always allow you to select current directory and mode which used, as if you look in Android's backstack feature's history.
Trigger by left bottom history button clicked.


#### Url history

<img src="https://github.com/puutaro/CommandClick/assets/55217593/373d5d55-6eda-44e0-b3b6-9bcef44dafdc" width="400">  

You look in url history by long press where you visited url 
(Afterward noting, switchable url history with history, or url history with button script exec)  



#### Change term size

Click toolbar right setting button, and terminal size change.

#### Add

This feature display when toolbar right setting button long pressed. Then, click `add`, so new script adding.
At the same time, if you installed code editor, edit new file.

More detail ref [add-DEVELOPER.md](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#add)


#### Run

Run script by click list item in index mode or play button edit mode (editExecute variable is `Always`)
Or run javascript file.


#### Edit

Edit script by form when long click list item in index mode 


#### Write

Edit script by editor when long click list item in index mode 

#### Delete

Delete script by `utility` -> `delete`  when long click list item in index mode   
At the same time, remove `fannel` direcotry (raw filename + `Dir`)  


#### Kill

(Shell only) Kill shellscript proccess by `utility` -> `kill`  when long click list item in index mode 


#### Description

Display description for script by `utility` -> `description`  when long click list item in index mode


#### Copy file

Copy file for script by `copy` -> `copy file`  when long click list item in index mode  
At the same time, copy `fannel` direcotry (raw filename + `Dir`)  

#### Copy file path

Copy file path for script by `copy` -> `copy file path`  when long click list item in index mode

#### Add

Add new script by `add`   when long click setting button(toolbar right) in index mode


#### Change app dir


Start `App directory` Manager by `setting` -> `change_app_dir` when long click setting button(toolbar right) in index mode
`App directory` is directory which index mode retreive
- when item long press, poupu `add`, `delete` and `edit` menu 
    - `add`: add `App directory` 
    - `delete`: delete `App directory`
    - `edit`: edit `App directory` name
   


#### Create shortcut
 
You can create shortcut for current `App directory` or `script` in only `index mode` or `edit execute Always`

#### Install fannel

`fannel` is bellow meaning  

>  fannel is ComamndClick using script (javascript, and shellscript)
> For instance, your click script in CommandClick. One thing I can say that CommandClick is developed for the purpose of changing javaxcript and shellscript to gui appication. That applies to click script. It's so called Gui application. We can say so. I call the gui application fannel.
 [detail](https://github.com/puutaro/commandclick-repository#desire) 

Bellow, how to install `fannel`  

1. long press right bottom setting button  
2. click `install_fannel`   
3. You can install `fannel` by clicking.   

When you wont to sync [git repository](https://github.com/puutaro/commandclick-repository), `sync` by left bottom sync button  and wait until `fannel` list update.   
  
<img src="https://github.com/puutaro/CommandClick/assets/55217593/4589a003-3eb5-46d9-a981-ad00930923ca" width="600">  


#### Config

You can setting `CommandClick` Configration
- detail setting reference [add-DEVELOPER.md](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#add)



#### Termux Setting

Command Click is use [`RUN_COMMAND` Intent](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent) in termux  
and, require termux storage setting.
You can set by onley this menu press.

* Below is a supplement.

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

#### Edit startup

Edit `statup script` contenst.  
`statup script` is setting script for `current app directory`. So this setting directly link to usability.    
[ref](#startup-script)

#### No scroll save url

Register scroll y position ignore domain 

#### Term reflesh

You can reflesh `web terminal view`.

#### Forward

You can forward `web terminal view` history.

#### Search mode

You can search `web terminal view` by toolbar search item.

##### Terminal filter

It's default setting in terminal short size. If you type string, realtime filter start.

##### Terminal search

When terminal mark or web mark long press, you can search typing word.

##### Launch web menu

This is shortcut menu with internet button.
When you look website, press internet button no highlight, launch menu by inernet button script


#### Highlight search

This is speed search mode.  
When you look website, highlight text and click internet button..



### Auto exec script

`Command Click` have auto exec script. This is used when `index mode` startup or end.

#### Startup script
1. This script is automaticaly executed when `index mode` startup.
But, in default, `onAutoExec` in setting variable is `OFF` so, if you enable this, you must be `ON` (reference to [add-DEVELOPER.md](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#add)
).

2. Override `config setting variable`, if you are change default value with your set value.



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


