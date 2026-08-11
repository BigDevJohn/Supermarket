package jv.supermarket.product;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jv.supermarket.shared.Mensagem;
import jv.supermarket.shared.RespostaAPI;
import jv.supermarket.shared.customexception.ResourceNotFoundException;

@RestController
@RequestMapping("/supermarket/produto")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    
    

    @Operation(summary = "Salva um novo produto", description = "Recebe uma DTO de produto, que possui em conjunto a(s) categoria(s) em que ele fará parte ( a categoria já deve existir no sistema anteriormente), lembrando que, um produto com o mesmo nome e marca não pode existir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Produto com mesmo com o mesmo nome e marca já existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class))),

            @ApiResponse(responseCode = "404", description = "Não existe uma categoria com os nomes informados", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class))),

            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class)))
    })
 
    @PostMapping("/save")
    public ResponseEntity<Produto> saveProduto(@RequestBody @Valid ProdutoRequestDTO produto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.saveProduto(produto));

    }

    @Operation(summary = "Busca um produto pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Nenhum produto foi encontrado com o id requisitado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class))),

            @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Produto> getProdutoById(
            @Parameter(description = "Id do produto a ser buscado. Exemplo: 1") @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(produtoService.getProdutoById(id));
    }

    @Operation(summary = "Busca todos os produtos existentes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Produto.class))))
    })
 
    @GetMapping("/all")
    public ResponseEntity<List<Produto>> getAllProdutos() {
        return ResponseEntity.status(HttpStatus.OK).body(produtoService.getAllProdutos());
    }
    @Operation(summary = "Busca todos os produtos pelo nome", description = "Busca todos os produtos pelo nome passado por um parâmetro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Produto.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum produto foi encontrado com o nome requisitado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class)))
    })
    @GetMapping("/by-nome")
    public ResponseEntity<List<Produto>> getProdutosByNome(@Parameter(description = "Nome do produto a ser buscado") @RequestParam String nome) {
 
        List<Produto> produtos = produtoService.getProdutosByNome(nome);
        if (produtos.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum produto foi encontrado com o Nome: "+ nome);
        }
        return ResponseEntity.status(HttpStatus.OK).body(produtos);
    }
    @Operation(summary = "Busca todos os produtos pela marca", description = "Busca todos os produtos pela marca passada por um parâmetro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Produto.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum produto foi encontrado com a marca requisitado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class)))
    })
    @GetMapping("/by-marca")
    public ResponseEntity<List<Produto>> getProdutosByMarca(
            @Parameter(description = "Marca do produto a ser buscado") @RequestParam String marca) {
 
        List<Produto> produtos = produtoService.getProdutosByMarca(marca);
        if (produtos.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum produto foi encontrado com a Marca: " + marca);
        }
        return ResponseEntity.status(HttpStatus.OK).body(produtos);
    }


    @Operation(summary = "Busca um produto pela marca e nome", description = "Busca um produto pela marca e nome passados por parâmetros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Produto.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum produto foi encontrado com a marca requisitado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class)))
    })
    @GetMapping("/by-marca-and-nome")
    public ResponseEntity<Produto> getProdutosByMarcaAndNome(
            @Parameter(description = "Nome da marca") @RequestParam String marca,
            @Parameter(description = "Nome do produto") @RequestParam String nome) {
        Produto produto = produtoService.getProdutoByMarcaAndNome(marca, nome);

        return ResponseEntity.status(HttpStatus.OK).body(produto);
    }

    @Operation(summary = "Busca todos os produtos pela categoria", description = "Busca todos os produtos pelo nome da categoria passado por um parâmetro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Produto.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum produto foi encontrado com a marca requisitado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class))),
            @ApiResponse(responseCode = "404", description = "Categoria informada não existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class)))
    })
    @GetMapping("/by-categoria-nome")
    public ResponseEntity<List<Produto>> getProdutosByCategoriaNome(
            @Parameter(description = "Nome da categoria que será buscado os produtos") @RequestParam String nome) {
        List<Produto> produtos = produtoService.getProdutosByCategoriaNome(nome);
 
        if (produtos.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum produto foi encontrado com a Categoria: " + nome);
        }
        return ResponseEntity.status(HttpStatus.OK).body(produtos);
    }

    

    

    @Operation(summary = "Atualiza um produto", description = "Atualiza um produto, recebendo o id do que será atualizado, e um json com as novas informações")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Produto.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum produto foi encontrado com o id informado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class)))
    })
 
    @PutMapping("/{id}")
    public ResponseEntity<Produto> updateProduto(@PathVariable Long id, @RequestBody @Valid Produto produto) {

        return ResponseEntity.status(HttpStatus.OK).body(produtoService.updateProduto(produto, id));
    }

    

    

    @Operation(summary = "Deleta um produto", description = "Deleta um produto, pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto deletado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespostaAPI.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum produto foi encontrado com o id informado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class)))
    })
 
    @DeleteMapping("/{id}")
    public ResponseEntity<RespostaAPI> deleteProduto(@PathVariable Long id) {
        produtoService.deleteProdutoById(id);

        return ResponseEntity.status(HttpStatus.OK).body(new RespostaAPI(Instant.now(),"Produto deletado com sucesso"));
    } 

    @Operation(summary = "Disponibiliza um produto", description = "Disponibiliza um produto, pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto disponibilizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespostaAPI.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum produto foi encontrado com o id informado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class))),
            @ApiResponse(responseCode = "409", description = "Produto já estava disponivel previamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class)))
    })
    @PutMapping("/{id}/disponibilizar")
    public ResponseEntity<RespostaAPI> makeProdutoAvaliable(@PathVariable Long id) {
        produtoService.tornarDisponivel(id);
        return ResponseEntity.ok(new RespostaAPI(Instant.now(), "Produto disponibilizado com sucesso."));
    }

    @Operation(summary = "Torna um produto indisponivel", description = "Torna um produto indisponivel, pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto indisponibilizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespostaAPI.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum produto foi encontrado com o id informado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class))),
            @ApiResponse(responseCode = "409", description = "Produto já estava indisponivel previamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class)))
    })
    @PutMapping("/{id}/indisponibilizar")
    public ResponseEntity<RespostaAPI> makeProdutoUnavaliable(@PathVariable Long id) {
        produtoService.tornarIndisponivel(id);
        return ResponseEntity.ok(new RespostaAPI(Instant.now(), "Produto indisponibilizado com sucesso."));
    }

    @Operation(summary = "Aumenta o estoque de um produto", description = "Aumenta o estoque de um produto, com base em um parâmetro informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque do produto atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum produto foi encontrado com o id informado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class)))
    })
    @PutMapping("/{id}/addEstoque")
    public ResponseEntity<Produto> increaseEstoque(@PathVariable Long id,
            @Parameter(description = "Quantidade a somar no estoque do produto") @RequestParam int quantidade) {
 
        return ResponseEntity.status(HttpStatus.OK).body(produtoService.addProdutoEstoque(quantidade, id));
    }

}
