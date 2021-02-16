package com.example.flowplayground.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.flowplayground.MainActivityViewModel
import com.example.flowplayground.R
import com.example.flowplayground.repo.AppDatabase
import com.example.flowplayground.repo.Dog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class AnimalListViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    @Throws(RuntimeException::class)
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            AnimalListViewModel::class.java.isAssignableFrom(modelClass) ->
                modelClass.getConstructor(AppDatabase::class.java).newInstance(db)
            else ->
                throw RuntimeException("Cannot create an instance of $modelClass")
        }
    }
}

class AnimalListViewModel(db: AppDatabase) : ViewModel() {
    private val selected: MutableStateFlow<Int> = MutableStateFlow(0)
    val text: Flow<String> = selected.map(::selectionToString)
    val allDogs: Flow<List<Dog>> = db.dogDao().getAllDogsFlow()

    private fun selectionToString(selection: Int): String =
        when (selection) {
            0 -> "All"
            else -> "Unknown"
        }
}

class AnimalList : Fragment() {

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val pageViewModel: AnimalListViewModel by viewModels {
        AnimalListViewModelFactory(activityViewModel.db)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textView: TextView = view.findViewById(R.id.section_label)

        val dogAdapter = DogAdapter(listOf())
        view.findViewById<RecyclerView>(R.id.result_list_1).adapter = dogAdapter

        lifecycleScope.run {
            launchWhenStarted { pageViewModel.text.collect { text -> textView.text = text } }
            launchWhenStarted { pageViewModel.allDogs.collect(dogAdapter::updateDataSet) }
        }
    }
}