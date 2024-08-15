package com.practicum.mymovies.ui.cast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.practicum.mymovies.databinding.FragmentMoviesCastBinding
import com.practicum.mymovies.presentation.cast.MovieCastViewModel
import com.practicum.mymovies.presentation.cast.MoviesCastState
import com.practicum.mymovies.util.invisible
import com.practicum.mymovies.util.visible
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MoviesCastFragment : Fragment() {

    private val moviesCastViewModel: MovieCastViewModel by viewModel {
        parametersOf(requireArguments().getString(ARGS_MOVIE_ID))
    }

    private val adapter = ListDelegationAdapter(
        movieCastHeaderDelegate(),
        movieCastPersonDelegate()
    )

    private lateinit var biding: FragmentMoviesCastBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        biding = FragmentMoviesCastBinding.inflate(inflater, container, false)
        return biding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        biding.moviesCastRecyclerView.adapter = adapter
        biding.moviesCastRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        moviesCastViewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is MoviesCastState.Content -> showContent(it)
                is MoviesCastState.Error -> showError(it)
                is MoviesCastState.Loading -> showLoading()
            }
        }
    }

    private fun showLoading() {
        biding.contentContainer.invisible()
        biding.errorMessageTextView.invisible()
        biding.progressBar.visible()
    }

    private fun showError(state: MoviesCastState.Error) {
        biding.contentContainer.invisible()
        biding.progressBar.invisible()

        biding.errorMessageTextView.visible()
        biding.errorMessageTextView.text = state.message
    }

    private fun showContent(state: MoviesCastState.Content) {
        biding.progressBar.invisible()
        biding.errorMessageTextView.invisible()

        biding.contentContainer.visible()
        biding.movieTitle.text = state.fullTitle
        adapter.items = state.items
        adapter.notifyDataSetChanged()
    }

    companion object {
        private const val ARGS_MOVIE_ID = "movie_id"
        const val TAG = "MoviesCastFragment"

        fun newInstance(movieId: String): Fragment {
            return MoviesCastFragment().apply {
                arguments = bundleOf(
                    ARGS_MOVIE_ID to movieId
                )
            }
        }
    }

}