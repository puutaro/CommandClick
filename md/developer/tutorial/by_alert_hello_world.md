# By alert, hello world

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



