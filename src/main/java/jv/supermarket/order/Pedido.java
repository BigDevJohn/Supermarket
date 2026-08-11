package jv.supermarket.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jv.supermarket.order.enums.PedidoStatus;
import jv.supermarket.user.Usuario;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PedidoStatus status;

    private LocalDateTime data;

    // Endereço (Quem sabe depois)

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Usuario user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pedido")
    private Set<PedidoItem> itens = new HashSet<PedidoItem>();

    public Pedido() {
    }

    public Pedido(PedidoStatus status, LocalDateTime data) {
        this.status = status;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PedidoStatus getStatus() {
        return status;
    }

    public void setStatus(PedidoStatus status) {
        this.status = status;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public Set<PedidoItem> getItens() {
        return itens;
    }

    public void addItem(PedidoItem item) {
        itens.add(item);
        item.setPedido(this);
    }

    public BigDecimal getPrecoTotal() {
        return itens.stream().map(PedidoItem::getPrecoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
