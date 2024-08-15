package com.practicum.mymovies.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.mymovies.R
import com.practicum.mymovies.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val poster = requireArguments().getString(ARGS_POSTER_URL) ?: ""
        val movieId = requireArguments().getString(ARGS_MOVIE_ID) ?: ""

        binding.viewPager.adapter =
            DetailsViewPagerAdapter(childFragmentManager, lifecycle, poster, movieId)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.poster)
                1 -> tab.text = getString(R.string.details)
            }
        }
        tabMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
    }

    companion object {
        private const val ARGS_MOVIE_ID = "MOVIE_ID_KEY"
        private const val ARGS_POSTER_URL = "POSTER_KEY"

        const val TAG = "DetailsFragment"

        fun newInstance(movieId: String, poster: String): Fragment {
            return DetailsFragment().apply {
                arguments = bundleOf(
                    ARGS_MOVIE_ID to movieId,
                    ARGS_POSTER_URL to poster
                )
            }
        }
    }

}