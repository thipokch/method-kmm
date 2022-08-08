package ch.thipok.method.style

import dev.icerock.moko.resources.FontResource

interface TypographySet {
    val font: FontResource
    val size: Double
    val lineHeight: Double
}