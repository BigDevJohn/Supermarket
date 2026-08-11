package jv.supermarket.cart;

import java.math.BigDecimal;
import java.util.Set;

public class CarrinhoDTO {

    private Set<CarrinhoItemDTO> items;
    private BigDecimal precoTotal;
    
    public Set<CarrinhoItemDTO> getItems() {
        return items;
    }
    public void setItems(Set<CarrinhoItemDTO> items) {
        this.items = items;
    }
    public BigDecimal getPrecoTotal() {
        return precoTotal;
    }
    public void setPrecoTotal(BigDecimal precoTotal) {
        this.precoTotal = precoTotal;
    }

    



}
