package com.example.monopolybuildcard.room

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolybuildcard.R
import com.example.monopolybuildcard.card.CardData
import com.example.monopolybuildcard.money.MoneyAdapter
import com.example.monopolybuildcard.money.MoneyData

/**
 * Adapter for the task list. Has a reference to the [TodoListModel] to send actions back to it.
 */
open class RoomAdapter(
    private var dataset: MutableList<String>
): RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.room_item, parent, false)

        return RoomViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val item = dataset[position]

        holder.roomText.text = item
        holder.roomText.setOnClickListener {
            onItemClick?.invoke(item)
        }
    }

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomText: TextView = itemView.findViewById(R.id.tv_room_name)
    }

    override fun getItemCount() = dataset.size

    @SuppressLint("NotifyDataSetChanged")
    fun addRoom(roomName: List<String>) {
        dataset.clear()
        dataset.addAll(roomName)
        notifyDataSetChanged()
    }
}