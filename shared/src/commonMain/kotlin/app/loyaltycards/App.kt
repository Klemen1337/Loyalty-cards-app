package app.loyaltycards

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import app.loyaltycards.di.AppContainer
import app.loyaltycards.ui.cards.CardListScreen
import app.loyaltycards.ui.cards.CardListViewModel
import app.loyaltycards.ui.theme.LoyaltyCardsTheme

/** Root composable shared by Android and iOS. */
@Composable
fun App(container: AppContainer) {
    LoyaltyCardsTheme {
        val viewModel = viewModel { CardListViewModel(container.cardRepository) }
        CardListScreen(viewModel)
    }
}
