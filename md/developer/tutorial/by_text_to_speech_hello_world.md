# By text to speech, hello world

This 
This is required section if you want to `CommandClick`'s [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) creator.    
[Funnels](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) are surprisingly easy to make.  
You experience `CommandClick`'s impact as low code tool.  


Table
-----------------

* [Step 1](#step-1)
* [Step 2](#step-2)
* [Step 3](#step-3)

## Step 1

Create `hello_world_by_alert.js`.  

```js.js
alert("hello world");
```

- `CommandClick` js [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) is bookmarklet.  

- Js [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) require `;` by each line. 

- This code is equal bellow code

```js.js
/// SETTING_SECTION_START
editExecute="NO"
scriptFileName="hello_world_by_alert.js"
/// SETTING_SECTION_END

alert("hello world");
```

-> [setting section](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#scriptfilename)  
-> [editExecute](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#editexecute)  
-> [scriptFileName](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#scriptfilename)  

## Step 2

Copy `hello_world_by_alert.js` to `/storage/emulated/0/Documents/cmdclick/default` directory

- [app directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory)

## Step 3

Execute by [run](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#run)  

- Enable to execute from [url history](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#url-history) by bellow step

1. Click [edit startup](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#edit-startup) in `setting`
2. Click `add` button in [homeScriptUrlsPath](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#homescripturlspath)
3. Click `hello_world_by_alert.js` in grid box
4. Close edit box.
5. Click `hello_world_by_alert.js` in [url history](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#url-history)


