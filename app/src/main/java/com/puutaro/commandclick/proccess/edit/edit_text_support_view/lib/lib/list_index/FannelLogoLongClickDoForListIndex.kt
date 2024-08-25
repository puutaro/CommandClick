package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index

import androidx.appcompat.widget.AppCompatImageView
import com.puutaro.commandclick.component.adapter.ListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.QrDialogClickHandler

object FannelLogoLongClickDoForListIndex {

    fun invoke(
        editFragment: EditFragment,
    ) {
        val binding = editFragment.binding
        val listIndexForEditAdapter = binding.editListRecyclerView.adapter as ListIndexAdapter
        listIndexForEditAdapter.qrLongClickListener =
            object: ListIndexAdapter.OnQrLogoLongClickListener {
                override fun onQrLongClick(
                    imageView: AppCompatImageView,
                    holder: ListIndexAdapter.ListIndexListViewHolder,
                    position: Int
                ) {
                    QrDialogClickHandler.handle(
                        true,
                        editFragment,
                        holder.fileName,
                        position
                    )
                }
            }
    }
}
