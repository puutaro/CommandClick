# JS import

`CommandClick` can import all file with uri. We can use javascript all of the world. This is `CommandClick` basic idea.  
`CommandClick` is open world app, as is, web browser, termux client, applicatoin maker,  applicatoin store, and library terminal.    
Bellow is how to import. You can enjoy this all range import application!  


Table
-----------------
* [Local path import](#local-path-import)
* [Assets import](#assets-import)
* [WEB import](#web-import)
* [Enable replace variariables](enable-replace-variariables)


## Local path import

```js.js
jsimport {path}   
```

* current directory -> `./`  
* move parent direcoty -> ../  
* other check [Javascript pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)   


## Assets import

```js.js
jsimport /android_asset/{relative path}  
```


## WEB import

```js.js
jsimport {URL}  
```

* It is possible to download by curl {URL}


## Enable replace variariables

ex1) local js script

```js.js
jsimport "{replace variable name}/libs/test.js"   
```

ex2) assets

```js.js
jsimport /android_asset/{replace variable name for asset}  
```

ex3) WEB

```js.js
jsimport {replace variable name for url}  
```

-> [Replace variariables](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md)
