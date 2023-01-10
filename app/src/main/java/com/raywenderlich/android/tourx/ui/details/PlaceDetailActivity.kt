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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.NumberPicker
import androidx.activity.viewModels
import com.raywenderlich.android.tourx.LOG_TAG
import com.raywenderlich.android.tourx.R
import com.raywenderlich.android.tourx.models.PlaceDetail
import com.raywenderlich.android.tourx.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_place_detail.*

@AndroidEntryPoint
class PlaceDetailActivity : BaseActivity<PlaceDetail>() {

  companion object {
    private const val PLACE_ID = "PLACE_ID"

    @JvmStatic
    fun start(context: Context, id: Int) {
      context.startActivity(
          Intent(context, PlaceDetailActivity::class.java).apply { putExtra(PLACE_ID, id) }
      )
    }
  }

  private val disposable = CompositeDisposable()
  private val viewModel: PlaceDetailViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_place_detail)

    setupNumberPicker()

    val id = intent.getIntExtra(PLACE_ID, Integer.MIN_VALUE)

    viewModel.loadPlaceDetails(id)
    viewModel.result.observe(this, this)
  }

  private fun setupNumberPicker() {
    with(numberOfPassengerPicker) {
      minValue = 1
      maxValue = 20
    }
  }

  override fun onTaskLoading() {
    progressBar.visibility = View.VISIBLE
    detailContainer.visibility = View.GONE
  }

  override fun onDataLoaded(data: PlaceDetail) {
    loadDataInUI(data)
  }

  override fun onTaskComplete() {
    progressBar.visibility = View.GONE
  }

  override fun onError(message: String) {
    detailContainer.visibility = View.GONE
    progressBar.visibility = View.GONE
  }

  private fun loadDataInUI(placeDetail: PlaceDetail) {
    with(placeDetail) {
      detailContainer.visibility = View.VISIBLE
      nameTextView.text = place.name
      planetTextView.text = place.planet
      costTextView.text = cost.price.toString()

      val isTwoWayTravelObservable = twoWayTravelCheckbox.toObservable()
      val totalPassengerObservable = numberOfPassengerPicker.toObservable()
      combineUsingCombineLatest(this, isTwoWayTravelObservable, totalPassengerObservable)

    }
  }

  /**
   * Uses concat operator to combine the two streams.
   * This does not work as concat operator maintains order and since the both observables are "infinite",
   * as soon as the first observables emits, the second observables never gets a chance to emit.
   */
  private fun combineUsingConcat(
      booleanObservable: Observable<Boolean>,
      integerObservable: Observable<Int>
  ): Disposable {
    return Observable.concat(booleanObservable, integerObservable)
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onNext = { input ->
          Log.i(LOG_TAG, input.toString())
        }).addTo(disposable)
  }

  /**
   * Using combineLatest, the latest emission from the Observables are relayed down the chain.
   */
  private fun combineUsingCombineLatest(
      data: PlaceDetail, booleanObservable: Observable<Boolean>,
      integerObservable: Observable<Int>
  ) {
    Observable.combineLatest<Boolean, Int, Pair<Boolean, Int>>(
        booleanObservable,
        integerObservable,
        BiFunction { t1, t2 ->
          return@BiFunction Pair(t1, t2)
        })
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onNext = { input ->
          val passengerCount = input.second
          val isTwoWayTravel = input.first
          resultTextView.text =
              viewModel.calculateTravelCost(data.cost, passengerCount, isTwoWayTravel)
                  .toString()
        }).addTo(disposable)
  }


  override fun onDestroy() {
    disposable.dispose()
    super.onDestroy()
  }

  /**
   * Converts the checked change event of [CheckBox] to streams
   */
  private fun CheckBox.toObservable(): Observable<Boolean> {
    return Observable.create<Boolean> {
      setOnCheckedChangeListener { _, isChecked ->
        it.onNext(isChecked)
      }
    }.startWith(isChecked)
  }

  /**
   * Converts the value change events of [NumberPicker] to streams.
   */
  private fun NumberPicker.toObservable(): Observable<Int> {
    return Observable.create<Int> {
      setOnValueChangedListener { _, _, newVal ->
        it.onNext(newVal)
      }
    }.startWith(value)
  }
}