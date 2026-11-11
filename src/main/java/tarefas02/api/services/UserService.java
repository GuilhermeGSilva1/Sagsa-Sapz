package tarefas02.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarefas02.api.exceptions.AuthorizationException;
import tarefas02.api.models.Perfil;
import tarefas02.api.models.User;
import tarefas02.api.models.dtos.UserCreateDTO;
import tarefas02.api.models.enums.ProfileEnum;
import tarefas02.api.repositories.PerfilRepository;
import tarefas02.api.repositories.UserRepository;
import tarefas02.api.security.UserSpringSecurity;
import tarefas02.api.services.exceptions.DataBindingViolationException;
import tarefas02.api.services.exceptions.ObjectNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    public static UserSpringSecurity authenticated() {
        try {
            return (UserSpringSecurity) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }

    private void checkAdmin() {
        UserSpringSecurity user = authenticated();

        if (user == null || !user.hasRole(ProfileEnum.ADMIN)) {
            throw new AuthorizationException("Acesso negado.");
        }
    }

    public User findById(Long id) {
        UserSpringSecurity user = authenticated();

        if (user == null || (!user.hasRole(ProfileEnum.ADMIN)
                && !Objects.equals(id, user.getId()))) {
            throw new AuthorizationException("Acesso negado.");
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Usuário não encontrado! Id: " + id));
    }

    public List<User> findAll() {
        checkAdmin();
        return userRepository.findAll();
    }

    @Transactional
    public User create(User obj) {
        obj.setId(null);

        // Padroniza o e-mail sem espaços e em letras minúsculas
        if (obj.getEmail() != null) {
            obj.setEmail(obj.getEmail().trim().toLowerCase());
        }

        if (userRepository.existsByEmail(obj.getEmail())) {
            throw new DataBindingViolationException("Este e-mail já está cadastrado.");
        }

        // Atribui o perfil informado ou faz fallback seguro para ID 3L
        Long perfilId = (obj.getPerfil() != null && obj.getPerfil().getId() != null) 
                ? obj.getPerfil().getId() 
                : 3L;

        Optional<Perfil> perfilOpt = perfilRepository.findById(perfilId);
        if (perfilOpt.isPresent()) {
            obj.setPerfil(perfilOpt.get());
        } else {
            perfilRepository.findById(3L).ifPresent(obj::setPerfil);
        }

        // Criptografa a senha com BCrypt
        if (obj.getPassword() != null) {
            obj.setPassword(bCryptPasswordEncoder.encode(obj.getPassword()));
        }

        // Evita NullPointerException ao manipular a coleção de perfis
        if (obj.getProfiles() != null) {
            obj.getProfiles().clear();
        }
        obj.addProfile(ProfileEnum.INSTRUTOR);

        return userRepository.save(obj);
    }

    public User fromDTO(UserCreateDTO dto) {
        User user = new User();
        user.setNome(dto.getNome());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        
        if (dto.getPerfilId() != null) {
            Perfil perfil = new Perfil();
            perfil.setId(Long.valueOf(dto.getPerfilId()));
            user.setPerfil(perfil);
        }

        return user;
    }

    @Transactional
    public User update(User obj) {
        User user = findById(obj.getId());

        if (obj.getPassword() != null && !obj.getPassword().isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(obj.getPassword()));
        }

        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);

        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException(
                    "Não é possível excluir este usuário pois existem registros relacionados.");
        }
    }
}