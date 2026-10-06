package app.loyaltycards.ui.cards

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.loyaltycards.domain.LoyaltyCard
import app.loyaltycards.ui.components.CardStrip
import app.loyaltycards.ui.components.PillButton
import app.loyaltycards.ui.components.PillStyle
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun EditCardsScreen(viewModel: CardListViewModel, onDone: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var pendingRemoval by remember { mutableStateOf<LoyaltyCard?>(null) }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Edit cards", style = MaterialTheme.typography.displaySmall)
                    Text(
                        text = cardCountLabel(uiState.cards.size),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                PillButton(text = "Done", onClick = onDone)
            }
            ReorderableCardList(
                cards = uiState.cards,
                onReorder = viewModel::reorder,
                onRemoveClick = { pendingRemoval = it },
            )
        }
    }

    pendingRemoval?.let { card ->
        RemoveCardSheet(
            card = card,
            onConfirm = {
                viewModel.remove(card)
                pendingRemoval = null
            },
            onDismiss = { pendingRemoval = null },
        )
    }
}

@Composable
private fun ReorderableCardList(
    cards: List<LoyaltyCard>,
    onReorder: (orderedIds: List<Long>) -> Unit,
    onRemoveClick: (LoyaltyCard) -> Unit,
) {
    // Local copy so drags feel instant; reset whenever the stored list changes.
    var orderedCards by remember(cards) { mutableStateOf(cards) }
    val haptics = LocalHapticFeedback.current
    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        orderedCards = orderedCards.move(from.index, to.index)
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    fun moveAndSave(from: Int, to: Int) {
        orderedCards = orderedCards.move(from, to)
        onReorder(orderedCards.map { it.id })
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(orderedCards, key = { _, card -> card.id }) { index, card ->
            ReorderableItem(reorderableState, key = card.id) { isDragging ->
                val elevation by animateDpAsState(if (isDragging) 10.dp else 0.dp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.semantics {
                        customActions = buildList {
                            if (index > 0) add(CustomAccessibilityAction("Move up") { moveAndSave(index, index - 1); true })
                            if (index < orderedCards.lastIndex) {
                                add(CustomAccessibilityAction("Move down") { moveAndSave(index, index + 1); true })
                            }
                        }
                    },
                ) {
                    RemoveButton(cardName = card.name, onClick = { onRemoveClick(card) })
                    Spacer(Modifier.size(12.dp))
                    CardStrip(
                        card = card,
                        modifier = Modifier
                            .weight(1f)
                            .shadow(elevation, RoundedCornerShape(18.dp))
                            .longPressDraggableHandle(
                                onDragStarted = { haptics.performHapticFeedback(HapticFeedbackType.LongPress) },
                                onDragStopped = { onReorder(orderedCards.map { it.id }) },
                            ),
                    )
                }
            }
        }
        item {
            Text(
                text = "Hold a card and drag to reorder.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )
        }
    }
}

/** Red minus in a pale circle, as in the design. */
@Composable
private fun RemoveButton(cardName: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(44.dp).semantics { contentDescription = "Remove $cardName" },
        shape = CircleShape,
        color = MaterialTheme.colorScheme.errorContainer,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .size(width = 14.dp, height = 2.5.dp)
                    .background(MaterialTheme.colorScheme.error, RoundedCornerShape(2.dp)),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RemoveCardSheet(card: LoyaltyCard, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    fun hideThen(action: () -> Unit) {
        scope.launch { sheetState.hide() }.invokeOnCompletion { action() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = null,
    ) {
        Column(
            Modifier.padding(horizontal = 24.dp).padding(top = 24.dp, bottom = 16.dp).navigationBarsPadding(),
        ) {
            CardStrip(card = card, showCodeEnding = false, modifier = Modifier.height(64.dp))
            Spacer(Modifier.height(20.dp))
            Text("Remove ${card.name}?", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "The card and its code will be deleted from your wallet. You can add it again anytime.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(24.dp))
            PillButton(
                text = "Remove card",
                onClick = { hideThen(onConfirm) },
                style = PillStyle.Destructive,
                modifier = Modifier.fillMaxWidth().height(56.dp),
            )
            Spacer(Modifier.height(12.dp))
            PillButton(
                text = "Cancel",
                onClick = { hideThen(onDismiss) },
                style = PillStyle.Outlined,
                modifier = Modifier.fillMaxWidth().height(56.dp),
            )
        }
    }
}
