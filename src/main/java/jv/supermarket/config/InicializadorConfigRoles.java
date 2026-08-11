package jv.supermarket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import jv.supermarket.user.Role;
import jv.supermarket.user.RoleRepository;
import jv.supermarket.user.Usuario;
import jv.supermarket.user.UsuarioService;

@Configuration
@Order(1)
public class InicializadorConfigRoles implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void run(String... args) throws Exception {
        // Criar roles se não existirem
        criarRoleSeNaoExistir("ROLE_ADMIN");
        criarRoleSeNaoExistir("ROLE_FUNCIONARIO");
        criarRoleSeNaoExistir("ROLE_CLIENTE");

        // Criar administrador padrão
        criarAdminPadrao();
    }

    private void criarRoleSeNaoExistir(String roleName) {
        if (!roleRepository.existsByNome(roleName)) {
            Role role = new Role();
            role.setNome(roleName);
            roleRepository.save(role);
            System.out.println("Role criada: " + roleName);
        } else{
            System.out.println("Role já existente: " + roleName);
        }
    }

    private void criarAdminPadrao() {
        String email = "admin@gmail.com";
        
        if (!usuarioService.existsByEmail(email)) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail(email);
            admin.setSenha("123456");

            usuarioService.saveAdmin(admin);
            System.out.println("Administrador padrão criado: " + email);
        }else{
            System.out.println("Administrador padrão já existe: " + email);
        }
    }
}
