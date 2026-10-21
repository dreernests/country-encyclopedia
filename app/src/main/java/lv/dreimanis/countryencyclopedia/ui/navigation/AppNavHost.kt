package lv.dreimanis.countryencyclopedia.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import lv.dreimanis.countryencyclopedia.ui.detail.CountryDetailScreen
import lv.dreimanis.countryencyclopedia.ui.list.CountryListScreen

@Serializable
object CountryListRoute

@Serializable
data class CountryDetailRoute(val uuid: String)

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = CountryListRoute,
        modifier = modifier,
    ) {
        composable<CountryListRoute> {
            CountryListScreen(
                onCountryClick = { uuid -> navController.navigate(CountryDetailRoute(uuid)) },
            )
        }

        composable<CountryDetailRoute> {
            CountryDetailScreen(
                onCountryClick = { uuid -> navController.navigate(CountryDetailRoute(uuid)) },
                onBack = { navController.navigateUp() },
            )
        }
    }
}
