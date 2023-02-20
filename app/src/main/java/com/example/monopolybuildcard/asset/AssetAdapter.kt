package com.example.monopolybuildcard.asset

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolybuildcard.GlobalCardData
import com.example.monopolybuildcard.R
import com.example.monopolybuildcard.Util
import com.example.monopolybuildcard.Util.sliceUntil
import com.example.monopolybuildcard.asset.AssetUtil.resetAllAssetPosted

/**
 * Adapter for the task list. Has a reference to the asset to send actions back to it.
 */
@SuppressLint("NotifyDataSetChanged")
open class AssetAdapter(
    private var dataset: MutableList<GlobalCardData>
) : RecyclerView.Adapter<AssetAdapter.AssetViewHolder>() {

    var onItemClick: ((GlobalCardData) -> Unit)? = null
    var onRotateCard: ((GlobalCardData, Boolean) -> Unit)? = null

    private var assetHasBeenPosted = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.asset_item, parent, false)

        return AssetViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val item = dataset[position]

        holder.setImageSource(item)
    }

    inner class AssetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val assetImage1: View = itemView.findViewById(R.id.inc_asset1)
        private val assetImage2: View = itemView.findViewById(R.id.inc_asset2)
        private val assetImage3: View = itemView.findViewById(R.id.inc_asset3)

        private val wildCardView1 = assetImage1.findViewById<View>(R.id.layout_parent_wild_card)
        private val wildCardView2 = assetImage2.findViewById<View>(R.id.layout_parent_wild_card)
        private val wildCardView3 = assetImage3.findViewById<View>(R.id.layout_parent_wild_card)

        fun setImageSource(cardItem: GlobalCardData) {
            val charAsset = cardItem.id ?: ""
            val topId = charAsset[0].toString()

            preparePropertyUI()

            if (assetHasBeenPosted.contains(topId) && topId != "") {
                val currentAssetPosted = AssetUtil.assetPosted[topId] ?: -5

                if (currentAssetPosted > 1) {
                    AssetUtil.assetPosted[topId] = 0
                    postAsset(charAsset, cardItem)
                }
                else AssetUtil.assetPosted[topId] = currentAssetPosted + 1
            } else if (charAsset != "") {
                assetHasBeenPosted += topId
                postAsset(charAsset, cardItem)
            }
        }

        private fun postAsset(charAsset: String, cardItem: GlobalCardData) {
            when (dataset.count { it.id?.get(0) == charAsset[0] }) {
                0 -> println("do nothing")
                1 -> renderCard(charAsset, assetImage1, wildCardView1, cardItem)
                else -> {
                    val indexData = dataset.indexOf(cardItem)
                    val currentAssetList = dataset.slice(indexData until dataset.size)

                    currentAssetList.filter { it.id?.get(0) == charAsset[0] }
                        .toMutableList()
                        .sliceUntil(3)
                        .forEachIndexed { index, cardData ->
                            val cardId = cardData.id ?: ""

                            when (index) {
                                0 -> renderCard(cardId, assetImage1, wildCardView1, cardData)
                                1 -> renderCard(cardId, assetImage2, wildCardView2, cardData)
                                2 -> renderCard(cardId, assetImage3, wildCardView3, cardData)
                            }
                        }
                }
            }
        }

        private fun preparePropertyUI() {
            assetImage1.visibility = View.GONE
            assetImage2.visibility = View.GONE
            assetImage3.visibility = View.GONE

            wildCardView1.visibility = View.GONE
            wildCardView2.visibility = View.GONE
            wildCardView3.visibility = View.GONE
        }

        private fun setClickableForBottomCard() {
            assetImage1.setOnClickListener(null)
            assetImage2.setOnClickListener(null)
            assetImage3.setOnClickListener(null)

            wildCardView1.setOnClickListener(null)
            wildCardView2.setOnClickListener(null)
            wildCardView3.setOnClickListener(null)
        }

        private fun renderCard(
            charAsset: String,
            selectedAsset: View,
            selectedWildAsset: View,
            cardItem: GlobalCardData
        ) {
            selectedAsset.visibility = View.VISIBLE
            setClickableForBottomCard()

            if (charAsset.length > 1) setupWildCard(selectedWildAsset, cardItem, charAsset)
            else setupAssetCard(selectedAsset, cardItem)
        }

        private fun setupAssetCard(cardView: View, cardData: GlobalCardData) {
            val assetImage = Util.mapIdToImage(cardData)

            cardView.findViewById<TextView>(R.id.tv_card_asset_name).text =
                "Blok ${cardData.id?.get(0)?.toUpperCase()}"
            cardView.findViewById<TextView>(R.id.tv_card_asset_price_nominal).text =
                cardData.price.toString()

            cardView.findViewById<ImageView>(R.id.iv_card).setImageResource(assetImage)
            cardView.setOnClickListener { onItemClick?.invoke(cardData) }
        }

        private fun setupWildCard(wildCardView: View, cardData: GlobalCardData, charId: String) {
            val cardPrice = AssetUtil.assetPrice[charId[0].toString()]

            wildCardView.visibility = View.VISIBLE

            val topImage = GlobalCardData(
                charId[0].toString(), cardData.value, cardData.type, cardData.price
            )
            val downImage = GlobalCardData(
                charId[1].toString(), cardData.value, cardData.type, cardData.price
            )

            wildCardView.findViewById<ImageView>(R.id.iv_wild_card_top)
                .setImageResource(Util.mapIdToImage(topImage))
            wildCardView.findViewById<ImageView>(R.id.iv_wild_card_bottom)
                .setImageResource(Util.mapIdToImage(downImage))

            wildCardView.findViewById<TextView>(R.id.tv_wild_card_asset_name).text =
                "Blok ${charId[1].toUpperCase()}"
            wildCardView.findViewById<TextView>(R.id.tv_wild_card_asset_price).text =
                cardPrice.toString()

            wildCardView.setOnClickListener { _ ->
                if (onRotateCard != null) {
                    onRotateCard?.invoke(
                        dataset.findLast { it.id == charId }!!,
                        AssetUtil.assetWildCard.contains(charId)
                    )
                } else onItemClick?.invoke(cardData)
            }
        }
    }

    override fun getItemCount() = dataset.size

    fun listAsset(): MutableList<GlobalCardData> = dataset

    fun replaceListAssetCard(listAssetData: MutableList<GlobalCardData>) {
        dataset = listAssetData
        assetHasBeenPosted = ""

        resetAllAssetPosted()
        notifyDataSetChanged()
    }


    fun totalPriceOfAsset(cardItem: GlobalCardData): Int {
        return dataset.filter { (it.id?.get(0) ?: "") == (cardItem.id?.get(0) ?: "") }.sumOf {
            it.price ?: 0
        }
    }

    fun rotateCard(assetId: String) {
        dataset.find { it.id == assetId }.let {
            it?.id = it?.id?.reversed()
            it?.price = AssetUtil.assetPrice[it?.id?.get(0).toString()]
        }
        assetHasBeenPosted = ""

        resetAllAssetPosted()
        notifyDataSetChanged()
    }
}