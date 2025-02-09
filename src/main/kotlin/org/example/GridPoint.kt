package org.example

data class GridPoint(
    val x: Int,
    val y: Int,
    val oddOrEven: Boolean,
    val horizontalDistanceFromCenter: Double,
    val verticalDistanceFromCenter: Double,
    val radiusFromCenter: Double,
)
