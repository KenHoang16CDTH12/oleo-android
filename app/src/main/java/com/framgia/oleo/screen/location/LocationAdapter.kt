package com.framgia.oleo.screen.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.framgia.oleo.R
import com.framgia.oleo.data.source.model.Place
import com.framgia.oleo.databinding.AdapterLocationBinding
import com.framgia.oleo.utils.Constant
import com.framgia.oleo.utils.OnItemRecyclerViewClick
import com.framgia.oleo.utils.extension.isCheckMultiClick

class LocationAdapter : RecyclerView.Adapter<LocationAdapter.Companion.LocationHolder>() {
    private var places: MutableList<Place> = mutableListOf()
    private lateinit var listener: OnItemRecyclerViewClick<Place>

    fun updateData(place: Place) {
        places.add(place)
        notifyItemInserted(places.lastIndex - Constant.DEFAULT_ONE)
    }

    fun setListener(listener: OnItemRecyclerViewClick<Place>) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
        val binding: AdapterLocationBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.adapter_location, parent, false)
        return LocationHolder(binding, listener)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        holder.bindData(places[position])
    }

    companion object {
        class LocationHolder(
            private val binding: AdapterLocationBinding,
            private val listener: OnItemRecyclerViewClick<Place>
        ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

            private lateinit var place: Place

            init {
                binding.viewModel = LocationAdapterViewModel()
                binding.root.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                if (isCheckMultiClick()) listener.onItemClickListener(place)
            }

            fun bindData(place: Place) {
                this.place = place
                binding.viewModel!!.setLocation(place)
            }
        }
    }
}
