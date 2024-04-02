package com.example.marvelcharacters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class MarvelAdapter(private val marvelList: List<MarvelCharacter>) : RecyclerView.Adapter<MarvelAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.marvel_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val character = marvelList[position]
        holder.bind(character)
    }

    override fun getItemCount() = marvelList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val marvelImage: ImageView = itemView.findViewById(R.id.marvel_image)
        private val marvelName: TextView = itemView.findViewById(R.id.marvel_name)
        private val marvelDescription: TextView = itemView.findViewById(R.id.marvel_description)

        fun bind(character: MarvelCharacter) {
            Glide.with(itemView)
                .load(character.imageUrl)
                .centerCrop()
                .into(marvelImage)
            marvelName.text = character.name
            marvelDescription.text = character.description

            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "Clicked on ${character.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}