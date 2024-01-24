package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index

import androidx.appcompat.widget.AppCompatImageView
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithIndexListView
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.QrDialogClickHandler

object FannelLogoLongClickDoForListIndex {

    fun invoke(
        editFragment: EditFragment,
        currentAppDirPath: String,
    ) {
        val binding = editFragment.binding
        val listIndexForEditAdapter = binding.editListRecyclerView.adapter as ListIndexForEditAdapter
        listIndexForEditAdapter.qrLongClickListener =
            object: ListIndexForEditAdapter.OnQrLogoLongClickListener {
                override fun onQrLongClick(
                    imageView: AppCompatImageView,
                    holder: ListIndexForEditAdapter.ListIndexListViewHolder,
                    position: Int
                ) {
                    QrDialogClickHandler.handle(
                        true,
                        editFragment,
                        currentAppDirPath,
                        holder.fileName,
                        listIndexForEditAdapter.qrDialogConfigMap
                    )
                }
            }
    }
}
