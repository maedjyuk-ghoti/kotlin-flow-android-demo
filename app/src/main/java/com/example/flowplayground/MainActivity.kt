package com.example.flowplayground

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.example.flowplayground.repo.Animal
import com.example.flowplayground.repo.AppDatabase
import com.example.flowplayground.repo.DatabaseHolder
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
                val addDogDialog = AddDogDialog(view.context)
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

    private class AddDogDialog(context: Context) {
        companion object {
            private val nameId = View.generateViewId()
            private val cutenessId = View.generateViewId()
            private val barkId = View.generateViewId()
        }

        val view: View
        private val nameET: EditText
        private val cutenessET: EditText
        private val barkVolumeET: EditText

        init {
            view = RelativeLayout(context).apply {
                nameET = EditText(context).apply {
                    this.id = nameId
                    this.hint = "Name"
                    this.inputType = InputType.TYPE_CLASS_TEXT
                    layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        this.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                        this.addRule(RelativeLayout.ALIGN_PARENT_START)
                    }
                }.also(this::addView)

                cutenessET = EditText(context).apply {
                    this.id = cutenessId
                    this.hint = "Cuteness"
                    this.inputType = InputType.TYPE_CLASS_NUMBER
                    layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        this.addRule(RelativeLayout.BELOW, nameId)
                        this.addRule(RelativeLayout.ALIGN_PARENT_START)
                    }
                }.also(this::addView)

                barkVolumeET = EditText(context).apply {
                    this.id = barkId
                    this.hint = "Bark Volume"
                    this.inputType = InputType.TYPE_CLASS_NUMBER
                    layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        this.addRule(RelativeLayout.BELOW, cutenessId)
                        this.addRule(RelativeLayout.ALIGN_PARENT_START)
                    }
                }.also(this::addView)
            }
        }

        fun getName(): String = nameET.text.toString()
        fun getCuteness(): Int = cutenessET.text.toString().toInt()
        fun getBarkVolume(): Int = barkVolumeET.text.toString().toInt()
    }
}