# convertUbuntuPath


Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Convert normal path to ubuntu path 

- [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel)

```js.js
jsPath.convertUbuntuPath(
  path: String
)

- Convert path without `/storage` prefix

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| path | string | file path |


ex1) 

```js.js
jsPath.convertUbuntuPath(  
  "/home/ubuntu",  
)
// -> "/data/user/0/com.puutaro.commandclick/files/1/rootfs/home/ubuntu"
```

ex1) 

```js.js
jsPath.convertUbuntuPath(  
  "/storage/emulated/0/Documents/cmdclick/AppDir/defaut",  
)
// -> "/storage/emulated/0/Documents/cmdclick/AppDir/defaut"
```


