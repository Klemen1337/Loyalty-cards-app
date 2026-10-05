package app.loyaltycards.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.loyaltycards.db.LoyaltyCardsDatabase

class AndroidDriverFactory(private val context: Context) : DriverFactory {
    override fun createDriver(): SqlDriver =
        AndroidSqliteDriver(LoyaltyCardsDatabase.Schema, context, DATABASE_NAME)
}
