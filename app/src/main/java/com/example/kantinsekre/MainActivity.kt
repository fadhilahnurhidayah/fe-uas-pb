package com.example.kantinsekre

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.activity.viewModels
import com.example.kantinsekre.databinding.ActivityMainBinding
import com.example.kantinsekre.presentation.SharedViewModel
import com.example.kantinsekre.presentation.product.ProductFragment
import com.example.kantinsekre.presentation.transaction.TransactionFragment
//import com.example.kantinsekre.presentation.report.ReportFragment
//import com.example.kantinsekre.presentation.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        supportActionBar?.hide()

        if (savedInstanceState == null) {
            loadFragment(ProductFragment())
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_product -> {
                    loadFragment(ProductFragment())
                    true
                }
                R.id.navigation_transaction -> {
                    loadFragment(TransactionFragment())
                    true
                }
//                R.id.navigation_report -> {
//                    loadFragment(ReportFragment())
//                    true
//                }
//                R.id.navigation_settings -> {
//                    loadFragment(SettingsFragment())
//                    true
//                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}