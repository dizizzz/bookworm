package mate.academy.springboot.repository.order;

import java.util.List;
import java.util.Optional;
import mate.academy.springboot.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdAndUserId(@Param("userId") Long userId,
                                     @Param("orderId") Long orderId);

    List<Order> findAllByUserId(@Param("userId") Long userId);
}
