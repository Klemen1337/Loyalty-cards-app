package app.loyaltycards.ui.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.loyaltycards.domain.LoyaltyCard
import app.loyaltycards.resources.Res
import app.loyaltycards.resources.add_card
import app.loyaltycards.resources.card_count
import app.loyaltycards.resources.close_search
import app.loyaltycards.resources.edit
import app.loyaltycards.resources.no_match_body
import app.loyaltycards.resources.no_match_title
import app.loyaltycards.resources.search_cards
import app.loyaltycards.resources.wallet_title
import app.loyaltycards.ui.components.CircleIconButton
import app.loyaltycards.ui.components.HeaderHeight
import app.loyaltycards.ui.components.HeaderTitle
import app.loyaltycards.ui.components.PillButton
import app.loyaltycards.ui.components.PillStyle
import app.loyaltycards.ui.components.WalletCard
import app.loyaltycards.ui.components.WalletCardDefaults
import app.loyaltycards.ui.preview.PreviewData
import app.loyaltycards.ui.preview.PreviewTheme
import kotlin.math.abs
import kotlin.math.max
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WalletScreen(
    viewModel: CardListViewModel,
    onAddCard: () -> Unit,
    onEditCards: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WalletContent(uiState = uiState, onAddCard = onAddCard, onEditCards = onEditCards)
}

@Composable
internal fun WalletContent(
    uiState: CardListUiState,
    onAddCard: () -> Unit,
    onEditCards: () -> Unit,
    initialSelectedId: Long? = null,
) {
    var selectedId by rememberSaveable { mutableStateOf(initialSelectedId) }
    var enlargedId by rememberSaveable { mutableStateOf<Long?>(null) }
    var searching by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }

    val visibleCards = if (searching && query.isNotBlank()) {
        uiState.cards.filter { it.name.contains(query.trim(), ignoreCase = true) }
    } else {
        uiState.cards
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                // A tap anywhere that isn't a card or button puts the pulled-out card back.
                .pointerInput(Unit) { detectTapGestures { selectedId = null } },
        ) {
            WalletHeader(
                count = if (uiState.isLoading) null else uiState.cards.size,
                onEdit = onEditCards,
                onSearch = {
                    searching = !searching
                    if (!searching) query = ""
                },
                onAdd = onAddCard,
            )
            AnimatedVisibility(visible = searching) {
                SearchField(
                    query = query,
                    onQueryChange = { query = it },
                    onClose = {
                        searching = false
                        query = ""
                    },
                )
            }
            when {
                // Cards load in a few milliseconds; a spinner would only flash.
                uiState.isLoading -> Box(Modifier.fillMaxSize())

                uiState.cards.isEmpty() -> EmptyWallet(onAddCard = onAddCard)

                visibleCards.isEmpty() -> EmptyMessage(
                    title = stringResource(Res.string.no_match_title),
                    body = stringResource(Res.string.no_match_body),
                )

                else -> CardStack(
                    cards = visibleCards,
                    selectedId = selectedId,
                    // Tapping the pulled-out card again puts it back; only its barcode opens the big view.
                    onCardClick = { card -> selectedId = if (card.id == selectedId) null else card.id },
                    onBarcodeClick = { card -> enlargedId = card.id },
                )
            }
        }
    }

    uiState.cards.firstOrNull { it.id == enlargedId }?.let { card ->
        EnlargedCardDialog(card = card, onDismiss = { enlargedId = null })
    }
}

@Composable
private fun WalletHeader(
    /** Null while the cards are still loading. */
    count: Int?,
    onEdit: () -> Unit,
    onSearch: () -> Unit,
    onAdd: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        HeaderTitle(
            title = stringResource(Res.string.wallet_title),
            subtitle = count?.let { pluralStringResource(Res.plurals.card_count, it, it) } ?: "",
            modifier = Modifier.weight(1f),
        )
        PillButton(
            text = stringResource(Res.string.edit),
            onClick = onEdit,
            style = PillStyle.Outlined,
            enabled = (count ?: 0) > 0,
            modifier = Modifier.height(HeaderHeight),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        )
        CircleIconButton(icon = Icons.Default.Search, contentDescription = stringResource(Res.string.search_cards), onClick = onSearch)
        CircleIconButton(icon = Icons.Default.Add, contentDescription = stringResource(Res.string.add_card), onClick = onAdd, filled = true)
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit, onClose: () -> Unit) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
            .focusRequester(focusRequester),
        placeholder = { Text(stringResource(Res.string.search_cards)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = stringResource(Res.string.close_search)) }
        },
        singleLine = true,
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        ),
    )
}

/**
 * Cards overlap like a wallet: each shows only its top strip, except the selected card
 * (shown whole with its barcode), the card just above it and the last card.
 */
@Composable
private fun CardStack(
    cards: List<LoyaltyCard>,
    selectedId: Long?,
    onCardClick: (LoyaltyCard) -> Unit,
    onBarcodeClick: (LoyaltyCard) -> Unit,
) {
    val listState = rememberLazyListState()
    ScrollSelectedIntoView(listState, cards, selectedId)

    // Pulling past either end spreads the cards apart, and they spring back on release.
    val stretch = rememberStretchOverscrollState()
    val stretchDp = with(LocalDensity.current) { stretch.stretchPx.toDp() }
    val extraGap = abs(stretchDp.value).dp * 0.9f / max(1, cards.lastIndex)
    val extraTop = if (stretchDp > 0.dp) stretchDp * 0.1f else 0.dp

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().nestedScroll(stretch.connection),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp + extraTop, bottom = 32.dp),
        overscrollEffect = null,
    ) {
        itemsIndexed(cards, key = { _, card -> card.id }) { index, card ->
            val isSelected = card.id == selectedId
            val isAboveSelected = cards.getOrNull(index + 1)?.id == selectedId
            val isBelowSelected = cards.getOrNull(index - 1)?.id == selectedId
            val visibleHeight by animateDpAsState(
                when {
                    isSelected -> WalletCardDefaults.ExpandedHeight
                    isAboveSelected || index == cards.lastIndex -> WalletCardDefaults.FullHeight
                    else -> WalletCardDefaults.PeekHeight
                },
            )
            val gap by animateDpAsState(if (index > 0 && (isSelected || isBelowSelected)) 16.dp else 0.dp)

            // The box is only as tall as the visible part; the card draws past it and the
            // next card covers the rest.
            val topSpacing = if (index > 0) gap + extraGap else gap
            Box(Modifier.fillMaxWidth().padding(top = topSpacing).height(visibleHeight)) {
                WalletCard(
                    card = card,
                    expanded = isSelected,
                    onClick = { onCardClick(card) },
                    onBarcodeClick = { onBarcodeClick(card) },
                    modifier = Modifier.wrapContentHeight(Alignment.Top, unbounded = true),
                )
            }
        }
    }
}

/** After a card expands, scrolls so its barcode isn't cut off at the bottom. */
@Composable
private fun ScrollSelectedIntoView(listState: LazyListState, cards: List<LoyaltyCard>, selectedId: Long?) {
    LaunchedEffect(selectedId) {
        val index = cards.indexOfFirst { it.id == selectedId }
        if (index < 0) return@LaunchedEffect
        delay(350) // let the expand animation settle
        val layout = listState.layoutInfo
        val item = layout.visibleItemsInfo.firstOrNull { it.index == index }
        if (item == null || item.offset < 0 || item.offset + item.size > layout.viewportEndOffset) {
            listState.animateScrollToItem(index)
        }
    }
}

@Composable
private fun EmptyMessage(title: String, body: String) {
    Box(Modifier.fillMaxSize().padding(32.dp), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(name = "Wallet", widthDp = 390, heightDp = 844)
@Composable
private fun WalletPreview() = PreviewTheme {
    WalletContent(CardListUiState(PreviewData.cards, isLoading = false), onAddCard = {}, onEditCards = {})
}

@Preview(name = "Wallet, card pulled out", widthDp = 390, heightDp = 844)
@Composable
private fun WalletSelectedPreview() = PreviewTheme {
    WalletContent(
        uiState = CardListUiState(PreviewData.cards, isLoading = false),
        onAddCard = {},
        onEditCards = {},
        initialSelectedId = PreviewData.ikea.id,
    )
}

@Preview(name = "Wallet, dark", widthDp = 390, heightDp = 844)
@Composable
private fun WalletDarkPreview() = PreviewTheme(darkTheme = true) {
    WalletContent(CardListUiState(PreviewData.cards, isLoading = false), onAddCard = {}, onEditCards = {})
}

@Preview(name = "Wallet, empty", widthDp = 390, heightDp = 844)
@Composable
private fun WalletEmptyPreview() = PreviewTheme {
    WalletContent(CardListUiState(emptyList(), isLoading = false), onAddCard = {}, onEditCards = {})
}
