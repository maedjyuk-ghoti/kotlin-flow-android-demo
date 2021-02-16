package com.example.flowplayground

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.example.flowplayground.repo.Animal
import com.example.flowplayground.repo.AppDatabase
import com.example.flowplayground.repo.DatabaseHolder
import com.example.flowplayground.ui.AddAnimalDialog
import com.example.flowplayground.ui.main.SectionsPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class MainActivityViewModelFactory(
    private val db: AppDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (MainActivityViewModel::class.java.isAssignableFrom(modelClass)) {
            modelClass.getConstructor(AppDatabase::class.java)
                .newInstance(db)
        } else {
            throw RuntimeException("Cannot create an instance of $modelClass")
        }
    }
}

class MainActivityViewModel(
    val db: AppDatabase
) : ViewModel() {
    suspend fun addAnimal(name: String, cuteness: Int, barkVolume: Int) {
        db.animalDao().add(Animal(name, cuteness, barkVolume))
    }
}

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(DatabaseHolder.getDatabase(application))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // make the lazy evaluation happen so that it's ready for the fragments
        viewModel.toString()

        val viewPager: ViewPager = findViewById<ViewPager>(R.id.view_pager)
            .apply {
                adapter = SectionsPagerAdapter(
                    this@MainActivity.resources::getString,
                    supportFragmentManager
                )
            }

        findViewById<TabLayout>(R.id.tabs)
            .setupWithViewPager(viewPager)

        findViewById<FloatingActionButton>(R.id.fab)
            .setOnClickListener { view ->
                val addDogDialog = AddAnimalDialog(view.context)
                AlertDialog.Builder(view.context)
                    .setView(addDogDialog.view)
                    .setPositiveButton("Save") { _, _ ->
                        lifecycleScope.launch {
                            viewModel.addAnimal(
                                addDogDialog.getName(),
                                addDogDialog.getCuteness(),
                                addDogDialog.getBarkVolume()
                            )
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                    .show()
            }
    }
}