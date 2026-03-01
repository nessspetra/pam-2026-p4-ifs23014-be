package org.delcom.repositories

import org.delcom.dao.FoodDAO
import org.delcom.entities.Food
import org.delcom.helpers.foodDaoToModel // Pastikan ini sudah ada di MappingHelper.kt
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.FoodTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class FoodRepository : IFoodRepository {
    override suspend fun getFoods(search: String): List<Food> = suspendTransaction {
        if (search.isBlank()) {
            FoodDAO.all()
                .orderBy(FoodTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map { foodDaoToModel(it) } // Menggunakan lambda untuk menghindari error inferensi tipe
        } else {
            val keyword = "%${search.lowercase()}%"

            FoodDAO
                .find {
                    FoodTable.nama.lowerCase() like keyword
                }
                .orderBy(FoodTable.nama to SortOrder.ASC)
                .limit(20)
                .map { foodDaoToModel(it) }
        }
    }

    override suspend fun getFoodById(id: String): Food? = suspendTransaction {
        FoodDAO
            .find { (FoodTable.id eq UUID.fromString(id)) }
            .limit(1)
            .map { foodDaoToModel(it) }
            .firstOrNull()
    }

    override suspend fun getFoodByName(name: String): Food? = suspendTransaction {
        FoodDAO
            .find { (FoodTable.nama eq name) }
            .limit(1)
            .map { foodDaoToModel(it) }
            .firstOrNull()
    }

    override suspend fun addFood(food: Food): String = suspendTransaction {
        val foodDAO = FoodDAO.new {
            nama = food.nama
            pathGambar = food.pathGambar
            deskripsi = food.deskripsi
            komposisi = food.komposisi
            kalori = food.kalori
            asalDaerah = food.asalDaerah
            createdAt = food.createdAt
            updatedAt = food.updatedAt
        }

        foodDAO.id.value.toString()
    }

    override suspend fun updateFood(id: String, newFood: Food): Boolean = suspendTransaction {
        val foodDAO = FoodDAO
            .find { FoodTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (foodDAO != null) {
            foodDAO.nama = newFood.nama
            foodDAO.pathGambar = newFood.pathGambar
            foodDAO.deskripsi = newFood.deskripsi
            foodDAO.komposisi = newFood.komposisi
            foodDAO.kalori = newFood.kalori
            foodDAO.asalDaerah = newFood.asalDaerah
            foodDAO.updatedAt = newFood.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun removeFood(id: String): Boolean = suspendTransaction {
        val rowsDeleted = FoodTable.deleteWhere {
            FoodTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}