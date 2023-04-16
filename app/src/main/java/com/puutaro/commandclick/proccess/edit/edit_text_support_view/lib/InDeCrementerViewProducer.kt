package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.fragment.EditFragment
import kotlinx.coroutines.*
import java.lang.Runnable

class InDeCrementerViewProducer {
    companion object {
        fun make(
            insertEditText: EditText,
            editParameters: EditParameters,
            numEntityMap: Map<String, String?>,
            weight: Float,
            onIncrement: Boolean = true,
        ): Button {
            val context = editParameters.context
            val initMinNum = -1000000
            val initMaxNum =  1000000
            val initStepNum = 1

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

            fun execIncDec(){
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
                    execIncDec()
                }
            }

            val insertButtonView = Button(context)
            if (onIncrement) {
                insertButtonView.text = "+"
            } else {
                insertButtonView.text = "-"
            }



            var job: Job? = null
            with(insertButtonView) {

                setOnClickListener{
                    incrementNum = incrementStepNum
                    execIncDec()
                }

                setOnTouchListener(View.OnTouchListener { v, event ->
                    val crementHandler = Handler(Looper.getMainLooper())
                    val crementRunner = inDeCrementRunner()
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            job = CoroutineScope(Dispatchers.IO).launch {
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
            insertButtonViewParam.weight = weight
            insertButtonView.layoutParams = insertButtonViewParam
            return insertButtonView
        }

        fun makeNumEntityMap (
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

        fun stringToInt(
            str: String?,
            initNum: Int = 0
        ): Int {
            return try {
                str?.toInt() ?: initNum
            } catch (e :Exception) {
                initNum
            }
        }

        private fun checkIntHowString (
            str: String?
        ): String? {
            return try {
                str?.toInt().toString()
            } catch (e: Exception){
                null
            }
        }
    }
}

enum class numEntityMapColumn(
    val order: Int
) {
    INIT_NUM(0),
    MIN_NUM(0),
    MAX_NUM(1),
    STEP_NUM(3),
}