package jv.supermarket.config;

import java.math.BigDecimal;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import jv.supermarket.cart.Carrinho;
import jv.supermarket.cart.CarrinhoItemService;
import jv.supermarket.cart.CarrinhoService;
import jv.supermarket.category.Categoria;
import jv.supermarket.category.CategoriaService;
import jv.supermarket.order.PedidoService;
import jv.supermarket.product.Produto;
import jv.supermarket.product.ProdutoRequestDTO;
import jv.supermarket.product.ProdutoService;
import jv.supermarket.user.Usuario;
import jv.supermarket.user.UsuarioService;

@Configuration
@Profile("test")
@Order(2)
public class InicializadorBDTest implements CommandLineRunner {

@Autowired
    private ProdutoService produtoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CarrinhoItemService itemService;

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private UsuarioService userService;

    @SuppressWarnings("unused")
    @Autowired
    private PedidoService pedidoService;

    @Override
    public void run(String... args) throws Exception {

        Usuario funcionario = new Usuario("kleber", "kleber@gmail.com", "123456");
        Usuario cliente = new Usuario("joao", "joao@gmail.com", "123456");

        funcionario = userService.saveFuncionario(funcionario);
        cliente = userService.saveCliente(cliente);

        // Criação de Categorias
        Categoria c1 = new Categoria("Eletrônicos");
        Categoria c2 = new Categoria("Mobília");
        Categoria c3 = new Categoria("Smartphones");
        Categoria c4 = new Categoria("Cozinha");

        // Salva Categorias
        categoriaService.saveCategoria(c4);
        categoriaService.saveCategoria(c3);
        categoriaService.saveCategoria(c2);
        categoriaService.saveCategoria(c1);

        // Criação e associação de Produtos
        Produto p1 = produtoService.saveProduto(new ProdutoRequestDTO("Smartphone", "Samsung", new BigDecimal(3000), 20,
                "O melhor da Samsung", Arrays.asList("Smartphones", "Eletrônicos")));

        produtoService.saveProduto(new ProdutoRequestDTO("Smartphone", "Xiaomi", new BigDecimal(3200), 32,
                "O mundo todo no seu bolso", Arrays.asList("Smartphones", "Eletrônicos")));

        produtoService.saveProduto(new ProdutoRequestDTO("Geladeira", "Samsung", new BigDecimal(4000), 10,
                "Gela que é uma beleza!", Arrays.asList("Cozinha", "Eletrônicos")));

        Produto p4 = produtoService.saveProduto(new ProdutoRequestDTO("Cama de Casal", "Plumatex", new BigDecimal(1500),
                5, "O que há de conforto para você", Arrays.asList("Mobília")));

        Carrinho carrinho = carrinhoService.getCarrinhoById(cliente.getId());

        itemService.addItemInCarrinho(p1.getId(), 2, carrinho.getId());
        itemService.addItemInCarrinho(p4.getId(), 1, carrinho.getId());
        itemService.removeItemOfCarrinho(carrinho.getId(), p4.getId());
        itemService.updateItemQuantidade(carrinho.getId(), p1.getId(), 3);


    }

}
