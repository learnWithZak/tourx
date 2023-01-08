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

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.raywenderlich.android.tourx.LOG_TAG
import com.raywenderlich.android.tourx.R
import com.raywenderlich.android.tourx.entities.Cost
import com.raywenderlich.android.tourx.entities.Place
import com.raywenderlich.android.tourx.ui.BaseActivity
import com.raywenderlich.android.tourx.ui.placelist.PlaceListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity<Nothing>() {
  private val gson = Gson()
  private val resource: Resources
    get() = this.resources

  private val viewModel: SplashViewModel by viewModels()

  private val costs: List<Cost>
    get() {
      val json = resource.openRawResource(R.raw.prices).bufferedReader().use { it.readText() }
      return gson.fromJson(json, object : TypeToken<List<Cost>>() {}.type)
    }

  private val places: List<Place>
    get() {
      val json = resource.openRawResource(R.raw.places).bufferedReader().use { it.readText() }
      return gson.fromJson(json, object : TypeToken<List<Place>>() {}.type)
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    makeFullScreen()
    setContentView(R.layout.activity_splash)

    viewModel.populateData(places = places, costs = costs)
    viewModel.result.observe(this, this)
  }

  override fun onDataLoaded(data: Nothing) {
    Log.i(LOG_TAG, "Populating db...")
  }

  override fun onTaskComplete() {
    startActivity(Intent(this, PlaceListActivity::class.java))
    finish()
  }

  override fun onError(message: String) {
    Log.e(LOG_TAG, message)
    Toast.makeText(this@SplashActivity, message, Toast.LENGTH_SHORT).show()
  }

  private fun makeFullScreen() {
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )
    supportActionBar?.hide()
  }
}