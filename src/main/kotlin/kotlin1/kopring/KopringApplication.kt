package kotlin1.kopring

import kotlin1.kopring.config.DotenvLoader
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KopringApplication


fun main(args: Array<String>) {
    DotenvLoader.loadDev()
    runApplication<KopringApplication>(*args)
}
