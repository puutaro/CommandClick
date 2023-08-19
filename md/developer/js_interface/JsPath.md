# JsPath

File path edit interfce  


```js.js

jsPath.compPrefix(  
  path: String,  
  prefix: String,  
  )
  -> complete prefix     
 
jsPath.compExtend(  
  path: String,  
  extend: String  
    )
  -> complete suffix    
 
jsPath.checkExtend(  
  tag: String,  
  extendTabSeparateStr: tab separated String  
  )
  -> boolean (true when including tab separated extend String)

jsPath.checkPrefix(
  name: String,  
  prefixTabSeparateStr: String  
    )
  -> boolean (true when including tab separated prefix String)

jsPath.removeExtend(  
  path: String,  
  extend: String  
)
  -> remove extend 

jsPath.removePrefix(  
  path: String,  
  prefix: String  
    )
  -> remove prefix      

```

```
