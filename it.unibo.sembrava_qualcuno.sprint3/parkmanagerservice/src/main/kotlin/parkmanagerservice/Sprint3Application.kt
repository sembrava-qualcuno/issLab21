package parkmanagerservice

import it.unibo.kactor.QakContext
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File
import java.net.URLDecoder

@SpringBootApplication
class Sprint3Application

fun main(args: Array<String>) {
    runBlocking {
        println(System.getProperty("user.dir"))
        QakContext.createContexts(
            "localhost", this, "carparking.pl","sysRules.pl"
        )
        runApplication<Sprint3Application>(*args)
    }
}
