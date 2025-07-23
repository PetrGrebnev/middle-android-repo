package com.example.androidpracticumcustomview.ui.theme

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.core.view.isVisible

/*
Задание:
Реализуйте необходимые компоненты;
Создайте проверку что дочерних элементов не более 2-х;
Предусмотрите обработку ошибок рендера дочерних элементов.
Задание по желанию:
Предусмотрите параметризацию длительности анимации.
 */

class CustomContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    private val alphaDuration: Long = 2000,
    private val translationDuration: Long = 5000
) : FrameLayout(context, attrs) {

    val animationView: HashMap<View, Boolean> = hashMapOf()

    init {
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        children.forEach { childView ->
            if(!animationView.getOrDefault(childView, false)) {
                childStartAnimation(childView, height)
            }
        }
    }

    override fun addView(child: View) {
        if (childCount > MAX_CHILD_VIEW) {
            throw IllegalStateException(MESSGE_ERROR_MOST_VIEW)
        }
        child.apply {
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                when (childCount) {
                    1 -> Gravity.BOTTOM
                    else -> Gravity.TOP
                }
            )
        }
        animationView.put(child, false)
        super.addView(child)
    }

    private fun childStartAnimation(child: View, parentHeight: Int) {
        child.apply {
            alpha = 0f
            translationY = when (indexOfChild(child)) {
                1 -> (parentHeight.toFloat() / 2).unaryMinus()
                else -> parentHeight.toFloat() / 2
            }
        }
        child.animate()
            .alpha(1f)
            .setDuration(alphaDuration)
            .translationY(0f)
            .setDuration(translationDuration)

        child.invalidate()
        animationView.put(child, true)
    }

    private val MAX_CHILD_VIEW = 2
    private val MESSGE_ERROR_MOST_VIEW = "Can't be more than 2 child view"
}