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
package com.raywenderlich.android.tourx.ui.placelist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.raywenderlich.android.tourx.R
import com.raywenderlich.android.tourx.entities.Places
import com.raywenderlich.android.tourx.ui.BaseActivity
import com.raywenderlich.android.tourx.ui.details.PlaceDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class PlaceListActivity : BaseActivity<Places>() {

  private val viewModel: PlaceListViewModel by viewModels()

  private lateinit var adapter: PlaceAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    recyclerView.layoutManager = LinearLayoutManager(this)

    adapter = PlaceAdapter {
      PlaceDetailActivity.start(this, it)
    }
    recyclerView.adapter = adapter

    viewModel.loadOnReceive()
    viewModel.result.observe(this, this)
  }

  override fun onTaskLoading() {
    adapter.reset()
    progressBar.visibility = View.VISIBLE
  }

  override fun onDataLoaded(data: Places) {
    adapter.add(data)
  }

  override fun onTaskComplete() {
    progressBar.visibility = View.GONE
  }

  override fun onError(message: String) {
    progressBar.visibility = View.GONE
    Toast.makeText(this@PlaceListActivity, message, Toast.LENGTH_SHORT).show()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menuLoadWhenReady -> viewModel.loadOnReceive()
      R.id.menuAtOnce -> viewModel.loadAllAtOnce()
      R.id.menuLoadQuickest -> viewModel.loadTheQuickestOne()
      R.id.enableExperimental -> viewModel.loadExperimental()
      R.id.switchOnNext -> viewModel.demonstrateSwitchOnNext()
      R.id.join -> viewModel.demonstrateJoinBehavior()
      R.id.dispose -> viewModel.disposeCurrentlyRunningStreams()
      R.id.groupJoin -> viewModel.demonstrateGroupJoin()
    }

    return super.onOptionsItemSelected(item)
  }

}