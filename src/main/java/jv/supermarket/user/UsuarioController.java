package jv.supermarket.user;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jv.supermarket.shared.Mensagem;
import jv.supermarket.shared.RespostaAPI;

@RestController
@RequestMapping("/supermarket/usuario")
public class UsuarioController {

    @Autowired
    UsuarioService userService;
    
    @Operation(summary = "Busca um usuário", description = "Busca um usuário pelo Id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", 
            description = "Usuário retornado com sucesso", 
            content = @Content( mediaType = "application/json",
                schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "404",
            description = "Usuário não encontrado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@Parameter(description = "Id do usuário a ser buscado") @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getById(id));
    }

    @Operation(summary = "Atualiza um usuario", description = "Atualiza um usuario com base em novos atributos enviados e no id atual")
    @ApiResponses({
        @ApiResponse(responseCode = "200", 
            description = "Usuário atualizado com sucesso", 
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "404",
            description = "Usuário não encontrado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @PutMapping("/{userId}/update")
    public ResponseEntity<Usuario> updateUsuario(@Parameter(description = "Novas informações do usuario") @RequestBody @Valid Usuario user, @Parameter(description = "Id do usuário") @PathVariable Long userId){
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUsuario(user, userId));
    }

    @Operation(summary = "Deleta um usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
            description = "Usuário deletado com sucesso",
            content =  @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespostaAPI.class))),
        @ApiResponse(responseCode = "404",
            description = "Nenhuma usuário foi encontrada com o id informado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<RespostaAPI> deleteUsuario(@Parameter(description = "Id do usuário")@PathVariable Long userId){
        userService.deleteUsuario(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new RespostaAPI(Instant.now(),"Usuário deletado com sucesso."));
    }
}
