package jv.supermarket.category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>{
    Categoria findByNome(String nome);
    Boolean existsByNome(String nome);
}
