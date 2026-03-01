package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Food

@Serializable
data class FoodRequest(
    var nama: String = "",
    var deskripsi: String = "",
    var komposisi: String = "",
    var kalori: String = "",
    var asalDaerah: String = "",
    var pathGambar: String = "",
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "deskripsi" to deskripsi,
            "komposisi" to komposisi,
            "kalori" to kalori,
            "asalDaerah" to asalDaerah,
            "pathGambar" to pathGambar
        )
    }

    fun toEntity(): Food {
        return Food(
            nama = nama,
            deskripsi = deskripsi,
            komposisi = komposisi,
            kalori = kalori,
            asalDaerah = asalDaerah,
            pathGambar = pathGambar,
        )
    }
}