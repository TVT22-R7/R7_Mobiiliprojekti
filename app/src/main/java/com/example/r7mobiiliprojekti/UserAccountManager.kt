package com.example.r7mobiiliprojekti

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color


object UserAccountManager {
    var googleAccountId: String? = null

}

object DarkmodeON {
    var isDarkMode by mutableStateOf(false)
        private set // Make isDarkMode private to only allow modification through toggleDarkMode

    val darkModeEnabled: Boolean
        get() = isDarkMode // Getter function to always reflect the current value of isDarkMode

    fun toggleDarkMode() {
        isDarkMode = !isDarkMode
    }
}

object  UiScale{
    var scale by mutableStateOf(1.0f)



    fun rescaleUI() {
        scale = 1.4f

    }
    fun resetScale(){
        scale =1.0f


    }
}
object DarkModeTextHelper {
    fun getTextColor(darkModeEnabled: Boolean): Color {
        return if (darkModeEnabled) Color.White else Color.Black
    }
}

