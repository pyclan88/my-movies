package com.practicum.mymovies.ui.movies

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.mymovies.R
import com.practicum.mymovies.databinding.FragmentMoviesBinding
import com.practicum.mymovies.domain.models.Movie
import com.practicum.mymovies.presentation.movies.MoviesState
import com.practicum.mymovies.presentation.movies.MoviesViewModel
import com.practicum.mymovies.ui.details.DetailsFragment
import com.practicum.mymovies.ui.root.RootActivity
import com.practicum.mymovies.utils.debounce
import com.practicum.mymovies.utils.invisible
import com.practicum.mymovies.utils.visible
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MoviesFragment : Fragment() {

    private val moviesViewModel by viewModel<MoviesViewModel>()

    private var adapter: MoviesAdapter? = null

    private lateinit var binding: FragmentMoviesBinding

    private lateinit var textWatcher: TextWatcher

    private lateinit var onMovieClickDebounce: (Movie) -> Unit

    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onMovieClickDebounce = debounce<Movie>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { movie ->
            findNavController().navigate(
                R.id.action_moviesFragment_to_detailsFragment,
                DetailsFragment.createArgs(movieId = movie.id, poster = movie.image)
            )
        }

        adapter = MoviesAdapter(
            object : MoviesAdapter.MovieClickListener {
                override fun onMovieClick(movie: Movie) {
                    (activity as RootActivity).animateBottomNavigationView()
                    onMovieClickDebounce(movie)
                }

                override fun onFavoriteToggleClick(movie: Movie) {
                    moviesViewModel.toggleFavorite(movie)
                }

            }
        )

        binding.moviesList.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.moviesList.adapter = adapter

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchHint.visibility =
                    if (binding.queryInput.hasFocus() && s?.isEmpty() == true) View.VISIBLE else View.GONE
                moviesViewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
            }

            override fun afterTextChanged(s: Editable?) {}

        }

        binding.queryInput.setOnFocusChangeListener { _, hasFocus ->
            binding.searchHint.visibility =
                if (hasFocus && binding.queryInput.text.isEmpty()) View.VISIBLE else View.GONE
        }

        textWatcher.let { binding.queryInput.addTextChangedListener(it) }

        moviesViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.queryInput.windowToken, 0)
        }

        moviesViewModel.observeShowToast().observe(viewLifecycleOwner) { toast ->
            showToast(toast)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        binding.moviesList.adapter = null
        textWatcher.let { binding.queryInput.removeTextChangedListener(it) }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
    }

    private fun render(state: MoviesState) {
        when (state) {
            is MoviesState.Loading -> showLoading()
            is MoviesState.Content -> showContent(state.movies)
            is MoviesState.Error -> showError(state.message)
            is MoviesState.Empty -> showEmpty(state.message)
        }
    }

    private fun showLoading() {
        binding.moviesList.invisible()
        binding.placeholderMessage.invisible()
        binding.progressBar.visible()
    }

    private fun showContent(movies: List<Movie>) {
        binding.moviesList.visible()
        binding.placeholderMessage.invisible()
        binding.progressBar.invisible()

        adapter?.movies?.clear()
        adapter?.movies?.addAll(movies)
        adapter?.notifyDataSetChanged()
    }

    private fun showError(errorMessage: String) {
        binding.moviesList.invisible()
        binding.placeholderMessage.visible()
        binding.progressBar.invisible()

        binding.placeholderMessage.text = errorMessage
    }

    private fun showEmpty(emptyMessage: String) {
        showError(emptyMessage)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }

}