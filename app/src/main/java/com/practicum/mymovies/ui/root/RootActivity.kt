package com.practicum.mymovies.ui.root

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.mymovies.R
import com.practicum.mymovies.databinding.ActivityRootBinding
import com.practicum.mymovies.utils.invisible
import com.practicum.mymovies.utils.visible

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.detailsFragment, R.id.moviesCastFragment -> {
                    binding.bottomNavigationView.invisible()
                }

                else -> {
                    binding.bottomNavigationView.visible()
                }
            }
        }
    }

    fun animateBottomNavigationView() {
        binding.bottomNavigationView.invisible()
    }

}