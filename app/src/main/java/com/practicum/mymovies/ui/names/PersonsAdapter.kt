package com.practicum.mymovies.ui.names

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.mymovies.databinding.ListItemPersonBinding
import com.practicum.mymovies.domain.models.Person

class PersonsAdapter : RecyclerView.Adapter<PersonViewHolder>() {

    var persons = ArrayList<Person>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PersonViewHolder(
            ListItemPersonBinding.inflate(layoutInspector, parent, false)
        )
    }

    override fun getItemCount(): Int = persons.size

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(persons.get(position))
    }
}