package jv.supermarket.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import jv.supermarket.auth.AuthResponseDTO;
import jv.supermarket.auth.TokenService;
import jv.supermarket.shared.Mensagem;

@RestController
@RequestMapping("/supermarket/admin")
public class AdminController {

    @Autowired
    UsuarioService userService;

    @Autowired
    TokenService tokenService;
    
    @Operation(summary = "Registro dos Funcionarios", description="Recebe os dados do novo funcionario, e retorna um JWT Token para o mesmo")
    @ApiResponses({
        @ApiResponse(responseCode = "201",
        description = "Funcionario registrado com sucesso",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AuthResponseDTO.class))),
        @ApiResponse(responseCode = "400",
            description = "Erro ao realizar registro: Já existe um usuário com esse email",
            content= @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @PostMapping("/criarFuncionario")
    public ResponseEntity<AuthResponseDTO> saveUsuario(@RequestBody @Valid Usuario user){
        Usuario usuario = userService.saveFuncionario(user);
        String token = tokenService.gerarToken(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponseDTO(usuario.getNome(),token));
    }
}
