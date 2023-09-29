package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import java.util.Locale

class AutoCompleteAdapter(
    context: Context,
    @LayoutRes resource: Int,
    internal var items: List<String> = listOf()
) : ArrayAdapter<String>(context, resource, items) {


    internal var tempItems: MutableList<String> = mutableListOf()
    internal var suggestions: MutableList<String> = mutableListOf()

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    private var filter: Filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return if (constraint != null) {
                suggestions.clear()
                tempItems.forEach {
                    if (it.toString().lowercase().contains(constraint.toString().lowercase())) {
                        suggestions.add(it)
                    }
                }
                suggestions.reverse()
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            } else {
                FilterResults()
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            val filterList = results.values as? List<String>
            if (results.count > 0) {
                clear()
                filterList?.forEach {
                    add(it)
                }.also {
                    notifyDataSetChanged()
                }
            }
        }
    }

    init {
        tempItems = items.toMutableList()
        suggestions = ArrayList()
    }

    override fun getFilter(): Filter {
        return filter
    }
}