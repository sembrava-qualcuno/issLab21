package it.unibo.parkmanagerservice

import it.unibo.kactor.QakContext
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Sprint3Application

fun main(args: Array<String>) {
    runBlocking {
        QakContext.createContexts("localhost", this, "carparking.pl", "sysRules.pl")

        runApplication<Sprint3Application>(*args)
    }
}
