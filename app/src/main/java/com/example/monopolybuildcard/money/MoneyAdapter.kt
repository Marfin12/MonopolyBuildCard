package com.example.monopolybuildcard.money

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolybuildcard.GlobalCardData
import com.example.monopolybuildcard.R
import com.example.monopolybuildcard.Util
import com.example.monopolybuildcard.card.CardType

/**
 * Adapter for the task list. Has a reference to the [TodoListModel] to send actions back to it.
 */
@SuppressLint("NotifyDataSetChanged")
open class MoneyAdapter(
    private var dataset: MutableList<GlobalCardData>
): RecyclerView.Adapter<MoneyAdapter.PlayerViewHolder>() {

    var onItemClick: ((GlobalCardData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.money_item, parent, false)

        return PlayerViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val item = dataset[position]

        holder.cardImage.setImageResource(Util.mapIdToImage(item))
        holder.cardImage.setOnClickListener { onItemClick?.invoke(item) }
    }

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardImage: ImageView = itemView.findViewById(R.id.iv_money_card)
    }

    override fun getItemCount() = dataset.size

    fun replaceListMoneyCard(listMoneyData: MutableList<GlobalCardData>) {
        dataset = listMoneyData
        notifyDataSetChanged()
    }
}