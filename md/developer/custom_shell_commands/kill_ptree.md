# kill_ptree


Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
  * [broadcast_action](#broadcast_action)
  * [extras](#extras)
* [example](#example)
  

## Overview

Kill All Process Tree


```sh.sh

kill_ptree \
	"${shell path1 or keyword1}" "${shell path2 or keyword2}" ..
```


## example

ex1) Kill current file's all process

```sh.sh
kill_ptree \
  "${0}"

```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)


ex2) Kill by keyword

```sh.sh
kill_ptree \
  "key word1" "key word2"

```
