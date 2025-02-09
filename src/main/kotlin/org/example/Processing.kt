package org.example

import org.example.lib.ColourHSB
import processing.core.PApplet

class Processing : PApplet() {

    private val canvasWidth = 2560
    private val canvasHeight = 1440
    private val gridSize = 20

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
                    ) + ((16.0/9.0) * Math.pow(distanceFromCenterY.toDouble(), 2.0))
                )

                val innerScale = sinusoidal((t / 1000.0) + (2.323453 * (if (oddOrEven) distanceFromCenterX else distanceFromCenterY) / canvasWidth))
//                    .let { if (oddOrEven) it else 1 - it }
                    .let { 0.1 + (0.8 * it) }

                val fgBrightness1 = (80 * Math.pow(1.0 - triangle((t / 200.0) + (6.0 * Math.pow(d / canvasWidth, 0.5)), 0.1), 2.0)) + 10
                val fgBrightness2 = (80 * Math.pow(1.0 - triangle((t / 270.0) + (6.0 * Math.pow(d / canvasWidth, 0.5)), 0.1), 2.0)) + 10
                val bgBrightness = (80 * Math.pow(triangle((-t / 600.0) + (4.0 * Math.pow(d / canvasWidth, 0.5)), 0.2), 4.0)) + 10

                val squareRotation = (t / 1200.0) + d / 200.0

                val ellipseRotation = (-t / 1000.0) + d / 300.0

                val colourT = t * 0.1

                square(
                    x = gridSquareLeft.toDouble(),
                    y = topOffset + (y.toDouble() * gridSize),
                    size = gridSize.toDouble(),
                    rotation = 0.0,
                    innerScale = 1.0,
                    colour = ColourHSB(colourT + 360, 100, bgBrightness),
                )

                if (oddOrEven) {
                    square(
                        x = gridSquareLeft.toDouble(),
                        y = topOffset + (y.toDouble() * gridSize),
                        size = gridSize.toDouble(),
                        rotation = squareRotation,
                        innerScale = innerScale * 0.6,
//                        axisRatio = 0.4,
                        colour = ColourHSB(colourT + 20, 100, fgBrightness1),
                    )
                } else {
                    ellipse(
                        x = gridSquareLeft.toDouble(),
                        y = topOffset + (y.toDouble() * gridSize),
                        size = gridSize.toDouble(),
                        rotation = ellipseRotation,
                        innerScale = innerScale,
                        axisRatio = 0.6,
                        colour = ColourHSB(colourT + 240, 100, fgBrightness2),
                    )
                }
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
        val tAbs = Math.abs(t)
        val x = tAbs - tAbs.toInt()
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
