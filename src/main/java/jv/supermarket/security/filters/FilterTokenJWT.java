package jv.supermarket.security.filters;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jv.supermarket.auth.TokenService;
import jv.supermarket.shared.customexception.ResourceNotFoundException;
import jv.supermarket.user.Role;
import jv.supermarket.user.Usuario;
import jv.supermarket.user.UsuarioRepository;

@Component
public class FilterTokenJWT extends OncePerRequestFilter {

    private final UsuarioRepository userRepo;

    private final TokenService tokenService;

    FilterTokenJWT(UsuarioRepository userRepo, TokenService tokenService) {
        this.userRepo = userRepo;
        this.tokenService = tokenService;
    }

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = this.recoverToken(request);
        if (token != null) {
            String login = tokenService.validarToken(token);

            if (login != null) {
                Usuario user = userRepo.findByEmail(login);

                if (user == null) {
                    throw new ResourceNotFoundException("usuário não encontrado com o email: " + login);
                }
                Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getNome()))
                        .collect(Collectors.toSet());

                System.out.println("Usuário autenticado: " + login);
                System.out.println("Roles do usuário autenticado: " +
                        user.getRoles().stream().map(Role::getNome).collect(Collectors.toList()));

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
                        authorities);

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    @SuppressWarnings("null")
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        String basePath = "/supermarket/auth";
        return path.equals(basePath + "/login") || path.equals(basePath + "/registrar");
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "").trim();
    }
}
