package com.puutaro.commandclick.component.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.proccess.qr.QrLogo
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SettingVariableReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class InstallFannelListAdapter(
    val cmdIndexFragment: CommandIndexFragment,
    private val currentAppDirPath: String,
    var fannelInstallerList: MutableList<String>,
): RecyclerView.Adapter<InstallFannelListAdapter.FannelInstallerListViewHolder>()
{
    val context = cmdIndexFragment.context
    val activity = cmdIndexFragment.activity
    private val maxTakeSize = 200
    private val qrPngNameRelativePath = UsePath.qrPngRelativePath
    private val qrLogo = QrLogo(cmdIndexFragment)


    class FannelInstallerListViewHolder(
        val activity: FragmentActivity?,
        val view: View
    ): RecyclerView.ViewHolder(view) {

        val fannelContentsQrLogoView =
            view.findViewById<AppCompatImageView>(
                com.puutaro.commandclick.R.id.install_fannel_adapter_contents
            )
        val fannelNameTextView =
            view.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.install_fannel_adapter_fannel_name
            )
        val fannelSummaryTextView =
            view.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.fannel_index_list_adapter_fannel_summary
            )
    }

    override fun getItemId(position: Int): Long {
        setHasStableIds(true)
        return super.getItemId(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FannelInstallerListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(
            com.puutaro.commandclick.R.layout.install_fannel_adapter_layout,
            parent,
            false
        )
        val fannelInstallerListViewHolder = FannelInstallerListViewHolder(
            activity,
            itemView
        )
        return fannelInstallerListViewHolder
    }

    override fun getItemCount(): Int = fannelInstallerList.size


    override fun onBindViewHolder(
        holder: FannelInstallerListViewHolder,
        position: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val fannelInstallerLineList =
                fannelInstallerList.getOrNull(position)?.split("\n")
            val fannelName = fannelInstallerLineList?.firstOrNull()
                ?: String()
            withContext(Dispatchers.Main) {
                holder.fannelNameTextView.text = fannelName
            }
            val fannelSumamry =
                fannelInstallerLineList?.getOrNull(1)
                ?: String()
            withContext(Dispatchers.Main) {
                holder.fannelSummaryTextView.text =
                    fannelSumamry
                        .trim()
                        .removePrefix("-")
                        .trim()
            }
            val fannelConList = withContext(Dispatchers.IO) {
                ReadText(
                    UsePath.cmdclickFannelDirPath,
                    fannelName
                ).textToList().take(maxTakeSize)
            }
            withContext(Dispatchers.Main) {
                setQrLogo(
                    currentAppDirPath,
                    fannelName,
                    holder,
                )
            }
            val fannelConBackGroundColorInt = withContext(Dispatchers.IO) {
                setFannelContentsBackColor(
                    fannelConList,
                    fannelName,
                )
            }
            withContext(Dispatchers.Main){
                holder.fannelContentsQrLogoView.setBackgroundColor(
                    context?.getColor(fannelConBackGroundColorInt) as Int
                )
                withContext(Dispatchers.Main) {
                    val itemView = holder.itemView
                    itemView.setOnClickListener {
                        fannelItemClickListener?.onFannelItemClick(
                            itemView,
                            holder
                        )
                    }
                    holder.fannelContentsQrLogoView.setOnClickListener {
                        fannelContentsClickListener?.onFannelContentsClick(
                            itemView,
                            holder
                        )
                    }
                }
            }
        }
    }

    var fannelItemClickListener: OnFannelItemClickListener? = null
    interface OnFannelItemClickListener {
        fun onFannelItemClick(
            itemView: View,
            holder: FannelInstallerListViewHolder
        )
    }


    var fannelContentsClickListener: OnFannelContentsItemClickListener? = null
    interface OnFannelContentsItemClickListener {
        fun onFannelContentsClick(
            itemView: View,
            holder: FannelInstallerListViewHolder
        )
    }

    private fun setFannelContentsBackColor(
        fannelConList: List<String>,
        fannelName: String,
    ): Int {
        if(
            context == null
        ) return com.puutaro.commandclick.R.color.fannel_icon_color
        val languageType =
            CommandClickVariables.judgeJsOrShellFromSuffix(fannelName)

        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
        ) as String
        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
        ) as String
        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            fannelConList,
            settingSectionStart,
            settingSectionEnd
        )
        val editExecuteValue = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.EDIT_EXECUTE,
            CommandClickScriptVariable.EDIT_EXECUTE_DEFAULT_VALUE
        )
        if(
            editExecuteValue
            == SettingVariableSelects.EditExecuteSelects.ALWAYS.name
        ) return com.puutaro.commandclick.R.color.terminal_color
        return com.puutaro.commandclick.R.color.fannel_icon_color
    }

    private fun setQrLogo(
        currentAppDirPath: String,
        fannelName: String,
        holder: FannelInstallerListViewHolder,
    ){
        val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
        val qrPngPathObjInInstallIndex =
            File(
                "${UsePath.cmdclickFannelDirPath}/${fannelDirName}/${qrPngNameRelativePath}"
            )
        if(
            qrPngPathObjInInstallIndex.isFile
        ) {
            holder.fannelContentsQrLogoView.load(qrPngPathObjInInstallIndex.absolutePath)
            return
        }
        val qrPngPathObjInFannelIndex =
            File(
                "${currentAppDirPath}/$fannelDirName/${qrPngNameRelativePath}"
            )
        if(
            qrPngPathObjInFannelIndex.isFile
        ) {
            holder.fannelContentsQrLogoView.load(qrPngPathObjInFannelIndex.absolutePath)
            return
        }
        qrLogo.createAndSaveRnd(
            "${currentAppDirPath}/${fannelName}",
            currentAppDirPath,
            fannelName,
        )?.let {
            holder.fannelContentsQrLogoView.setImageDrawable(it)
        }
    }

}
