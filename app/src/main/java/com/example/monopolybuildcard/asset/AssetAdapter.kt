package com.example.monopolybuildcard.asset

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolybuildcard.GlobalCardData
import com.example.monopolybuildcard.R
import org.w3c.dom.Text

/**
 * Adapter for the task list. Has a reference to the [TodoListModel] to send actions back to it.
 */
@SuppressLint("NotifyDataSetChanged")
open class AssetAdapter(
    private var dataset: MutableList<GlobalCardData>
): RecyclerView.Adapter<AssetAdapter.AssetViewHolder>() {

    var onItemClick: ((GlobalCardData) -> Unit)? = null
    private var assetHasBeenPosted = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.asset_item, parent, false)

        return AssetViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val item = dataset[position]
        holder.setImageSource(item)
        holder.setOnClickAssetImage(item, onItemClick)
    }

    inner class AssetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val assetImage1: View = itemView.findViewById(R.id.inc_asset1)
        private val assetImage2: View = itemView.findViewById(R.id.inc_asset2)
        private val assetImage3: View = itemView.findViewById(R.id.inc_asset3)

        fun setOnClickAssetImage(
            cardItem: GlobalCardData,
            onItemClick: ((GlobalCardData) -> Unit)?
        ) {
            assetImage1.setOnClickListener { onItemClick?.invoke(cardItem) }
            assetImage2.setOnClickListener { onItemClick?.invoke(cardItem) }
            assetImage3.setOnClickListener { onItemClick?.invoke(cardItem) }
        }

        fun setImageSource(cardItem: GlobalCardData) {
            assetImage1.findViewById<ImageView>(R.id.iv_card).setImageResource(R.drawable.spr_card_asset_brown_apartement)
            assetImage2.findViewById<ImageView>(R.id.iv_card).setImageResource(R.drawable.spr_card_asset_brown_apartement)
            assetImage3.findViewById<ImageView>(R.id.iv_card).setImageResource(R.drawable.spr_card_asset_brown_apartement)

            assetImage1.findViewById<TextView>(R.id.tv_card_asset_name).visibility = View.VISIBLE
            assetImage2.findViewById<TextView>(R.id.tv_card_asset_name).visibility = View.VISIBLE
            assetImage3.findViewById<TextView>(R.id.tv_card_asset_name).visibility = View.VISIBLE

            assetImage1.findViewById<ConstraintLayout>(R.id.layout_card_asset_price).visibility = View.VISIBLE
            assetImage2.findViewById<ConstraintLayout>(R.id.layout_card_asset_price).visibility = View.VISIBLE
            assetImage3.findViewById<ConstraintLayout>(R.id.layout_card_asset_price).visibility = View.VISIBLE

            assetImage1.visibility = View.GONE
            assetImage2.visibility = View.GONE
            assetImage3.visibility = View.GONE

            val charAsset = cardItem.id?.get(0) ?: ' '
            if (!assetHasBeenPosted.contains(charAsset) && charAsset != ' ') {
                assetHasBeenPosted += charAsset

                assetImage1.findViewById<TextView>(R.id.tv_card_asset_name).text =
                    "Blok ${cardItem.id?.uppercase()}"
                assetImage2.findViewById<TextView>(R.id.tv_card_asset_name).text =
                    "Blok ${cardItem.id?.uppercase()}"
                assetImage3.findViewById<TextView>(R.id.tv_card_asset_name).text =
                    "Blok ${cardItem.id?.uppercase()}"

                when (dataset.count { it.id?.get(0) == charAsset }) {
                    1 -> {
                        assetImage1.visibility = View.VISIBLE
                        assetImage2.visibility = View.GONE
                        assetImage3.visibility = View.GONE
                    }
                    2 -> {
                        assetImage1.visibility = View.VISIBLE
                        assetImage2.visibility = View.VISIBLE
                        assetImage3.visibility = View.GONE
                    }
                    3 -> {
                        assetImage1.visibility = View.VISIBLE
                        assetImage2.visibility = View.VISIBLE
                        assetImage3.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun getItemCount() = dataset.size

    fun replaceListAssetCard(listAssetData: MutableList<GlobalCardData>) {
        dataset = listAssetData
        assetHasBeenPosted = ""
        notifyDataSetChanged()
    }

    fun listAsset(): MutableList<GlobalCardData> {
        return dataset
    }
}