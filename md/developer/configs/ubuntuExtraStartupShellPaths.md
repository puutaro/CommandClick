# ubuntuExtraStartupShellPaths.tsv

Set auto start shell script path

Table
-----------------
<!-- vim-markdown-toc GFM -->

* [Use case](#use-case)
* [How to specify](#how-to-specify)
* [Ex ubuntuExtraStartupShellPaths.tsv](#ex-ubuntuextrastartupshellpaths.tsv)
* [ubuntuExtraStartupShellPaths.tsv format](#ubuntuextrastartupshellpaths.tsv-format)
* [First field](#first-field)
  * [Env variable on shell path](#env-variable-on-shell-path)
  * [shell path macro](#shell-path-macro)
* [Second field](#second-field)
  * [Key-values table](#key-values-table)

## Use case

For make startup script, service, and daemon etc...

## How to specify

Put this tsv to `/storage/emulated/0/Documents/cmdclick/AppDir/system/cmdclickConfigDir/settings`  
So, automatically reflect in ubuntu service


## Ex ubuntuExtraStartupShellPaths.tsv

```tsv.tsv
PULSE.sh\tonAutoRestore=ON,disable=ON
${APP_ROOT_PATH}/AppDir/default/loop.sh\tonAutoRestore=ON,disable=ON
```

## ubuntuExtraStartupShellPaths.tsv format


```js.js
{shell path1}\t{key1-1}={value1-1},{key1-2}={value1-2},{key1-3}={value1-3},...
{shell path2}\t{key2-1}={value2-1},{key2-2}={value2-2},{key2-3}={value2-3},...
{shell path3}\t{key3-1}={value3-1},{key3-2}={value3-2},{key3-3}={value3-3},...
.
.
.
```

## First field

Shell path that you want to start automatically

### Env variable on shell path

You can use [ubuntu env variable](https://github.com/puutaro/CommandClick/blob/master/md/developer/ubuntu_env_variables.md) in shell path

### shell path macro

| Macro      | Description                                                        | 
|------------|--------------------------------------------------------------------|
| `PULSE.sh` | Start pulseaudio, by this, you can play sound from ubuntu directly |

## Second field

Key-value that you want to set

### Key-values table


| Key name        | value                    | Description                              | 
|-----------------|--------------------------|------------------------------------------|
| `onAutoRestore`        | `ON` <br> other | Reboot ubuntu and restore when be killed |
| `disable`        | `ON` <br> other          | disable to auto start                    |

- Concat by `,`


## settingimport

Import enable to this config, -> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/settingImport.md)

