package br.com.zupacademy.romeu.proposta.configuracao;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests(authorizeRequests ->
            authorizeRequests
                    .antMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                    .antMatchers(HttpMethod.GET, "/propostas/**").hasAuthority("SCOPE_proposta-scope")
                    .antMatchers(HttpMethod.GET, "/cartoes/**").hasAuthority("SCOPE_proposta-scope")
                    .antMatchers(HttpMethod.POST, "/cartoes/**").hasAuthority("SCOPE_proposta-scope")
                    .antMatchers(HttpMethod.POST, "/propostas/**").hasAuthority("SCOPE_proposta-scope")
                    .anyRequest().authenticated()
    )
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
  }

}