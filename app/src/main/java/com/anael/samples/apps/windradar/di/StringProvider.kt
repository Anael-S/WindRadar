package com.anael.samples.apps.windradar.di

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StringProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun get(id: Int): String = context.getString(id)
}
