package com.practicum.mymovies.ui.names

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.mymovies.databinding.FragmentNamesBinding
import com.practicum.mymovies.domain.models.Person
import com.practicum.mymovies.presentation.names.NamesState
import com.practicum.mymovies.presentation.names.NamesViewModel
import com.practicum.mymovies.utils.invisible
import com.practicum.mymovies.utils.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

class NamesFragment : Fragment() {

    private val viewModel by viewModel<NamesViewModel>()

    private val adapter = PersonsAdapter()

    private var _binding: FragmentNamesBinding? = null
    private val binding
        get() = _binding!!


    private lateinit var textWatcher: TextWatcher

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.personsList.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.personsList.adapter = adapter

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        textWatcher.let { binding.queryInput.addTextChangedListener(it) }

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.queryInput.windowToken, 0)
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher.let { binding.queryInput.removeTextChangedListener(it) }
    }

    private fun showToast(additionalMessage: String?) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
    }

    private fun render(state: NamesState) {
        when (state) {
            is NamesState.Loading -> showLoading()
            is NamesState.Content -> showContent(state.persons)
            is NamesState.Error -> showError(state.message)
            is NamesState.Empty -> showEmpty(state.message)
        }
    }

    private fun showLoading() {
        binding.personsList.invisible()
        binding.placeholderMessage.invisible()
        binding.progressBar.visible()
    }

    private fun showContent(persons: List<Person>) {
        binding.personsList.visible()
        binding.placeholderMessage.invisible()
        binding.progressBar.invisible()

        adapter.persons.clear()
        adapter.persons.addAll(persons)
        adapter.notifyDataSetChanged()
    }

    private fun showError(errorMessage: String) {
        binding.personsList.invisible()
        binding.placeholderMessage.visible()
        binding.progressBar.invisible()

        binding.placeholderMessage.text = errorMessage
    }

    private fun showEmpty(emptyMessage: String) {
        showError(emptyMessage)
    }

}