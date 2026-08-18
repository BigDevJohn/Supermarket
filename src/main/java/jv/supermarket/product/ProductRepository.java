package jv.supermarket.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Boolean existsByNameAndBrand(String name, String brand);

    boolean existsByIdAndAvailable(Long id, boolean available);

    Product findByIdAndAvailable(Long id, boolean available);

    List<Product> findAllByAvailable(boolean available);

    List<Product> findByName(String name);

    List<Product> findByNameAndAvailable(String name, boolean available);

    List<Product> findByBrand(String brand);

    List<Product> findByBrandAndAvailable(String brand, boolean available);

    Product findByBrandAndName(String brand, String name);

    Product findByBrandAndNameAndAvailable(String brand, String name, boolean available);

    @Query("SELECT p FROM Product p JOIN FETCH p.categories c WHERE c.name = :name")
    List<Product> findByCategoryName(@Param("name") String name);

    @Query("SELECT p FROM Product p JOIN FETCH p.categories c WHERE c.name = :name AND p.available = :available")
    List<Product> findByCategoryNameAndAvailable(@Param("name") String name, @Param("available") boolean available);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByNameContainingIgnoreCaseAndAvailable(String name, boolean available);

    List<Product> findByBrandContainingIgnoreCase(String brand);

    List<Product> findByBrandContainingIgnoreCaseAndAvailable(String brand, boolean available);

    @Query("SELECT DISTINCT p FROM Product p JOIN p.categories c " +
           "WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByCategoryNameContaining(@Param("name") String name);

    @Query("SELECT DISTINCT p FROM Product p JOIN p.categories c " +
           "WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND p.available = true")
    List<Product> findByCategoryNameContainingAndAvailable(@Param("name") String name);

}
