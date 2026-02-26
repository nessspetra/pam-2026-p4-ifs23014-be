package org.delcom.module

import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.PlantRepository
import org.delcom.repositories.IFoodRepository
import org.delcom.repositories.FoodRepository
import org.delcom.services.PlantService
import org.delcom.services.FoodService
import org.delcom.services.ProfileService
import org.koin.dsl.module

val appModule = module {
    // --- Plant Section (Tetap Dipertahankan) ---
    single<IPlantRepository> {
        PlantRepository()
    }

    single {
        PlantService(get())
    }

    single<IFoodRepository> {
        FoodRepository()
    }

    // Mendaftarkan Food Service dan menyuntikkan (get) Food Repository ke dalamnya
    single {
        FoodService(get())
    }

    // --- Profile Section ---
    single {
        ProfileService()
    }
}