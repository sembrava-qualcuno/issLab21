package parkmanagerservice

import it.unibo.kactor.QakContext
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@SpringBootApplication
@EnableWebSecurity
class Sprint4Application

@ObsoleteCoroutinesApi
fun main(args: Array<String>) {
    val MANAGER_USERNAME = System.getenv("MANAGER_USERNAME") ?: "manager"
    val MANAGER_PASSWORD = System.getenv("MANAGER_PASSWORD") ?: "admin"

    println("SpringBootApplication: Manager credentials set to username=$MANAGER_USERNAME passowrd=$MANAGER_PASSWORD")

    runBlocking {
        launch {
            QakContext.createContexts("localhost", this, "carparking.pl", "sysRules.pl")
        }
        launch(newSingleThreadContext("SpringThread")) {
            runApplication<Sprint4Application>(*args) {
                addInitializers(beans {
                    bean {
                        val encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
                        fun user(username: String, password: String, vararg roles: String) =
                                User.withUsername(username)
                                .password(encoder.encode(password))
                                .roles(*roles)
                                .build()
                        InMemoryUserDetailsManager(user(MANAGER_USERNAME, MANAGER_PASSWORD, "MANAGER"))
                    }
                })
            }
        }
    }
}
