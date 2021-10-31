package parkmanagerservice

import it.unibo.kactor.QakContext
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File
import java.net.URLDecoder

@SpringBootApplication
class Sprint3Application

@ObsoleteCoroutinesApi
fun main(args: Array<String>) {
    runBlocking {
        println(System.getProperty("user.dir"))
        launch(newSingleThreadContext("QakThread")) {
            QakContext.createContexts("localhost", this, "carparking.pl","sysRules.pl")
        }
        launch(newSingleThreadContext("SpringThread")) {
            runApplication<Sprint3Application>(*args)
        }
    }
}
