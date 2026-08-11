package jv.supermarket.cart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CarrinhoItemRepository extends JpaRepository<CarrinhoItem, Long> {
    List<CarrinhoItem> findByCarrinho(Carrinho carrinho);

    void deleteAllByCarrinhoId(Long carrinhoId);
}
