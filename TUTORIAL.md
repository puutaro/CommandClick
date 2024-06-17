# Tutorial

<img src="https://github.com/puutaro/CommandClick/assets/55217593/e4e6f75b-a35e-47f1-bb41-144d8ea88185" width="500">  

`CommandClick`'s true value is low code tool.  
Ofcourse, `CommandClick` can use as web browser, mobile ubuntu terminal. These feature is very useful.  
However that alone don't know `CommandClick`'s true aim.   
`CommandClick` is android app maker.   
It can also be made with surprisingly litle effort.   
The less effort, the better.   
Do you play date to prety girl and delicious lunch to decrease time for code implementation time?    
Becuase `CommmandClick` itself include ubuntu and web browser, due to effort, you can do everything.   
But, it's is worst.  
`CommandClick`'s significance is less `effort` development.  
Although this tool require understanding specification ofcourse, it's aime save your effort. 
This tutorial tell you knowledge for low cost.  
I hope your android development life will become easy and fast.  

Requirement
-----------------

Javascript code basic knowledge.   
In [By notification](#by-notification) section, require shellscript basic knowledge.  


Table
-----------------
<!-- vim-markdown-toc GFM -->

* Best 4 hello world
    * [By alert](#by_alert_hello_world)
    * [By text to speech1](https://github.com/puutaro/CommandClick/blob/master/md/developer/tutorial/by_text_to_speech_hello_world.md)
    * [By text to speech2 game](https://github.com/puutaro/CommandClick/blob/master/md/developer/tutorial/by_text_to_speech_hello_world2_game.md)
    * [By notification](https://github.com/puutaro/CommandClick/blob/master/md/developer/tutorial/by_notification_hello%20world.md)


# By alert, hello world  <a id="by_alert_hello_world"></a>

This is required section if you want to `CommandClick`'s [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) creator.    
[Funnels](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) are surprisingly easy to make.  
You experience `CommandClick`'s impact as low code tool.  


By alert tutorial table
-----------------

* [Step 1](#step-1)
* [Step 2](#step-2)
* [Step 3](#step-3)

## Step 1

Create `hello_world_by_alert.js`.  

```js.js
// [1]
/// SETTING_SECTION_START
// [2]
editExecute="NO"
// [3]
scriptFileName="hello_world_by_alert.js"
/// SETTING_SECTION_END

alert("hello world");
```

[1] -> [setting section](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#scriptfilename)  
[2] -> [editExecute](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#editexecute)  
[3] -> [scriptFileName](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#scriptfilename)  


- `CommandClick` js [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) is bookmarklet.  

- Js [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) require `;` by each line. 

- This code is equal bellow code

```js.js
alert("hello world");
```

## Step 2

Copy `hello_world_by_alert.js` to `/storage/emulated/0/Documents/cmdclick/default` directory<sub>[1]</sub> 

[1] -> [app directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory)

## Step 3

Execute by [run](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#run)  

<img src="https://github.com/puutaro/CommandClick/assets/55217593/dda94eb8-e865-4672-9b33-93df9e7e83f9" width="400">  

- Enable to execute from [url history](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#url-history) by bellow step

1. Click [edit preference](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#edit-preference) in `setting`
2. Click `add` button in [homeScriptUrlsPath](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#homescripturlspath)
3. Click `hello_world_by_alert.js` in grid box
4. Close edit box.
5. Click `hello_world_by_alert.js` in [url history](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#url-history)


## Other tutorial check bellow


-> [By text to speech1](https://github.com/puutaro/CommandClick/blob/master/md/developer/tutorial/by_text_to_speech_hello_world.md)  
-> [By text to speech2 game](https://github.com/puutaro/CommandClick/blob/master/md/developer/tutorial/by_text_to_speech_hello_world2_game.md)  
-> [By notification](https://github.com/puutaro/CommandClick/blob/master/md/developer/tutorial/by_notification_hello%20world.md)  
