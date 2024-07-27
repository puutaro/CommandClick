Table
-----------------
* [Result](#result)
* [Argument](#argument)


## Result

Show multi select list dialog
-> selected elements string seprated by newline 


```js.js

jsDialog.multiListDialog(
	title: String,  
	currentItemListNewlineSepaStr: String,  
	preSelectedItemListNewlineSepaStr: String,  
    )
```

## Argument

| arg name | type | description                               |
| -------- | -------- |-------------------------------------------|
| title | string | title string                              |
| currentItemListNewlineSepaStr | string | list string sepalated by newline          |
| preSelectedItemListNewlineSepaStr | string | selected elements string sepalated by newline |


ex1)   

```js.js

jsDialog.multiListDialog(
	"sample",  
	"item1\nitem2\nitem3\nitem4\nitem5"  
	"item1\nitem3",  
);

(select item3 item5)

	-> "item3\nitem5";

```

