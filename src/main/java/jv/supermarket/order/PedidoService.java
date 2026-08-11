package jv.supermarket.order;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jv.supermarket.cart.Carrinho;
import jv.supermarket.cart.CarrinhoItem;
import jv.supermarket.cart.CarrinhoService;
import jv.supermarket.order.enums.PedidoStatus;
import jv.supermarket.product.Produto;
import jv.supermarket.product.ProdutoRepository;
import jv.supermarket.product.ProdutoService;
import jv.supermarket.shared.customexception.OutOfStockException;
import jv.supermarket.shared.customexception.ResourceNotFoundException;
import jv.supermarket.user.Role;
import jv.supermarket.user.RoleRepository;
import jv.supermarket.user.Usuario;
import jv.supermarket.user.UsuarioService;

@Service
public class PedidoService {

    @Autowired
    CarrinhoService carrinhoService;

    @Autowired
    PedidoItemRepository itemRepo;

    @Autowired
    PedidoRepository pedidoRepo;

    @Autowired
    UsuarioService userService;

    @Autowired
    ProdutoRepository produtoRepo;

    @Autowired
    ProdutoService produtoService;

    @Autowired
    RoleRepository roleRepo;

    @Transactional
    public Pedido createPedido() {
        Usuario user = userService.getLoggedUsuario();
        Carrinho carrinho = carrinhoService.getCarrinhoById(user.getId());
        if (carrinho.getItens().isEmpty()) {
            throw new ResourceNotFoundException(
                    "Carrinho vazio. Adicione itens a ele primeiro antes de tentar realizar um pedido");
        }

        checkEstoqueCarrinho(carrinho);
        Pedido pedido = new Pedido();

        Set<PedidoItem> itens = convertCarrinhoItensToPedidoItens(carrinho.getItens());

        for (PedidoItem pedidoItem : itens) {
            pedido.addItem(pedidoItem);
        }
        pedido.setUser(user);
        pedido.setStatus(PedidoStatus.ESPERANDO_PAGAMENTO);
        pedido.setData(LocalDateTime.now());

        carrinhoService.clearCarrinho(carrinho.getId());

        return pedidoRepo.save(pedido);

    }

    @Transactional
    public PedidoDTO getPedido(Long id) {
        Usuario user = userService.getLoggedUsuario();
        Pedido pedido = getPedidoById(id);

        for (Role role : user.getRoles()) {
            if (role.getNome().equals("ROLE_ADMIN")) {
                PedidoDTO dto = convertPedidoToDTO(pedido);
                return dto;
            } else if (role.getNome().equals("ROLE_CLIENTE")) {
                if (pedido.getUser().getId().equals(user.getId())) {
                    PedidoDTO dto = convertPedidoToDTO(pedido);
                    return dto;
                }
            }
        }
        throw new AccessDeniedException("Pedido não encontrado para este cliente");
    }

    public Set<PedidoDTO> getPedidosByUsuario() {
        Set<Pedido> pedidos = pedidoRepo.findByUserId(userService.getLoggedUsuario().getId());
        if (pedidos == null || pedidos.size() == 0) {
            throw new ResourceNotFoundException("O usuário não possui nenhum pedido");
        }
        return pedidos.stream()
                .map(pedido -> convertPedidoToDTO(pedido))
                .collect(Collectors.toSet());
    }

    public Pedido getPedidoById(Long id) {
        return pedidoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o Id: " + id));
    }

    public Pedido cancelPedido(Long pedidoId) {
        Usuario user = userService.getLoggedUsuario();
        Pedido pedido = getPedidoById(pedidoId);

        for (Role role : user.getRoles()) {
            if (role.getNome().equals("ROLE_ADMIN")) {
                pedido.setStatus(PedidoStatus.CANCELADO);
                return pedidoRepo.save(pedido);
            } else if (role.getNome().equals("ROLE_CLIENTE")) {
                if (pedido.getUser().getId().equals(user.getId())) {
                    pedido.setStatus(PedidoStatus.CANCELADO);
                    return pedidoRepo.save(pedido);
                }
            }
        }
        throw new AccessDeniedException("Pedido não encontrado para este cliente");

    }

    private void checkEstoqueCarrinho(Carrinho carrinho) {
        for (CarrinhoItem item : carrinho.getItens()) {
            Produto produto = item.getProduto();
            int estoqueDisponivel = produto.getEstoque();
            if (!isSufficientEstoque(item.getQuantidade(), estoqueDisponivel)) {
                int quantidadeExcedente = item.getQuantidade() - estoqueDisponivel;
                throw new OutOfStockException(
                        "O pedido do produto " + produto.getNome() + " da marca: " + produto.getMarca()
                                + " excede em " + quantidadeExcedente
                                + " o estoque disponível, por favor, diminua a quantidade para finalizar a compra");
            }
        }

    }

    private Set<PedidoItem> convertCarrinhoItensToPedidoItens(Set<CarrinhoItem> CarItens) {
        Set<PedidoItem> itens = new HashSet<PedidoItem>();

        for (CarrinhoItem carItem : CarItens) {
            PedidoItem item = new PedidoItem();
            Produto produto = carItem.getProduto();
            item.setProduto(produto);
            item.setQuantidade(carItem.getQuantidade());

            produto.setEstoque(produto.getEstoque() - item.getQuantidade());

            produtoRepo.save(produto);

            itens.add(item);
        }

        return itens;
    }

    private boolean isSufficientEstoque(int quantidade, int produtoEstoque) {
        return quantidade <= produtoEstoque;
    }

    private PedidoItemDTO convertItemToDTO(PedidoItem item) {
        PedidoItemDTO dto = new PedidoItemDTO();

        dto.setProduto(produtoService.convertToDTO(item.getProduto()));
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoTotal(item.getPrecoTotal());

        return dto;
    }

    private PedidoDTO convertPedidoToDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();

        dto.setData(pedido.getData());
        dto.setStatus(pedido.getStatus());
        dto.setPrecoTotal(pedido.getPrecoTotal());
        dto.setItems(pedido.getItens().stream()
                .map(item -> convertItemToDTO(item))
                .collect(Collectors.toSet()));

        return dto;
    }
}