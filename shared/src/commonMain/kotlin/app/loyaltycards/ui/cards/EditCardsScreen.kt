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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.loyaltycards.domain.LoyaltyCard
import app.loyaltycards.resources.Res
import app.loyaltycards.resources.cancel
import app.loyaltycards.resources.card_count
import app.loyaltycards.resources.done
import app.loyaltycards.resources.edit_cards_title
import app.loyaltycards.resources.move_down
import app.loyaltycards.resources.move_up
import app.loyaltycards.resources.remove_card
import app.loyaltycards.resources.remove_card_body
import app.loyaltycards.resources.remove_card_named
import app.loyaltycards.resources.remove_card_question
import app.loyaltycards.resources.reorder_hint
import app.loyaltycards.ui.components.CardStrip
import app.loyaltycards.ui.components.PillButton
import app.loyaltycards.ui.components.PillStyle
import app.loyaltycards.ui.preview.PreviewData
import app.loyaltycards.ui.preview.PreviewTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun EditCardsScreen(viewModel: CardListViewModel, onDone: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    EditCardsContent(
        cards = uiState.cards,
        onReorder = viewModel::reorder,
        onRemove = viewModel::remove,
        onDone = onDone,
    )
}

@Composable
internal fun EditCardsContent(
    cards: List<LoyaltyCard>,
    onReorder: (orderedIds: List<Long>) -> Unit,
    onRemove: (LoyaltyCard) -> Unit,
    onDone: () -> Unit,
) {
    var pendingRemoval by remember { mutableStateOf<LoyaltyCard?>(null) }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(Res.string.edit_cards_title), style = MaterialTheme.typography.displaySmall)
                    Text(
                        text = pluralStringResource(Res.plurals.card_count, cards.size, cards.size),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                PillButton(text = stringResource(Res.string.done), onClick = onDone)
            }
            ReorderableCardList(
                cards = cards,
                onReorder = onReorder,
                onRemoveClick = { pendingRemoval = it },
            )
        }
    }

    pendingRemoval?.let { card ->
        RemoveCardSheet(
            card = card,
            onConfirm = {
                onRemove(card)
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
    val moveUpLabel = stringResource(Res.string.move_up)
    val moveDownLabel = stringResource(Res.string.move_down)
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
                            if (index > 0) add(CustomAccessibilityAction(moveUpLabel) { moveAndSave(index, index - 1); true })
                            if (index < orderedCards.lastIndex) {
                                add(CustomAccessibilityAction(moveDownLabel) { moveAndSave(index, index + 1); true })
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
                text = stringResource(Res.string.reorder_hint),
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
    val description = stringResource(Res.string.remove_card_named, cardName)
    Surface(
        onClick = onClick,
        modifier = Modifier.size(44.dp).semantics { contentDescription = description },
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
internal fun RemoveCardSheet(card: LoyaltyCard, onConfirm: () -> Unit, onDismiss: () -> Unit) {
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
            Text(stringResource(Res.string.remove_card_question, card.name), style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.remove_card_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(24.dp))
            PillButton(
                text = stringResource(Res.string.remove_card),
                onClick = { hideThen(onConfirm) },
                style = PillStyle.Destructive,
                modifier = Modifier.fillMaxWidth().height(56.dp),
            )
            Spacer(Modifier.height(12.dp))
            PillButton(
                text = stringResource(Res.string.cancel),
                onClick = { hideThen(onDismiss) },
                style = PillStyle.Outlined,
                modifier = Modifier.fillMaxWidth().height(56.dp),
            )
        }
    }
}

@Preview(name = "Edit cards", widthDp = 390, heightDp = 844)
@Composable
private fun EditCardsPreview() = PreviewTheme {
    EditCardsContent(cards = PreviewData.cards, onReorder = {}, onRemove = {}, onDone = {})
}
