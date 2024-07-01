# checkPrefix


Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Check prefix  
-> Boolean (true when including tab separated prefix String)


```js.js
jsPath.checkPrefix(
  name: String,  
  prefixTabSeparateStr: String  
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| name | string | file name |
| prefixTabSeparateStr | string | prefix list sepalated by tab |


ex1) 

```js.js
jsPath.checkPrefix(
  "prefixFile.txt",  
  "prefix\tprefix1"  
)
-> true

```

ex2) 

```js.js
jsPath.checkPrefix(
  "prefix2File.txt",  
  "prefix\tprefix1"  
)
-> false

```

