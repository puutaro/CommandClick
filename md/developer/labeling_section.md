# Labeling section


Table
-----------------
<!-- vim-markdown-toc GFM -->

* [Overview](#overview)
* [How to write](#How-to-write)
	* [Enable specify markdown file path](#enable-specify-markdown-file-path)


## Overview

This section is description for `fannel`  

## How to write

This notation is markdown. At the same time, require comment out for js or shell.

ex1) markdown for js with comment out

```js.js

/// LABELING_SECTION_START
// Display summary for current site @puutaro
// ## Support long press menu
// ---------

// | type | enable |
// | ----- | ----- |
// | src anchor | o |
// | src image anchor | o |
// | image | x |

/// LABELING_SECTION_END

```


ex2) markdown for shell with comment out

```sh.sh

### LABELING_SECTION_START
 Display summary for current site @puutaro
# ## Support long press menu
# ---------

# | type | enable |
# | ----- | ----- |
# | src anchor | o |
# | src image anchor | o |
# | image | x |

### LABELING_SECTION_END

```

#### Enable specify markdown file path

In this case, specified file is markdown file with `md` extend.  

```js.js

/// LABELING_SECTION_START
// file://${01}/${001}/markdown.md
/// LABELING_SECTION_END

```

markdown.md
```md.md

Display summary for current site @puutaro

## Support long press menu
---------

| type | enable |
| ----- | ----- |
| src anchor | o |
| src image anchor | o |
| image | x |

```
