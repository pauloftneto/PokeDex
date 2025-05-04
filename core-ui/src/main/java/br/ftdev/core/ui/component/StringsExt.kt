package br.ftdev.core.ui.component

import android.content.Context
import coil.request.ImageRequest

private const val LENGTH = 3

fun Int.toPaddedId(): String = toString().padStart(LENGTH, '0')

fun String.toImageRequest(context: Context) = ImageRequest.Builder(context)
    .data(this)
    .allowHardware(false)
    .crossfade(true)
    .build()