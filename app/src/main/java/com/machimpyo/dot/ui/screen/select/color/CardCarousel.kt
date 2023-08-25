package com.machimpyo.dot.ui.screen.select.color

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatSpringSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.machimpyo.dot.ui.screen.select.Letter
import com.machimpyo.dot.utils.extension.LetterColorList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Stable
interface CircularCarouselState {
    val angle: Float
    val minorAxisFactor: Float

    suspend fun stop()
    suspend fun snapTo(angle: Float)
    suspend fun decayTo(angle: Float, velocity: Float)
    fun setMinorAxisFactor(factor: Float)
}

class CircularCarouselStateImpl : CircularCarouselState {
    private val _angle = Animatable(0f)
    private val _eccentricity = mutableStateOf(1f)

    override val angle: Float
        get() = _angle.value

    override val minorAxisFactor: Float
        get() = _eccentricity.value

    private val decayAnimationSpec = FloatSpringSpec(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessVeryLow,
    )

    override suspend fun stop() {
        _angle.stop()
    }

    override suspend fun snapTo(angle: Float) {
        _angle.snapTo(angle)
    }

    override suspend fun decayTo(angle: Float, velocity: Float) {
        _angle.animateTo(
            targetValue = angle,
            initialVelocity = velocity,
            animationSpec = decayAnimationSpec,
        )
    }

    override fun setMinorAxisFactor(factor: Float) {
        _eccentricity.value = factor.coerceIn(-1f, 1f)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CircularCarouselStateImpl

        if (_angle != other._angle) return false
        if (_eccentricity != other._eccentricity) return false
        if (decayAnimationSpec != other.decayAnimationSpec) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _angle.hashCode()
        result = 31 * result + _eccentricity.hashCode()
        result = 31 * result + decayAnimationSpec.hashCode()
        return result
    }
}

@Composable
fun rememberCircularCarouselState(): CircularCarouselState = remember {
    CircularCarouselStateImpl()
}

@Composable
fun CircularCarousel(
    numItems: Int,
    modifier: Modifier = Modifier,
    itemFraction: Float = .25f,
    state: CircularCarouselState = rememberCircularCarouselState(),
    contentFactory: @Composable (Int) -> Unit,
) {
    require(numItems > 0) { "The number of items must be greater than 0" }
    require(itemFraction > 0f && itemFraction < .5f) { "Item fraction must be in the (0f, .5f) range" }
    Layout(
        modifier = modifier
            .then(modifier)
            .drag(state)
            .selectableGroup()
        ,
        content = {
            val angleStep = 360f / numItems.toFloat()
            repeat(numItems) { index ->
                val itemAngle = (state.angle + angleStep * index.toFloat()).normalizeAngle()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(if (itemAngle <= 180f) 180f - itemAngle else itemAngle - 180f),
                    contentAlignment = Alignment.Center
                ) {
                    contentFactory(index)
                }
            }
        }
    ) { measurables, constraints ->
        val itemDimension = constraints.maxHeight * itemFraction
//        val itemConstraints = Constraints.fixed(
//            width = itemDimension.toInt(),
//            height = itemDimension.toInt(),
//        )
        val itemConstraints = Constraints()

        val placeables = measurables.map { measurable -> measurable.measure(itemConstraints) }
        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight,
        ) {
            val availableHorizontalSpace = constraints.maxWidth - itemDimension / 2
            val horizontalOffset = availableHorizontalSpace / 2.0
            val verticalOffset = (constraints.maxHeight - itemDimension).toInt() / 2
            val angleStep = 2.0 * PI / numItems.toDouble()
            placeables.forEachIndexed { index, placeable ->
                val itemAngle = (state.angle.toDouble().degreesToRadians() + (angleStep * index.toDouble())) % 360.0
                val offset = getCoordinates(
                    width = availableHorizontalSpace / 2.0,
                    height = (constraints.maxHeight.toDouble() / 2.0 - itemDimension) * state.minorAxisFactor,
                    angle = itemAngle,
                )

//                placeable.placeRelative(
//                    x = (horizontalOffset + offset.x).roundToInt(),
//                    y = offset.y.roundToInt() + verticalOffset,
//                )

                placeable.placeRelative(
                    x = (horizontalOffset + offset.x).roundToInt(),
                    y = offset.y.roundToInt() + verticalOffset,
                )
            }
        }
    }
}

private fun Modifier.drag(
    state: CircularCarouselState,
) = pointerInput(Unit) {
    val decay = splineBasedDecay<Float>(this)
    coroutineScope {
        while (true) {
            val pointerInput = awaitPointerEventScope { awaitFirstDown() }
            state.stop()
            val tracker = VelocityTracker()
            val degreesPerPixel = 180f / size.width.toFloat()
            awaitPointerEventScope {
                val isTopHalf = pointerInput.position.y < size.height / 2f
                // 1
                val signum = when {
                    isTopHalf && state.minorAxisFactor >= 0f -> -1f
                    isTopHalf -> 1f
                    state.minorAxisFactor >= 0f -> 1f
                    else -> -1f
                }
                // 2
                this.drag(pointerId = pointerInput.id) { change ->
                    val horizontalDragOffset = state.angle + signum * change.positionChange().x * degreesPerPixel
                    launch {
                        state.snapTo(horizontalDragOffset)
                    }
                    // 3
                    val scaleFactor = state.minorAxisFactor + change.positionChange().y / (size.height / 2f)
                    // 4
                    state.setMinorAxisFactor(scaleFactor)
                    tracker.addPosition(change.uptimeMillis, change.position)
                    if (change.positionChange() != Offset.Zero) change.consume()
                }
                val velocity = tracker.calculateVelocity().x / 10
                val targetAngle = decay.calculateTargetValue(
                    state.angle,
                    signum * velocity,
                )
                launch {
                    state.decayTo(
                        angle = targetAngle,
                        velocity = signum * velocity * degreesPerPixel,
                    )
                }
            }
        }
    }
}
private fun getCoordinates(width: Double, height: Double, angle: Double): Offset {
    val x = width * sin(angle)
    val y = height * cos(angle)
    return Offset(
        x = x.toFloat(),
        y = y.toFloat(),
    )
}

private fun Double.degreesToRadians(): Double = this / 360.0 * 2.0 * PI

private fun Float.normalizeAngle(): Float = (this % 360f).let { angle -> if (this < 0f) 360f + angle else angle }

@Composable
fun ColorLetterFactory(viewModel: SelectLetterColorViewModel, index: Int = 0, onCLick: () -> Unit = {}) {
    Letter(background = {},
        color= LetterColorList[index],
        modifier = Modifier
            .size(61.dp, 135.dp)
            .clickable(onClick = onCLick),
        onClick = onCLick
    )

//    Box(
//        modifier = Modifier
//            .size(61.dp)
//            .clip(CircleShape)
//            .background(
//                color = viewModel.letterColors.get(index)
//            )
//            .clickable {
//                onCLick
//            }
//    )
}


