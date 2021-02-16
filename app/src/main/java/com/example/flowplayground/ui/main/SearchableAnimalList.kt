package com.example.flowplayground.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.flowplayground.MainActivityViewModel
import com.example.flowplayground.R
import com.example.flowplayground.repo.Animal
import com.example.flowplayground.repo.AppDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest

class SearchableAnimalListViewModelFactory(
    private val db: AppDatabase
) : ViewModelProvider.Factory {
    @Throws(RuntimeException::class)
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            SearchableAnimalListViewModel::class.java.isAssignableFrom(modelClass) ->
                modelClass.getConstructor(AppDatabase::class.java)
                    .newInstance(db)
            else ->
                throw RuntimeException("Cannot create an instance of $modelClass")
        }
    }
}

class SearchableAnimalListViewModel(db: AppDatabase) : ViewModel() {
    private val _searchName: MutableStateFlow<String> = MutableStateFlow("")

    @ExperimentalCoroutinesApi
    val allDogsSearch: Flow<List<Animal>> = _searchName.flatMapLatest(db.animalDao()::search)

    fun searchName(name: String) {
        _searchName.value = name
    }
}

class SearchableAnimalList : Fragment() {
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: SearchableAnimalListViewModel by viewModels {
        SearchableAnimalListViewModelFactory(activityViewModel.db)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_searchable, container, false)
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<EditText>(R.id.search_box)
            .doOnTextChanged { text, _, _, _ -> viewModel.searchName(text.toString()) }

        val dogAdapter = DogAdapter(listOf())
        view.findViewById<RecyclerView>(R.id.result_list).adapter = dogAdapter

        lifecycleScope.launchWhenStarted {
            viewModel.allDogsSearch.collect(dogAdapter::updateDataSet)
        }
    }
}