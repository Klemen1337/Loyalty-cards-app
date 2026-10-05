package app.loyaltycards.ui.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.loyaltycards.domain.LoyaltyCard
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun CardListScreen(viewModel: CardListViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CardListContent(
        uiState = uiState,
        onReorder = viewModel::reorder,
        onRemove = viewModel::remove,
        onUndoRemove = viewModel::undoRemove,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CardListContent(
    uiState: CardListUiState,
    onReorder: (orderedIds: List<Long>) -> Unit,
    onRemove: (LoyaltyCard) -> Unit,
    onUndoRemove: (LoyaltyCard) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val removeWithUndo: (LoyaltyCard) -> Unit = { card ->
        onRemove(card)
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = "${card.name} removed",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) onUndoRemove(card)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Loyalty cards") },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }

            uiState.cards.isEmpty() -> EmptyState(Modifier.fillMaxSize().padding(padding))

            else -> ReorderableCardList(
                cards = uiState.cards,
                contentPadding = padding,
                onReorder = onReorder,
                onRemove = removeWithUndo,
            )
        }
    }
}

@Composable
private fun ReorderableCardList(
    cards: List<LoyaltyCard>,
    contentPadding: PaddingValues,
    onReorder: (orderedIds: List<Long>) -> Unit,
    onRemove: (LoyaltyCard) -> Unit,
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
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(orderedCards, key = { _, card -> card.id }) { index, card ->
            ReorderableItem(reorderableState, key = card.id) { isDragging ->
                CardRow(
                    card = card,
                    isDragging = isDragging,
                    canMoveUp = index > 0,
                    canMoveDown = index < orderedCards.lastIndex,
                    onMoveUp = { moveAndSave(index, index - 1) },
                    onMoveDown = { moveAndSave(index, index + 1) },
                    onRemove = { onRemove(card) },
                    dragHandleModifier = Modifier.draggableHandle(
                        onDragStarted = { haptics.performHapticFeedback(HapticFeedbackType.LongPress) },
                        onDragStopped = { onReorder(orderedCards.map { it.id }) },
                    ),
                )
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier.padding(32.dp), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No cards yet",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Cards you add will show up here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
