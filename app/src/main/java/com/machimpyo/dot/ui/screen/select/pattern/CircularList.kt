package com.machimpyo.dot.ui.screen.select.pattern

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class CircularListConfig(
    /** isRepeatable:
     *  True => 원 내에서 사분면의 각 한면에 한 리스트가 차지하게 됨 (총 네개의 리스트가 위치한다고 보면 됨)
     *            TODO 현재는 content에 리스트 4번을 넣거나 해야 됩니다... 아니면 오류남
     *  False => 한 원에 한 리스트 (단, 4의 배수개로 리스트의 개수가 존재해야함..)
     *  **/
    val isRepeatable: Boolean = true,
    /** 타원 정도. 1f는 완벽한 원이고, 0.0f에 가까워질 수록 세로가 짧은 타원이 된다.*/
    val curvature: Float = 1f,
    /** 원의 긴쪽 반지름 **/
    val radius: Dp = 100.dp,
    /** fitToConstraints:
     * true => radius를 무시하고 Constraints에 따라 감.( 단, Constraints의 Height 크기 기준임)
     * false => radius를 반영 (단, list가 잘릴 수 있음)
     * **/
    val fitToConstraints: Boolean = true,
)

@Stable
interface CircularListState {
    val angle: Float

    suspend fun stop()

    suspend fun snapTo(angle:Float)

    suspend fun decayTo(angle:Float, velocity: Float)

}

@Composable
fun CircularList(
    config: CircularListConfig = CircularListConfig(),
    modifier: Modifier = Modifier,
    circularFraction: Float = 1f,
    contents: @Composable () -> Unit,
) {

    //찌그러지는 정도
    check(config.curvature > 0f && config.curvature <= 1.0f) { "Circular curvature must be positive and not over 1.0f"}

    Layout(
        modifier = modifier,
        content = contents,
    ){measurables, constraints ->

        if (!config.isRepeatable) {
            check(measurables.size % 4 == 0) { "한 원에 리스트를 그리려면 리스트의 원소 개수가 4의 배수여야합니다." }
        } else {
            //나중엔 바꿔야함
            check(measurables.size % 4 == 0) { "리스트를 그리려면 리스트의 원소 개수가 4의 배수여야합니다." }
        }

        val itemConstraints = Constraints()

        var placeables = measurables.map{measurable ->  measurable.measure(itemConstraints)}

        val radiusToPx = config.radius.roundToPx()

        fun confirmRadius(): Int = if (config.fitToConstraints) { constraints.maxHeight } else { radiusToPx }

        layout(
            width = confirmRadius(),
            height = confirmRadius(),
        ) {

            val radius = confirmRadius() / 2
            val size = placeables.size

            //TODO 나중에 config로 옮겨야됨, LetterPatterNum도 Config내에서 계산
            if (config.isRepeatable) {

                val degree = 360.0 / (placeables.size)
                val numberInQuadrant: Int = placeables.size / 4

                for (i in 0..placeables.size) {

                    val index: Int = i

                    if (i in 0 until numberInQuadrant) {
                        val radian = degree * (PI / 180) * i

                        placeables[index].placeRelative(
                            x = (radius * (sin(radian) + 1) - placeables[index].width / 2)
                                .toInt(),
                            y = (-placeables[index].height / 2 + radius * (1 - config.curvature*cos(radian)))
                                .toInt()
                        )
                    } else if (i in numberInQuadrant until 2 * numberInQuadrant) {
                        val radian = (degree * i - 90) * (PI / 180)

                        placeables[index].placeRelative(
                            x = (radius * (cos(radian) + 1) - placeables[index].width / 2)
                                .toInt(),
                            y = (-placeables[index].height / 2 + radius * (1 + config.curvature*sin(radian)))
                                .toInt()
                        )

                    } else if (i in 2 * numberInQuadrant until 3 * numberInQuadrant) {
                        val radian = (degree * i - 180) * (PI / 180)

                        placeables[index].placeRelative(
                            x = (radius * (1 - sin(radian)) - placeables[index].width / 2)
                                .toInt(),
                            y = (-placeables[index].height / 2 + radius * (1 + config.curvature*cos(radian)))
                                .toInt()
                        )
                    } else if (i in 3 * numberInQuadrant until 4 * numberInQuadrant) {
                        val radian = (degree * i - 270) * (PI / 180)

                        placeables[index].placeRelative(
                            x = (radius * (1 - cos(radian)) - placeables[index].width / 2)
                                .toInt(),
                            y = (-placeables[index].height / 2 + radius * (1 - config.curvature*sin(radian)))
                                .toInt()
                        )
                    }
                }
//
//                val degree = 360.0 / (placeables.size * 4)
//
//                for (i in 0..placeables.size * 4-1) {
//
//                    val index: Int = i % placeables.size
//
//                    if (i in 0..size - 1) {
//                        val radian = degree * (PI / 180) * i
//
//                        placeables[index].placeRelative(
//                            x = (radius * (sin(radian) + 1) - placeables[index].width / 2)
//                                .toInt(),
//                            y = (-placeables[index].height / 2 + radius * (1 - cos(radian)))
//                                .toInt()
//                        )
//                    } else if (i in size .. 2 * size - 1) {
//                        val radian = (degree * i - 90) * (PI / 180)
//
//                        placeables[index].placeRelative(
//                            x = (radius * (cos(radian) + 1) - placeables[index].width / 2)
//                                .toInt(),
//                            y = (-placeables[index].height / 2 + radius * (1 + sin(radian)))
//                                .toInt()
//                        )
//
//                    } else if (i in 2 * size..3 * size - 1) {
//                        val radian = (degree * i - 180) * (PI / 180)
//
//                        placeables[index].placeRelative(
//                            x = (radius * (1 - sin(radian)) - placeables[index].width / 2)
//                                .toInt(),
//                            y = (-placeables[index].height / 2 + radius * (1 + cos(radian)))
//                                .toInt()
//                        )
//                    } else if (i in 3 * size..4 * size - 1) {
//                        val radian = (degree * i - 270) * (PI / 180)
//
//                        placeables[index].placeRelative(
//                            x = (radius * (1 - cos(radian)) - placeables[index].width / 2)
//                                .toInt(),
//                            y = (-placeables[index].height / 2 + radius * (1 - sin(radian)))
//                                .toInt()
//                        )
//                    }
//                }
            } else {
                val degree = 360.0 / (placeables.size)
                val numberInQuadrant: Int = placeables.size / 4

                for (i in 0..placeables.size) {

                    val index: Int = i

                    if (i in 0..numberInQuadrant - 1) {
                        val radian = degree * (PI / 180) * i

                        placeables[index].placeRelative(
                            x = (radius * (sin(radian) + 1) - placeables[index].width / 2)
                                .toInt(),
                            y = (-placeables[index].height / 2 + radius * (1 - config.curvature*cos(radian)))
                                .toInt()
                        )
                    } else if (i in numberInQuadrant..2 * numberInQuadrant - 1) {
                        val radian = (degree * i - 90) * (PI / 180)

                        placeables[index].placeRelative(
                            x = (radius * (cos(radian) + 1) - placeables[index].width / 2)
                                .toInt(),
                            y = (-placeables[index].height / 2 + radius * (1 + config.curvature*sin(radian)))
                                .toInt()
                        )

                    } else if (i in 2 * numberInQuadrant..3 * numberInQuadrant - 1) {
                        val radian = (degree * i - 180) * (PI / 180)

                        placeables[index].placeRelative(
                            x = (radius * (1 - sin(radian)) - placeables[index].width / 2)
                                .toInt(),
                            y = (-placeables[index].height / 2 + radius * (1 + config.curvature*cos(radian)))
                                .toInt()
                        )
                    } else if (i in 3 * numberInQuadrant..4 * numberInQuadrant - 1) {
                        val radian = (degree * i - 270) * (PI / 180)

                        placeables[index].placeRelative(
                            x = (radius * (1 - cos(radian)) - placeables[index].width / 2)
                                .toInt(),
                            y = (-placeables[index].height / 2 + radius * (1 - config.curvature*sin(radian)))
                                .toInt()
                        )
                    }
                }
            }

        }

    }
}