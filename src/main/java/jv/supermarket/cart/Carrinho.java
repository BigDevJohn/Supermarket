package jv.supermarket.cart;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jv.supermarket.user.Usuario;

@Entity
public class Carrinho {

    @Id
    private Long id;

    @JsonIgnore
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Usuario user;

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CarrinhoItem> itens = new HashSet<CarrinhoItem>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<CarrinhoItem> getItens() {
        return itens;
    }

    public void adicionarItem(CarrinhoItem item) {
        itens.add(item);
        item.setCarrinho(this);
    }

    public void removerItem(CarrinhoItem item) {
        itens.remove(item);
        item.setCarrinho(null);
    }

    public BigDecimal getValorTotal() {
        return this.itens.stream().map(CarrinhoItem::getSubTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public void limparCarrinho() {
        this.itens.clear();
    }

}
