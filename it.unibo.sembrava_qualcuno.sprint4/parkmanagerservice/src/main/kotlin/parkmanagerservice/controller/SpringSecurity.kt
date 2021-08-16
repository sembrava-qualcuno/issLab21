package parkmanagerservice.controller

import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class AdminSecurityConfiguration : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http
                ?.authorizeRequests()
                    ?.antMatchers("/client*/**")?.permitAll()
                    ?.antMatchers("/manager")?.authenticated()
                    ?.antMatchers("/parkingArea/**")?.authenticated()
                ?.and()
                ?.formLogin()
                    ?.loginPage("/manager/login")
                    ?.failureUrl("/manager/loginError")
                    ?.permitAll()
                ?.and()
                ?.logout()
                    ?.logoutUrl("/manager/logout")
            }
}

