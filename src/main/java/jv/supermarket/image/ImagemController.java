package jv.supermarket.image;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
@RequestMapping("/supermarket/imagem")
public class ImagemController {

    @Autowired
    ImagemService is;

    @Operation(summary = "Cria uma imagem", description = "Cria uma imagem, recebendo o arquivo dela e o id do produto que ela será relacionada")
    @ApiResponses({
        @ApiResponse(responseCode = "201",
            description = "Imagem criada com sucesso",
            content =  @Content(mediaType = "application/json",
                schema = @Schema(implementation = ImagemDTO.class))),
        @ApiResponse(responseCode = "404",
            description = "Nenhum produto foi encontrado com o id informado",
            content =  @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class))),
        @ApiResponse(responseCode = "500",
            description = "Ocorreu um erro interno ao criar as imagens",
            content =  @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @PostMapping("/upload")
    public ResponseEntity<List<ImagemDTO>> uploadImagem(@RequestParam List<MultipartFile> arquivos, @RequestParam Long produtoId){
       List<ImagemDTO> imagensSalvas = is.saveImagens(produtoId, arquivos);
        return ResponseEntity.status(HttpStatus.CREATED).body(imagensSalvas);
    }

    @Operation(summary = "Busca as imagens de um produto", description = "Busca todas as imagens de um produto pelo seu id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", 
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation = ImagemDTO.class)))),
        @ApiResponse(responseCode = "404",
            description = "Nenhum produto foi encontrado com o id informado",
            content =  @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Mensagem.class))),
    })
    @GetMapping("/by-produto-id/{produtoId}")
    public ResponseEntity<List<ImagemDTO>> getImagemByProduto(@Parameter(description = "Id do produto a ter suas imagens buscadas") @PathVariable Long produtoId){
        return ResponseEntity.status(HttpStatus.OK).body(is.getImagensByProdutoId(produtoId));
    }
    
    @Operation(summary = "Faz o download do arquivo de uma imagem", description = "Faz o download do arquivo de uma imagem com base no id")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
            description = "Imagem baixada com sucesso",
            content =  @Content(mediaType = "application/octet-stream")),
        @ApiResponse(responseCode = "404",
            description = "Nenhuma imagem foi encontrada com o id informado",
            content =  @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadImagem(@Parameter(description = "Id da imagem") @PathVariable Long id) throws SQLException{
        Imagem imagem = is.getImagemById(id);
        ByteArrayResource imagemBytes = new ByteArrayResource(imagem.getImagem().getBytes(1, (int) imagem.getImagem().length())); // Forma de pegar os bytes da imagem

        //@formatter:off
        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.parseMediaType(imagem.getTipoArquivo()))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attechment; filename:\""+imagem.getNomeArquivo()+"\"")
                            .body(imagemBytes);
        //@formatter:on
    }

    @Operation(summary = "Atualiza uma imagem")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
            description = "Imagem atualizada com sucesso",
            content =  @Content(mediaType = "application/json",
                schema = @Schema(implementation = Imagem.class))),
        @ApiResponse(responseCode = "404",
            description = "Nenhuma imagem foi encontrada com o id informado",
            content =  @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class))),
    })
    @PutMapping("/{id}")
    public ResponseEntity<Imagem> updateImage(@Parameter(description = "Id da imagem a ser atualizada") @PathVariable Long id, @RequestParam MultipartFile arquivos){
        is.updateImagem(arquivos, id);
        return ResponseEntity.status(HttpStatus.OK).body(is.getImagemById(id));
    }

    @Operation(summary = "Deleta uma imagem")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
            description = "Imagem deletada com sucesso",
            content =  @Content(mediaType = "application/json",
                schema = @Schema(implementation = RespostaAPI.class))),
        @ApiResponse(responseCode = "404",
        description = "Nenhuma imagem foi encontrada com o id informado",
            content =  @Content(mediaType = "application/json",
                schema = @Schema(implementation = Mensagem.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<RespostaAPI> deleteImage(@PathVariable Long id){
        is.deleteImagem(id);
        return ResponseEntity.status(HttpStatus.OK).body(new RespostaAPI(Instant.now(),"Imagem deletada com sucesso"));
    }

}
