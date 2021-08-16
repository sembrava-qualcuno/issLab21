package parkmanagerservice

import it.unibo.kactor.QakContext
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.security.core.userdetails.User
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.provisioning.UserDetailsManager

@SpringBootApplication
@EnableWebSecurity
class Sprint4Application

@ObsoleteCoroutinesApi
fun main(args: Array<String>) {
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
                        InMemoryUserDetailsManager(user("manager", "admin", "MANAGER"))
                    }
                })
            }
        }
    }
}
