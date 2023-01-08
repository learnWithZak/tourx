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

package com.raywenderlich.android.tourx.ui.splash

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import com.raywenderlich.android.tourx.BaseViewModel
import com.raywenderlich.android.tourx.LOG_TAG
import com.raywenderlich.android.tourx.database.AppDatabase
import com.raywenderlich.android.tourx.entities.Cost
import com.raywenderlich.android.tourx.entities.Places
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SplashViewModel
@ViewModelInject constructor(private val database: AppDatabase) : BaseViewModel<Nothing>() {

  fun populateData(places: Places, costs: List<Cost>) {
    val insertPlaceSource = database.getPlaceDao().bulkInsert(places)
        .delay(2, TimeUnit.SECONDS)
        .doOnComplete { Log.i(LOG_TAG, "Completed inserting places into database") }
    val insertCostSource = database.getCostDao().bulkInsert(costs)
        .delay(1, TimeUnit.SECONDS)
        .doOnComplete { Log.i(LOG_TAG, "Completed inserting costs into database") }

    insertPlaceSource.mergeWith(insertCostSource)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onComplete = onCompleteHandler, onError = onErrorHandler)
        .addTo(disposable)
  }
}