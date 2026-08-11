package jv.supermarket.category;

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

@RestController
@RequestMapping("/supermarket/categoria")
public class CategoriaController {

    @Autowired
    CategoriaService cs;

    @Operation(summary = "Cria uma categoria")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
            description= "Categoria criada com sucesso", 
            content = @Content(mediaType ="application/json", 
                schema = @Schema(implementation = Categoria.class))),
        @ApiResponse(responseCode = "409",
            description= "Já existe uma categoria com esse nome", 
            content = @Content(mediaType ="application/json", 
                schema = @Schema(implementation = Mensagem.class)))
    })
    @PostMapping("/save")
    public ResponseEntity<Categoria> saveCategoria(@RequestBody  @Valid Categoria categoria) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cs.saveCategoria(categoria));

    }

    @Operation(summary = "Busca uma categoria", description = "Busca uma categoria pelo Id")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
            description= "Categoria encontrada com sucesso", 
            content = @Content(mediaType ="application/json", 
                schema = @Schema(implementation = Categoria.class))),
        @ApiResponse(responseCode = "404",
            description= "Não foi encontrada uma categoria com esse Id", 
            content = @Content(mediaType ="application/json", 
                schema = @Schema(implementation = Mensagem.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getCategoriaById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(cs.getCategoriaById(id));
    }

    @Operation(summary = "Busca uma categoria", description = "Busca uma categoria pelo nome")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
            description= "Categoria encontrada com sucesso", 
            content = @Content(mediaType ="application/json", 
                schema = @Schema(implementation = Categoria.class))),
        @ApiResponse(responseCode = "404",
            description= "Não foi encontrada uma categoria com esse nome", 
            content = @Content(mediaType ="application/json", 
                schema = @Schema(implementation = Mensagem.class)))
    })
    @GetMapping("/by-nome")
    public ResponseEntity<Categoria> getCategoriaByNome(@Parameter(description = "Nome da categoria a ser buscada")@RequestParam String nome) {
        return ResponseEntity.status(HttpStatus.OK).body(cs.getCategoriaByNome(nome));
    }

    @Operation(summary = "Busca todas as categoria")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
            description= "Categorias encontradas com sucesso", 
            content = @Content(mediaType ="application/json", 
                array = @ArraySchema( schema = @Schema(implementation = Categoria.class)))),
        @ApiResponse(responseCode = "200",
            description= "Não foi encontrada nenhuma categoria", 
            content = @Content(mediaType ="application/json", 
                 schema = @Schema(implementation = Mensagem.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<Categoria>> getAllCategorias() {
        return ResponseEntity.status(HttpStatus.OK).body(cs.getAllCategorias());
    }

    @Operation(summary = "Deleta uma categoria", description = "Deleta uma categoria pelo Id. Caso um produto só tenha essa categoria, ele é excluido também")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
            description= "Categoria deletada com sucesso", 
            content = @Content(mediaType ="application/json", 
                schema = @Schema(implementation = Categoria.class))),
        @ApiResponse(responseCode = "404",
            description= "Não foi encontrada uma categoria com esse Id", 
            content = @Content(mediaType ="application/json", 
                schema = @Schema(implementation = Mensagem.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<RespostaAPI> deleteCategoria(@PathVariable Long id) {
        cs.deleteCategoriaById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new RespostaAPI(Instant.now(),"Categoria deletada com sucess."));
    }

    @Operation(summary = "Atualiza uma categoria", description = "Atualiza uma categoria pelo id, recebendo as novas informações pela requisição")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
            description= "Categoria atualizada com sucesso", 
            content = @Content(mediaType ="application/json", 
                schema = @Schema(implementation = Categoria.class))),
        @ApiResponse(responseCode = "404",
            description= "Não foi encontrada uma categoria com esse id", 
            content = @Content(mediaType ="application/json", 
                schema = @Schema(implementation = Mensagem.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> updateCategoria(@PathVariable Long id, @RequestBody @Valid Categoria categoriaAtualizada) {
        return ResponseEntity.status(HttpStatus.OK).body(cs.updateCategoria(id, categoriaAtualizada));
    }

}
