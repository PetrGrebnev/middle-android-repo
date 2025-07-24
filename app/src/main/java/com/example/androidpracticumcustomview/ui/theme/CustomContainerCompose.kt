package com.example.androidpracticumcustomview.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


/*
Задание:
Реализуйте необходимые компоненты;
Создайте проверку что дочерних элементов не более 2-х;
Предусмотрите обработку ошибок рендера дочерних элементов.
Задание по желанию:
Предусмотрите параметризацию длительности анимации.
 */
@Composable
fun CustomContainerCompose(
    firstChild: @Composable (() -> Unit)?,
    secondChild: @Composable (() -> Unit)?,
    alphaDuration: Int = 2000,
    translationDuration: Int = 5000,
) {
    // Блок создания и инициализации переменных
    // ..
    val offsetTargetValue = Offset(0f, 0f)
    var heightParent by remember { mutableStateOf<Float?>(null) }
    var enabledAnimateAlpha by remember { mutableStateOf(false) }

    var animateOffsetY = remember { Animatable(offsetTargetValue, Offset.VectorConverter) }
    val animateAlpha: Float by animateFloatAsState(
        targetValue = if (enabledAnimateAlpha) 1f else 0f,
        animationSpec = tween(alphaDuration),
        label = "alpha"
    )

    // Блок активации анимации при первом запуске
    LaunchedEffect(heightParent) {
        heightParent?.let { height ->
            enabledAnimateAlpha = true
            launch {
                animateOffsetY.snapTo(Offset(0f, height / 2f))
                animateOffsetY.animateTo(
                    targetValue = offsetTargetValue,
                    animationSpec = tween(translationDuration)
                )
            }
        }
    }

    // Основной контейнер
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                heightParent = it.height.toFloat()
            }
    ) {
        if (firstChild != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset {
                        IntOffset(
                            animateOffsetY.value.x.roundToInt(),
                            animateOffsetY.value.y.roundToInt()
                        )
                    }
                    .graphicsLayer { alpha = animateAlpha }
            ) {

                firstChild()
            }
        }

        if (secondChild != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset {
                        IntOffset(
                            animateOffsetY.value.x.roundToInt(),
                            animateOffsetY.value.y.roundToInt().unaryMinus()
                        )
                    }
                    .graphicsLayer { alpha = animateAlpha }
            ) {

                secondChild()
            }
        }
    }
}