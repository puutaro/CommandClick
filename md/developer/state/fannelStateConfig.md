# fannelStateConfig

* [OVERVIEW](#overview)
    * [What's fannel state ?](#what's-fannel-state-?)
    * [fannelStateConfig ex](#fannelstateconfig-ex)
    * [format for fannelStateConfig](#format-for-fannelstateconfig)
    * [Key-value table for fannelStateConfig](#key-value-table-for-fannelstateconfig)

## OVERVIEW

Control [fannel root table](https://github.com/puutaro/CommandClick/blob/master/md/developer/state/fannelStateRootTableTsv.md)

### What's fannel state ?

-> [reference to other fannel setting variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/state/fannelStateRootTableTsv.md#fannelstateroottable-tsv-ex) 

### fannelStateConfig ex

```js.js
firstState="manager",

noRegisterStates="config",
```

### format for fannelStateConfig

key-value

### Key-value table for fannelStateConfig

| Key name       | value                            | Description                                                                                                                        | 
|----------------|----------------------------------|------------------------------------------------------------------------------------------------------------------------------------|
| `firstState`   | state name                       | Contiue to set first impression fannel state                                                                                       |
| `noRegisterStates`   | state name list separated by `,` | no register fannel state to [fannelStateStock.tsv](https://github.com/puutaro/CommandClick/blob/master/md/developer/state/fannelStateStockTsv.md) |

