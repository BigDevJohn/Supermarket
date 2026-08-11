package jv.supermarket.order.enums;

public enum PedidoStatus {
    ESPERANDO_PAGAMENTO("Esperando pagamento"),
    PREPARANDO("Preparando"),
    ENVIADO("Enviado"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado");

    private final String descricao;

    PedidoStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
