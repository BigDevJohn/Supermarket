package jv.supermarket.product;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import jv.supermarket.category.Categoria;
import jv.supermarket.category.CategoriaRepository;
import jv.supermarket.shared.customexception.AlreadyExistException;
import jv.supermarket.shared.customexception.ResourceNotFoundException;
import jv.supermarket.user.Usuario;
import jv.supermarket.user.UsuarioService;

@Service
public class ProdutoService {

    @Autowired
    ProdutoRepository pr;

    @Autowired
    CategoriaRepository cr;

    @Autowired
    UsuarioService userService;

    private Boolean produtoExist(String nome, String marca) {
        return pr.existsByNomeAndMarca(nome, marca);
    }

    public List<Produto> getAllProdutos() {
        if (isCliente()) {
            pr.findAllByDisponivel(true);
        }
        return pr.findAll();
    }

    @Transactional
    public Produto saveProduto(ProdutoRequestDTO dto) {
        if (produtoExist(dto.getNome(), dto.getMarca())) {
            throw new AlreadyExistException("Já existe um produto com este nome e marca.");
        }
        Produto produto = new Produto();
        BeanUtils.copyProperties(dto, produto);

        produto = addCategoriasEmProduto(produto, dto.getCategorias());

        return pr.save(produto);
    }

    public Produto addCategoriasEmProduto(Produto produto, List<String> categorias) {
        for (String categoriaNome : categorias) {
            if (cr.existsByNome(categoriaNome)) {
                Categoria categoria = cr.findByNome(categoriaNome);
                produto.getCategorias().add(categoria);
            } else {
                throw new ResourceNotFoundException("A categoria com o nome: " + categoriaNome + " não foi encontrada");
            }
        }
        return produto;
    }

    public Produto getProdutoById(Long id) {
        // if (isCliente()) {
        // return pr.findByIdAndDisponivel(id, true);
        // }
        return pr.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto com o id: " + id + " não encontrado"));
    }

    public Produto updateProduto(Produto produto, Long id) {
        if (pr.existsById(id)) {
            Produto produtoSalvo = getProdutoById(id);

            produtoSalvo.setNome(produto.getNome());
            produtoSalvo.setMarca(produto.getMarca());
            produtoSalvo.setPreco(produto.getPreco());
            produtoSalvo.setEstoque(produto.getEstoque());
            produtoSalvo.setDescricao(produto.getDescricao());

            return pr.save(produtoSalvo);
        }
        throw new ResourceNotFoundException("Produto com o id: " + id + " não encontrado");

    }

    public void deleteProdutoById(Long id) {
        if (pr.existsById(id)) {
            try {
                pr.deleteById(id);
            } catch (DataIntegrityViolationException e) {
                tornarIndisponivel(id);
            }

        } else {
            throw new ResourceNotFoundException("Produto com o id: " + id + " não encontrado");
        }

    }
    
    public void tornarIndisponivel(Long id) {
        if (pr.existsById(id)) {
            Produto produto = pr.findById(id).get();
            if (!produto.isDisponivel()) {
                throw new IllegalStateException("O produto já estava como indisponivel previamente.");
            }
            produto.setDisponivel(false);
            pr.save(produto);
        }else{
            throw new ResourceNotFoundException("Produto com o id: " + id + " não encontrado");
        }
    }
    public void tornarDisponivel(Long id) {
        if (pr.existsById(id)) {
            Produto produto = pr.findById(id).get();
            if (produto.isDisponivel()) {
                throw new IllegalStateException("O produto já estava como disponivel previamente.");
            }
            
            produto.setDisponivel(true);
            pr.save(produto);
        }else{
            throw new ResourceNotFoundException("Produto com o id: " + id + " não encontrado");
        }
        
    }

    public Produto addProdutoEstoque(int quantidade, Long id) {
        if (pr.existsById(id)) {
            Produto produto = getProdutoById(id);
            produto.setEstoque(produto.getEstoque() + quantidade);
            return pr.save(produto);
        }
        throw new ResourceNotFoundException("Produto com o id: " + id + " não encontrado");

    }

    public List<Produto> getProdutosByNome(String nome) {
        if (isCliente()) {
            pr.findByNomeContainingIgnoreCaseAndDisponivel(nome, true);
        }
        return pr.findByNomeContainingIgnoreCase(nome);
    }

    public List<Produto> getProdutosByMarca(String marca) {
        if (isCliente()) {
            return pr.findByMarcaContainingIgnoreCaseAndDisponivel(marca, true);
        }
        return pr.findByMarcaContainingIgnoreCase(marca);
    }
        
    public Produto getProdutoByMarcaAndNome(String marca, String nome) {
        if (produtoExist(nome, marca)) {
            if (isCliente()) {
                return pr.findByMarcaAndNomeAndDisponivel(marca, nome, true);
            }
            return pr.findByMarcaAndNome(marca, nome);
        }

        throw new ResourceNotFoundException(
                "Nenhum produto foi encontrado com a Marca: " + marca + " e o Nome: " + nome);
    }

    public List<Produto> getProdutosByCategoriaNome(String nome) {

            List<Produto> produtos;
            if (isCliente()) {
                produtos = pr.findByCategoriaNomeContainingAndDisponivel(nome);
            } else {
                produtos = pr.findByCategoriaNomeContaining(nome);
            }

            if (produtos.isEmpty()) {
                throw new ResourceNotFoundException("Não existem produtos pertencentes a categoria com o nome:" + nome);
            }
            return produtos;

    }

    public ProdutoDTO convertToDTO(Produto produto) {
        ProdutoDTO produtoDTO = new ProdutoDTO();

        BeanUtils.copyProperties(produto, produtoDTO);

        return produtoDTO;
    }

    public boolean existById(Long produtoId) {
        return pr.existsById(produtoId);
    }

    private boolean isCliente() {
        Usuario user = userService.getLoggedUsuario();
        boolean isCliente = user.getRoles().stream()
                .anyMatch(role -> role.getNome().equals("ROLE_CLIENTE"));
        return isCliente;
    }

}
