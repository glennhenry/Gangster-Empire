package dev.gangster.data.collection

data class PlayerData(
    val x: Int = 0
) {
    companion object {
        fun admin(): PlayerData {
            return PlayerData(
                x = 0
            )
        }
    }
}
