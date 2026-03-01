package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object FoodTable : UUIDTable("foods") {
    val nama = varchar("nama", 100)
    val pathGambar = varchar("path_gambar", 255)
    val deskripsi = text("deskripsi")
    val komposisi = text("komposisi")
    val kalori = varchar("kalori", 50)
    val asalDaerah = varchar("asal_daerah", 100)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}