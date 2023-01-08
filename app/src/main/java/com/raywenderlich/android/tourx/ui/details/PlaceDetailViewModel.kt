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

package com.raywenderlich.android.tourx.ui.details

import androidx.hilt.lifecycle.ViewModelInject
import com.raywenderlich.android.tourx.BaseViewModel
import com.raywenderlich.android.tourx.entities.Cost
import com.raywenderlich.android.tourx.models.PlaceDetail
import com.raywenderlich.android.tourx.networking.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers

class PlaceDetailViewModel
@ViewModelInject constructor(private val service: ApiService) : BaseViewModel<PlaceDetail>() {

  fun loadPlaceDetails(id: Int) {
    startLoading()
    val costSource = service.fetchCostById(id)
    val placeSource = service.fetchPlaceById(id)

    costSource.zipWith(placeSource)
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { recordStartTime() }
        .observeOn(AndroidSchedulers.mainThread())
        .map {
          return@map PlaceDetail(cost = it.first, place = it.second)
        }
        .subscribeBy(onSuccess = onSuccessHandler, onError = onErrorHandler)
        .addTo(disposable)
  }

  fun calculateTravelCost(cost: Cost, passengerCount: Int, isTwoWayTravel: Boolean) =
      cost.price.times(passengerCount).times(if (isTwoWayTravel) 2 else 1)

}