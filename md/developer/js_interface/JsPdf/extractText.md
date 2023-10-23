# extractText


Table
-----------------

* [Result](#overview)
* [Argument](#argument)


## Result

Extracted text from pdf


```js.js
jsPath.extractText(  
  path: string  
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| path | string | pdf file path |



ex1) comp

```js.js
jsPath.extractText(  
  "${01}/${001}/pdf.pdf"
)
-> pdf text

```
- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)

