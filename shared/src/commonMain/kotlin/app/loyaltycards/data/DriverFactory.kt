package app.loyaltycards.data

import app.cash.sqldelight.db.SqlDriver

/** Creates the platform SQLite driver for [app.loyaltycards.db.LoyaltyCardsDatabase]. */
fun interface DriverFactory {
    fun createDriver(): SqlDriver
}

internal const val DATABASE_NAME = "loyalty_cards.db"
