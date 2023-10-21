# filter

Table
-----------------
* [Result](#result)
* [Argument](#argument)


## Result

Save filterd tsv instance with tag, also header  

```js.js
jsCsv.filter(
  srcTag: String,
  destTag: String,
  tabSepaFormura: String ({schema1},>,1500\t{schema2},in,Monday,\t{schema3},=,super man\t..)  
)
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| srcTag | string | csv data src tag that you want to filter |
| destTag | string | csv destination tag that you want to output |
| tabSepaFormura | string | filter query |


- tabSepaFormura conditions table

| condition  | description |
| --------  | -------- |
| `>` | filter by greater left than right |
| `<` | filter by less left than right |
| `>=` | filter by equal greater left than right |
| `<=` | filter by equal less left than right |
| `=` | filter by equal left to right |
| `in` | filter by include left to right |

- left, condition, right sepalated by `,` 
- Each condition concat by tab
 


ex1) 

```js.js
jsCsv.filter(
  "srcTag1",
  "destTag1",
  {schema1},>,1500\t{schema2},in,Monday,\t{schema3},=,super man\t..)  
)
```

