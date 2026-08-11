package jv.supermarket.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    
    boolean existsByEmail(String email);

    Usuario findByEmail(String email);
}
