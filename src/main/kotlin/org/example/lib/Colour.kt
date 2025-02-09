package org.example.lib

data class ColourHSB(val h: Float, val s: Float, val b: Float) {
    constructor(h: Number, s: Number, b: Number) : this(h.toFloat() % 360, s.toFloat(), b.toFloat())
}
