package com.example.bms_dashbboard_mobile

import AddDataFragment
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.etebarian.meowbottomnavigation.MeowBottomNavigation

class DashboardActivity : AppCompatActivity() {
    private lateinit var bottomNavigation : MeowBottomNavigation
    private lateinit var fragment: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        bottomNavigation = findViewById(R.id.bottom_navigation)
        fragment = findViewById(R.id.fragment_container)

        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.ic_home))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.ic_add))
        bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.ic_setting))



        bottomNavigation.setOnClickMenuListener {
            when (it.id) {
                1 -> {
                    replaceFragment(OverviewFragment())
                }
                2 -> {
                    replaceFragment(AddDataFragment())
                }
                3 -> {
                    replaceFragment(SettingsFragment())
                }
            }
        }

        replaceFragment(OverviewFragment())
        bottomNavigation.show(1)
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

}