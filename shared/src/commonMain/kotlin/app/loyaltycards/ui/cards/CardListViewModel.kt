package app.loyaltycards.ui.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.loyaltycards.data.CardRepository
import app.loyaltycards.domain.LoyaltyCard
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CardListUiState(
    val cards: List<LoyaltyCard> = emptyList(),
    val isLoading: Boolean = true,
)

/** Shared by the wallet and the edit screen. */
class CardListViewModel(private val repository: CardRepository) : ViewModel() {

    val uiState: StateFlow<CardListUiState> = repository.observeCards()
        .map { CardListUiState(cards = it, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CardListUiState())

    fun reorder(orderedIds: List<Long>) {
        viewModelScope.launch { repository.reorder(orderedIds) }
    }

    fun remove(card: LoyaltyCard) {
        viewModelScope.launch { repository.delete(card.id) }
    }
}

internal fun cardCountLabel(count: Int): String = if (count == 1) "1 card" else "$count cards"
