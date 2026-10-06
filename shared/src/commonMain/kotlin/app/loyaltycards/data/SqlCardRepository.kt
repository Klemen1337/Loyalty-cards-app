package app.loyaltycards.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.loyaltycards.db.LoyaltyCardsDatabase
import app.loyaltycards.db.Loyalty_card
import app.loyaltycards.domain.BarcodeFormat
import app.loyaltycards.domain.LoyaltyCard
import app.loyaltycards.domain.StoreCatalog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SqlCardRepository(
    database: LoyaltyCardsDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CardRepository {

    private val queries = database.loyaltyCardQueries

    override fun observeCards(): Flow<List<LoyaltyCard>> =
        queries.selectAll()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { rows -> rows.map { it.toDomain() } }

    @OptIn(ExperimentalTime::class)
    override suspend fun add(
        storeId: String?,
        name: String,
        cardNumber: String,
        barcodeFormat: BarcodeFormat,
        colorArgb: Long,
        nameOnCard: String?,
    ): Long = withContext(ioDispatcher) {
        queries.transactionWithResult {
            queries.insert(
                store_id = storeId,
                name = name,
                card_number = cardNumber,
                barcode_format = barcodeFormat.name,
                color = colorArgb,
                name_on_card = nameOnCard,
                position = queries.nextPosition().executeAsOne(),
                created_at = Clock.System.now().toEpochMilliseconds(),
            )
            queries.lastInsertedId().executeAsOne()
        }
    }

    override suspend fun reorder(orderedIds: List<Long>) {
        withContext(ioDispatcher) {
            queries.transaction {
                orderedIds.forEachIndexed { index, id ->
                    queries.updatePosition(position = index.toLong(), id = id)
                }
            }
        }
    }

    override suspend fun delete(id: Long) {
        withContext(ioDispatcher) { queries.deleteById(id) }
    }
}

private fun Loyalty_card.toDomain() = LoyaltyCard(
    id = id,
    storeId = store_id,
    name = name,
    cardNumber = card_number,
    barcodeFormat = BarcodeFormat.fromName(barcode_format),
    // Catalog stores follow the catalog, so brand color updates reach existing cards.
    colorArgb = StoreCatalog.byId(store_id)?.colorArgb ?: color,
    nameOnCard = name_on_card,
    position = position,
    createdAtMillis = created_at,
)
