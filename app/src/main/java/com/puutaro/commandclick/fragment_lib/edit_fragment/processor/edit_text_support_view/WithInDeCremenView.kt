package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view

import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.SetVariableTypeColumn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WithInDeCremenView(
    private val editFragment: EditFragment,
) {

    private val context = editFragment.context
    private val initMinNum = -1000000
    private val initMaxNum =  1000000
    private val initStepNum = 1


    fun createNumInDeCreamenter(
        currentVariableValueSource: String?,
        insertEditText: EditText,
        setVariableMap: Map<String,String>
    ): LinearLayout {

        val linearParamsForEditTextTest = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearParamsForEditTextTest.weight = 0.9F
        val innerLayout = LinearLayout(context)
        innerLayout.orientation = LinearLayout.HORIZONTAL

        val variableTypeValue = setVariableMap.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )
        val numEntityMap = makeNumEntityMap (
            variableTypeValue,
        )

        val initNumString = numEntityMap.get(numEntityMapColumn.INIT_NUM.name)
        val currentVariableValue = if(initNumString == null){
            stringToInt(
                currentVariableValueSource,
            ).toString()
        } else {
            initNumString
        }

        insertEditText.setText(currentVariableValue)
        insertEditText.inputType = InputType.TYPE_CLASS_NUMBER
        insertEditText.layoutParams = linearParamsForEditTextTest
        insertEditText.setFocusableInTouchMode(true);
        linearParamsForEditTextTest.weight = 0.6F
        insertEditText.layoutParams = linearParamsForEditTextTest
        innerLayout.addView(insertEditText)

        val incButton = createIncDecButton(
            insertEditText,
            numEntityMap
        )
        innerLayout.addView(incButton)
        val decButton = createIncDecButton(
            insertEditText,
            numEntityMap,
            false
        )
        innerLayout.addView(decButton)
        return innerLayout
    }

    private fun createIncDecButton(
        insertEditText: EditText,
        numEntityMap: Map<String, String?>,
        onIncrement: Boolean = true,
    ): Button {

        val minNum = stringToInt(
            numEntityMap.get(numEntityMapColumn.MIN_NUM.name),
            initMinNum
        )
        val maxNum = stringToInt(
            numEntityMap.get(numEntityMapColumn.MAX_NUM.name),
            initMaxNum
        )
        val incrementStepNum = stringToInt(
            numEntityMap.get(numEntityMapColumn.STEP_NUM.name),
            initStepNum
        )

        var incrementNum = incrementStepNum

        fun esecIncDec(){
            val currentNumber = stringToInt(
                insertEditText.text.toString()
            )
            val crementedNumber = if (onIncrement) {
                currentNumber + incrementNum
            } else {
                currentNumber - incrementNum
            }
            val filterdIncrementNumber = if(minNum > crementedNumber){
                minNum
            } else if (crementedNumber > maxNum){
                maxNum
            } else {
                crementedNumber
            }
            insertEditText.setText(filterdIncrementNumber.toString())
        }

        fun inDeCrementRunner(
        ): Runnable {
            return kotlinx.coroutines.Runnable {
                esecIncDec()
            }
        }

        val insertButtonView = Button(context)
        if (onIncrement) {
            insertButtonView.setText("+")
        } else {
            insertButtonView.setText("-")
        }



        var job: Job? = null
        with(insertButtonView) {

            setOnClickListener{
                incrementNum = incrementStepNum
                esecIncDec()
            }

            setOnTouchListener(View.OnTouchListener { v, event ->
                val crementHandler = Handler(Looper.getMainLooper())
                val crementRunner = inDeCrementRunner()
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        job = editFragment.viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                            editFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                var roopTimes = 1
                                var delayTime = 150L
                                incrementNum = incrementStepNum
                                while (true) {
                                    delay(delayTime)
                                    incrementNum = roopTimes / 10 + incrementStepNum
                                    crementHandler.post(
                                        crementRunner
                                    )
                                    roopTimes++
                                    if(delayTime <= 100L) continue
                                    delayTime = delayTime - roopTimes
                                }
                            }
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        v.performClick()
                        job?.cancel()
                        crementHandler.removeCallbacksAndMessages(crementRunner);
                    }
                }
                true
            })
        }
        val insertButtonViewParam = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        insertButtonViewParam.weight = 0.2F
        insertButtonView.layoutParams = insertButtonViewParam
        return insertButtonView
    }
}


internal fun makeNumEntityMap (
    variableTypeValue: String?,
): Map<String, String?>{
    val variableValueList = variableTypeValue?.split('!')
    val minMaxElement = variableValueList?.firstOrNull( {
        it.contains("..")
    })
    val minMaxElementIndex = variableValueList?.indexOf(minMaxElement)
    val minMaxElementList = minMaxElement?.split("..")
    return mapOf(
        numEntityMapColumn.INIT_NUM.name to if(
            minMaxElementIndex == null || minMaxElementIndex <= 0
        ){
            null
        } else {
            checkIntHowString(
                variableValueList.getOrNull(minMaxElementIndex - 1)
            )
        },
        numEntityMapColumn.MIN_NUM.name to checkIntHowString(
            minMaxElementList?.getOrNull(numEntityMapColumn.MIN_NUM.order)
        ),
        numEntityMapColumn.MAX_NUM.name to checkIntHowString(
            minMaxElementList?.getOrNull(numEntityMapColumn.MAX_NUM.order),
        ),
        numEntityMapColumn.STEP_NUM.name to if(
            minMaxElementIndex == null || minMaxElementIndex == -1
        ){
            null
        } else {
            checkIntHowString(
                variableValueList.getOrNull(minMaxElementIndex + 1)
            )
        },
    )
}

internal fun checkIntHowString (
    str: String?
): String? {
    return try {
        str?.toInt().toString()
    } catch (e: Exception){
        null
    }
}


internal fun stringToInt(
    str: String?,
    initNum: Int = 0
): Int {
    return try {
        str?.toInt() ?: initNum
    } catch (e :Exception) {
        initNum
    }
}

private enum class numEntityMapColumn(
        val order: Int
    ) {
    INIT_NUM(0),
    MIN_NUM(0),
    MAX_NUM(1),
    STEP_NUM(3),
}