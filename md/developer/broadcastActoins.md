# broadcastActions

`CommandClik` has custom broadcast action.   
Here, introduce these.



Table
-----------------
* [`com.puutaro.commandclick.url.launch`](#launch_url)
    * [Schema](#launch_url_schema) 
* [`com.puutaro.commandclick.html.launch`](#launch_edit_site)
    * [Schema](#launch_edit_site_schema) 
* [`com.puutaro.commandclick.url.monitor_text_path`](#monitor_text_path)
* [`com.puutaro.commandclick.text_to_speech.stop`](#text_to_speech_stop)
* [`com.puutaro.commandclick.text_to_speech.previous`](#text_to_speech_previous)
* [`com.puutaro.commandclick.text_to_speech.from`](#text_to_speech_from)
* [`com.puutaro.commandclick.text_to_speech.to`](#text_to_speech_to)
* [`com.puutaro.commandclick.text_to_speech.next`](#text_to_speech_next)
* [`com.puutaro.commandclick.pulse_server.stop`](#pulse_server_stop)
* [`com.puutaro.commandclick.pulse_server.restart`](#pulse_server_restart)
* [`com.puutaro.commandclick.ubuntu_service.start`](#ubuntu_service_start)
* [`com.puutaro.commandclick.ubuntu_service.stop`](#ubuntu_service_stop)
* [`com.puutaro.commandclick.ubuntu_service.is_active`](#ubuntu_service_is_active)
* [`com.puutaro.commandclick.ubuntu_service.background_cmd_kill`](#ubuntu_service_background_cmd_kill)
    * [Schema](#ubuntu_service_background_cmd_kill_schema)   
* [`com.puutaro.commandclick.ubuntu_service.background_cmd_start`](#ubuntu_service_background_cmd_start)
    * [Schema](#ubuntu_service_background_cmd_start_schema)   
* [`com.puutaro.commandclick.ubuntu_service.admin_cmd_start`](#ubuntu_service_admin_cmd_start)
    * [Schema](#ubuntu_service_admin_cmd_start_schema)   
* [`com.puutaro.commandclick.ubuntu_service.open_fannel`](#ubuntu_service_open_fannel)
    * [Schema](#ubuntu_service_open_fannel_schema)   
* [`com.puutaro.commandclick.ubuntu_service.shell2http`](#ubuntu_service_shell2http)
    * [Schema](#ubuntu_service_shell2http_schema)   

## `com.puutaro.commandclick.html.launch` <a id="launch_url"></a>

Launch url in `CommandClick` 

- Enable in monitor visible
  
### Schema <a id="launch_url_schema"></a>

| schema | type | description | 
| --------- | --------- | --------- |
| url | string | url string |


## `com.puutaro.commandclick.html.launch` <a id="launch_edit_site_schema"></a>

Launch html to enable edit bellow tsv.  

- edit target path

```
edit target title1\turl1
edit target title2\turl2
.
.
.
```

- add src tsv path

```
add src title1\turl1
add src title2\turl2
.
.
.
```

- edit site image
  
<img src="https://user-images.githubusercontent.com/55217593/222952726-f5ce0753-f299-44cd-a9b0-a021c56d3b4c.png" width="400">  

### Schema <a id="launch_edit_site"></a>

| schema | type | description | 
| --------- | --------- | --------- |
| edit_path | string | edit target tsv path |
| src_path | string | add src tsv path |
| on_click_sort | boolean string | sort on click: `true`/`false` |
| on_sortable_js | boolean string | enable sorting element: `true`/`false` |
| on_click_url | boolean string | enable to jump on click element: `true`/`false` |
| on_dialog | boolean string | edit target tsv path: `true`/`false` |
| filter_code | js code to return boolean | js code to filter adding url |

## `com.puutaro.commandclick.url.monitor_text_path` <a id="monitor_text_path"></a>

Monitor update by [temp monitor update contents file](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#update_monitor)

- Before this broadcast, write upadate contents to [temp monitor update contents file](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#update_monitor)

## `com.puutaro.commandclick.text_to_speech.stop` <a id="text_to_speech_stop"></a>

Stop text to speech 

## `com.puutaro.commandclick.text_to_speech.previous` <a id="text_to_speech_previous"></a>

Back to previous track in text to speech play list

## `com.puutaro.commandclick.text_to_speech.from` <a id="text_to_speech_from"></a>

Back to previous section in text to speech track  

## `com.puutaro.commandclick.text_to_speech.to` <a id="text_to_speech_to"></a>

Forward to next section in text to speech track   

## `com.puutaro.commandclick.text_to_speech.next` <a id="text_to_speech_next"></a>

Forward to next track in text to speech play list  

## `com.puutaro.commandclick.pulse_server.stop` <a id="pulse_server_stop"></a>

Stop pulse audio reciever service 

## `com.puutaro.commandclick.pulse_server.restart` <a id="pulse_server_restart"></a>

Start & re-satrt pulse audio reciever service 

## `com.puutaro.commandclick.ubuntu_service.start` <a id="ubuntu_service_start"></a>

Start ubuntu service  

## `com.puutaro.commandclick.ubuntu_service.stop` <a id="ubuntu_service_stop"></a>

Stop and re-start ubuntu service  

## `com.puutaro.commandclick.ubuntu_service.is_active` <a id="ubuntu_service_is_active"></a>

Ubuntu service active check. On active, put [isActiveUbuntuService.txt](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#is_active_ubuntu_service)

## `com.puutaro.commandclick.ubuntu_service.background_cmd_kill` <a id="ubuntu_service_background_cmd_kill"></a>

Kill specific sript path process tree.  

### Schema <a id="ubuntu_service_background_cmd_kill_schema"></a>

| schema | type | description | 
| --------- | --------- | --------- |
| ubuntu_croutine_job_type | string | shell script paths sepalated by tab |


## `com.puutaro.commandclick.ubuntu_service.background_cmd_start` <a id="ubuntu_service_background_cmd_start"></a>

Start background shell script  

### Schema <a id="ubuntu_service_background_cmd_start_schema"></a>

| schema | type | description | 
| --------- | --------- | --------- |
| shell_path | string | shell script path |
| cmd_args_tab_sepa_str | string | args sepalated by tab |
| monitorFileName | string | script stdout output file: [term[1-4]](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#output_monitor) |

## `com.puutaro.commandclick.ubuntu_service.admin_cmd_start` <a id="ubuntu_service_admin_cmd_start"></a>

Start admin shell script

### Schema <a id="ubuntu_service_admin_cmd_start_schema"></a>

| schema | type | description | 
| --------- | --------- | --------- |
| shell_path | string | shell script path |
| cmd_args_tab_sepa_str | string | args sepalated by tab |
| monitorFileName | string | script stdout [output file](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#output_monitor) |


## `com.puutaro.commandclick.ubuntu_service.open_fannel` <a id="ubuntu_service_open_fannel"></a>

Launch [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel)

### Schema <a id="ubuntu_service_open_fannel_schema"></a>

| schema | type | description | 
| --------- | --------- | --------- |
| fannelDirPath | string | [fannel dir path](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory) |
| fannelName | string | fannel name |


## `com.puutaro.commandclick.ubuntu_service.shell2http` <a id="ubuntu_service_shell2http"></a>

Start foreground shell script

### Schema <a id="ubuntu_service_shell2http_schema"></a>

| schema | type | description | 
| --------- | --------- | --------- |
| shell_path | string | shell script path |
| cmd_args_tab_sepa_str | string | args sepalated by tab |

