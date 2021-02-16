package com.example.flowplayground

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.flowplayground.repo.AppDatabase
import com.example.flowplayground.repo.DatabaseHolder
import com.example.flowplayground.repo.Dog
import com.example.flowplayground.ui.main.SectionsPagerAdapter
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
): ViewModel()

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
                adapter = SectionsPagerAdapter(this@MainActivity.resources::getString, supportFragmentManager)
            }

        findViewById<TabLayout>(R.id.tabs)
            .setupWithViewPager(viewPager)

        findViewById<FloatingActionButton>(R.id.fab)
            .setOnClickListener { view ->
                val addDogDialog = AddDogDialog()
                AlertDialog.Builder(view.context)
                    .setView(addDogDialog.getView(view.context))
                    .setPositiveButton("Save") { _, _ -> lifecycleScope.launch { viewModel.db.dogDao().addDog(addDogDialog.getDogFromValues()) } }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                    .show()
            }
    }

    private class AddDogDialog {
        private val nameId = View.generateViewId()
        private val cutenessId = View.generateViewId()
        private val barkId = View.generateViewId()

        private lateinit var name: EditText
        private lateinit var cuteness: EditText
        private lateinit var barkLevel: EditText

        fun getView(context: Context): View =
            RelativeLayout(context).apply {
                name = EditText(context).apply {
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

                cuteness = EditText(context).apply {
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

                barkLevel = EditText(context).apply {
                    this.id = barkId
                    this.hint = "Bark Level"
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

        fun getDogFromValues(): Dog =
            Dog(name.text.toString(),
                cuteness.text.toString().toInt(),
                barkLevel.text.toString().toInt())
    }
}