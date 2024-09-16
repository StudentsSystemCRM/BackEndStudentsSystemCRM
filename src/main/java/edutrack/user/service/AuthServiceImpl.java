package edutrack.user.service;

import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.user.dto.response.LoginSuccessResponse;
import edutrack.user.dto.response.Role;
import edutrack.user.entity.UserEntity;
import edutrack.user.exception.AccessException;
import edutrack.user.exception.ResourceExistsException;
import edutrack.user.repository.AccountRepository;
import edutrack.user.util.EntityDtoUserMapper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    PasswordEncoder passwordEncoder;
    AccountRepository userRepository;

    @Override
    @Transactional
    public LoginSuccessResponse registration(String invite, UserRegisterRequest data) {

        //TODO  invite check invite

        UserEntity user = userRepository.findByEmail(data.getEmail());

        if (user != null)
            throw new ResourceExistsException("user with email " + data.getEmail() + "already exists");
        user = EntityDtoUserMapper.INSTANCE.userRegisterRequestToUser(data);
        user.setRoles(new HashSet<>(List.of(Role.USER)));
        userRepository.save(user);

        return EntityDtoUserMapper.INSTANCE.userToLoginSuccessResponse(user);
    }

    @Override
    public LoginSuccessResponse loginByEmailAndPassword(String email, String password) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getHashedPassword())) {
            throw new AccessException("Invalid email or password");
        }

        return EntityDtoUserMapper.INSTANCE.userToLoginSuccessResponse(user);
    }
}
