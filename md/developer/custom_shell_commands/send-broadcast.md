# send-broadcast

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
  * [broadcast_action](#broadcast_action)
  * [extras](#extras)
* [example](#example)
  

## Overview

Broadcast sender


```sh.sh

send-broadcast \
	{arguments}
```

## Argument

### --action, -a <a id="broadcast_action"></a>

Intent action in broadcast

[broadcastactions](https://github.com/puutaro/CommandClick/blob/master/md/developer/broadcastActoins.md#broadcastactions)

ex)   

  --action "com.puutaro.commandclick.url.launch"

### --extras, -e <a id="extras"></a>

Intent's extra string

ex)  

  --extras "https://github.com/puutaro/CommandClick"


## example

ex1) [launch url](https://github.com/puutaro/CommandClick/blob/master/md/developer/broadcastActoins.md#launch_url)  

```sh.sh
send-broadcast \
	-a "com.puutaro.commandclick.url.launch" \
	-e "https://github.com/puutaro/CommandClick" \
>/dev/null 2>&1

```

ex2) [launch edit site](https://github.com/puutaro/CommandClick/blob/master/md/developer/broadcastActoins.md#launch_edit_site)

```sh.sh
send-broadcast \
	-a "com.puutaro.commandclick.html.launch" \
	-e "edit_path=${tubePlayListPath}" \
	-e "src_path=" \
	-e "on_click_sort=false" \
	-e "on_sortable_js=true" \
	-e "on_click_url=true" \
	-e "filter_code=true" \
>/dev/null 2>&1

```
