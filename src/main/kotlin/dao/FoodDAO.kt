package org.delcom.dao

import org.delcom.tables.FoodTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID

class FoodDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, FoodDAO>(FoodTable)

    // Atribut spesifik untuk Foods
    var nama by FoodTable.nama
    var pathGambar by FoodTable.pathGambar
    var deskripsi by FoodTable.deskripsi
    var komposisi by FoodTable.komposisi      // Contoh: Bahan-bahan makanan
    var kalori by FoodTable.kalori             // Contoh: Informasi nutrisi
    var asalDaerah by FoodTable.asalDaerah     // Contoh: Asal makanan
    var createdAt by FoodTable.createdAt
    var updatedAt by FoodTable.updatedAt
}