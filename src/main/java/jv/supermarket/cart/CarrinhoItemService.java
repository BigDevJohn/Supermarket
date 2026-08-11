package jv.supermarket.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jv.supermarket.product.Produto;
import jv.supermarket.product.ProdutoService;
import jv.supermarket.shared.customexception.OutOfStockException;
import jv.supermarket.shared.customexception.ResourceNotFoundException;

@Service
public class CarrinhoItemService {

    @Autowired
    CarrinhoItemRepository carrinhoItemRepository;

    @Autowired
    ProdutoService produtoService;

    @Autowired
    CarrinhoService carrinhoService;

    @Transactional
    public void addItemInCarrinho(Long produtoId, int quantity, Long carrinhoId) {
        Produto produto = produtoService.getProdutoById(produtoId);

        if (!isQuantidadeAllowed(quantity, produto.getEstoque())) {
            throw new OutOfStockException("O estoque do produto é insuficiente para a quantidade requisitada");
        }

        if (!carrinhoService.existsById(carrinhoId)) {
            throw new ResourceNotFoundException("Carrinho não encontrado com o id: " + carrinhoId);
        }
        if (quantity <1) {
            throw new IllegalArgumentException("A quantidade do item deve ser maior que 0");
        }

        Carrinho carrinho = carrinhoService.getCarrinhoById(carrinhoId);
        CarrinhoItem carrinhoItem = carrinho.getItens().stream()
                .filter(item -> item.getProduto().getId().equals(produtoId))
                .findFirst()
                .orElseGet(() -> createNewCarrinhoItem(carrinho, produto, quantity));

        if (carrinhoItem.getId() == null) {
            saveNewCarrinhoItem(carrinho, carrinhoItem);
        } else {
            updateItemQuantidade(carrinhoId, produtoId, quantity);
        }
    }

    private CarrinhoItem createNewCarrinhoItem(Carrinho carrinho, Produto produto, int quantity) {
        CarrinhoItem novoItem = new CarrinhoItem();
        novoItem.setCarrinho(carrinho);
        novoItem.setProduto(produto);
        novoItem.setQuantidade(quantity);
        return novoItem;
    }

    private void saveNewCarrinhoItem(Carrinho carrinho, CarrinhoItem carrinhoItem) {
        carrinhoItemRepository.save(carrinhoItem);
        carrinho.adicionarItem(carrinhoItem);
        carrinhoService.saveCarrinho(carrinho);
    }

    @Transactional
    public void removeItemOfCarrinho(Long carrinhoId, Long produtoId) {
        CarrinhoItem item = getCarrinhoItem(carrinhoId, produtoId);
        Carrinho carrinho = carrinhoService.getCarrinhoById(carrinhoId);

        carrinho.removerItem(item);

        carrinhoService.saveCarrinho(carrinho);

    }

    @Transactional
    public void updateItemQuantidade(Long carrinhoId, Long produtoId, int quantidade) {
        CarrinhoItem item = getCarrinhoItem(carrinhoId, produtoId);

        int estoqueAtual = produtoService.getProdutoById(produtoId).getEstoque();

        if (quantidade < 1) {
            throw new IllegalArgumentException("A quantidade do item deve ser maior que 0");
        }

        if (!isQuantidadeAllowed(quantidade, estoqueAtual)) {
            throw new OutOfStockException("O estoque do produto é insuficiente para a quantidade requisitada");
        }

        
        item.setQuantidade(quantidade);
        carrinhoItemRepository.save(item);

    }

    private CarrinhoItem getCarrinhoItem(Long carrinhoId, Long produtoId) {
        Carrinho carrinho = carrinhoService.getCarrinhoById(carrinhoId);

        return carrinho.getItens().stream().filter(itens -> itens.getProduto().getId().equals(produtoId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Não existe nenhum produto com Id: " + produtoId + " No carrinho com Id: " + carrinhoId));
    }

    private boolean isQuantidadeAllowed(int quantidade, int produtoEstoque) {
        return quantidade <= produtoEstoque;
    }

}