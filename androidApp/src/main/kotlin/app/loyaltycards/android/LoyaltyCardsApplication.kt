package app.loyaltycards.android

import android.app.Application
import app.loyaltycards.data.AndroidDriverFactory
import app.loyaltycards.di.AppContainer

class LoyaltyCardsApplication : Application() {
    val container: AppContainer by lazy { AppContainer(AndroidDriverFactory(this)) }
}
