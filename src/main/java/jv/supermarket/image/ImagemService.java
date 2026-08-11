package jv.supermarket.image;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jv.supermarket.product.Produto;
import jv.supermarket.product.ProdutoService;
import jv.supermarket.shared.customexception.ImageSavingException;
import jv.supermarket.shared.customexception.ResourceNotFoundException;

@Service
public class ImagemService {

    @Autowired
    private ImagemRepository ir;
    @Autowired
    private ProdutoService pr;

    public Imagem getImagemById(Long id) {
        return ir.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma imagem encontrada com o id: " + id));
    }

    public List<ImagemDTO> getImagensByProdutoId(Long produtoId) {
        List<Imagem> imagens = ir.findByProdutoId(produtoId);
        if (imagens == null) {
            throw new ResourceNotFoundException("Nenhuma imagem encontrada para o produto de id: " + produtoId);
        }
        List<ImagemDTO> imagensDTO = imagens.stream().map(imagem -> convertToDTO(imagem)).collect(Collectors.toList());

        return imagensDTO;
    }

    public List<ImagemDTO> saveImagens(Long produtoId, List<MultipartFile> arquivos) {
        Produto produto = pr.getProdutoById(produtoId);

        List<ImagemDTO> imagensSalvas = new ArrayList<ImagemDTO>();
        for (MultipartFile arquivo : arquivos) {
            try {
                Imagem imagem = new Imagem();

                imagem.setNomeArquivo(arquivo.getOriginalFilename());
                imagem.setTipoArquivo(arquivo.getContentType());

                imagem.setImagem(new SerialBlob(arquivo.getBytes()));
                imagem.setProduto(produto);

                Imagem imagemSalva = ir.save(imagem);

                imagemSalva.setUrlDownload("/supermarket/imagem/" + imagemSalva.getId() + "/download");
                ir.save(imagemSalva);

                imagensSalvas.add(convertToDTO(imagemSalva));

            } catch (IOException | SQLException e) {
                throw new ImageSavingException("Erro ao salvar as imagens informadas");
            }
        }
        return imagensSalvas;
    }

    public void updateImagem(MultipartFile file, Long imagemId) {
        Imagem imagem = getImagemById(imagemId);
        try {
            imagem.setNomeArquivo(file.getOriginalFilename());
            imagem.setTipoArquivo(file.getContentType());
            imagem.setImagem(new SerialBlob(file.getBytes()));
            ir.save(imagem);
        } catch (IOException | SQLException e) {
            throw new ImageSavingException("Erro ao atualizar as imagens solicitadas");
        }

    }

    public void deleteImagem(Long imagemId) {
        if (ir.existsById(imagemId)) {
            ir.deleteById(imagemId);
        }else{
            throw new ResourceNotFoundException("Imagem com o id: " + imagemId + " não encontrada");
        }
        
    }

    private ImagemDTO convertToDTO(Imagem imagem) {
        ImagemDTO dto = new ImagemDTO();
        BeanUtils.copyProperties(imagem, dto);

        return dto;
    }
}
