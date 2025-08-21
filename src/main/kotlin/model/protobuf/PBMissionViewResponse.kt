package dev.gangster.model.protobuf

import dev.gangster.model.protobuf.common.PBMission
import kotlinx.serialization.Serializable

/**
 * for viewmissions
 *
 * example:  %xt%viewmissions%1%-1%CAEQABgAIAoo6AdSJAhIEJAHGDcgCSicBDABOhQIABABGAEiBAgAEAAoADIECAAQAFJCCCwQ+g4YYiASKLgIMAE6MggJEAEYAyIGCOgHEMIDKAAyBAgIEB04AkoECDEQZlIECAMQAnIMCCMVmplZPx0AAAAAUiQIVRCdCxhHIA0oqgYwAToUCAAQARgBIgQIABAAKAAyBAgAEAA=%
 *
 * result: ok = 1, player_busy = 2, not_enough_energy = 3, mission_not_available = 4
 */
@Serializable
data class PBMissionViewResponse(
    val result: Int = 1,
    val energyRefiller: Int,
    val freeMissionEnergyDrinks: Int,
    val maxEnergyRefil: Int,
    val missionEnergy: Int,

    // i think its sicilian event mission
    val cityEventMission: Int?,
    val cityEventMissionProgress: Int?,
    val cityEventTotalMissions: Int?,
    val cityEventMissionsTimeLeft: Int?,

    val missions: List<PBMission>,
) {
    companion object {
        fun dummy(): PBMissionViewResponse {
            return PBMissionViewResponse(
                result = 1,
                energyRefiller = 1,
                freeMissionEnergyDrinks = 1,
                maxEnergyRefil = 7,
                missionEnergy = 13,
                cityEventMission = null,
                cityEventMissionProgress = null,
                cityEventTotalMissions = null,
                cityEventMissionsTimeLeft = null,
                missions = listOf(
                    PBMission.dummy(1),
                    PBMission.dummy(2),
                    PBMission.dummy(3),
                )
            )
        }
    }
}
