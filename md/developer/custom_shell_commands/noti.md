# noti

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
  * [notification-type](#notification_type)
  * [channel_num](#channel_num)
  * [importance](#importance)
  * [icon_name](#icon_name)
  * [alert_once](#alert_once)
  * [title](#title)
  * [message](#message)
  * [delete](#delete)
  * [button](#button)
  * [notification_style](#notification_style)
  * [example](#example)
  

## Overview

Launch notification on android, for background service, etc.


```sh.sh

noti \
	{arguments}
```

## Argument

### --notification-type, -t <a id="notification_type"></a>

- notification type table

| type | description |
| ------ | -------|
| launch | launch notification |
| exit | close notification |

### --channel-num, -cn <a id="channel_num"></a>

channel num (int)
Specify 30+ number, becuase of 1 ~ 30 system channel num 

### [Optional] --importance, -i <a id="importance"></a>

- notification importance table

| type | description |
| ------ | -------|
| high | importance high |
| low | importance low (default)|


### [Optional] --icon-name, -in <a id="icon_name"></a>

Can specify　bellow pre reserved icon names

-> [pre reserved icon names](https://github.com/puutaro/CommandClick/blob/master/md/developer/collection/icons.md)


### [Optional] --alert-once, -o <a id="alert_once"></a>

Enable notification alert only once 

### [Optional] --title, -t <a id="title"></a>

title string

### [Optional] --message, -m <a id="message"></a>

message string

### [Optional] --delete, -d <a id="delete"></a>

notification delete action 

- action key table

| key | description |
| ------ | -------|
| shellPath | execute shell script path |
| execType | exec type: `fore`(default)/`back` |
| args | args sepalated by `&` |
| timeout | time out mili sec string |


- enalble this option by concat `,` 

ex) 
    --delete="shellPath=${shell path},args=aa&bb" 


### [Optional] --button, -b <a id="button"></a>

set button 

- button action key table

| key | description |
| ------ | -------|
| label | button label, enable to specify icon in media style by MACRO: `CANCEL`, `PREVIOUS`, `FROM`, `STOP`, `PLAY`, `TO`, `NEXT` |
| shellPath | execute shell script path |
| execType | exec type: `fore`(default)/`back` |
| args | args sepalated by `&` |
| timeout | time out mili sec string |

* enable this option by concat ','
* enable multiple spedified up to 5 with concat `,`
 
ex)   
    --button="label=button1,shellPath=${shellPath1},args=arg1"
    --button="label=button2,shellPath=${shellPath2},args=arg1&arg2"
    --button="label=button3,shellPath=${shellPath3}"
    --button="label=button4,shellPath=${shellPath4}"
    --button="label=button5,shellPath=${shellPath5}"




### [Optional] --notification-style, -s <a id="notification_style"></a>

Enable to specify only `media`
- compactActionsInts: specify button index up to 3 in compact,  

| key | description |
| ------ | -------|
| type | Enable to specify only `media` |
| compactActionsInts | specify button index up to 3 in compact |

ex)   
    --notification-style="type=media,compactActionsInts=0&1&3"

## example

ex1) launch notification  

```sh.sh
noti \
	-t launch \
	-cn 20 \
	--icon-name play \
	--importance high \
	--title "${title}" \
	--message "${message}" \
	--alert-once \
	--delete "shellPath=${NOTI_EXIT_SHELL_PATH},args=${NOTIFICATION_CAHNEL_NUM}" \
	-s "type=media,compactActionsInts=0&2&4" \
	--button "label=PREVIOUS,shellPath=${NOTI_PRV_SHELL_PATH}" \
	--button "label=FROM,shellPath=${NOTI_FROM_SHELL_PATH}" \
	--button "label=PAUSE,shellPath=${NOTI_PAUSE_SHELL_PATH}" \
	--button "label=TO,shellPath=${NOTI_TO_SHELL_PATH}" \
	--button "label=NEXT,shellPath=${NOTI_NEXT_SHELL_PATH}" \
>/dev/null 2>&1

```

ex2)  

```sh.sh
noti \
	-t exit \
	-cn "20"


```
