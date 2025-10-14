/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.anael.samples.apps.windradar.data

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the wind data from a weather search API
 */
data class WindData(
    @field:SerializedName("wind_speed_10m")  val windSpeeds: List<Double>,
    @field:SerializedName("wind_gusts_10m") val windGusts: List<Double>,
    @field:SerializedName("time") val time: List<String>
)
