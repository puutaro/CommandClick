# fannelStateStock.tsv

## OVERVIEW

Save current fannel state

* [OVERVIEW](#overview)
* [Ex fanenlStateStock](#ex-fanenlstatestock)
    * [Use case](#use-case)
    * [Format for fannelStateStock](#format-for-fannelstatestock)

## Ex fanenlStateStock

```tsv.tsv
fannelState\t${current fannel state}
```

### Use case

Require to set first fannel state  
(Alternative solution, set default in [fannelStateRootTable.tsv](https://github.com/puutaro/CommandClick/blob/master/md/developer/state/fannelStateRootTableTsv.md#macro-path))  
Because, `firstState` in [fannelStateConfig](https://github.com/puutaro/CommandClick/blob/master/md/developer/state/fannelStateConfig.md#key-value-table-for-fannelstateconfig) continue to set first state, not temporarily set first state.      

### Format for fannelStateStock

key-value

