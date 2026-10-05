package app.loyaltycards.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.loyaltycards.db.LoyaltyCardsDatabase

class IosDriverFactory : DriverFactory {
    override fun createDriver(): SqlDriver =
        NativeSqliteDriver(LoyaltyCardsDatabase.Schema, DATABASE_NAME)
}
