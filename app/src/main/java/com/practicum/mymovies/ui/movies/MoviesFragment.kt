package com.practicum.mymovies.ui.movies

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.mymovies.R
import com.practicum.mymovies.databinding.FragmentMoviesBinding
import com.practicum.mymovies.domain.models.Movie
import com.practicum.mymovies.presentation.movies.MoviesState
import com.practicum.mymovies.presentation.movies.MoviesViewModel
import com.practicum.mymovies.ui.details.DetailsFragment
import com.practicum.mymovies.util.invisible
import com.practicum.mymovies.util.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

class MoviesFragment : Fragment() {

    private val moviesViewModel by viewModel<MoviesViewModel>()

    private val adapter = MoviesAdapter(
        object : MoviesAdapter.MovieClickListener {
            override fun onMovieClick(movie: Movie) {
                if (clickDebounce()) {
                    parentFragmentManager.commit {
                        replace(
                            R.id.rootFragmentContainerView,
                            DetailsFragment.newInstance(
                                movieId = movie.id,
                                poster = movie.image,
                            ),
                            DetailsFragment.TAG
                        )
                        addToBackStack(DetailsFragment.TAG)
                }
                }
            }

            override fun onFavoriteToggleClick(movie: Movie) {
                moviesViewModel.toggleFavorite(movie)
            }

        }
    )

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var binding: FragmentMoviesBinding

    private lateinit var textWatcher: TextWatcher

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

        binding.moviesList.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.moviesList.adapter = adapter

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                moviesViewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
            }

            override fun afterTextChanged(s: Editable?) {}

        }

        textWatcher.let { binding.queryInput.addTextChangedListener(it) }

        moviesViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        moviesViewModel.observeShowToast().observe(viewLifecycleOwner) { toast ->
            showToast(toast)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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

        adapter.movies.clear()
        adapter.movies.addAll(movies)
        adapter.notifyDataSetChanged()
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

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}