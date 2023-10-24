# get_rvar

Table
-----------------
* [Overview](#overview)
* [js -> shell usage](#js_shell_usage)
    * [Get replace variables contents](#get-replace-variables-contents)
    * [Get variable value by variable name](#get-variable-value-by-variable-name)
* [shll, shell -> shell usage](#shell_shell_shell_usage)
    * [Get variable value by variable name](#get-variable-value-by-variable-name)

## Overview

[Replace variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md) getter

```sh.sh

get_rvar \
	{arguments}
```

## js -> shell usage <a id="js_shell_usage"></a>

Basicaliy, this cmd is called in `js` -> `shell` flow by [execScript](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsUbuntu/execScript.md), [runByBackground](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsUbuntu/runByBackground.md).  


### Get replace variables contents

```sh.sh

readonly replace_variables_contents=$(\
  get_rvar \
	  "{0}" \
)
```

### Get variable value by variable name

```sh.sh

readonly replace_variable_name1_value=$(\
  get_rvar \
	  "{replace_variables_contents}" "${replace_variable_name1}" \
)
```

## shll, shell -> shell usage <a id="shell_shell_shell_usage"></a>

As another case, this cmd is called in `shell`, and `shell` -> `shell` flow 

- Cannot use from termux

### Get variable value by variable name

```sh.sh

readonly replace_variable_name1_value=$(\
  get_rvar \
	  "{replace_variables_contents}" "${replace_variable_name1}" "${path to include fannel dir path}" \
)
```
