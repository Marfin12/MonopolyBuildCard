package com.example.monopolybuildcard.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monopolybuildcard.*
import com.example.monopolybuildcard.Util.checkDoubleTheRent
import com.example.monopolybuildcard.Util.substringLastTwoChar
import com.example.monopolybuildcard.asset.AssetAdapter
import com.example.monopolybuildcard.card.CardAdapter
import com.example.monopolybuildcard.card.CardType
import com.example.monopolybuildcard.databinding.ActivityMainBinding
import com.example.monopolybuildcard.money.MoneyAdapter
import com.example.monopolybuildcard.player.PlayerAdapter
import com.example.monopolybuildcard.player.PlayerData
import com.example.monopolybuildcard.player.PlayerStatus
import com.example.monopolybuildcard.room.RoomStatus
import com.example.monopolybuildcard.Constant.CardName
import com.example.monopolybuildcard.Util.mapIdToImage
import com.example.monopolybuildcard.main.MainUtil.adjustVisibilityPopupButtonGroup
import com.example.monopolybuildcard.main.MainUtil.hideEnemyPostedCard
import com.example.monopolybuildcard.main.MainUtil.isPaymentActionType
import com.example.monopolybuildcard.main.MainUtil.prepareActionUI
import com.example.monopolybuildcard.main.MainUtil.renderWildCard
import com.example.monopolybuildcard.main.MainUtil.showEnemyPostedCard

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
    private var isUpdateThePlayerData = true

    private var currentPlayerData = PlayerData()
    private var currentRoomData = RoomData()
    private var currentPlayerIndex = -1

    private var selectedOtherPlayerIndex = -1
    private var selectedOtherPlayerId = ""

    private val playerAdapter: PlayerAdapter = PlayerAdapter(mutableListOf())
    private val cardAdapter: CardAdapter = CardAdapter(mutableListOf())
    private val moneyAdapter: MoneyAdapter = MoneyAdapter(mutableListOf())
    private val assetAdapter: AssetAdapter = AssetAdapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

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
            if (roomData.status == RoomStatus.STARTED) playerAdapter.clearAllPlayer()

            roomData.users?.forEachIndexed { index, playerData ->
                if (playerData.id == Util.getAndroidId(this) && isUpdateThePlayerData) {
                    currentPlayerData = playerData
                    currentPlayerIndex = index

                    cardAdapter.replaceCard(currentPlayerData.cards)

                    initPlayer()
                } else {
                    playerAdapter.addPlayer(playerData)
                }
            }

            roomData.actions?.let { actionCardList ->
                cardAdapter.replaceActionPostedCard(actionCardList)

                actionCardList.filter { it.card?.id == CardName.ACTION_SAY_NO }.forEach {
                    if (it.card?.ownerId != currentPlayerData.id) {
                        playerAdapter.setPlayerAction(
                            it.card!!,
                            it.card?.ownerId ?: ""
                        )
                    }
                }

                actionCardList.filter { it.card?.id == CardName.ACTION_SAY_NO }

                if (actionCardList.size > 0) {
                    val lastActionCard = actionCardList.last()
                    val id = lastActionCard.card?.id ?: ""

                    if (Util.isIdEqualToAnyAction(id) && currentPlayerData.status == PlayerStatus.RUNNING)
                        resetComponent()

                    if (!actionCardList.any { it.card?.type == CardType.PROPERTY_TYPE }
                        && currentPlayerData.status == PlayerStatus.RUNNING
                    ) {
                        setRotateOnProperty()
                    } else assetAdapter.onRotateCard = null

                    if (lastActionCard.card?.type == CardType.ACTION_TYPE) {
                        playerAdapter.setPlayerAction(
                            lastActionCard.card!!,
                            lastActionCard.card?.ownerId ?: ""
                        )
                    }
                } else {
                    setRotateOnProperty()
                }
            }

            when (currentPlayerData.status) {
                PlayerStatus.WAITING -> {
                    updatePlayerPopupWhenShown()

                    showEnemyPostedCard(binding, currentRoomData)
                }
                PlayerStatus.RESPONDING -> {
                    prepareResponseForEnemy(checkDoubleTheRent(currentRoomData.actions!!))
                }
                else -> {
                    updatePlayerPopupWhenShown()

                    hideEnemyPostedCard(binding)
                }
            }

            if (currentPlayerData.status == PlayerStatus.COLLECTING) {
                val isEnemyNotPay =
                    currentRoomData.users?.any { it.status == PlayerStatus.RESPONDING }
                if (isEnemyNotPay == false) {
                    currentRoomData.users?.find { it.id == currentPlayerData.id }?.status =
                        PlayerStatus.RUNNING

                    mainViewModel.postACard(roomName, currentRoomData, mutableListOf())
                }
            }

            if (currentPlayerData.properties.size > 0 && currentPlayerData.money.size > 0) {
                binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    endToEnd =
                        (binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.parent as View).id
                    topToBottom = binding.layoutIncludePopupPlayer.rvListMoney.id
                }
                binding.layoutIncludePopupPlayer.layoutFloatButtonYesNoGroup.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    endToEnd =
                        (binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.parent as View).id
                    topToBottom = binding.layoutIncludePopupPlayer.rvListMoney.id
                }
            } else {
                binding.layoutIncludePopupPlayer.layoutFloatButtonYesNoGroup.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    endToEnd =
                        (binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.parent as View).id
                    topToBottom =
                        (binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.parent as View).id
                }
            }
        }
    }

    private fun initPlayer() {
        binding.tvPlayerName.text = currentPlayerData.name
        binding.btnStart.isVisible =
            currentPlayerData.shouldHost == true && currentRoomData.status == RoomStatus.WAITING
        binding.btnSkip.isVisible = currentPlayerData.status == PlayerStatus.RUNNING
        binding.rvListCard.isVisible = currentPlayerData.status == PlayerStatus.RUNNING
    }

    private fun initComponent() {
        binding.layoutIncludeSelectedAssetCard.root.visibility = View.GONE

        binding.ivPlayerInfo.setOnClickListener {
            showSelectedPlayerAssetMoneyInfo(currentPlayerData)
        }

        binding.layoutIncludePopupPlayer.ivPopupClose.setOnClickListener {
            onCloseButtonPopupClick()
        }

        binding.layoutIncludePopupPlayer.btnPopupNotKeepShow.setOnClickListener {
            preventPopupShowing()
        }

        binding.layoutIncludePopupPlayer.btnPopupKeepShow.setOnClickListener {
            keepPopupShowing()
        }

        binding.layoutIncludePopupPlayer.cbShowPopup.setOnCheckedChangeListener { compoundButton, _ ->
            isShowPopupAfterSelectingCard = (compoundButton as CompoundButton).isChecked
        }

        binding.btnSkip.setOnClickListener {
            if (cardAdapter.listCard().size < 8) onSkip()
            else {
                cardAdapter.discardCardMode()
                binding.btnSkip.isVisible = false
            }
        }

        binding.btnStart.setOnClickListener {
            playerAdapter.clearAllPlayer()
            mainViewModel.playTheGame(roomName, currentRoomData)
        }
    }

    private fun resetComponent() {
        onCloseButtonPopupClick()

        assetAdapter.onItemClick = null
        assetAdapter.onDealBreakerAction = null
        cardAdapter.onSayNo = null

        binding.rvListCard.visibility = View.VISIBLE
        binding.btnSkip.text = "Skip"
        binding.btnSkip.backgroundTintList =
            resources.getColorStateList(R.color.teal_700, this.theme)

        cardAdapter.onCardAdded = { cardData, position ->
            if (currentPlayerData.status == PlayerStatus.RUNNING) {
                addPlayerCard(cardData, position)

                if (isShowPopupAfterSelectingCard && cardData.type != CardType.ACTION_TYPE) {
                    showSelectedPlayerAssetMoneyInfo(currentPlayerData)
                }
            }
        }

        initComponent()
    }

    private fun initAdapter() {
        binding.rvListPlayer.adapter = playerAdapter
        binding.rvListPlayer.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        playerAdapter.onItemInfoClick = { playerData, _ ->
            showSelectedPlayerAssetMoneyInfo(playerData)
            selectedOtherPlayerId = playerData.id
            selectedOtherPlayerIndex =
                currentRoomData.users?.indexOfFirst { it.id == playerData.id } ?: -1
        }

        binding.rvListCard.adapter = cardAdapter
        binding.rvListCard.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )

        cardAdapter.onCardAdded = { cardData, position ->
            if (currentPlayerData.status == PlayerStatus.RUNNING) {
                addPlayerCard(cardData, position)

                if (isShowPopupAfterSelectingCard && cardData.type != CardType.ACTION_TYPE) {
                    showSelectedPlayerAssetMoneyInfo(currentPlayerData)
                }
            }
        }
        cardAdapter.onCardDiscard = { _, position ->
            if (currentPlayerData.status == PlayerStatus.RUNNING) {
                discardPlayerCard(position)
            }
        }

        with(binding.layoutIncludePopupPlayer) {
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

    private fun addPlayerCard(cardData: GlobalCardData, position: Int) {
        var currentPlayerStatus = PlayerStatus.RUNNING
        val actionCard = GlobalActionData(cardData, null, null)

        when (cardData.type) {
            CardType.PROPERTY_TYPE -> currentPlayerData.properties.add(cardData)
            CardType.MONEY_TYPE -> currentPlayerData.money.add(cardData)
            CardType.ACTION_TYPE -> {
                assetAdapter.onRotateCard = null
                currentPlayerStatus = PlayerStatus.COLLECTING

                when (cardData.id) {
                    CardName.ACTION_PASS_GO -> {
                        currentPlayerStatus = PlayerStatus.RUNNING
                        cardAdapter.draw2Card(currentRoomData, currentPlayerIndex)
                    }
                    CardName.ACTION_SLY_DEAL -> slyDealAction(actionCard, position)
                    CardName.ACTION_DEAL_BREAKER -> dealBreakerAction(actionCard, position)
                    CardName.ACTION_FORCED_DEAL -> forceDealAction(actionCard, position)
                    CardName.ACTION_DEBT_COLLECTOR -> debtCollectorAction(actionCard, position)
                    CardName.ACTION_HAPPY_BIRTHDAY -> happyBirthdayAction(actionCard, position)
                    CardName.DOUBLE_THE_RENT -> doubleTheRent(actionCard)
                    CardName.RENT_BLOK_ANY_TYPE -> rentActionAnyAsset(mutableListOf(actionCard))
                    CardName.RENT_BLOK_BC_TYPE,
                    CardName.RENT_BLOK_DE_TYPE,
                    CardName.RENT_BLOK_FG_TYPE -> rentActionSingleAsset(mutableListOf(actionCard))
                }
            }
        }

        currentRoomData.users?.get(currentPlayerIndex)?.status = currentPlayerStatus

        if (currentPlayerStatus != PlayerStatus.COLLECTING) postCardToServer(actionCard, position)
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
        assetAdapter.replaceListAssetCard(playerData.properties)
        moneyAdapter.replaceListMoneyCard(playerData.money)

        binding.layoutIncludePopupPlayer.root.visibility = View.VISIBLE
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

        playerAdapter.clearPlayerAction()
        mainViewModel.nextPlayerTurn(roomName, updatedRoomData)
    }

    private fun onCloseButtonPopupClick() {
        autoChooseForPopupShowing()

        selectedOtherPlayerIndex = -1
        selectedOtherPlayerId = ""
        binding.layoutIncludePopupPlayer.root.visibility = View.GONE
    }

    private fun autoChooseForPopupShowing() {
        if (isPopupNeverShownAfterSelectingCard) keepPopupShowing()
    }

    private fun keepPopupShowing() {
        isPopupNeverShownAfterSelectingCard = false
        isShowPopupAfterSelectingCard = true
        binding.layoutIncludePopupPlayer.cbShowPopup.isChecked = false
        binding.layoutIncludePopupPlayer.root.visibility = View.GONE
        selectedOtherPlayerIndex = -1

        adjustVisibilityPopupButtonGroup(binding, isPopupNeverShownAfterSelectingCard)
    }

    private fun preventPopupShowing() {
        isPopupNeverShownAfterSelectingCard = false
        isShowPopupAfterSelectingCard = false
        binding.layoutIncludePopupPlayer.cbShowPopup.isChecked = true
        binding.layoutIncludePopupPlayer.root.visibility = View.GONE
        selectedOtherPlayerIndex = -1

        adjustVisibilityPopupButtonGroup(binding, isPopupNeverShownAfterSelectingCard)
    }

    private fun setRotateOnProperty() {
        assetAdapter.onRotateCard = { properties, isOriginId ->
            if (selectedOtherPlayerIndex == -1) {
                assetAdapter.rotateCard(properties.id!!)

                currentRoomData.users?.get(currentPlayerIndex)?.properties =
                    assetAdapter.listAsset()

                val actionData = GlobalActionData(
                    GlobalCardData(
                        CardName.PROPERTY_FLIP,
                        if (isOriginId) 1 else 0,
                        CardType.PROPERTY_TYPE,
                        0
                    ),
                    properties
                )

                mainViewModel.postACard(
                    roomName,
                    currentRoomData,
                    mutableListOf(actionData)
                )
            }
        }
    }

    private fun postCardToServer(actionData: GlobalActionData, position: Int) {
        cardAdapter.removeCard(position)
        currentRoomData.users?.get(currentPlayerIndex)?.cards = cardAdapter.listCard()

        if (actionData.card?.type == CardType.ACTION_TYPE) onCloseButtonPopupClick()
        mainViewModel.postACard(roomName, currentRoomData, mutableListOf(actionData))
    }

    private fun postCardsToServer(actionData: MutableList<GlobalActionData>) {
        actionData.forEach {
            cardAdapter.removeCards(it.card!!)
        }
        currentRoomData.users?.get(currentPlayerIndex)?.cards = cardAdapter.listCard()

        onCloseButtonPopupClick()
        mainViewModel.postACard(roomName, currentRoomData, actionData)
    }

    private fun slyDealAction(actionCard: GlobalActionData, position: Int = -1) {
        if (position == -1) {
            val sendToId =
                currentRoomData.users?.indexOfFirst { it.id == actionCard.card?.ownerId } ?: -1

            currentRoomData.users?.get(currentPlayerIndex)?.properties?.remove(actionCard.cardTaken)
            currentRoomData.users?.get(sendToId)?.properties?.add(actionCard.cardTaken!!)

            mainViewModel.postACard(roomName, currentRoomData, mutableListOf())
        } else {
            prepareActionUI(binding, this, onSkip = { cancelAction() })

            assetAdapter.onItemClick = { assetData ->
                val isFullProperty = assetAdapter.listAsset().count { it.id == assetData.id } >= 3

                if (selectedOtherPlayerIndex != -1 && !isFullProperty) {
                    currentRoomData.users?.get(selectedOtherPlayerIndex)?.status =
                        PlayerStatus.RESPONDING
                    actionCard.cardTaken = assetData

                    postCardToServer(actionCard, position)
                }
            }
        }
    }

    private fun forceDealAction(actionCard: GlobalActionData, position: Int = -1) {
        if (position == -1) {
            val cardTaken = actionCard.cardTaken
            val cardGiven = actionCard.cardGiven
            val sendToId = currentRoomData.users?.indexOfFirst {
                it.id == actionCard.card?.ownerId
            } ?: -1

            currentRoomData.users?.get(currentPlayerIndex)?.properties?.remove(cardTaken)
            currentRoomData.users?.get(currentPlayerIndex)?.properties?.add(cardGiven!!)

            currentRoomData.users?.get(sendToId)?.properties?.remove(cardGiven)
            currentRoomData.users?.get(sendToId)?.properties?.add(cardTaken!!)

            mainViewModel.postACard(roomName, currentRoomData, mutableListOf())
        } else {
            prepareActionUI(binding, this, onSkip = { cancelAction() })
            binding.layoutEnemyCardPosted.visibility = View.VISIBLE

            assetAdapter.onItemClick = { assetData ->
                if (assetAdapter.listAsset().count { it.id == assetData.id } < 3) {
                    if (selectedOtherPlayerIndex == -1) {
                        actionCard.cardGiven = assetData
                        binding.layoutIncludeSelectedAssetCard.root.visibility = View.VISIBLE
                        binding.layoutIncludeSelectedAssetCard.ivCard.setImageResource(
                            mapIdToImage(assetData)
                        )

                        binding.layoutIncludePopupPlayer.root.visibility = View.GONE
                    } else {
                        actionCard.cardTaken = assetData
                        binding.layoutIncludeSelectedAssetCardForcedDeal.root.visibility =
                            View.VISIBLE
                        binding.layoutIncludeSelectedAssetCardForcedDeal.ivCard.setImageResource(
                            mapIdToImage(assetData)
                        )

                        currentRoomData.users?.get(selectedOtherPlayerIndex)?.status =
                            PlayerStatus.RESPONDING

                        binding.layoutIncludeSelectedAssetCardForcedDeal.root.visibility = View.GONE
                        binding.layoutIncludePopupPlayer.root.visibility = View.GONE
                        selectedOtherPlayerIndex = -1
                    }
                }

                if ((actionCard.cardTaken != null) && (actionCard.cardGiven != null)) {
                    binding.layoutEnemyCardPosted.visibility = View.GONE
                    binding.layoutIncludeSelectedAssetCard.root.visibility = View.GONE
                    binding.layoutIncludeSelectedAssetCardForcedDeal.root.visibility = View.GONE

                    postCardToServer(actionCard, position)
                }
            }
        }
    }

    private fun dealBreakerAction(actionCard: GlobalActionData, position: Int = -1) {
        if (position == -1) {
            val selectedIndex = currentRoomData.users?.indexOfFirst {
                it.id == actionCard.card?.ownerId
            } ?: 0

            actionCard.cardDealBreaker?.forEach { cardData ->
                currentRoomData.users?.get(selectedIndex)?.properties?.add(cardData)
                currentRoomData.users?.get(currentPlayerIndex)?.properties?.remove(cardData)
            }

            currentRoomData.users?.get(currentPlayerIndex)?.properties?.removeAll {
                it.id == actionCard.cardTaken?.id
            }

            mainViewModel.postACard(roomName, currentRoomData, mutableListOf())
        } else {
            prepareActionUI(binding, this, onSkip = { cancelAction() })

            assetAdapter.onItemClick = null
            assetAdapter.onDealBreakerAction = { fullAssetData ->
                if (selectedOtherPlayerIndex != -1) {
                    currentRoomData.users?.get(selectedOtherPlayerIndex)?.status =
                        PlayerStatus.RESPONDING
                    actionCard.cardDealBreaker = fullAssetData

                    postCardToServer(actionCard, position)
                }
            }
        }
    }

    private fun debtCollectorAction(actionCard: GlobalActionData, position: Int) {
        prepareActionUI(binding, this, onSkip = { cancelAction() })

        playerAdapter.onPlayerViewClick = { playerData, _ ->
            if (playerData.money.size > 0 || playerData.properties.size > 0) {
                currentRoomData.users?.find { it.id == playerData.id }?.status =
                    PlayerStatus.RESPONDING
                postCardToServer(actionCard, position)
            }
        }
    }

    private fun happyBirthdayAction(actionCard: GlobalActionData, position: Int) {
        prepareActionUI(binding, this, onSkip = { cancelAction() })

        currentRoomData.users?.filter { it.id != currentPlayerData.id }?.forEach {
            if (it.money.size > 0 || it.properties.size > 0)
                it.status = PlayerStatus.RESPONDING
        }

        if (currentRoomData.users?.any { it.status == PlayerStatus.RESPONDING } == true)
            postCardToServer(actionCard, position)
    }

    private fun doubleTheRent(actionCard: GlobalActionData) {
        prepareActionUI(binding, this, onSkip = { cancelAction() })
        cardAdapter.replaceCard(currentPlayerData.cards
            .filter { it.id?.contains("rent") == true && it.id != CardName.DOUBLE_THE_RENT }
            .toMutableList()
        )
        binding.rvListCard.visibility = View.VISIBLE

        cardAdapter.onCardAdded = { card, _ ->
            val selectedRentCard = GlobalActionData(
                card, null, null
            )
            val actionCardsRent = mutableListOf(actionCard, selectedRentCard)

            cardAdapter.replaceCard(currentPlayerData.cards)

            if (card.id?.contains("any") == true) rentActionAnyAsset(actionCardsRent)
            else rentActionSingleAsset(actionCardsRent)
        }
    }

    private fun rentActionAnyAsset(cardData: MutableList<GlobalActionData>) {
        prepareActionUI(binding, this, onSkip = { cancelAction() })

        if (currentPlayerData.properties.isNotEmpty() && selectedOtherPlayerIndex == -1) {
            showSelectedPlayerAssetMoneyInfo(currentPlayerData)
            assetAdapter.onItemClick = { assetData ->
                cardData.last().card?.value = assetAdapter.totalPriceOfAsset(assetData)

                currentRoomData.users?.filter { it.id != currentPlayerData.id }?.forEach {
                    it.status = PlayerStatus.RESPONDING
                }

                postCardsToServer(cardData)
            }
        }
    }

    private fun rentActionSingleAsset(cardData: MutableList<GlobalActionData>) {

        prepareActionUI(binding, this, onSkip = { cancelAction() })

        val whichAsset = substringLastTwoChar(cardData.last().card?.id ?: "")

        val playerAsset = currentPlayerData.properties
            .filter { whichAsset.contains(it.id?.get(0).toString()) }
            .map { it.id?.get(0).toString() }
            .distinct()
            .joinToString(separator = "") // either contain "ab", "a", "b", ""

        if (playerAsset.isNotEmpty()) {
            showSelectedPlayerAssetMoneyInfo(currentPlayerData)
            binding.layoutIncludePopupPlayer.ivPopupClose.setOnClickListener {}
            assetAdapter.onItemClick = { assetData ->
                val assetId = assetData.id?.get(0).toString()
                if (playerAsset.contains(assetId) && selectedOtherPlayerIndex == -1) {
                    cardData.last().card?.value = assetAdapter.totalPriceOfAsset(assetData)

                    currentRoomData.users?.filter { it.id != currentPlayerData.id }?.forEach {
                        it.status = PlayerStatus.RESPONDING
                    }

                    postCardsToServer(cardData)
                }
            }
        }
    }

    private fun payToPlayer(totalLoan: Int, toId: String) {
        val currentTotalMoney =
            currentRoomData.users?.get(currentPlayerIndex)?.money?.size ?: 0
        val currentTotalAsset =
            currentRoomData.users?.get(currentPlayerIndex)?.properties?.size ?: 0

        if (currentTotalMoney <= 0 && currentTotalAsset <= 0) finishPayment()
        else {
            var totalHasToBePaid = totalLoan

            showSelectedPlayerAssetMoneyInfo(currentPlayerData)
            binding.layoutIncludePopupPlayer.ivPopupClose.setOnClickListener {}

            moneyAdapter.onItemClick = { moneyData ->
                if (moneyAdapter.itemCount > 0) {
                    val toPlayerIndex = currentRoomData.users?.indexOfFirst { it.id == toId } ?: -1

                    totalHasToBePaid -= moneyData.price ?: 0

                    currentRoomData.users?.get(currentPlayerIndex)?.money?.remove(moneyData)
                    currentRoomData.users?.get(toPlayerIndex)?.money?.add(moneyData)
                    currentRoomData.users?.get(currentPlayerIndex)?.money?.let {
                        moneyAdapter.replaceListMoneyCard(it)
                    }

                    val totalMoney =
                        currentRoomData.users?.get(currentPlayerIndex)?.money?.size ?: 0
                    val totalAsset =
                        currentRoomData.users?.get(currentPlayerIndex)?.properties?.size ?: 0

                    if (totalHasToBePaid <= 0 || (totalMoney <= 0 && totalAsset <= 0)) finishPayment()
                }
            }

            assetAdapter.onItemClick = { assetData ->
                if (assetAdapter.itemCount > 0 && moneyAdapter.itemCount <= 0) {
                    val toPlayerIndex = currentRoomData.users?.indexOfFirst { it.id == toId } ?: -1

                    totalHasToBePaid -= assetData.price ?: 0

                    currentRoomData.users?.get(currentPlayerIndex)?.properties?.remove(assetData)
                    currentRoomData.users?.get(toPlayerIndex)?.properties?.add(assetData)

                    val totalAsset =
                        currentRoomData.users?.get(currentPlayerIndex)?.properties?.size ?: 0

                    currentRoomData.users?.get(currentPlayerIndex)?.properties?.let {
                        assetAdapter.replaceListAssetCard(it)
                    }

                    if (totalHasToBePaid <= 0 || totalAsset <= 0) finishPayment()
                }
            }
        }
    }

    private fun finishPayment() {
        currentRoomData.users?.get(currentPlayerIndex)?.status = PlayerStatus.WAITING
        isUpdateThePlayerData = true

        onCloseButtonPopupClick()
        binding.layoutIncludePopupPlayer.ivPopupClose.setOnClickListener {
            onCloseButtonPopupClick()
        }

        mainViewModel.postACard(roomName, currentRoomData, mutableListOf())
        resetComponent()
    }

    private fun prepareResponseForEnemy(actionData: GlobalActionData) {
        isUpdateThePlayerData = false
        val totalLoan = actionData.card?.value ?: -1

        binding.btnSkip.backgroundTintList =
            resources.getColorStateList(R.color.red_700, this.theme)
        binding.btnSkip.visibility = View.VISIBLE

        if (isPaymentActionType(actionData)) {
            binding.btnSkip.text = "Ok, I'll pay"
            showEnemyPostedCard(binding, currentRoomData)
        } else {
            binding.btnSkip.text = "Ok, Proceed"
            hideEnemyPostedCard(binding)
            binding.layoutEnemyCardPosted.isVisible = true
        }

        if (actionData.cardTaken != null) {
            val cardTakenId = actionData.cardTaken?.id ?: ""

            if (cardTakenId.length > 1) {
                renderWildCard(binding.layoutIncludeSelectedWildCard, actionData.cardTaken!!)
            } else {
                binding.layoutIncludeSelectedAssetCard.root.visibility = View.VISIBLE
                binding.layoutIncludeSelectedAssetCard
                    .ivCard
                    .setImageResource(mapIdToImage(actionData.cardTaken!!))
            }
        }

        if (actionData.cardGiven != null) {
            val cardTakenId = actionData.cardGiven?.id ?: ""

            if (cardTakenId.length > 1) {
                renderWildCard(
                    binding.layoutIncludeSelectedWildCardForcedDeal,
                    actionData.cardGiven!!
                )
            } else {
                binding.layoutIncludeSelectedAssetCardForcedDeal.root.visibility = View.VISIBLE
                binding.layoutIncludeSelectedAssetCardForcedDeal.ivCard
                    .setImageResource(mapIdToImage(actionData.cardGiven!!))
            }
        }

        if (actionData.cardDealBreaker != null && actionData.cardDealBreaker!!.size > 0) {
            val cardTaken = actionData.cardDealBreaker?.get(0)
            val cardTakenId = cardTaken?.id ?: ""

            if (cardTakenId.length > 1) {
                renderWildCard(binding.layoutIncludeSelectedWildCard, cardTaken!!)
            } else {
                binding.layoutIncludeSelectedAssetCard.root.visibility = View.VISIBLE
                binding.layoutIncludeSelectedAssetCard.ivCard
                    .setImageResource(mapIdToImage(cardTaken!!))
            }
        }

        binding.btnSkip.setOnClickListener {
            isUpdateThePlayerData = true
            currentRoomData.users?.get(currentPlayerIndex)?.status = PlayerStatus.WAITING

            when (actionData.card?.id) {
                CardName.ACTION_SLY_DEAL -> slyDealAction(actionData)
                CardName.ACTION_DEAL_BREAKER -> dealBreakerAction(actionData)
                CardName.ACTION_FORCED_DEAL -> forceDealAction(actionData)
                else -> {
                    isUpdateThePlayerData = false
                    currentRoomData.users?.get(currentPlayerIndex)?.status = PlayerStatus.RESPONDING

                    payToPlayer(totalLoan, actionData.card?.ownerId!!)
                }
            }

            if (isUpdateThePlayerData) resetComponent()
        }

        if (currentPlayerData.cards.any { it.id == CardName.ACTION_SAY_NO }) {
            val showSayNoCards = mutableListOf<GlobalCardData>()

            showSayNoCards.addAll(currentPlayerData.cards)
            cardAdapter.replaceCard(
                showSayNoCards.filter { it.id == CardName.ACTION_SAY_NO }.toMutableList()
            )

            binding.rvListCard.visibility = View.VISIBLE

            cardAdapter.onSayNo = { position ->
                cardAdapter.onSayNo = null
                val actionCard = GlobalActionData(
                    currentRoomData.users?.get(currentPlayerIndex)?.cards?.removeAt(position)
                )

                currentRoomData.actions?.add(actionCard)
                currentRoomData.users?.get(currentPlayerIndex)?.status = PlayerStatus.WAITING
                isUpdateThePlayerData = true

                mainViewModel.postACard(roomName, currentRoomData, mutableListOf())
                resetComponent()
            }
        }
    }

    private fun cancelAction() {
        cardAdapter.replaceCard(currentPlayerData.cards)
        currentRoomData.users?.get(currentPlayerIndex)?.status = PlayerStatus.RUNNING
        resetComponent()
    }

    private fun updatePlayerPopupWhenShown() {
        if (binding.layoutIncludePopupPlayer.root.visibility == View.VISIBLE) {
            if (selectedOtherPlayerIndex == -1) showSelectedPlayerAssetMoneyInfo(currentPlayerData)
            else {
                val playerData = currentRoomData.users?.get(selectedOtherPlayerIndex)

                showSelectedPlayerAssetMoneyInfo(playerData!!)
            }
        }
    }
}
