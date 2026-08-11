package jv.supermarket.order;

import java.math.BigDecimal;

import jv.supermarket.product.ProdutoDTO;

public class PedidoItemDTO {

    private ProdutoDTO produto;
    private int quantidade;
    private BigDecimal precoTotal;

    public PedidoItemDTO() {
    }

    public PedidoItemDTO(ProdutoDTO produto, int quantidade, BigDecimal precoTotal) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoTotal = precoTotal;
    }

    public ProdutoDTO getProduto() {
        return produto;
    }

    public void setProduto(ProdutoDTO produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(BigDecimal precoTotal) {
        this.precoTotal = precoTotal;
    }

}
