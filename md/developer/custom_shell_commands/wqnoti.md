# wqmsg

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
  * [--help, -h](#help) 
  * [--pid, -p](#pid)
  * [--channel-num, -cn](#channel-num)
  * [--importance, -i](#importance)
  * [--icon-name, -in](#icon-name)
  * [--title, -t](#title)
  * [--cancel-shell-path, -cs](#cancel-shell-path)
* [example](#example)

## Overview

Toast wait quiz until backgrond process complete


```sh.sh
wqmsg \
  "${pid}" \
  "${mark string}"
```

## Argument

### --help, -h <a id="help"></a>

help contents

### --pid, -p <a id="pid"></a>

Wait target pid

### --channel-num, -cn <a id="channel-num"></a>

-> [About channel num](https://github.com/puutaro/CommandClick/blob/master/md/developer/custom_shell_commands/noti.md#channel_num)

### [Optional] --importance, -i <a id="importance"></a>

-> [About importance](https://github.com/puutaro/CommandClick/blob/master/md/developer/custom_shell_commands/noti.md#importance)

### [Optional] --icon-name, -in <a id="icon-name"></a>

-> [About icon name](https://github.com/puutaro/CommandClick/blob/master/md/developer/custom_shell_commands/noti.md#icon_name)

- default icon macro name `download`

### [Optional] --title, -t <a id="title"></a>

[About title](https://github.com/puutaro/CommandClick/blob/master/md/developer/custom_shell_commands/noti.md#title)

### [Optional] --cancel-shell-path, -cs <a id="cancel-shell-path"></a>

[About delete](https://github.com/puutaro/CommandClick/blob/master/md/developer/custom_shell_commands/noti.md#delete)

- with `CANCEL` button action

## example

ex1)

```sh.sh

sleep 60 &
sleep_pid=$!

wqnoti \
	-p "${sleep_pid}" \
	-cn 31 \
	-i "high" \
	--title "[1/2] Summary downlaod.." \
	--cancel-shell-path "${NEWS_SPEECHER_STOP_ALL_PROCESS_SHELL_PATH}"

```
