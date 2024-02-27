#!/bin/bash


touch $HOME/ansihtmltouch.txt
cat $HOME/script.log  \
| ansi2txt \
> "/storage/emulated/0/Documents/cmdclick/AppDir/default/ansi2html.txt"