package com.practicum.mymovies.ui.names

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.mymovies.R
import com.practicum.mymovies.databinding.ListItemPersonBinding
import com.practicum.mymovies.domain.models.Person

class PersonViewHolder(
    private val binging: ListItemPersonBinding
) : RecyclerView.ViewHolder(
    binging.root
) {

    fun bind(person: Person) {
        Glide.with(itemView)
            .load(person.photoUrl)
            .placeholder(R.drawable.ic_person)
            .circleCrop()
            .into(binging.photo)

        binging.name.text = person.title
        binging.description.text = person.description
    }

}