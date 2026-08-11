package jv.supermarket.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jv.supermarket.shared.Mensagem;
import jv.supermarket.shared.customexception.BadAuthRequestException;
import jv.supermarket.user.Usuario;
import jv.supermarket.user.UsuarioService;

@RestController
@RequestMapping("/supermarket/auth")
public class AuthController {
    
    @Autowired
    TokenService tokenService;

    @Autowired
    UsuarioService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Operation(summary = "Login dos usuarios", description="Recebe o email e a senha, e retorna um JWT Token para o usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
            description = "Usuário logado com sucesso",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AuthResponseDTO.class))),
        @ApiResponse(responseCode = "400",
            description = "Erro ao realizar login: email ou senha incorreta",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid AuthLoginRequestDTO dto) {
        Usuario user = userService.getByEmail(dto.email());

        if (passwordEncoder.matches(dto.senha(), user.getSenha())) {
            String token = tokenService.gerarToken(user);

            return ResponseEntity.status(HttpStatus.OK).body(new AuthResponseDTO(user.getNome(), token));
        }
        throw new BadAuthRequestException("Erro ao realizar login: email ou senha incorreta");

    }

    @Operation(summary = "Registro dos clientes", description="Recebe os dados do novo cliente, e retorna um JWT Token para o mesmo")
    @ApiResponses({
        @ApiResponse(responseCode = "201",
        description = "Cliente registrado com sucesso",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AuthResponseDTO.class))),
        @ApiResponse(responseCode = "400",
            description = "Erro ao realizar registro: Já existe um usuário com esse email",
            content= @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @PostMapping("/registrar")
    public ResponseEntity<AuthResponseDTO> saveCliente(@RequestBody @Valid Usuario user){
        Usuario usuario = userService.saveCliente(user);
        String token = tokenService.gerarToken(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponseDTO(usuario.getNome(),token));
    }
}
