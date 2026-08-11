package jv.supermarket.order;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository  extends JpaRepository<Pedido, Long>{

    Set<Pedido> findByUserId(Long userId);
    
}
