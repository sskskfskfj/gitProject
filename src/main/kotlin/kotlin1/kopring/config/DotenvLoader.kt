package kotlin1.kopring.config

import io.github.cdimascio.dotenv.Dotenv

object DotenvLoader {
    fun loadDev() {
        val dotenv = Dotenv.load()

        dotenv.entries().forEach { entry ->
            System.setProperty(entry.key, entry.value)
        }
    }
}