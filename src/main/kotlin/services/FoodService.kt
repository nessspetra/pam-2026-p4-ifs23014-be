package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.FoodRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IFoodRepository
import java.io.File
import java.util.*

class FoodService(private val foodRepository: IFoodRepository) {

    // Mengambil semua data makanan
    suspend fun getAllFoods(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val foods = foodRepository.getFoods(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar makanan",
            mapOf(Pair("foods", foods))
        )
        call.respond(response)
    }

    // Mengambil data makanan berdasarkan id
    suspend fun getFoodById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID makanan tidak boleh kosong!")

        val food = foodRepository.getFoodById(id) ?: throw AppException(404, "Data makanan tidak tersedia!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data makanan",
            mapOf(Pair("food", food))
        )
        call.respond(response)
    }

    // Ambil data request multipart (Text + File)
    private suspend fun getFoodRequest(call: ApplicationCall): FoodRequest {
        val foodReq = FoodRequest()
        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)

        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "nama" -> foodReq.nama = part.value.trim()
                        "deskripsi" -> foodReq.deskripsi = part.value
                        "komposisi" -> foodReq.komposisi = part.value
                        "kalori" -> foodReq.kalori = part.value
                        "asalDaerah" -> foodReq.asalDaerah = part.value
                    }
                }
                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" } ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/foods/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs()

                    part.provider().copyAndClose(file.writeChannel())
                    foodReq.pathGambar = filePath
                }
                else -> {}
            }
            part.dispose()
        }
        return foodReq
    }

    // Validasi data makanan
    private fun validateFoodRequest(foodReq: FoodRequest) {
        val validatorHelper = ValidatorHelper(foodReq.toMap())
        validatorHelper.required("nama", "Nama makanan tidak boleh kosong")
        validatorHelper.required("deskripsi", "Deskripsi tidak boleh kosong")
        validatorHelper.required("komposisi", "Komposisi tidak boleh kosong")
        validatorHelper.required("kalori", "Informasi kalori tidak boleh kosong")
        validatorHelper.required("asalDaerah", "Asal daerah tidak boleh kosong")
        validatorHelper.required("pathGambar", "Gambar makanan harus diunggah")
        validatorHelper.validate()

        val file = File(foodReq.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar makanan gagal diunggah!")
        }
    }

    // Menambahkan data makanan baru
    suspend fun createFood(call: ApplicationCall) {
        val foodReq = getFoodRequest(call)
        validateFoodRequest(foodReq)

        val existFood = foodRepository.getFoodByName(foodReq.nama)
        if (existFood != null) {
            val tmpFile = File(foodReq.pathGambar)
            if (tmpFile.exists()) tmpFile.delete()
            throw AppException(409, "Makanan dengan nama ini sudah ada!")
        }

        val foodId = foodRepository.addFood(foodReq.toEntity())
        val response = DataResponse(
            "success",
            "Berhasil menambahkan data makanan",
            mapOf(Pair("foodId", foodId))
        )
        call.respond(response)
    }

    // Mengubah data makanan
    suspend fun updateFood(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID tidak boleh kosong!")
        val oldFood = foodRepository.getFoodById(id) ?: throw AppException(404, "Data tidak ditemukan!")

        val foodReq = getFoodRequest(call)
        if (foodReq.pathGambar.isEmpty()) foodReq.pathGambar = oldFood.pathGambar

        validateFoodRequest(foodReq)

        if (foodReq.nama != oldFood.nama) {
            val existFood = foodRepository.getFoodByName(foodReq.nama)
            if (existFood != null) {
                if (foodReq.pathGambar != oldFood.pathGambar) File(foodReq.pathGambar).delete()
                throw AppException(409, "Nama makanan sudah digunakan!")
            }
        }

        if (foodReq.pathGambar != oldFood.pathGambar) {
            File(oldFood.pathGambar).delete()
        }

        foodRepository.updateFood(id, foodReq.toEntity())
        call.respond(DataResponse<String>("success", "Berhasil mengubah data makanan", null))
    }

    // Menghapus data makanan
    suspend fun deleteFood(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID tidak boleh kosong!")
        val food = foodRepository.getFoodById(id) ?: throw AppException(404, "Data tidak ditemukan!")

        if (foodRepository.removeFood(id)) {
            File(food.pathGambar).delete()
            call.respond(DataResponse<String>("success", "Berhasil menghapus data makanan", null))
        } else {
            throw AppException(400, "Gagal menghapus data!")
        }
    }

    // Mengambil file gambar makanan
    suspend fun getFoodImage(call: ApplicationCall) {
        val id = call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest)
        val food = foodRepository.getFoodById(id) ?: return call.respond(HttpStatusCode.NotFound)
        val file = File(food.pathGambar)

        if (!file.exists()) return call.respond(HttpStatusCode.NotFound)
        call.respondFile(file)
    }
}