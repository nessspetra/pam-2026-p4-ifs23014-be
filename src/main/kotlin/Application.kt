package org.delcom

import io.github.cdimascio.dotenv.dotenv
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.serialization.json.Json
import org.delcom.module.appModule
import org.delcom.helpers.configureDatabases
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    // Memuat variabel lingkungan dari file .env
    val dotenv = dotenv {
        directory = "."
        ignoreIfMissing = false
    }

    // Mendaftarkan variabel .env ke System Property agar bisa dibaca oleh DatabaseHelper
    dotenv.entries().forEach {
        System.setProperty(it.key, it.value)
    }

    // Menjalankan server Ktor melalui Netty Engine
    EngineMain.main(args)
}

fun Application.module() {

    // Konfigurasi CORS (Cross-Origin Resource Sharing) agar API bisa diakses oleh berbagai host
    install(CORS) {
        anyHost()
    }

    // Konfigurasi Content Negotiation menggunakan Kotlinx Serialization
    install(ContentNegotiation) {
        json(
            Json {
                explicitNulls = false // Tidak menampilkan field yang bernilai null dalam response
                prettyPrint = true     // Format JSON agar mudah dibaca
                ignoreUnknownKeys = true // Mengabaikan key yang tidak dikenal saat deserialisasi
            }
        )
    }

    // Inisialisasi Koin (Dependency Injection)
    install(Koin) {
        // Memuat modul yang berisi pendaftaran Plant, Food, dan Profile
        modules(appModule)
    }

    // Menjalankan konfigurasi database (Koneksi PostgreSQL)
    configureDatabases()

    // Menjalankan konfigurasi routing (Endpoint Plants, Foods, dan Profile)
    configureRouting()
}