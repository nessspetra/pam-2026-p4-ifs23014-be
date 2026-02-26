package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.PlantDAO
import org.delcom.dao.FoodDAO // Pastikan file FoodDAO.kt sudah ada di package org.delcom.dao
import org.delcom.entities.Plant
import org.delcom.entities.Food // Pastikan file Food.kt sudah ada di package org.delcom.entities
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

// Helper untuk menjalankan transaksi database secara asynchronous
suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

/**
 * Mapping untuk Delcom Plants (Tetap Dipertahankan)
 */
fun daoToModel(dao: PlantDAO): Plant = Plant(
    id = dao.id.value.toString(),
    nama = dao.nama,
    pathGambar = dao.pathGambar,
    deskripsi = dao.deskripsi,
    manfaat = dao.manfaat,
    efekSamping = dao.efekSamping,
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt
)

/**
 * Mapping untuk Aplikasi Foods (Aplikasi Baru)
 * Menentukan tipe kembalian ': Food' secara eksplisit untuk menghindari 'Platform Call' error
 */
fun foodDaoToModel(dao: FoodDAO): Food = Food(
    id = dao.id.value.toString(),
    nama = dao.nama,
    pathGambar = dao.pathGambar,
    deskripsi = dao.deskripsi,
    komposisi = dao.komposisi,
    kalori = dao.kalori,
    asalDaerah = dao.asalDaerah,
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt
)