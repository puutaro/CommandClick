package com.puutaro.commandclick.component.adapter

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.imageview.ShapeableImageView
import com.puutaro.commandclick.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GridListAdapter(
    val imagePathList: List<String>,
): RecyclerView.Adapter<GridListAdapter.GridListViewHolder>() {

    class GridListViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val itemImageView = view.findViewById<ShapeableImageView>(R.id.grid_list_v2_adapter_image)
    }

    override fun getItemCount(): Int = imagePathList.size

    override fun getItemId(position: Int): Long {
        //return super.getItemId(position)
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        //return super.getItemViewType(position)
        return position
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GridListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(
            R.layout.grid_list_v2_adapter_layout,
            parent,
            false
        )
        val promptListViewHolder =
            GridListViewHolder(itemView)
        itemView.setOnClickListener {
            itemClickListener?.onItemClick(promptListViewHolder)
        }
        return promptListViewHolder
    }

    private val cornerRateMap = makeCornerRateMap()

    override fun onBindViewHolder(
        holder: GridListViewHolder,
        position: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val imagePath = withContext(Dispatchers.IO){
                imagePathList.get(position)
            } ?: return@launch
            withContext(Dispatchers.Main){
                val itemImageView = holder.itemImageView
                val itemImageContext = itemImageView.context
                itemImageView.apply {
                    viewTreeObserver?.addOnPreDrawListener(object :
                        ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            viewTreeObserver?.removeOnPreDrawListener(this)
                            CoroutineScope(Dispatchers.Main).launch {
                                // Remove listener because we don't want this called before _every_ frame
//                                val cornerFirstStopAnimator = withContext(Dispatchers.IO) {
//                                    makeCornerAnimation(
//                                        itemImageView,
//                                        cornerRateMap,
//                                        100L,
//                                        0f,
//                                    )
//                                }
//                                val cornerChangeAnimator = withContext(Dispatchers.IO) {
//                                    makeCornerAnimation(
//
//                                        itemImageView,
//                                        cornerRateMap,
//                                        1L,
//                                        null,
//                                    )
//                                }
                                // アニメーションセットを作成し、順に再生
                                withContext(Dispatchers.Main) {
                                    setShape(
                                        itemImageView,
                                        cornerRateMap,
                                    )
//                                    val animatorSet = AnimatorSet().apply {
//                                        playSequentially(
//                                            cornerFirstStopAnimator,
//                                            cornerChangeAnimator
//                                        )
//                                    }
//                                    animatorSet.start()
                                }
                            }
                            return true // true because we don't want to skip this frame
                        }
                    })
                }
//                withContext(Dispatchers.IO){
//                    delay(200)
//                }
                val requestBuilder: RequestBuilder<Drawable> =
                    Glide.with(itemImageContext)
                        .asDrawable()
                        .sizeMultiplier(0.1f)
                Glide
                    .with(itemImageContext)
                    .load(imagePath)
                    .skipMemoryCache( true )
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .thumbnail( requestBuilder )
                    .into(itemImageView)
//                YoYo.with(Techniques.RotateInUpRight)
//                    .duration(300)
//                    .repeat(0)
//                    .playOn(itemImageView)
            }
            withContext(Dispatchers.Main){
                holder.itemView.setOnClickListener {
                    itemClickListener?.onItemClick(
                        holder
                    )
                }
            }
        }
    }

    var itemClickListener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(holder: GridListViewHolder)
    }

    private fun makeCornerAnimation(
        itemImageView: ShapeableImageView,
        curCornerRateMap: Map<CornerKey, Float>,
        duration: Long,
        fixAnimateValue: Float? = null
    ): ValueAnimator {
        val curWidth = itemImageView.width
        val cornerChangeAnimator = ValueAnimator.ofFloat(0f, duration.toFloat()).apply setCornerChange@{
            this.duration = duration
            this.addUpdateListener { valueAnimator ->
//                                val radius = valueAnimator.animatedValue as Float
                val shapeAppearanceModel =
                    itemImageView.shapeAppearanceModel.toBuilder()
                        .setTopLeftCornerSize(
                            culcRadius(
                                curWidth,
                                makeValueFunc(
                                    duration,
                                    fixAnimateValue ?: valueAnimator.animatedValue as Float,
                                    curCornerRateMap[CornerKey.TOP_LEFT] as Float,
                                )
//                                        ((2 * duration - cornerChangeAnimator.animatedValue as Float) * 0.5f) / duration
                            )
                        )
                        .setTopRightCornerSize(
                            culcRadius(
                                curWidth,
                                makeValueFunc(
                                    duration,
                                    fixAnimateValue ?: valueAnimator.animatedValue as Float,
                                    curCornerRateMap[CornerKey.TOP_RIGHT] as Float,
                                )
//                                            ((2 * duration - cornerChangeAnimator.animatedValue as Float) * 0.05f) / duration
                            )
                        )
                        .setBottomRightCornerSize(
                            culcRadius(
                                curWidth,
                                makeValueFunc(
                                    duration,
                                    fixAnimateValue ?: valueAnimator.animatedValue as Float,
                                    curCornerRateMap[CornerKey.BOTTOM_RIGHT] as Float,
                                )
//                                            ((2 * duration - cornerChangeAnimator.animatedValue as Float) * 0.5f) / duration
                            )
                        )
                        .setBottomLeftCornerSize(
                            culcRadius(
                                curWidth,
                                makeValueFunc(
                                    duration,
                                    fixAnimateValue ?: valueAnimator.animatedValue as Float,
                                    curCornerRateMap[CornerKey.BOTTOM_LEFT] as Float,
                                )
//                                            ((2 * duration - cornerChangeAnimator.animatedValue as Float) * 0.05f) / duration
                            )
                        )
                        .build()
                itemImageView.shapeAppearanceModel = shapeAppearanceModel
            }
        }
        return cornerChangeAnimator
    }

    private fun setShape(
        itemImageView: ShapeableImageView,
        curCornerRateMap: Map<CornerKey, Float>,
    ){
        val curWidth = itemImageView.width
        val shapeAppearanceModel =
            itemImageView.shapeAppearanceModel.toBuilder()
                .setTopLeftCornerSize(
                    culcRadius(
                        curWidth,
                        curCornerRateMap[CornerKey.TOP_LEFT] as Float
                    )
                )
                .setTopRightCornerSize(
                    culcRadius(
                        curWidth,
                        curCornerRateMap[CornerKey.TOP_RIGHT] as Float
                    )
                )
                .setBottomRightCornerSize(
                    culcRadius(
                        curWidth,
                        curCornerRateMap[CornerKey.BOTTOM_RIGHT] as Float,
                    )
                )
                .setBottomLeftCornerSize(
                    culcRadius(
                        curWidth,
                        curCornerRateMap[CornerKey.BOTTOM_LEFT] as Float,
                    )
                )
                .build()
        itemImageView.shapeAppearanceModel = shapeAppearanceModel
    }

    private enum class CornerKey{
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_RIGHT,
        BOTTOM_LEFT,
    }

    private fun makeValueFunc(
        duration: Long,
        curDuration: Float,
        goalRRate: Float,
    ): Float {
        val value = let {
            val start = 3000F
            val end = duration.toFloat()
            start - ( (start - end) * curDuration / duration )
        }
        return (value * goalRRate) / duration
    }

    private fun makeCornerRateMap(): Map<CornerKey, Float> {
        val surplus = (0..1).random()
        val cornerRateList = (1..4).map {
            if (it % 2 == surplus) 0.4f else 0.02f
        }
        return mapOf(
            CornerKey.TOP_LEFT to cornerRateList[0],
            CornerKey.TOP_RIGHT to cornerRateList[1],
            CornerKey.BOTTOM_RIGHT to cornerRateList[2],
            CornerKey.BOTTOM_LEFT to cornerRateList[3],
        )
    }

    private fun culcRadius(
        width: Int,
        rate: Float,
    ): Float {
        return width * rate
    }
}