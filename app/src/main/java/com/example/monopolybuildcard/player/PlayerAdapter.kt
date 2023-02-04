package com.example.monopolybuildcard.player

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolybuildcard.R


/**
 * Adapter for the task list. Has a reference to the [TodoListModel] to send actions back to it.
 */
@SuppressLint("NotifyDataSetChanged")
open class PlayerAdapter(
    private val dataset: MutableList<PlayerData>
): RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    var onItemInfoClick: ((PlayerData, Int) -> Unit)? = null
    var onPlayerViewClick: ((PlayerData, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.player_item, parent, false)

        return PlayerViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val item = dataset[position]

        holder.playerName.text = item.name
//        holder.playerMoney.text = "${item.money} M"
//        holder.playerAsset.text = item.properties.toString()
        holder.playerTurnOutline.isVisible = item.shouldRunning == true

        holder.playerInfo.setOnClickListener {
            onItemInfoClick?.invoke(item, position)
        }
        holder.playerView.setOnClickListener {
            onPlayerViewClick?.invoke(item, position)
        }
    }

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerName: TextView = itemView.findViewById(R.id.tv_enemy_name)
        val playerMoney: TextView = itemView.findViewById(R.id.tv_enemy_money)
        val playerAsset: TextView = itemView.findViewById(R.id.tv_enemy_asset)
        val playerInfo: ImageView = itemView.findViewById(R.id.iv_enemy_info)
        val playerTurnOutline: View = itemView.findViewById(R.id.view_enemy_square)
        val playerView: View = itemView.findViewById(R.id.view_enemy_parent)
    }

    override fun getItemCount() = dataset.size

    fun setPlayerTurn(whichPlayer: Int) {
        dataset.forEachIndexed { index, playerData ->
            playerData.shouldRunning = index == whichPlayer
        }
        notifyDataSetChanged()
    }

    fun listPlayer(): List<PlayerData> {
        return dataset
    }

    fun addPlayer(playerData: PlayerData) {
        dataset.add(playerData)
        notifyDataSetChanged()
    }

    fun clearAllPlayer() {
        dataset.clear()
        notifyDataSetChanged()
    }
}