package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.custom_view.ColorPickerFlag
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.map.CmdClickMap
import com.skydoves.colorpickerview.ActionMode
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference
import kotlin.math.max


class ColorPickerJsDialog(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private var returnValue = String()
    private var onDialog = false

    private val keySeparator = '|'

    private enum class ColorPickerConfig(
        val key: String
    ){
        INITIAL_COLOR("initialColor"),
        TITLE("title"),
    }

    fun create(
        configMapCon: String,
    ): String {
        returnValue = String()
        onDialog = true
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    execCreate(
                        configMapCon,
                        )
                } catch(e: Exception){
                    LogSystems.stdErr(
                        terminalFragmentRef.get()?.context,
                        e.toString()
                    )
                }
            }
            withContext(Dispatchers.IO) {
                for (i in 1..1000) {
                    delay(100)
                    if (
                        !onDialog
                    ) break
                }
            }
        }
        return returnValue
    }

    private fun execCreate(
        configMapCon: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
            ?: return Unit.also {
                dismiss(String())
            }
        val colorPickerConfigMap = CmdClickMap.createMap(
            configMapCon,
            keySeparator
        ).toMap()
        val title = colorPickerConfigMap.get(
            ColorPickerConfig.TITLE.key
        )
        val initialColor = colorPickerConfigMap.get(
            ColorPickerConfig.INITIAL_COLOR.key
        )?.let {
            try {
                Color.parseColor(it)
            } catch (e: Exception){
                LogSystems.stdErr(
                    context,
                "[COLOR PICKER] initail color parse err")
                dismiss(String())
                return
            }
        }
        val colorPickerDialogBuilder = ColorPickerDialog.Builder(
            context,
//            R.style.FullScreenRoundCornerDialogTheme
        ).apply {
            val screenWidth = ScreenSizeCalculator.pxWidth(terminalFragment.activity)
            val colorPickerView: ColorPickerView = this.colorPickerView
            colorPickerView.apply {
                setFlagView(
                    ColorPickerFlag(
                        context,
                        R.layout.color_picker_flag
                    )
                )
                val paddingPxSize = let {
                    if(
                        screenWidth > 1000f
                    ) return@let 0f
                    val baseWidth = 720f
                    val maxSize = 20f
                    val minSize = 0f
                    val incline = (minSize - maxSize) / (1080f - baseWidth)
                    val culcSize = incline  * (screenWidth - baseWidth) + maxSize
                    if(
                        culcSize <= minSize
                    ) return@let maxSize
                    if(
                        culcSize >= maxSize
                    ) return@let maxSize
                    culcSize
                }

                val paddingDpSize = ScreenSizeCalculator.toDp(context, paddingPxSize)
                setPadding(paddingDpSize)
                actionMode = ActionMode.ALWAYS
                if(
                    initialColor != null
                ) {
                    setInitialColor(initialColor)
                }
            } // sets a custom flagView
            if(
                !title.isNullOrEmpty()
            ){
                setCustomTitle(
                    AppCompatTextView(context).apply {
                        setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.fannel_icon_color
                            )
                        )
                        textSize = 25f
                        val horizonPadding = ScreenSizeCalculator.toDp(context, 20)
                        val verticalPadding = ScreenSizeCalculator.toDp(context, 10)
                        setPadding(
                            horizonPadding,
                            verticalPadding,
                            horizonPadding,
                            verticalPadding,
                            )
                        text = title
                    }
                )
//                setTitle(title)
            }
            setColorPickerView(colorPickerView)
            setPreferenceName("MyColorPickerDialog")
            val okEmoji = "✅"
            setPositiveButton("SELECT", ColorEnvelopeListener { envelope, fromUser ->
                val hexCodeSource = envelope.hexCode
                val hexCode = "#${hexCodeSource.substring(2)}"
                dismiss(hexCode)
            })
            val cancelEmoji = "❌"
            setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialogInterface, i ->
                dismiss(String())
                dialogInterface.dismiss()
            })
            setOnCancelListener {
                dismiss(String())
            }
        }
        val colorPickerDialog = colorPickerDialogBuilder.create()
        colorPickerDialog.show()
        colorPickerDialog.getButton(
            DialogInterface.BUTTON_POSITIVE
        ).setTextColor(
            context?.getColor(R.color.fannel_icon_color) as Int
        )
        colorPickerDialog.getButton(
            DialogInterface.BUTTON_NEGATIVE
        ).setTextColor(
            context.getColor(
                R.color.fannel_icon_color
            )
        )
        colorPickerDialog.window
            ?.apply {
//                setLayout(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT
//                )
                setGravity(Gravity.BOTTOM)
            }
    }

    private fun dismiss(
        returnValueStr: String,
    ){
        onDialog = false
        returnValue = returnValueStr
    }

}