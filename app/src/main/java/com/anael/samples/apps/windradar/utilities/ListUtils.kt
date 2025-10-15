package com.anael.samples.apps.windradar.utilities

object ListUtils {
    fun <T> List<T>.sliceBy(indices: List<Int>): List<T> = indices.map { this[it] }
}
