package lv.dreimanis.countryencyclopedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import lv.dreimanis.countryencyclopedia.ui.navigation.AppNavHost
import lv.dreimanis.countryencyclopedia.ui.theme.CountryEncyclopediaTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CountryEncyclopediaTheme {
                AppNavHost()
            }
        }
    }
}
