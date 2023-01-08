/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.raywenderlich.android.tourx.networking

import com.raywenderlich.android.tourx.MIN_DELAY_IN_MS
import com.raywenderlich.android.tourx.database.AppDatabase
import com.raywenderlich.android.tourx.entities.Cost
import com.raywenderlich.android.tourx.entities.Place
import com.raywenderlich.android.tourx.entities.Places
import io.reactivex.Single
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

class MockApiService(private val db: AppDatabase) : ApiService {

  /**
   * Simulates a slower operation than [fetchEarthPlaces]
   */
  override fun fetchMarsPlaces(): Single<List<Place>> {
    return db.getPlaceDao().loadPlacesByPlanet("Mars")
        .delay(MIN_DELAY_IN_MS + 2000, TimeUnit.MILLISECONDS)
  }

  /**
   * Faster operation as compared to [fetchMarsPlaces]
   */
  override fun fetchEarthPlaces(): Single<List<Place>> {
    return db.getPlaceDao().loadPlacesByPlanet("Earth")
        .delay(MIN_DELAY_IN_MS, TimeUnit.MILLISECONDS)
  }

  /**
   * Immediately returns an error.
   */
  override fun fetchFromExperimentalApi(): Single<Places> {
    return Single.error { RuntimeException("Error while loading experimental features.") }
  }

  override fun fetchCostById(id: Int): Single<Cost> {
    return db.getCostDao().loadPlaceById(id)
        .delay(MIN_DELAY_IN_MS + 500, TimeUnit.MILLISECONDS)
  }

  override fun fetchPlaceById(id: Int): Single<Place> {
    return db.getPlaceDao().loadPlaceById(id)
        .delay(MIN_DELAY_IN_MS + 1000, TimeUnit.MILLISECONDS)
  }
}