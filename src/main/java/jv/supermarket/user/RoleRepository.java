package jv.supermarket.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long>{

    Role findByNome(String string);

    boolean existsByNome(String roleName);
    
}
