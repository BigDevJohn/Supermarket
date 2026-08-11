package jv.supermarket.cart;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jv.supermarket.shared.Mensagem;
import jv.supermarket.shared.RespostaAPI;
import jv.supermarket.user.Usuario;
import jv.supermarket.user.UsuarioService;

@RestController
@RequestMapping("/supermarket/carrinho")
public class CarrinhoController {

    @Autowired
    CarrinhoService carrinhoService;

    @Autowired
    CarrinhoItemService ItemService;

    @Autowired
    UsuarioService usuarioService;

    @Operation(summary = "Retorna o carrinho do Usuário logado")
    @ApiResponses({
        @ApiResponse(description = "Carrinho do Usuário retornado com sucesso", 
            responseCode = "200",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Carrinho.class))),
        @ApiResponse(responseCode = "404",
            description = "Carrinho não encontrado. Usuário necessita ser um cliente para ter um carrinho",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @GetMapping("/show")
    public ResponseEntity<CarrinhoDTO> showCarrinho() {
        Usuario user = usuarioService.getLoggedUsuario();

        return ResponseEntity.status(HttpStatus.OK).body(carrinhoService.getCarrinho(user.getId()));
    }

    @Operation(summary = "Adiciona um item no carrinho", description="Adiciona um item no carrinho do usuário pelo id do Produto, se o produto já existir no carrinho, ele apenas soma a quantidade.")
    @ApiResponses({
        @ApiResponse(responseCode="200",
            description = "Item adicionado com sucesso.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespostaAPI.class))),
        @ApiResponse(responseCode="404",
        description = "Item não encontrado. Verifique o id do produto",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class))),
        @ApiResponse(responseCode="400",
            description = "Quantidade inválida. A quantidade deve ser um número inteiro positivo",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class))),
        @ApiResponse(responseCode="404",
            description = "Carrinho não encontrado. Usuário necessita ser um cliente para ter um carrinho",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @PostMapping("/addItem/{itemId}")
    public ResponseEntity<RespostaAPI> addItem(@Parameter(description = "Id do produto a ser adicionado") @PathVariable Long itemId, @Parameter(description = "Quantidade do produto a ser adicionado") @RequestParam int quantidade) {
        Usuario user = usuarioService.getLoggedUsuario();
        ItemService.addItemInCarrinho(itemId, quantidade, user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(new RespostaAPI(Instant.now(),
                "Item de id: " + itemId + " adicionado no carrinho com id: " + user.getId() + " com sucesso."));
    }

    @Operation(summary = "Remove um item no carrinho", description="Remove um item no carrinho do usuário pelo id do Produto")
    @ApiResponses({
        @ApiResponse(responseCode="200",
            description = "Item removido com sucesso.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespostaAPI.class))),
        @ApiResponse(responseCode="404",
        description = "Item não encontrado. Verifique o id do produto",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class))),
        @ApiResponse(responseCode="404",
            description = "Carrinho não encontrado. Usuário necessita ser um cliente para ter um carrinho",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @DeleteMapping("/removeItem/{itemId}")
    public ResponseEntity<RespostaAPI> removeItem(@PathVariable Long itemId) {
        Usuario user = usuarioService.getLoggedUsuario();
        ItemService.removeItemOfCarrinho(user.getId(), itemId);
        return ResponseEntity.status(HttpStatus.OK).body(new RespostaAPI(Instant.now(),
                "Item de id: " + itemId + " removido do carrinho com id: " + user.getId() + " com sucesso."));
    }

    @Operation(summary = "Modifica a quantidade de um item no carrinho", description="Modifica a quantidade de um item no carrinho do usuário pelo id do Produto")
    @ApiResponses({
        @ApiResponse(responseCode="200",
            description = "Item atualizado com sucesso.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespostaAPI.class))),
        @ApiResponse(responseCode="404",
        description = "Item não encontrado. Verifique o id do produto",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class))),
        @ApiResponse(responseCode="400",
            description = "Quantidade inválida. A quantidade deve ser um número inteiro positivo",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class))),
        @ApiResponse(responseCode="404",
            description = "Carrinho não encontrado. Usuário necessita ser um cliente para ter um carrinho",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @PutMapping("/item/{itemId}/update")
    public ResponseEntity<RespostaAPI> updateItemQuantidade(@PathVariable Long itemId, @RequestParam int quantidade) {
        Usuario user = usuarioService.getLoggedUsuario();
        System.out.println("ID do usuario: " + user.getId());
        ItemService.updateItemQuantidade(user.getId(), itemId, quantidade);
        return ResponseEntity.status(HttpStatus.OK).body(new RespostaAPI(Instant.now(),
                "Item de id: " + itemId + " modificado no carrinho com id: " + user.getId() + " com sucesso."));
    }

    @Operation(summary = "Esvazia o carrinho do Usuário")
    @ApiResponses({
        @ApiResponse(responseCode="200",
            description = "Carrinho limpo com sucesso.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespostaAPI.class))),
        @ApiResponse(responseCode="404",
            description = "Carrinho não encontrado. Usuário necessita ser um cliente para ter um carrinho",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @DeleteMapping("/clear")
    public ResponseEntity<RespostaAPI> clearCarrinho() {
        Usuario user = usuarioService.getLoggedUsuario();
        carrinhoService.clearCarrinho(user.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new RespostaAPI(Instant.now(), "Carrinho com id: " + user.getId() + " limpo com sucesso."));
    }

}
