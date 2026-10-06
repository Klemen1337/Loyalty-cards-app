package app.loyaltycards

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.loyaltycards.di.AppContainer
import app.loyaltycards.ui.add.AddCardScreen
import app.loyaltycards.ui.add.AddCardViewModel
import app.loyaltycards.ui.cards.CardListViewModel
import app.loyaltycards.ui.cards.EditCardsScreen
import app.loyaltycards.ui.cards.WalletScreen
import app.loyaltycards.ui.theme.LoyaltyCardsTheme
import kotlinx.serialization.Serializable

@Serializable
private data object WalletRoute

@Serializable
private data object EditCardsRoute

@Serializable
private data object AddCardRoute

/** Root composable shared by Android and iOS. */
@Composable
fun App(container: AppContainer) {
    LoyaltyCardsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = WalletRoute) {
                composable<WalletRoute> {
                    WalletScreen(
                        viewModel = viewModel { CardListViewModel(container.cardRepository) },
                        onAddCard = { navController.navigate(AddCardRoute) },
                        onEditCards = { navController.navigate(EditCardsRoute) },
                    )
                }
                composable<EditCardsRoute> {
                    EditCardsScreen(
                        viewModel = viewModel { CardListViewModel(container.cardRepository) },
                        onDone = { navController.popBackStack() },
                    )
                }
                composable<AddCardRoute> {
                    AddCardScreen(
                        viewModel = viewModel { AddCardViewModel(container.cardRepository) },
                        onBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}
