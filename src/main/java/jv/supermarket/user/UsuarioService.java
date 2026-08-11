package jv.supermarket.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jv.supermarket.cart.Carrinho;
import jv.supermarket.cart.CarrinhoRepository;
import jv.supermarket.shared.customexception.AlreadyExistException;
import jv.supermarket.shared.customexception.ResourceNotFoundException;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository userRepo;

    @Autowired
    private CarrinhoRepository carrinhoRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Usuario saveCliente(Usuario usuario) {
        if (existsByEmail(usuario.getEmail())) {
            throw new AlreadyExistException("Já existe um usuário com este email");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        Role role = roleRepo.findByNome("ROLE_CLIENTE");
        usuario.getRoles().add(role);

        usuario = userRepo.save(usuario);

        if (usuario.getCarrinho() == null) {
            Carrinho carrinho = new Carrinho();
            carrinho.setUser(usuario);

            carrinhoRepo.save(carrinho);
        }
        return usuario;
    }
    @Transactional
    public Usuario saveAdmin(Usuario usuario) {
        if (existsByEmail(usuario.getEmail())) {
            throw new AlreadyExistException("Já existe um usuário com este email");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        Role role = roleRepo.findByNome("ROLE_ADMIN");
        usuario.getRoles().add(role);

        usuario = userRepo.save(usuario);
        return usuario;
    }

    @Transactional
    public Usuario saveFuncionario(Usuario usuario) {
        if (existsByEmail(usuario.getEmail())) {
            throw new AlreadyExistException("Já existe um usuário com este email");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        Role role = roleRepo.findByNome("ROLE_FUNCIONARIO");
        usuario.getRoles().add(role);

        usuario = userRepo.save(usuario);

        if (usuario.getCarrinho() == null) {
            Carrinho carrinho = new Carrinho();
            carrinho.setUser(usuario);

            carrinhoRepo.save(carrinho);
        }
        return usuario;
    }

    public Usuario getLoggedUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();
    
            if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
                return getByEmail(springUser.getUsername()); // Busca no banco
            }
    
            if (principal instanceof Usuario usuario) {
                return usuario;
            }
        }
        throw new ResourceNotFoundException("Usuário logado não encontrado");
    }
    

    public Usuario getById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o id: " + userId));
    }

    public Usuario getByEmail(String email) {
        return Optional.ofNullable(userRepo.findByEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o email: " + email));

    }

    public Usuario updateUsuario(Usuario usuario, Long userId) {
        if (existById(userId)) {
            Usuario user = getById(userId);

            user.setNome(usuario.getNome());
            user.setEmail(usuario.getEmail());
            user.setSenha(usuario.getSenha());

            return userRepo.save(user);
        } else {
            throw new ResourceNotFoundException("Não foi encontrado nenhum usuário com o id: " + userId);
        }
    }

    public void deleteUsuario(Long userId) {
        if (existById(userId)) {
            userRepo.deleteById(userId);
        } else {
            throw new ResourceNotFoundException("Não foi encontrado nenhum usuário com o id: " + userId);
        }
    }

    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    private boolean existById(Long id) {
        return userRepo.existsById(id);
    }

}
