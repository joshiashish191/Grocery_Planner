package net.softglobe.groceryplanner.model

import net.softglobe.groceryplanner.BuildConfig

object URLs {
    val BASE_URL = BuildConfig.BASE_URL
    const val LOGIN = "login.php"
    const val REGISTER = "register.php"
    const val FORGOT_PASS = "forgot-pass/sendmail.php"
    const val RESET_PASS = "forgot-pass/reset-pass.php"
    const val CHANGE_PASS = "change-pass.php"
    const val BACKUP_TO_SERVER = "export-backup.php"
    const val BACKUP_FROM_SERVER = "import-backup.php"
    const val METADATA = "metadata.php"
    const val USER_METADATA = "user-metadata.php"
    const val USER_DETAILS = "user-details.php"
    const val USER_OPERATIONS = "user-operations.php"
    const val PAYMENT_FETCH = "payment/payment-fetch.php"
    const val UPDATE_USER_PAYMENT_DETAILS = "payment/update-user-payment-details.php"
    const val GRANT_REWARD = "grant-reward.php"
    const val BUY_WITH_COINS = "payment/buy-with-coins.php"
}