package jv.supermarket.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import jv.supermarket.order.enums.PedidoStatus;

public class PedidoDTO {
    private Set<PedidoItemDTO> Items;
    private BigDecimal precoTotal;
    private PedidoStatus status;
    private LocalDateTime data;

    public PedidoDTO() {
    }

    public PedidoDTO(Set<PedidoItemDTO> items, BigDecimal precoTotal, PedidoStatus status, LocalDateTime data) {
        Items = items;
        this.precoTotal = precoTotal;
        this.status = status;
        this.data = data;
    }

    public Set<PedidoItemDTO> getItems() {
        return Items;
    }

    public void setItems(Set<PedidoItemDTO> items) {
        Items = items;
    }

    public BigDecimal getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(BigDecimal precoTotal) {
        this.precoTotal = precoTotal;
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
    
    

}