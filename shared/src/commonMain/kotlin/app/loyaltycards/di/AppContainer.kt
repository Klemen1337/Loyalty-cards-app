package app.loyaltycards.di

import app.loyaltycards.data.CardRepository
import app.loyaltycards.data.DriverFactory
import app.loyaltycards.data.SqlCardRepository
import app.loyaltycards.db.LoyaltyCardsDatabase

/** App-wide dependencies. Create one instance per process. */
class AppContainer(driverFactory: DriverFactory) {
    private val database = LoyaltyCardsDatabase(driverFactory.createDriver())

    val cardRepository: CardRepository = SqlCardRepository(database)
}
