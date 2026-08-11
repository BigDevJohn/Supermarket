package jv.supermarket.product;

import java.math.BigDecimal;
import java.util.List;

public class ProdutoRequestDTO {
    private String nome;
    private String marca;
    private BigDecimal preco;
    private String descricao;
    private int estoque;
    private List<String> categorias;

    public ProdutoRequestDTO(String nome, String marca, BigDecimal preco, int estoque, String descricao,
            List<String> categorias) {
        this.nome = nome;
        this.marca = marca;
        this.preco = preco;
        this.descricao = descricao;
        this.estoque = estoque;
        this.categorias = categorias;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<String> categorias) {
        this.categorias = categorias;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

}
