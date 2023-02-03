package com.example.monopolybuildcard.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monopolybuildcard.GlobalCardData
import com.example.monopolybuildcard.R
import com.example.monopolybuildcard.Util
import com.example.monopolybuildcard.asset.AssetAdapter
import com.example.monopolybuildcard.card.CardAdapter
import com.example.monopolybuildcard.card.CardType
import com.example.monopolybuildcard.databinding.ActivityMainBinding
import com.example.monopolybuildcard.money.MoneyAdapter
import com.example.monopolybuildcard.player.PlayerAdapter
import com.example.monopolybuildcard.player.PlayerData

class MainActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ROOM_NAME = "extra_room_name"

        fun launch(activity: Activity, roomName: String) {
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra(EXTRA_ROOM_NAME, roomName)

            activity.startActivity(intent)
        }
    }

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var roomName: String

    private var isShowPopupAfterSelectingCard = true
    private var isPopupNeverShownAfterSelectingCard = true

    private var currentPlayerData = PlayerData()
    private var currentRoomData = RoomData()
    private var currentPlayerIndex = -1
    private val playerAdapter: PlayerAdapter = PlayerAdapter(mutableListOf())

    private val cardAdapter: CardAdapter = CardAdapter(mutableListOf())
    private val moneyAdapter: MoneyAdapter = MoneyAdapter(mutableListOf())
    private val assetAdapter: AssetAdapter = AssetAdapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        roomName = intent.getStringExtra(EXTRA_ROOM_NAME) ?: ""

        initViewModel()
        initComponent()
        initAdapter()
    }

    private fun initViewModel() {
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        mainViewModel.updatePlayer(roomName)

        mainViewModel.roomDataData.observe(this) { roomData ->
            currentRoomData = roomData
            if (roomData.status != "waiting") playerAdapter.clearAllPlayer()
            roomData.actions?.let { cardAdapter.replaceActionPostedCard(it) }
            roomData.users?.forEachIndexed { index, playerData ->
                if (playerData.id == Util.getAndroidId(this@MainActivity)) {
                    currentPlayerData = playerData
                    currentPlayerIndex = index

                    cardAdapter.replaceCard(currentPlayerData.cards)

                    initPlayer()
                } else {
                    playerAdapter.addPlayer(playerData)
                }
            }

            if (currentPlayerData.shouldRunning == false) {
                showEnemyPostedCard()
            } else {
                hideEnemyPostedCard()
            }

            if (currentPlayerData.listAsset.size > 0 && currentPlayerData.listMoney.size > 0) {
                binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    endToEnd = (binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.parent as View).id
                    topToBottom = binding.layoutIncludePopupPlayer.rvListMoney.id
                }
                binding.layoutIncludePopupPlayer.layoutFloatButtonYesNoGroup.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    endToEnd = (binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.parent as View).id
                    topToBottom = binding.layoutIncludePopupPlayer.rvListMoney.id
                }
            } else {
                binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    endToEnd = (binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.parent as View).id
                    topToBottom = (binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.parent as View).id
                }
                binding.layoutIncludePopupPlayer.layoutFloatButtonYesNoGroup.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    endToEnd = (binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.parent as View).id
                    topToBottom = (binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.parent as View).id
                }
            }
        }
    }

    private fun initPlayer() {
        binding.tvPlayerName.text = currentPlayerData.name
        binding.tvPlayerAsset.text = "Asset: ${currentPlayerData.asset.toString()}"
        binding.tvPlayerMoney.text = "Money: ${currentPlayerData.money} M"
        binding.btnStart.isVisible =
            currentPlayerData.shouldHost == true && currentRoomData.status == "waiting"
        binding.btnSkip.isVisible = currentPlayerData.shouldRunning == true
        binding.rvListCard.isVisible = currentPlayerData.shouldRunning == true
    }

    private fun initComponent() {
        binding.layoutIncludeSelectedMoneyCard.ivAddCard.visibility = View.GONE
        binding.layoutIncludeSelectedActionCard.ivAddCard.visibility = View.GONE
        binding.layoutIncludeSelectedAssetCard.root.visibility = View.GONE

        binding.ivPlayerInfo.setOnClickListener {
            showSelectedPlayerAssetMoneyInfo(currentPlayerData)
        }

        binding.layoutIncludePopupPlayer.ivPopupClose.setOnClickListener {
            autoChooseForPopupShowing()
            binding.layoutIncludePopupPlayer.root.visibility = View.GONE
        }

        binding.layoutIncludePopupPlayer.btnPopupNotKeepShow.setOnClickListener {
            preventPopupShowing()
        }

        binding.layoutIncludePopupPlayer.btnPopupKeepShow.setOnClickListener {
            keepPopupShowing()
        }

        binding.layoutIncludePopupPlayer.cbShowPopup.setOnCheckedChangeListener { compoundButton, b ->
            isShowPopupAfterSelectingCard = (compoundButton as CompoundButton).isChecked
        }

        binding.btnSkip.setOnClickListener {
            autoChooseForPopupShowing()

            if (cardAdapter.listCard().size < 8) onSkip()
            else {
                cardAdapter.discardCardMode()
                binding.btnSkip.isVisible = false
            }
        }

        binding.btnStart.setOnClickListener {
            playerAdapter.clearAllPlayer()
            mainViewModel.shareTheCard(roomName, currentRoomData)
        }
    }

    private fun initAdapter() {
        binding.rvListPlayer.adapter = playerAdapter
        binding.rvListPlayer.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        playerAdapter.onItemInfoClick = { playerData ->
            showSelectedPlayerAssetMoneyInfo(playerData)
        }

        binding.rvListCard.adapter = cardAdapter
        binding.rvListCard.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )

        cardAdapter.onCardAdded = { cardData, position ->
            if (currentPlayerData.shouldRunning == true) {
                addPlayerCard(cardData, position)

                if (isShowPopupAfterSelectingCard) {
                    showSelectedPlayerAssetMoneyInfo(currentPlayerData)
                }
            }
        }
        cardAdapter.onCardDiscard = { _, position ->
            if (currentPlayerData.shouldRunning == true) {
                discardPlayerCard(position)
            }
        }

        with (binding.layoutIncludePopupPlayer) {
            rvListAsset.adapter = assetAdapter
            rvListAsset.layoutManager = LinearLayoutManager(
                this@MainActivity, LinearLayoutManager.HORIZONTAL, false
            )

            rvListMoney.adapter = moneyAdapter
            rvListMoney.layoutManager = LinearLayoutManager(
                this@MainActivity, LinearLayoutManager.HORIZONTAL, false
            )
        }
    }

    private fun adjustVisibilityPopupButtonGroup() {
        if (isPopupNeverShownAfterSelectingCard) {
            binding.layoutIncludePopupPlayer.layoutFloatShowThisPopupGroup.visibility =
                View.VISIBLE
            binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.visibility =
                View.GONE
        } else {
            binding.layoutIncludePopupPlayer.layoutFloatShowThisPopupGroup.visibility =
                View.GONE
            binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.visibility =
                View.VISIBLE
        }
    }

    private fun addPlayerCard(cardData: GlobalCardData, position: Int) {
        when (cardData.type) {
            CardType.ASSET_TYPE -> {
                currentPlayerData.listAsset.add(cardData)
            }
            CardType.MONEY_TYPE -> {
                currentPlayerData.listMoney.add(cardData)
            }
        }
        cardAdapter.removeCard(position)

        currentPlayerData.cards = cardAdapter.listCard()
        currentRoomData.users?.set(currentPlayerIndex, currentPlayerData)

        mainViewModel.postACard(roomName, currentRoomData, cardData)
    }

    private fun discardPlayerCard(position: Int) {
        currentRoomData.cards?.ready?.add(cardAdapter.getCard(position))
        cardAdapter.removeCard(position)

        currentPlayerData.cards = cardAdapter.listCard()
        currentRoomData.users?.set(currentPlayerIndex, currentPlayerData)

        if (cardAdapter.listCard().size <= 7) {
            onSkip()
        }
    }

    private fun showSelectedPlayerAssetMoneyInfo(playerData: PlayerData) {
        assetAdapter.replaceListAssetCard(playerData.listAsset)
        moneyAdapter.replaceListMoneyCard(playerData.listMoney)

        binding.layoutIncludePopupPlayer.root.visibility = View.VISIBLE
    }

    private fun showEnemyPostedCard() {
        binding.layoutEnemyCardPosted.isVisible = true
        binding.layoutIncludeSelectedMoneyCard.ivCard.setImageResource(R.drawable.spr_card_money_1)
        binding.layoutIncludeSelectedActionCard.ivCard.setImageResource(R.drawable.spr_card_action_deal_breaker)

        currentRoomData.actions?.forEach {
            when (it.type) {
                CardType.MONEY_TYPE -> binding.layoutIncludeSelectedMoneyCard.root.isVisible =
                    true
                CardType.ASSET_TYPE -> {
                    binding.layoutIncludeSelectedAssetCard.root.isVisible = true
                    binding.layoutIncludeSelectedAssetCard.tvCardAssetName.text = "Blok ${it.id}"
                }
                CardType.ACTION_TYPE -> binding.layoutIncludeSelectedActionCard.root.isVisible =
                    true
            }
        }
    }

    private fun hideEnemyPostedCard() {
        binding.layoutEnemyCardPosted.isVisible = false
        binding.layoutIncludeSelectedMoneyCard.root.isVisible = false
        binding.layoutIncludeSelectedAssetCard.root.isVisible = false
        binding.layoutIncludeSelectedActionCard.root.isVisible = false
    }

    private fun onSkip() {
        playerAdapter.clearAllPlayer()
        cardAdapter.undoDiscardMode()

        currentRoomData.actions?.clear()
        currentPlayerData.cards = cardAdapter.listCard()

        currentRoomData.users?.set(currentPlayerIndex, currentPlayerData)
        val updatedRoomData = Util.updateRoomData(
            currentRoomData,
            currentRoomData.cards!!
        )
        mainViewModel.nextPlayerTurn(roomName, updatedRoomData)
    }

    private fun autoChooseForPopupShowing() {
        if (isPopupNeverShownAfterSelectingCard) {
            keepPopupShowing()
        }
    }

    private fun keepPopupShowing() {
        isPopupNeverShownAfterSelectingCard = false
        isShowPopupAfterSelectingCard = false
        binding.layoutIncludePopupPlayer.cbShowPopup.isChecked = false
        binding.layoutIncludePopupPlayer.root.visibility = View.GONE

        adjustVisibilityPopupButtonGroup()
    }

    private fun preventPopupShowing() {
        isPopupNeverShownAfterSelectingCard = false
        isShowPopupAfterSelectingCard = true
        binding.layoutIncludePopupPlayer.cbShowPopup.isChecked = true
        binding.layoutIncludePopupPlayer.root.visibility = View.GONE

        adjustVisibilityPopupButtonGroup()
    }
}