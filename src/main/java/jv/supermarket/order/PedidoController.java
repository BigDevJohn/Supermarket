package jv.supermarket.order;

import java.time.Instant;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jv.supermarket.shared.Mensagem;
import jv.supermarket.shared.RespostaAPI;

@RestController
@RequestMapping("/supermarket/pedido")
public class PedidoController {
    
    @Autowired
    PedidoService pedidoService;

    @Operation(summary = "Cria um pedido", description = "Cria um pedido com base nos produtos adicionados no carrinho do usuário")
    @ApiResponses({
        @ApiResponse(responseCode="201",
            description = "Pedido criado com sucesso.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Pedido.class))),
        @ApiResponse(responseCode="404",
            description = "Carrinho vazio. Adicione itens a ele primeiro antes de tentar realizar um pedido.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class))),
        @ApiResponse(responseCode="404",
            description = "Estoque excedido. Um dos produtos requisitados não possui estoque o suficiente.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @PostMapping("/criar")
    public ResponseEntity<Pedido> createPedido() {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.createPedido());
    }

    @Operation(summary = "Busca um pedido", description = "Busca um pedido pelo id. Caso o usuário não seja um Administrador ou dono do pedido, a requisição será negada.")
    @ApiResponses({
        @ApiResponse(responseCode="200",
            description = "Pedido encontrado com sucesso.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = PedidoDTO.class))),
        @ApiResponse(responseCode="404",
            description = "Pedido não encontrado. Verifique o id do pedido.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class))),
        @ApiResponse(responseCode="403",
            description = "Requisição não permitida. O usuário não é um administrador ou dono do pedido.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> getPedidoById(@Parameter(description = "Id do pedido") @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(pedidoService.getPedido(id));
    }

    @Operation(summary = "Busca todos os pedidos do usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
            description = "Pedidos do usuário encontrados com sucesso",
            content = @Content(mediaType = "application/json", 
                array = @ArraySchema(schema = @Schema(implementation = PedidoDTO.class)))),
        @ApiResponse(responseCode = "404",
            description = "Usuário não possui pedidos.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))        
    })
    @GetMapping("by-user")
    public ResponseEntity<Set<PedidoDTO>> getPedidoByUser() {
        return ResponseEntity.status(HttpStatus.OK).body(pedidoService.getPedidosByUsuario());
    }

    @Operation(summary = "Cancela um pedido")
    @ApiResponses({
        @ApiResponse(responseCode="200",
            description = "Pedido cancelado com sucesso.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespostaAPI.class))),
        @ApiResponse(responseCode="404",
            description = "Pedido não encontrado. Verifique o id do pedido.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class))),
        @ApiResponse(responseCode="403",
            description = "Requisição não permitida. O usuário não é um administrador ou dono do pedido.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @DeleteMapping("/{id}/cancelar")
    public ResponseEntity<RespostaAPI> cancelPedido(@Parameter(description = "Id do pedido") @PathVariable(name = "id") Long pedidoId) {
        pedidoService.cancelPedido(pedidoId);
        return ResponseEntity.status(HttpStatus.OK).body(new RespostaAPI(Instant.now(), "Pedido cancelado com sucesso."));
    }
}
