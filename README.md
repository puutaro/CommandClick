![cmdclick_image](https://user-images.githubusercontent.com/55217593/199425521-3f088fcc-93b0-4a84-a9fd-c75418f40654.png)
# Command Click
So called 'shell browser', It's termux gui client with web browser feature

![image](https://user-images.githubusercontent.com/55217593/216516311-c65c2795-30e3-4487-bd13-0fe8f7e72cdf.png)

It's a shellscript manager app from gui that have execution, edit, delete, and create as feature.

Pros
----
- Easily turn shellscript into a GUI application in android.
- Versatile usage for Terminal, Crome, OS setting, etc.
- Not only termux gui client but also web browser.
- Offer ritch edit dialog to termux.


Table of Contents
-----------------
<!-- vim-markdown-toc GFM -->

* [Pre Setting](#pre-setting)

* [Upgrading](#upgrading)
  * [Upgrading for windows 10](#upgrading-for-windows10)
  * [Upgrading for Ubuntu or Debian](#upgrading-for-ubuntu-or-debian)
* [Demo](#demo)
* [Usage](#usage)
  * [Launch](#launch)
  * [Add](#add)
  * [Run](#run)
  * [Edit](#edit)
  	* [by gui](#by-gui)
  	* [by editor](#by-editor)
  	* [description by gui](#description-by-gui)
  * [Exit](#exit)
  * [Move](#move)
  * [Install](#install)
  * [Setting](#setting)
  * [Delete](#delete)
  * [App directory manager](#app-directory-manager)
      * [Launch](#launch)
      * [Add](#add)
      * [Change directory](#change-directory)
      * [Edit](#edit)
      * [Exit](#exit)
      * [Delete](#delete)
  * [Shell to Gui](#shell-to-gui)
  * [Shortcut table](#shortcut-table)
  * [Trouble Shouting](#trouble-shouting)
  	 * [Not Startup](#not-startup)


Pre Setting
-----
Command Click is use [`RUN_COMMAND` Intent](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent) in termux  
and, require termux storage setting.
For Instance, bellow process.
1. Add com.termux.permission.RUN_COMMAND permission
      `Android Settings` -> `Apps` -> `CommandClick` -> `Permissions` -> `Additional permissions` -> `Run commands in Termux environment`
3. Enable `allow-external-apps` [detail](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent#allow-external-apps-property-mandatory)
4. Add Storage permission. [detail](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent#storage-permission-optional)
5. Execute `termux-setup-storage` on termux

 
