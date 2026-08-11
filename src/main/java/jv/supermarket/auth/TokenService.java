package jv.supermarket.auth;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jv.supermarket.user.Usuario;


@Service
public class TokenService {

    @Value("${api.security.secret}")
    private String segredo;

    public String gerarToken(Usuario user) {
        Algorithm algoritmo = Algorithm.HMAC256(segredo);

        String token = JWT.create()
                .withSubject(user.getEmail())
                .withIssuer("Supermarket API")
                .withExpiresAt(gerarTempoDeExpiracao())
                .sign(algoritmo);

        return token;
    }

    public String validarToken(String token){
            Algorithm algoritmo = Algorithm.HMAC256(segredo);
            
            return JWT.require(algoritmo)
                    .withIssuer("Supermarket API")
                    .build()
                    .verify(token)
                    .getSubject();
    }

    private Instant gerarTempoDeExpiracao(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
