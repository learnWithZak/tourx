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

package com.raywenderlich.android.tourx

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raywenderlich.android.tourx.models.State
import io.reactivex.disposables.CompositeDisposable
import java.util.*

/**
 * Handlers must be called in UI Thread.
 */
open class BaseViewModel<T> : ViewModel() {
  private var startTime: Long = Calendar.getInstance().timeInMillis
  protected var disposable = CompositeDisposable()

  protected val _result = MutableLiveData<State<T>>()
  val result: LiveData<State<T>>
    get() = _result

  protected val onErrorHandler: (Throwable) -> Unit = {
    Log.i(LOG_TAG, "onErrorHandler after: " + diffWith() + "s")
    _result.value = State.Error(it.message ?: "Failed to load data.")
  }

  protected val onDataLoadHandler: (T) -> Unit = {
    Log.i(LOG_TAG, "onDataLoadHandler after: " + diffWith() + "s")
    _result.value = State.Loaded(it)
  }

  protected val onCompleteHandler: () -> Unit = {
    Log.i(LOG_TAG, "completeHandler after: " + diffWith() + "s")
    _result.value = State.Complete
  }

  protected val onSuccessHandler: (T) -> Unit = {
    Log.i(LOG_TAG, "onSuccessHandler callback after: " + diffWith() + "s")
    onDataLoadHandler(it)
    onCompleteHandler()
  }

  protected fun recordStartTime() {
    startTime = Calendar.getInstance().timeInMillis
  }

  private fun diffWith(): Long {
    return (Calendar.getInstance().timeInMillis - startTime) / 1000
  }

  protected fun startLoading() {
    Log.i(LOG_TAG, "--------------")
    disposeCurrentlyRunningStreams()
    _result.value = State.Loading
  }

  fun disposeCurrentlyRunningStreams() {
    disposable.dispose()
    disposable = CompositeDisposable()
  }

  override fun onCleared() {
    disposable.clear()
    super.onCleared()
  }
}