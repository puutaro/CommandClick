# Command Click Usage

<img src="https://github.com/puutaro/CommandClick/assets/55217593/e4e6f75b-a35e-47f1-bb41-144d8ea88185" width="500"> 

[![Android: 8+](https://img.shields.io/badge/Android-8+-blueviolet.svg?style=popout&logo=android)]((https://opensource.org/licenses/MIT))
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
![GitHub release (with filter)](https://img.shields.io/github/v/release/puutaro/CommandClick)
![GitHub repo size](https://img.shields.io/github/repo-size/puutaro/CommandClick)
<img src="https://img.shields.io/endpoint?url=https://apt.izzysoft.de/fdroid/api/v1/shield/com.mirfatif.permissionmanagerx&label=IzzyOnDroid&cacheSeconds=86400">
![GitHub all releases](https://img.shields.io/github/downloads/puutaro/CommandClick/total)  

Table of Contents
-----------------

<!-- vim-markdown-toc GFM -->

* [Ubuntu](#ubuntu)
  * [Setup ubuntu](#setup-ubuntu)
  * [Backup ubuntu rootfs](#backup-ubuntu-rootfs)
* [Historys](#history)
  * [History](#history)
  * [Url history](#url-history)
* [Fannel(addon) List](#fannel-list)
  * [Image click](#image-click)
  * [Image long click](#image-long-click)
  * [Body Click](#body-click)
    * [Run](#run)
  * [Body long click](#body-long-click)
    * [Edit](#edit)
    * [Write](#write)
    * [Kill](#kill)
    * [Description](#description)
    * [Copy file](#copy-file)
    * [Copy file path](#copy-file-path)
* [Settings](#settings)
  * [Change term size](#change-term-size)
  * [Edit preference](#edit-preference)
  * [No scroll save url](#no-scroll-save-url)
  * [Install fannel](#install-fannel)
  * [Scan QR](#scan-qr)
  * [Reflesh monitor](#reflesh-monitor)
  * [Select monitor](#select-monitor)
  * [Restart ubuntu](#restart-ubuntu)
  * [Add](#add)
  * [App dir manager](#app-dir-manager)
  * [Create shortcut](#create-shortcut)
  * [Termux setting](#termux-setting)
  * [Config](#config)
    * [Change ubuntu sleep minutes](change-ubuntu-sleep-minutes)
  * [Forward](#forward)
* [Search mode](#search-mode)
  * [Terminal filter](#terminal-filter)
  * [Terminal search](#terminal-search)
  * [Launch web menu](#launch-web-menu)
  * [Highlight search](#highlight-search)
  * [Highlight search dialog feature](#highlight-search-dialog-feature)
* [Auto exec script](#auto-exec-script)
  * [Preference](#preference)
* [Internet Button exec script](#internet-button-exec-script)
* [Button exec script](#button-exec-script)
* [Developer page](#developer-page)
* [CommandClick repository](#commandclick-repository)
* [Generally TroubleShooting](#generally-troubleshooting)
* [Ubuntu, debian or widnows version](#ubuntu-debian-or-widnows-version)

## Ubuntu

----------------

### Setup ubuntu

'CommandClick' is enable ubuntu as backend with sound.

[Procedure]  

<img src="https://github.com/puutaro/CommandClick/assets/55217593/2406d8eb-b836-43eb-8dd0-1169c954e64b" width="400">  

1. Press `Setup` button in Notification  
2. Complete about 5 minutes
3. Press `Terminal` button 

<img src="https://github.com/puutaro/CommandClick/assets/55217593/83f7668a-e3a2-4df5-867e-2d2491ebf7f5" width="400">  

- `Restart` -> Press button when ubuntu not working
- ssh username & password: `cmdclick`

ex)

```sh.sh
sshpass -p cmdclick \
    ssh -p 10022 "cmdclick@${android ipv4 address}"
```

### Backup ubuntu rootfs

'CommandClick' is enable current rootfs backup.

[Procedure]  

1. Press `BACKUP` button in Notification  

<img src="https://github.com/puutaro/CommandClick/assets/55217593/f2e0b09b-3724-410a-b3a5-38ac5ed96e3d" width="250">  

2. Launch ubuntu rootfs backup manager  
3. Press `BACKUP` button  

<img src="https://github.com/puutaro/CommandClick/assets/55217593/677a7634-0145-4569-824b-457a0d602c79" width="250">  

4. Wait backup comp  

5. Press `INIT` button  

6. Press `RESTORE`  

<img src="https://github.com/puutaro/CommandClick/assets/55217593/15bfaadb-bb6d-454b-86a0-f4f11c196423" width="250">  

## Index mode

---------------

This mode is main mode. Top is `web monitor view`, down is `fannel(addon) name list`, bottom is toolbar.  
Main usage is executoin script by net surfing and list script clicking, other usage is maintenance script or app by longpress or toolbar.  

<img src="https://github.com/puutaro/CommandClick/assets/55217593/12ebcad4-b447-4c5f-ad4c-3ad1a685d606" width="400">  

## Historys

---------------

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

## Fannel(addon) List <a id="fannel-list"></a>

### Image click

#### Description

Display description for script 

[Procedure]  

1. Click QR code

<img src="https://github.com/puutaro/CommandClick/assets/55217593/8e9fd681-0b66-48bc-bb24-e82cb5b95f85" width="400">  

### Image long click

Display qr code dialog

[Procedure]  

1. Long click QR code
2. Click bellow like image

<img src="https://github.com/puutaro/CommandClick/assets/55217593/3e353332-c974-48df-90f4-f1365d074bd9" width="500"> 

### Body click

#### Run

Run script or launch app mode (when editExecute variable is `Always`)  

[Procedure]  

1. Click script item

<img src="https://github.com/puutaro/CommandClick/assets/55217593/bfa4ba53-faa8-4b3b-88ef-1c978e8495f5" width="400">  

### Body long click

#### Edit

Edit script.  

[Procedure] 

1. Long press list item
2. Click `edit` in menu

<img src="https://github.com/puutaro/CommandClick/assets/55217593/e5ac7e04-a506-4c3d-b7f7-5505ac6f7b03" width="400">  

#### Delete

Delete script  

[Procedure]  

1. Long press list item
2. Click `Utility` -> `delete` in menu

#### Kill

Kill proccess  

[Procedure]  

1. Long press list item
2. Click `Utility` -> `kill` in menu
3. Select kill type

<img src="https://github.com/puutaro/CommandClick/assets/55217593/72d1f628-ee6f-43eb-8e8c-6b43860ec03b" width="300">  

- kill type

| type                | description                                    |
| ------------------- | ---------------------------------------------- |
| `kill app`          | **Kill all app process**                       |
| `kill this process` | Kill this fannel(script) process               |
| `select kill`       | Kill process selected from process list dialog |

### Description

Display description for script 

[Procedure]  

1. Long press list item
2. Click `Utility` -> `Description` in menu

<img src="https://github.com/puutaro/CommandClick/assets/55217593/8e9fd681-0b66-48bc-bb24-e82cb5b95f85" width="400">  

#### Write

Edit script by editor  

[Procedure]  

1. Long press list item
2. Click `Utility` -> `write` in menu

### Copy file

Copy file for script   

[Procedure]  

1. Long press list item
2. Click `Utility` -> `copy file` in menu 

### Copy file path

Copy file path for script 

[Procedure]  

1. Long press list item
2. Click `Utility` -> `copy file path` in menu

## Settings

-----------------

Mainly, setting browser and addon (fannel) 

### Change term size

Terminal size change.  

[Procedure]  

1. Click toolbar right setting button

<img src="https://github.com/puutaro/CommandClick/assets/55217593/7a7eed05-c669-499a-b7c4-7b59f2947e09" width="400">  

### Edit preference

Edit `preference`   
`preference` is setting for `current app directory`. So this setting directly link to usability.    
[ref](#preference)

[Procedure]  

1. Long press right buttom gear button  
2. Click `edit_startup` in popup menu

<img src="https://github.com/puutaro/CommandClick/assets/55217593/2e73099f-b2f7-4241-80e9-1a28a4a4a100" width="400">

### No scroll save url

Register scroll y position ignore domain 

[Procedure]  

1. Long press right buttom gear button  
2. Click `no scroll save url` in popup menu

### Scan QR

This is high power feature.  
Enable scan bellow type qr

| type            | description                                                              |
| --------------- | ------------------------------------------------------------------------ |
| WIFI            | setup ssid and pin                                                       |
| SMS             | send sms                                                                 |
| GMAIL           | send gmail                                                               |
| TEL             | call number                                                              |
| GOOGLE CALENDAR | register google calendar                                                 |
| URL             | launch url                                                               |
| GIT CLONE       | git clone <br> ref: [Git clone QR](#Image long click)                    |
| FILE DOWNLOAD   | file download by p2p <br> ref: [Fannel upload by P2P](#Image long click) |
| SCP_DOWNLOAD    | file downlaod by rsync                                                   |
| JAVASCRIPT      | Load javascript                                                          |

### Install fannel

`fannel` is bellow meaning    

>  fannel is ComamndClick using script (javascript, and shellscript)
> For instance, your click script in CommandClick. One thing I can say that CommandClick is developed for the purpose of changing javaxcript and shellscript to gui appication. That applies to click script. It's so called Gui application. We can say so. I call the gui application fannel.

[Procedure]    

1. Long press right bottom gear button  
2. click `install_fannel`   
3. You can install `fannel` by clicking.   

When you wont to sync [git repository](https://github.com/puutaro/commandclick-repository), `sync` by left bottom sync button  and wait until `fannel` list update.   

<img src="https://github.com/puutaro/CommandClick/assets/55217593/4589a003-3eb5-46d9-a981-ad00930923ca" width="400">  

### Reflesh monitor

You can reflesh `web terminal view`.

[Procedure]  

1. Long press right buttom gear button  
2. Click `manager` -> "term_reflesh" in popup menu

### Select monitor

[Procedure]  

1. Long press right buttom gear button  
2. Click 'manager' -> `select term` in popup menu 
3. select term from term list

<img src="https://github.com/puutaro/CommandClick/assets/55217593/b49cc5ff-27e4-4362-a3d7-b12ba5af2eb4" width="250">  

- [term type](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#output_monitor)

### Restart ubuntu

Restart ubuntu forcibly

[Procedure]  

1. Long press right buttom gear button  
2. Click `manager` -> `restart ubuntu` in popup menu

### Add

Add new script.  
At the same time, if you installed code editor, edit new file.    

More detail ref [DEVELOPER.md](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#add)

[Procedure]  

1. Long press right buttom gear button  
2. Click `manager` -> `add`  in popup menu 

### App dir manager

Start `App directory` Manager 

[Procedure]  

1. Long press right buttom gear button  
2. Click `setting` -> "change_app_dir" in popup menu

`App directory` is directory which index mode retreive

- when item long press, poupu like bellow menu table `add`, `delete`, `copy` and `rename` menu

| menu     | description                 |
| -------- | --------------------------- |
| `add`    | Add `App directory`         |
| `delete` | Delete `App directory`      |
| `rename` | Rename `App directory` name |
| `copy`   | Copy `App directory`        |

### Create shortcut

You can create shortcut for current `App directory` or `script`  

[Procedure]  

1. Long press right buttom gear button  
2. Click `setting` -> "create_short_cut" in popup menu

### Config

You can setting `CommandClick` Configration

- detail [setting reference](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md)

[Procedure]  

1. Long press right buttom gear button  
2. Click `setting` -> "config" in popup menu

#### Change ubuntu sleep minutes

Ubuntu Sleep delay minutes is set for battery life.  default `20` min   

[Procedure]  

1. Long press right buttom gear button  
2. Click `setting` -> "config" in popup menu
3. Change `ubuntuSleepDelayMinInScreenOff`

<img src="https://github.com/puutaro/CommandClick/assets/55217593/fd4c5304-6cbe-4d22-92f3-732c9345e638" width="400">  

- 0: no sleep

### Termux Setting

-> [About termux setup](https://github.com/puutaro/CommandClick/blob/master/md/usage/termux_setup.md)

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
- When not working, [install webview cannary](https://github.com/puutaro/CommandClick?tab=readme-ov-file#recommend-install-webview-canary)   

<img src="https://github.com/puutaro/CommandClick/assets/55217593/bb4824ea-da17-43a8-bfc9-c1443d031b92" width="400">  

#### Highlight search dialog feature

Highlight search inpact source is this feature. 
More precisely, thanks to shortcut button power. 

- click

<img src="https://github.com/puutaro/CommandClick/assets/55217593/a2c2cf37-fbeb-49d4-8c5b-80324d2ffffc" width="400">  

- long press

<img src="https://github.com/puutaro/CommandClick/assets/55217593/b8a9dc77-f533-469a-8d63-59cb5b4ecfd1" width="400">  

- When not working, [install webview cannary](https://github.com/puutaro/CommandClick?tab=readme-ov-file#recommend-install-webview-canary)   

### Auto exec script

`Command Click` have auto exec script. This is used when `index mode` startup or end.

### Preference

This `preference` is automaticaly read when `index mode` startup.

Override `config setting variable`, if you are change default value with your set value.

### Internet Button exec script

This script is executed, click when internet buton is grey globle mark and long terminal mode is active.

### Button exec script

This script is executed when history buton click or long click, if you select  `urlHistoryOrButtonExec` to `BUTTON_EXEC` in setting variable.
Also whether click or long click torigger, due to `historySwitch` setting  (reference to [DEVELOPER.md](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#add)
).

Developer page
-----------------

-> [DEVELOPER.md](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md)

`CommandClick` is powered by javascript mainly. For that, This has many javascript interface and option.
These enforce browser feature like addon. If you had made nice script, `Commandclick` is more suitable browser for you. 
I mean, `CommandClick` is customize browser.
And, `CommandClick` is multi application. Beyond addon field, `CommandClick` is multiple standalone application(I call `fannel`).  
I mean, `CommandClick` is low code tool. You can make andorid application by less effort.  
Welcome to above developer page that expands depending on you.  

Commandclick-repository
-----------------

-> [CommandClick's fannel repository](https://github.com/puutaro/commandclick-repository)

`fannel` is ComamndClick using script (javascript, and shellscript)
For instance, your click script in CommandClick. One thing I can say that CommandClick is developed for the purpose of changing javaxcript and shellscript to gui appication. That applies to click script. It's so called Gui application. We can say so. I call the gui application `fannel`

Generally TroubleShooting
-----------------

- Javascript err -> [[Recommend] Install WebView Canary](https://github.com/puutaro/CommandClick/blob/master/README.md#recomend-install-webview-canary)
  
  - Probably js not working 

- App not working -> [**kill this app**](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#kill)
  
  - Although this is frequently, incident is unknown
    　　 

- When `fannel` suddenly not working, update latest `CommandClick` and `fannel`.
  
  - `CommandClick` and `fannel` is new baby. Therefre, these is frequetly updated without backwards compatible.　I continuly enforce usability and fanctionality. Before long, if these grow up adult, I weight stability. But, please feel at ease, most often even now, latest version works fine.  

Ubuntu, debian and windows version <a id="ubuntu-debian-or-widnows-version"></a>
-----------------

-> [ubuntu, debian or windows version](https://github.com/puutaro/cmdclick)  
