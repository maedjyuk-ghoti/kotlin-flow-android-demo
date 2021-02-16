package com.example.flowplayground.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.flowplayground.R

private val TAB_TITLES: Array<Int> =
    arrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2,
    )

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(
    private val getString: (Int) -> String?,
    fm: FragmentManager
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> AnimalList()
            1 -> SearchableAnimalList()
            else -> AnimalList()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}