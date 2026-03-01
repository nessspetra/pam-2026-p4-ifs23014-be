package org.delcom.repositories

import org.delcom.entities.Food

interface IFoodRepository {
    suspend fun getFoods(search: String): List<Food>
    suspend fun getFoodById(id: String): Food?
    suspend fun getFoodByName(name: String): Food?
    suspend fun addFood(food: Food): String
    suspend fun updateFood(id: String, newFood: Food): Boolean
    suspend fun removeFood(id: String): Boolean
}