package org.delcom

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.data.AppException
import org.delcom.data.ErrorResponse
import org.delcom.helpers.parseMessageToMap
import org.delcom.services.PlantService
import org.delcom.services.FoodService // Import Service baru
import org.delcom.services.ProfileService
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val plantService: PlantService by inject()
    val foodService: FoodService by inject() // Suntikkan FoodService
    val profileService: ProfileService by inject()

    install(StatusPages) {
        // Tangkap AppException (tetap sama untuk semua endpoint)
        exception<AppException> { call, cause ->
            val dataMap: Map<String, List<String>> = parseMessageToMap(cause.message)

            call.respond(
                status = HttpStatusCode.fromValue(cause.code),
                message = ErrorResponse(
                    status = "fail",
                    message = if (dataMap.isEmpty()) cause.message else "Data yang dikirimkan tidak valid!",
                    data = if (dataMap.isEmpty()) null else dataMap.toString()
                )
            )
        }

        // Tangkap semua Throwable lainnya
        exception<Throwable> { call, cause ->
            call.respond(
                status = HttpStatusCode.fromValue(500),
                message = ErrorResponse(
                    status = "error",
                    message = cause.message ?: "Unknown error",
                    data = ""
                )
            )
        }
    }

    routing {
        get("/") {
            call.respondText("API telah berjalan. Dibuat oleh Gayus Jones Petra.")
        }

        // --- Route Plants (Tetap Dipertahankan) ---
        route("/plants") {
            get { plantService.getAllPlants(call) }
            post { plantService.createPlant(call) }
            get("/{id}") { plantService.getPlantById(call) }
            put("/{id}") { plantService.updatePlant(call) }
            delete("/{id}") { plantService.deletePlant(call) }
            get("/{id}/image") { plantService.getPlantImage(call) }
        }

        // --- Route Foods (Aplikasi Baru Kamu) ---
        route("/foods") {
            get {
                foodService.getAllFoods(call)
            }
            post {
                foodService.createFood(call)
            }
            get("/{id}") {
                foodService.getFoodById(call)
            }
            put("/{id}") {
                foodService.updateFood(call)
            }
            delete("/{id}") {
                foodService.deleteFood(call)
            }
            // Route untuk mengambil gambar makanan
            get("/{id}/image") {
                foodService.getFoodImage(call)
            }
        }

        // --- Route Profile ---
        route("/profile"){
            get {
                profileService.getProfile(call)
            }
            get("/photo") {
                profileService.getProfilePhoto(call)
            }
        }
    }
}