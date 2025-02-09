package org.example

import kotlin.math.abs
import kotlin.math.floor
import org.example.lib.ColourHSB
import processing.core.PApplet

class Processing : PApplet() {

    private val canvasWidth = 2560
    private val canvasHeight = 1440
    private val gridSize = 20
    private lateinit var gridPoints: List<GridPoint>

    private val secondsToCapture = 60
    private val videoFrameRate = 60
    private var videoFramesCaptured = 0

    private var t = 0

    override fun settings() {
        size(canvasWidth, canvasHeight)
    }

    override fun setup() {
        background(0)
        noStroke()
        colorMode(HSB, 360F, 100F, 100F)

        val leftOffset = (width % gridSize) / 2
        val topOffset = (height % gridSize) / 2

        gridPoints = (0 until canvasWidth / gridSize).flatMap { x ->
            (0 until canvasHeight / gridSize).map { y ->
                val gridSquareLeft = leftOffset + (x * gridSize)
                val gridSquareCenterX = gridSquareLeft + (gridSize / 2.0)
                val centerX = canvasWidth / 2.0
                val horizontalDistanceFromCenter = abs(gridSquareCenterX - centerX)

                val gridSquareTop = topOffset + (y * gridSize)
                val gridSquareCenterY = gridSquareTop + (gridSize / 2.0)
                val centerY = canvasHeight / 2.0
                val verticalDistanceFromCenter = abs(gridSquareCenterY - centerY)

                GridPoint(
                    x = gridSquareLeft,
                    y = gridSquareTop,
                    oddOrEven = (x + y) % 2 == 0,
                    horizontalDistanceFromCenter = horizontalDistanceFromCenter,
                    verticalDistanceFromCenter = verticalDistanceFromCenter,
                    radiusFromCenter = Math.sqrt(
                        Math.pow(horizontalDistanceFromCenter, 2.0) +
                                ((16.0 / 9.0) * Math.pow(verticalDistanceFromCenter, 2.0))
                    )
                )
            }
        }

        gridPoints = gridPoints.sortedBy { it.radiusFromCenter }
    }

    override fun draw() {
        t += 1
        val colourT = t * 0.1

        // background squares
        gridPoints.forEach {
            val bgBrightness =
                (60 * Math.pow(triangle((-t / 600.0) + (4.0 * Math.pow(it.radiusFromCenter / canvasWidth, 0.5)), 0.2), 4.0)) + 10

            square(
                x = it.x.toDouble(),
                y = it.y.toDouble(),
                size = gridSize.toDouble(),
                rotation = 0.0,
                innerScale = 1.0,
                colour = ColourHSB(colourT + 360, 100, bgBrightness),
            )
        }

        // foreground shapes
        gridPoints.forEach {
            val innerScale =
                sinusoidal((t / 1000.0) + (5.323453 * (if (it.oddOrEven) it.horizontalDistanceFromCenter else it.verticalDistanceFromCenter) / canvasWidth))
//                    .let { if (oddOrEven) it else 1 - it }
                    .let { 0.1 + (1.0 * it) }

            val fgBrightness1 =
                (80 * Math.pow(1.0 - triangle((t / 150.0) + (6.0 * Math.pow(it.radiusFromCenter / canvasWidth, 0.5)), 0.1), 2.0)) + 10
            val fgBrightness2 =
                (80 * Math.pow(1.0 - triangle((t / 270.0) + (6.0 * Math.pow(it.radiusFromCenter / canvasWidth, 0.5)), 0.1), 2.0)) + 10

            val squareRotation = (t / 100.0) + it.radiusFromCenter / 200.0

            val ellipseRotation = (-t / 60.0) + it.radiusFromCenter / 300.0

            if (it.oddOrEven) {
                square(
                    x = it.x.toDouble(),
                    y = it.y.toDouble(),
                    size = gridSize.toDouble(),
                    rotation = squareRotation,
                    innerScale = innerScale * 0.6,
                    colour = ColourHSB(colourT + 20, 100, fgBrightness1),
                )
            } else {
                ellipse(
                    x = it.x.toDouble(),
                    y = it.y.toDouble(),
                    size = gridSize.toDouble(),
                    rotation = ellipseRotation,
                    innerScale = innerScale,
                    axisRatio = 0.5,
                    colour = ColourHSB(colourT + 240, 100, fgBrightness2),
                )
            }
        }

//        if (videoFramesCaptured > videoFrameRate * secondsToCapture) {
//            println("stopping")
//            stop()
//        } else {
//            saveFrame("export/####-export.tga")
//            videoFramesCaptured++
//        }
    }

    private fun square(
        x: Double,
        y: Double,
        size: Double,
        rotation: Double,
        innerScale: Double,
        colour: ColourHSB
    ) {
        val offset = size * (1 - innerScale) * 0.5
        val innerSize = size * innerScale
        pushMatrix()
        fill(colour)
        translate((x + (size / 2)).toFloat(), (y + (size / 2)).toFloat())
        rotate(rotation.toFloat())
        rect(-innerSize / 2.0, -innerSize / 2.0, size * innerScale, size * innerScale)
        popMatrix()
    }

    private fun ellipse(
        x: Double,
        y: Double,
        size: Double,
        rotation: Double,
        innerScale: Double,
        axisRatio: Double,
        colour: ColourHSB
    ) {
        pushMatrix()
        fill(colour)
        translate((x + (size / 2)).toFloat(), (y + (size / 2)).toFloat())
        rotate(rotation.toFloat())
        ellipse(0, 0, size * innerScale * axisRatio, size * innerScale)
        popMatrix()
    }

    private fun triangle(t: Double, offset: Double = 0.5): Double {
        val x = t - floor(t)
        return if (x < offset) {
            1 - (x / offset)
        } else {
            (x - offset) / (1 - offset)
        }
    }

    private fun sinusoidal(t: Double): Double {
        val tAbs = Math.abs(t)
        val x = tAbs - tAbs.toInt()
        return 0.5 * Math.sin(x * 2 * Math.PI) + 0.5
    }

    private fun rect(x: Number, y: Number, width: Number, height: Number) {
        rect(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
    }

    private fun ellipse(x: Number, y: Number, width: Number, height: Number) {
        ellipse(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
    }

    private fun fill(colour: ColourHSB) {
        fill(colour.h, colour.s, colour.b)
    }

}
