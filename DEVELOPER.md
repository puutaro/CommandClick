
# Command Click Developer page
<img src="https://github.com/puutaro/CommandClick/assets/55217593/e4e6f75b-a35e-47f1-bb41-144d8ea88185" width="700">  

  
This page is for developer. CommandClick true value change self made script to android app.   
I hope you get that knowledge.  
Mainly, `ComamndClick` is enforced by javascript. So, most bellow context is for javascript development.  
Although via `termux`, enforced by shellscript, this page's main contens is javascript.  


Table of Contents
-----------------
<!-- vim-markdown-toc GFM -->

* [Structure](#structure)
* [Fannel structure](#fannel-structure)
* [Labeling section](#labeling-section)
* [Setting variable](#setting-variable)
* [Import library](#import-library)
	* [Local path import](#local-path-import)
	* [Assets import](#assets-import)
	* [WEB import](#web-import)
* [Url command](#url-command)
* [Html automaticaly creation command to edit target edit file](#html-automaticaly-creation-command-to-edit-target-edit-file)
* [File api](#file-api)
* [JavaScript interface](#javascript-interface)
* [Javascript pre reserved word](#javascript-pre-reserved-word)
* [Include Javascript Library](#include-javascript-library)
* [Include css Library](#include-css-library)
* [Html tag output](#html-tag-output)
* [Html tag output](#html-tag-output)
* [Javascript TroubleShooting](#javascript-troubleshooting)
* [CommandClick repository](#commandclick-repository)


### Structure


<img src="https://github.com/puutaro/CommandClick/assets/55217593/e06a623e-0fd6-4325-ac9f-b795e2d2a4aa" width="500">  

### Fannel structure

`fannel` is `ComamndClick` using script  

<img src="https://github.com/puutaro/CommandClick/assets/55217593/866958e3-8643-4cf0-b610-000f8245397f" width="400">  

- labeling section

- setting variable contents  
  -> [Setting variable](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md)

- cmd variable contents  
  user difinition setting variables  

- script contents  
  `javascript`' or `shellscript`' contents


### Labeling section

This section is description for fannel(js or shell file enforced by `CommandClick`).

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/labeling_section.md)

### Setting variable 

  `CommandClick`'s system setting variables  
  
  -> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md)

  

### Import library  

`CommandClick` can import all file with uri. We can use javascript all of the world. This is `CommandClick` basic idea.  
`CommandClick` is open world app, as is, web browser, termux client, applicatoin maker,  applicatoin store, and library terminal.    
Bellow is how to import. You can enjoy this all range import application!  

#### Local path import

```js.js
ccimport {path}   
```

* current directory -> `./`  
* move parent direcoty -> ../  
* other check [Javascript pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)   

#### Assets import

```js.js
ccimport /android_asset/{relative path}  
```

#### WEB import

```js.js
ccimport {URL}  
```

* It is possible to download by curl {URL}


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




### File api
`CommandClick` automaticaly create files in `App directory`/`system`/`url`. This is used by system, alse is userinterface for app signal.
- `cmdclickUrlHistory` 
      - CommandClick use recent used url launch etc.
- `urlLoadFinished`
      - This is made when url load finished. When you make `fannenl`(javascript, shell, and html application), you may use this.

### JavaScript interface

-> [javascript interface directory](https://github.com/puutaro/CommandClick/tree/master/md/developer/js_interface)  

`CommandClick` is javascript framework for andorid. Particularly, this methods strongly support your android app development(`fannel` development).  
This, so colled, android app row code library.


### Javascript pre reserved word

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md) 


### Include Javascript Library  

First, I respect bellow javascript package author.  
Bellow respectable package is inclided assets. you can import like bellow.

- Sortable.js -> Add html with `<script type="text/javascript" src="file:///android_asset/js/Sortable.js"></script>`  
- jquery-ui -> Add html with `<script type="text/javascript" src="file:///android_asset/js/jquery-ui.min.js"></script>`  
- jquery-3.6.3.min.js -> Add html with `<script type="text/javascript" src="file:///android_asset/js/jquery-3.6.3.min.js"></script>`  
- long-press-event.min.js -> Add html with `<script type="text/javascript" src="file:///android_asset/js/long-press-event.min.js"></script>`  
- chart.min.js -> Add html with `<script src="file:///android_asset/js/chart.min.js" ></script>`  
- chartjs-adapter-date-fns.bundle.min.js -> Add html with `<script src="file:///android_asset/js/chartjs-adapter-date-fns.bundle.min.js"></script>`  


### Include css Library  

First, I respect bellow css package author.  
Bellow respectable package is inclided assets. you can import like bellow.

- jquery-ui.css -> Add html with `<link rel="stylesheet" href="file:///android_asset/css/jquery-ui.css">`  



### Html tag output

`CommandClick` script output trminal as html, so html tag is valid. You can use tag by bellow.
 - `<` -> `cmdclickLeastTag`
 - `>` -> `cmdclickGreatTag`

   - `Span tag` no working in script output. If you wont to use this, launch html file.
   - Url string automaticaly change anchor tag, but if you put 'href="' prefix in front of this string, no auto change.



### Javascript TroubleShooting  


- When your javascript's file cannot execute, you confirm how script step semicolon(`;`) exist except for function argument.  
	- Becuase javaxcript file convert one linear script string, as it, javascript:(function() { `${js contents}` })(); and webvoew.loadUrl().  

- Javascript's `while roop` ocationaly cuase crush. add bellow code to the roop.  

```js.js
	if(
		jsStop.how().includes("true")
	) throw new Error('exit');
```  


- Optinaly may replace delay function with `jsUtil.sleep($milisecond);`
	- The `Roop crush` is occur by memory leak.



### Commandclick-repository

CommandClick's fannel repository

`fannel` is ComamndClick using script (javascript, and shellscript)
For instance, your click script in CommandClick. One thing I can say that CommandClick is developed for the purpose of changing javaxcript and shellscript to gui appication. That applies to click script. It's so called Gui application. We can say so. I call the gui application `fannel`
  
[link](https://github.com/puutaro/commandclick-repository)  





