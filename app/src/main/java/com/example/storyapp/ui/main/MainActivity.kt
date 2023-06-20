package com.example.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.R
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.adapter.StoriesAdapter
import com.example.storyapp.data.remote.story.StoryModel
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.preference.UserPreference
import com.example.storyapp.ui.detail.DetailStoryActivity
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.ui.maps.MapsActivity
import com.example.storyapp.ui.setStory.SetStoryActivity
import com.example.storyapp.ui.setting.ThemeActivity


class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var storiesAdapter: StoriesAdapter
    private lateinit var viewModelFactory: ViewModelFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModelFactory = ViewModelFactory.getInstance(binding.root.context)
        mainViewModel = ViewModelProvider(
            this, viewModelFactory
        )[MainViewModel::class.java]

        storiesAdapter = StoriesAdapter()
        mainViewModel.getStoryList.observe(this) {
            storiesAdapter.submitData(lifecycle, it)
            showStories()
        }
        addStory()
        fabAnimation()
        loggedInCheck()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.theme_setting -> {
                Intent(this@MainActivity, ThemeActivity::class.java).also {
                    startActivity(it)
                }
            }
            R.id.language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.logout -> {
                mainViewModel.logout()
                Intent(this@MainActivity, LoginActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
            R.id.maps -> {
                Intent(this@MainActivity, MapsActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loggedInCheck() {
        val userPref = UserPreference(this)
        if (userPref.getUser().userId == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    private fun addStory() {
        binding.addStory.setOnClickListener {
            val intent = Intent(this@MainActivity, SetStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fabAnimation() {
        binding.rvStories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, x: Int, y: Int) {
                super.onScrolled(rv, x, y)
                if (y > 0 && binding.addStory.isShown) {
                    binding.addStory.animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(200)
                        .start()
                } else {
                    binding.addStory.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(200)
                        .start()
                }
            }
        })
    }

    private fun showStories() {
        binding.tvEmpty.visibility = View.INVISIBLE
        binding.rvStories.visibility = View.VISIBLE
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storiesAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storiesAdapter.retry()
                }
            )
            setHasFixedSize(true)
        }
        storiesAdapter.setOnItemClickCallback(object : StoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(story: StoryModel?) {
                Intent(this@MainActivity, DetailStoryActivity::class.java).apply {
                    putExtra(DetailStoryActivity.DETAIL, story)
                    startActivity(this)
                }

            }

        })

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}