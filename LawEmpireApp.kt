package com.lawempire.sierraleone

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.lawempire.sierraleone.audio.LEAudioManager
import com.lawempire.sierraleone.billing.LEBillingManager
import com.lawempire.sierraleone.utils.GameManager

class LawEmpireApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialise GameManager (loads saved player data)
        GameManager.init(this)

        // Initialise audio manager
        LEAudioManager.init(this)

        // Register audio manager with process lifecycle
        // so music pauses/resumes with app background/foreground
        ProcessLifecycleOwner.get().lifecycle.addObserver(LEAudioManager)

        // Initialise billing (connects to Google Play)
        LEBillingManager.init(this) { reward ->
            // Grant IAP reward to player when purchase is confirmed
            val player = GameManager.player
            player.devPoints += reward.devPoints
            player.coins     += reward.coins
            player.gems      += reward.gems
            if (reward.elitePassDays > 0) {
                player.elitePassDaysRemaining = reward.elitePassDays
            }
            GameManager.logActivity("Purchase confirmed: +⚡${reward.devPoints} DP, +🪙${reward.coins}")
            GameManager.save()
        }
    }
}
