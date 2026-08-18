package jv.supermarket.order;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Set<Order> findByUserId(Long userId);

}
