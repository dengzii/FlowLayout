package com.dengzii.flowlayout

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class FlowLayout : ViewGroup {

    var rowSpace = 0
    var colSpace = 0
    var tagHeight = 0

    companion object {
        private const val TAG = "FlowLayout"
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        context?.obtainStyledAttributes(attrs, R.styleable.FlowLayout)?.apply {
            tagHeight = getDimensionPixelSize(R.styleable.FlowLayout_tag_height, 0)
            colSpace = getDimensionPixelSize(R.styleable.FlowLayout_tag_col_space, 0)
            rowSpace = getDimensionPixelSize(R.styleable.FlowLayout_tag_row_space, 0)
        }?.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val w = MeasureSpec.getSize(widthMeasureSpec)
        var h = MeasureSpec.getSize(heightMeasureSpec)

        if (childCount == 0) {
            setMeasuredDimension(w, 0)
            return
        }
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        if (tagHeight == 0) {
            tagHeight = getChildAt(0).measuredHeight
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            var row = 1
            var widthSpace = w
            var child: View
            var itemWidth: Int
            for (i in 0 until childCount) {
                child = getChildAt(i)
                itemWidth = child.measuredWidth + colSpace
                widthSpace -= itemWidth
                if (widthSpace < 0) {
                    row++
                    widthSpace = w - itemWidth
                }
            }
            h = row * (tagHeight + rowSpace)
        }
        setMeasuredDimension(w, h)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var row = 0
        var right = 0
        var bottom: Int
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childW = child.measuredWidth
            val childH = child.measuredHeight
            right += childW
            bottom = (childH + rowSpace) * row + childH
            if (right > (r - colSpace)) {
                row++
                right = childW
                bottom = (childH + rowSpace) * row + childH
            }
            child.layout(right - childW, bottom - childH, right, bottom)
            right += colSpace
        }
    }
}
