package app.loyaltycards.data

import app.loyaltycards.domain.BarcodeFormat
import app.loyaltycards.domain.LoyaltyCard
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    /** All cards in display order. Emits again whenever the stored cards change. */
    fun observeCards(): Flow<List<LoyaltyCard>>

    /** Adds a card at the end of the wallet and returns its id. */
    suspend fun add(
        storeId: String?,
        name: String,
        cardNumber: String,
        barcodeFormat: BarcodeFormat,
        colorArgb: Long,
        nameOnCard: String?,
    ): Long

    /** Persists a new display order. [orderedIds] lists card ids top to bottom. */
    suspend fun reorder(orderedIds: List<Long>)

    suspend fun delete(id: Long)
}
