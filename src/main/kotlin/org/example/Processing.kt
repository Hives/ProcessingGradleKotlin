package org.example

import org.example.lib.ColourHSB
import processing.core.PApplet

class Processing : PApplet() {

    private val canvasWidth = 2560
    private val canvasHeight = 1440
    private val gridSize = 30

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
    }

    override fun draw() {
        t++

        val leftOffset = (width % gridSize) / 2
        val topOffset = (height % gridSize) / 2

        for (x in 0 until canvasWidth / gridSize) {
            for (y in 0 until canvasHeight / gridSize) {
                val oddOrEven = (x + y) % 2 == 0

                val gridSquareLeft = leftOffset + (x * gridSize)
                val gridSquareCenterX = gridSquareLeft + (gridSize / 2)
                val centerX = canvasWidth / 2
                val distanceFromCenterX = abs(gridSquareCenterX - centerX)

                val gridSquareTop = topOffset + (y * gridSize)
                val gridSquareCenterY = gridSquareTop + (gridSize / 2)
                val centerY = canvasHeight / 2
                val distanceFromCenterY = abs(gridSquareCenterY - centerY)

                val d = Math.sqrt(
                    Math.pow(
                        distanceFromCenterX.toDouble(),
                        2.0
                    ) + ((16.0 / 9.0) * Math.pow(distanceFromCenterY.toDouble(), 2.0))
                )

                val innerScale = sinusoidal((t / 1000.0) + (2.323453 * (if (oddOrEven) distanceFromCenterX else distanceFromCenterY) / canvasWidth))
//                    .let { if (oddOrEven) it else 1 - it }
//                    .let { 0.1 + (0.8 * it) }

                val fgBrightness = (80 * Math.pow(1.0 - triangle((t / 200.0) + (3.0 * d / canvasWidth), 0.1), 2.0)) + 10
                val bgBrightness = (80 * Math.pow(triangle((-t / 600.0) + (4.0 * d / canvasWidth)), 2.0)) + 10

                shapeInSquare(
                    x = gridSquareLeft.toDouble(),
                    y = topOffset + (y.toDouble() * gridSize),
                    size = gridSize.toDouble(),
                    innerScale = innerScale,
                    fg = ColourHSB(240, 100, fgBrightness),
                    bg = ColourHSB(360, 100, bgBrightness),
                    shape = if (oddOrEven) Shape.CIRCLE else Shape.SQUARE,
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

    private fun shapeInSquare(
        x: Double,
        y: Double,
        size: Double,
        innerScale: Double,
        fg: ColourHSB,
        bg: ColourHSB,
        shape: Shape
    ) {
        fill(bg)
        rect(x, y, size, size)
        fill(fg)
        when (shape) {
            Shape.SQUARE -> {
                val innerSize = size * innerScale;
                rect(x + ((size - innerSize) / 2), y + ((size - innerSize) / 2), innerSize, innerSize)
            }

            Shape.CIRCLE -> {
                ellipse(x + (size / 2), y + (size / 2), size * innerScale, size * innerScale)
            }
        }
    }

    private enum class Shape { SQUARE, CIRCLE }

    private fun triangle(t: Double, offset: Double = 0.5): Double {
        val tAbs = Math.abs(t)
        val x = tAbs - tAbs.toInt()
        return if (x < offset) {
            1 - (x / offset)
        } else {
            (x - offset) / (1 - offset)
        }
    }

    private fun sawtooth(t: Double): Double {
        val tAbs = Math.abs(t)
        return tAbs - tAbs.toInt()
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
