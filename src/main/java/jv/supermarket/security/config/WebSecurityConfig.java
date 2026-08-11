package jv.supermarket.security.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jv.supermarket.security.CustomUserDetailsService;
import jv.supermarket.security.filters.FilterTokenJWT;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CustomUserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    AuthenticationManager authManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService());

        provider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(provider);
    }

    @Bean
    public CorsConfigurationSource corsConf() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173"); // Domínio do frontend
        config.addAllowedHeader("*");
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Aplica CORS em todas as rotas
        return source;
    }

    @Autowired
    FilterTokenJWT filterToken;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String url_auth = "/supermarket/auth/";
        String url_produtos = "/supermarket/produto/";
        String url_categorias = "/supermarket/categoria/";
        String url_imagens = "/supermarket/imagem/";
        String url_carrinho = "/supermarket/carrinho/";
        String url_pedido = "/supermarket/pedido/";
        String url_admin = "/supermarket/admin/";

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //@formatter:off
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/h2-console/**")
                    .permitAll()
                .requestMatchers(url_auth+"**")
                    .permitAll()
                .requestMatchers(
                        "/v3/api-docs/**",   // Documentação JSON
                        "/swagger-ui/**",    // Recursos Swagger UI
                        "/swagger-ui.html",  // Página inicial do Swagger
                        "/webjars/**"        // Recursos estáticos
                ).permitAll()
                
                .requestMatchers(HttpMethod.GET, url_produtos+"**")
                    .hasAnyRole("ADMIN", "FUNCIONARIO", "CLIENTE")
                .requestMatchers(HttpMethod.POST, url_produtos + "save")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, url_produtos + "**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, url_produtos + "**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, url_produtos+"{id:\\d+}/**")
                    .hasAnyRole("ADMIN", "FUNCIONARIO")
                
                .requestMatchers(HttpMethod.POST, url_categorias + "save")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, url_categorias +"{id:\\d+}/addEstoque")
                    .hasAnyRole("ADMIN", "FUNCIONARIO")
                .requestMatchers(HttpMethod.PUT, url_categorias +"{id:\\d+}")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, url_categorias +"{id:\\d+}")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, url_categorias +"**")
                    .hasAnyRole("ADMIN", "CLIENTE", "FUNCIONARIO")
                    
                .requestMatchers(HttpMethod.POST, url_imagens+"upload")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, url_imagens +"{id:\\d+}")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, url_imagens +"{id:\\d+}")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, url_imagens+"**")
                    .hasAnyRole("ADMIN", "FUNCIONARIO", "CLIENTE")
                    
                .requestMatchers(HttpMethod.GET, url_carrinho+"show")
                    .hasRole("CLIENTE")
                .requestMatchers(HttpMethod.POST, url_carrinho+"addItem/{itemId:\\d+}")
                    .hasRole("CLIENTE")
                .requestMatchers(HttpMethod.DELETE, url_carrinho+"removeItem/{itemId:\\d+}")
                    .hasRole("CLIENTE")
                .requestMatchers(HttpMethod.PUT, url_carrinho+"item/{itemId:\\d+}/update")
                    .hasRole("CLIENTE")
                .requestMatchers(HttpMethod.DELETE, url_carrinho+"clear")
                    .hasRole("CLIENTE")
                    
                .requestMatchers(HttpMethod.POST, url_pedido+"criar")
                    .hasRole("CLIENTE")
                .requestMatchers(HttpMethod.GET, url_pedido+"{id:\\d+}")
                    .hasAnyRole("ADMIN","CLIENTE")
                .requestMatchers(HttpMethod.GET, url_pedido+"by-user")
                    .hasRole("CLIENTE")
                .requestMatchers(HttpMethod.DELETE, url_pedido+"cancelar")
                    .hasRole("CLIENTE")
                    
                .requestMatchers(HttpMethod.POST, url_admin+"criarFuncionario")
                    .hasRole("ADMIN"));
            //@formatter:on

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(filterToken, UsernamePasswordAuthenticationFilter.class);

        http.headers(header -> header
                .frameOptions(Customizer.withDefaults()).disable());

        http.csrf(csrf -> csrf.disable());

        http.cors(cors -> cors.configurationSource(corsConf()));

        http.userDetailsService(userDetailsService());

        return http.build();
    }

}
