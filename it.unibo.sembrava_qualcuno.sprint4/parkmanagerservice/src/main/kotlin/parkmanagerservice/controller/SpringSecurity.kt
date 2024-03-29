package parkmanagerservice.controller

import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class AdminSecurityConfiguration : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http
                ?.csrf()?.disable()
                ?.authorizeRequests()
                    ?.antMatchers("/client*/**")?.permitAll()
                    ?.antMatchers("/manager", "/parkingArea/**")?.authenticated()
                ?.and()
                ?.formLogin()
                    ?.loginPage("/manager/login")
                    ?.defaultSuccessUrl("/manager", true)
                    ?.failureUrl("/manager/loginError")
                    ?.permitAll()
                ?.and()
                ?.logout()
                    ?.logoutUrl("/manager/logout")
                    ?.deleteCookies("JSESSID")
                    ?.invalidateHttpSession(true)
            }
}

