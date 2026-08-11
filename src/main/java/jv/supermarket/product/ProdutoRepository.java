package jv.supermarket.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Boolean existsByNomeAndMarca(String nome, String marca);

    boolean existsByIdAndDisponivel(Long id, boolean disponivel);

    Produto findByIdAndDisponivel(Long id, boolean disponivel);

    List<Produto> findAllByDisponivel(boolean disponivel);

    List<Produto> findByNome(String nome);

    List<Produto> findByNomeAndDisponivel(String nome, boolean disponivel);

    List<Produto> findByMarca(String marca);

    List<Produto> findByMarcaAndDisponivel(String marca, boolean disponivel);

    Produto findByMarcaAndNome(String marca, String nome);

    Produto findByMarcaAndNomeAndDisponivel(String marca, String nome, boolean disponivel);

    @Query("SELECT p FROM Produto p JOIN FETCH p.categorias c WHERE c.nome = :nome")
    List<Produto> findByCategoriaNome(@Param("nome") String nome);

    @Query("SELECT p FROM Produto p JOIN FETCH p.categorias c WHERE c.nome = :nome AND p.disponivel = :disponivel")
    List<Produto> findByCategoriaNomeAndDisponivel(@Param("nome") String nome, @Param("disponivel") boolean disponivel);

    //métodos para pegar pedaços de texto
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    List<Produto> findByNomeContainingIgnoreCaseAndDisponivel(String nome, boolean disponivel);

    List<Produto> findByMarcaContainingIgnoreCase(String marca);

    List<Produto> findByMarcaContainingIgnoreCaseAndDisponivel(String marca, boolean disponivel);

    @Query("SELECT DISTINCT p FROM Produto p JOIN p.categorias c " +
        "WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ")    
    List<Produto> findByCategoriaNomeContaining(@Param("nome") String nome);

    @Query("SELECT DISTINCT p FROM Produto p JOIN p.categorias c " +
       "WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')) " +
       "AND p.disponivel = true")
    List<Produto> findByCategoriaNomeContainingAndDisponivel(@Param("nome") String nome);

}
