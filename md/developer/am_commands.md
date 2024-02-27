# am commands

`CommandClick` has broadcast am command bellow.

Tables
-----------------
<!-- vim-markdown-toc GFM -->

* [Url command](#url-command)
* [Html automaticaly creation command to edit target edit file](#html-automaticaly-creation-command-to-edit-target-edit-file)


### Url command

Exec bellow command in `CommandClick` shellscript, so that you can launch web site.
(This command is only active when command click focus)

```sh.sh
am broadcast \
 -a "com.puutaro.commandclick.url.launch" \
 --es url "{url}"
```

```sh.sh
ex) am broadcast \
 -a "com.puutaro.commandclick.url.launch" \
 --es url "https://github.com/puutaro/CommandClick/edit/master/README.md"
```


### Html automaticaly creation command to edit target edit file 

Exec bellow command in `CommandClick` shellscript, so that you can make automaticaly make html, css and javascript.
(This command is only active when command click focus)

```sh.sh
am broadcast \
		-a "com.puutaro.commandclick.html.launch" \
		--es edit_path "{target edit file path}" \
		--es src_path "{source file path}" \
		--es on_click_sort "boolean(sortable when link click)" \
		--es on_sortable_js "boolean(sortable link list)" \
		--es on_click_url "boolean(launch url when link click)" \
		--es filter_code "{javascript filter code}"
``` 

  - `edit_path` is file path edit by html, also file name is title  
	- ex)  Target edit file is tsv, which composed two row.
                urltitle1  urlString1
                urltitle2  urlString2
                urltitle3  urlString3
                .
                .
                .  
		  
  - (Optional) `src_path` is source file path for input text string, Ordinaly, first hit one line's title display default string, and hold first hit line's url  
	- ex)  Source file is tsv, which composed two row like above.  
  - (Optional) `on_click_sort` is how to sort top when link click.  
  - (Optional) `filter_code` filter target source file  by javascript code. default value is `true`. You can use `urlString` and `urlTitle` variable to filter.  
  
```sh.sh
ex) am broadcast \
		-a "com.puutaro.commandclick.html.launch" \
		--es edit_path "${PARENT_DIR_PATH}/tubePlayList" \
		--es src_path "${PARENT_DIR_PATH}/cmdclickUrlHistory" \
		--es on_click_sort "false" \
		--es on_sortable_js "true" \
		--es on_click_url "true" \
		--es filter_code "urlString.startsWith('http') && urlString.includes(\"youtube\");"
```

- edit html esxample

<img src="https://user-images.githubusercontent.com/55217593/222952726-f5ce0753-f299-44cd-a9b0-a021c56d3b4c.png" width="400">  


