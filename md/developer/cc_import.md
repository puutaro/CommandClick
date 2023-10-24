# CC import

`CommandClick` can import all file with uri. We can use javascript all of the world. This is `CommandClick` basic idea.  
`CommandClick` is open world app, as is, web browser, termux client, applicatoin maker,  applicatoin store, and library terminal.    
Bellow is how to import. You can enjoy this all range import application!  


Table
-----------------
* [Local path import](#local-path-import)
* [Assets import](#assets-import)
* [WEB import](#web-import)


## Local path import

```js.js
ccimport {path}   
```

* current directory -> `./`  
* move parent direcoty -> ../  
* other check [Javascript pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)   

## Assets import

```js.js
ccimport /android_asset/{relative path}  
```

## WEB import

```js.js
ccimport {URL}  
```

* It is possible to download by curl {URL}

