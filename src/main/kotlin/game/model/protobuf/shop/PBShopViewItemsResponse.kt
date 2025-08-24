package dev.gangster.game.model.protobuf.shop

import dev.gangster.game.model.protobuf.common.PBItem
import dev.gangster.game.model.protobuf.common.PBShopType
import dev.gangster.game.model.protobuf.common.PBShopTypeConstants
import kotlinx.serialization.Serializable

/**
 * Example request:
 * 1. blackmarket: %xt%viewitems%1%-1%CAEQxeQEGjIIDxABGAMiBgjoBxDCAygAMgQICBAdOAJKBAg4EF9SBAgDEAJyDAgjFZqZWT8dAAAAABpOCCMQARgDIgYI6AcQwgMoADIECBAQMTgESgQIPhBaUgQIBRADcgwIIxWamVk/HQAAAAByDAgkFZqZGb4dj8L1PHIMCCQVmpkZPh0K16M8Gi0IChABGAUiBgigHxCIDigAMgUIABCPTlIECAIQAnIMCAcVCtejPB0AAAAAeAEaKwgCEAEYBiIGCKAfEIgOKAAyBQgAEI9OUgQIAxACcgwICRXNzEw9HQAAAAAaMggPEAIYAyIGCMIDENYOKAEyBAgIEB04AkoECDwQdVIECAMQAnIMCCMVrkeBPx0AAAAAGk4IIxACGAMiBgjGChC6GSgBMgQIEBAxOARKBAhYEF9SBAgFEANyDAgjFR+Fiz8dAAAAAHIMCCQVmpkZvh2PwvU8cgwIJBWamRk+HQrXozwaLQgKEAEYBSIGCNgEEJAcKAEyBQgAEI9OUgQIAhACcgwIBxUK1yM9HQAAAAB4ARorCAIQARgGIgYI2AQQkBwoATIFCAAQj05SBAgDEAJyDAgJFY/C9T0dAAAAAA==%
 * 2. consumables: %xt%viewitems%1%-1%CAMQ3qMEGjAIChABGAQiBQjQDxBkKAAyBQgAEI9OOAJSBAgCEAJgMmgAcgwIDhXNzEw+Hc3MTD4aNggBEAEYBCIGCNAPEPoBKAAyBQgAEI9OOAFSBAgDEAJg+gFoAHIMCAYVzcxMPR0AAAAAeAF4Aho0CAIQARgEIgYI0A8Q+gEoADIFCA0Qj044AVIECAMQAmD6AWgAcgwIBhXNzEw9HQAAAAB4Axo2CAMQARgEIgYI0A8Q+gEoADIFCA0Qj044AVIECAMQAmD6AWgAcgwIBhXNzEw9HQAAAAB4BHgFGjEIChACGAQiBgiQARD6ASgBMgUIABCPTjgCUgQIAhACYDJoAHIMCA4VmpmZPh2amZk+GjYIARACGAQiBgiPARD0AygBMgUIABCPTjgBUgQIAxACYPoBaAByDAgGFa5H4T0dAAAAAHgBeAIaNAgCEAIYBCIGCI8BEPQDKAEyBQgNEI9OOAFSBAgDEAJg+gFoAHIMCAYVrkfhPR0AAAAAeAMaNggDEAIYBCIGCI8BEPQDKAEyBQgNEI9OOAFSBAgDEAJg+gFoAHIMCAYVrkfhPR0AAAAAeAR4BRoxCA4QARgEIgYIkCEQtgIoADIFCAAQj044A1IECAIQAmAKaAByDAgLFQrXIz0dAAAAABo2CAkQARgEIgYI0A8Q+gEoADIFCA0Qj044AVIECAMQAmD6AWgAcgwIHxXNzEw9HSlcjz14BHgFGjEIDhACGAQiBgiyAhCwBCgBMgUIABCPTjgDUgQIAhACYApoAHIMCAsVzczMPR0AAAAAGjYICRACGAQiBgiPARD0AygBMgUIDRCPTjgBUgQIAxACYPoBaAByDAgfFa5H4T0dPQpXPngEeAU=%
 * 3. kiosk: %xt%viewitems%1%-1%CAIQh+PD8v//////ARoUCAAQARgBIgQIABAAKAAyBAgAEAAaIghUEAEYASIGCKAGEOgCKAAyBAgFEBQ4BEIICA0QABgAIA0aIwieARABGAEiBgigBhDoAigAMgQIChAeOAdCCAgAEAAYGyAAGioIARABGAIiBQgoEOgCKAEyBQgAEI9OQggIBRAAGAAgAFoGCICjBRAAYAEaIQgPEAEYASIFCHgQ0AUoATIECAoQHjgBQggIABAAGAAgHRohCFQQARgBIgUIeBDQBSgBMgQIBRAUOARCCAgAEAAYHSAAGiMIngEQAhgBIgYI6AIQ3gsoATIECAoQHjgHQggIABAAGCEgABorCAEQAhgCIgYI6AIQ3gsoATIFCAAQj05CCAgNEAAYACAAWgYIgKMFEABgASIrCAUQAhgCIgYIwgMQ1g4oATIFCAAQj05CCAgAEBkYACAAWgYIgPUkEABgAQ==%
 */
@Serializable
data class PBShopViewItemsResponse(
    val shop: PBShopType,
    val refresh: Int,
    val items: List<PBItem>,
    val safe: PBItem?,   // locked item
    val remaining: Int?, // special field for extras shop
) {
    companion object {
        fun dummyBlackMarket(): PBShopViewItemsResponse {
            return PBShopViewItemsResponse(
                shop = PBShopTypeConstants.BLACK_MARKET,
                refresh = 900,
                items = listOf(
                    PBItem.dummyWeapon(1),
                    PBItem.dummyWeapon(1),
                    PBItem.dummyWeapon(1),
                    PBItem.dummyWeapon(1),

                    PBItem.dummyWeapon(1),
                    PBItem.dummyWeapon(1),
                    PBItem.dummyWeapon(1),
                    PBItem.dummyWeapon(1),
                ),
                safe = null,
                remaining = null
            )
        }

        fun dummyConsumables(): PBShopViewItemsResponse {
            return PBShopViewItemsResponse(
                shop = PBShopTypeConstants.CONSUMABLES,
                refresh = 900,
                items = listOf(
                    PBItem.dummyAmmo(1),
                    PBItem.dummyAmmo(1),
                    PBItem.dummyAmmo(1),
                    PBItem.dummyAmmo(1),

                    PBItem.dummyAmmo(1),
                    PBItem.dummyAmmo(1),
                    PBItem.dummyAmmo(1),
                    PBItem.dummyAmmo(1),

                    PBItem.dummyAmmo(1),
                    PBItem.dummyAmmo(1),
                    PBItem.dummyAmmo(1),
                    PBItem.dummyAmmo(1),
                ),
                safe = null,
                remaining = null
            )
        }

        fun dummyKiosk(): PBShopViewItemsResponse {
            return PBShopViewItemsResponse(
                shop = PBShopTypeConstants.KIOSK,
                refresh = 900,
                items = listOf(
                    PBItem.dummyGear(1),
                    PBItem.dummyGear(1),
                    PBItem.dummyGear(1),

                    PBItem.dummyGear(1),
                    PBItem.dummyGear(1),
                    PBItem.dummyGear(1),

                    PBItem.dummyFood(1),
                    PBItem.dummyFood(1),
                ),
                safe = null,
                remaining = null
            )
        }
    }
}
