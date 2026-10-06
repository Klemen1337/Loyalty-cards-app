package app.loyaltycards.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.loyaltycards.data.CardRepository
import app.loyaltycards.domain.BarcodeFormat
import app.loyaltycards.domain.Store
import app.loyaltycards.domain.StoreCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddCardFormState(
    val storeQuery: String = "",
    /** Set when the user picked a suggestion; cleared as soon as they edit the store name. */
    val selectedStore: Store? = null,
    val cardCode: String = "",
    val nameOnCard: String = "",
    val showErrors: Boolean = false,
    val isSaving: Boolean = false,
) {
    val storeError: String? get() = if (showErrors && storeQuery.isBlank()) "Enter the store name" else null
    val codeError: String? get() = if (showErrors && normalizedCode.isEmpty()) "Enter the code under the barcode" else null
    val suggestions: List<Store>
        get() = if (selectedStore != null) emptyList() else StoreCatalog.search(storeQuery).take(5)

    /** Spaces are only for readability; scanners need the bare code. */
    val normalizedCode: String get() = cardCode.filterNot { it.isWhitespace() }
}

class AddCardViewModel(private val repository: CardRepository) : ViewModel() {

    private val _state = MutableStateFlow(AddCardFormState())
    val state: StateFlow<AddCardFormState> = _state.asStateFlow()

    fun onStoreQueryChange(value: String) = _state.update { it.copy(storeQuery = value, selectedStore = null) }

    fun onStoreSelected(store: Store) = _state.update { it.copy(storeQuery = store.name, selectedStore = store) }

    fun onCardCodeChange(value: String) = _state.update { it.copy(cardCode = value) }

    fun onNameOnCardChange(value: String) = _state.update { it.copy(nameOnCard = value) }

    fun save(onSaved: () -> Unit) {
        val form = _state.value
        if (form.isSaving) return
        if (form.storeQuery.isBlank() || form.normalizedCode.isEmpty()) {
            _state.update { it.copy(showErrors = true) }
            return
        }
        _state.update { it.copy(isSaving = true) }

        val storeName = form.storeQuery.trim()
        val store = form.selectedStore ?: StoreCatalog.findByName(storeName)
        viewModelScope.launch {
            repository.add(
                storeId = store?.id,
                name = store?.name ?: storeName,
                cardNumber = form.normalizedCode,
                barcodeFormat = BarcodeFormat.infer(form.normalizedCode),
                colorArgb = store?.colorArgb ?: StoreCatalog.fallbackColor(storeName),
                nameOnCard = form.nameOnCard.trim().ifEmpty { null },
            )
            onSaved()
        }
    }
}
