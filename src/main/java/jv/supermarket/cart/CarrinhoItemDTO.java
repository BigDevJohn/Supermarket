package jv.supermarket.cart;

import java.math.BigDecimal;

import jv.supermarket.product.ProdutoDTO;

public class CarrinhoItemDTO {

    private ProdutoDTO produto;

    private int quantidade;

    private BigDecimal subTotal;

    public CarrinhoItemDTO() {
    }

    public CarrinhoItemDTO(ProdutoDTO produto, int quantidade, BigDecimal subTotal) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.subTotal = subTotal;
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

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

}
