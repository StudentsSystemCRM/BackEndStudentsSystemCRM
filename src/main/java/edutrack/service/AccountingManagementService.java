package edutrack.service;

import java.security.Principal;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import edutrack.dto.request.accounting.PasswordUpdateRequest;
import edutrack.dto.request.accounting.UserRegisterRequest;
import edutrack.dto.request.accounting.UserRoleRequest;
import edutrack.dto.request.accounting.UserUpdateRequest;
import edutrack.dto.response.accounting.LoginSuccessResponse;
import edutrack.dto.response.accounting.UserDataResponse;
import edutrack.entity.accounting.User;
import edutrack.exception.ResourceExistsException;
import edutrack.repository.UserRepository;
import edutrack.util.EntityDtoMapper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountingManagementService implements IAccountingManagement{
	
	UserRepository userRepository;

    @Override
    @Transactional
    public LoginSuccessResponse registration(String invite, UserRegisterRequest data) {
    	
    	//TODO  invite check invite
    	
    	User user = userRepository.findByEmail(data.getEmail());
    	if (user != null)
			throw new ResourceExistsException("user with email " + data.getEmail() + "already exists");
		user = EntityDtoMapper.INSTANCE.userRegisterRequestToUser(data);
		userRepository.save(user);
		return EntityDtoMapper.INSTANCE.userToLoginSuccessResponse(user);
    }

    @Override
    public LoginSuccessResponse login(Principal user) {
    	User userService = userRepository.findByEmail(user.getName());
        return EntityDtoMapper.INSTANCE.userToLoginSuccessResponse(userService);
    }

    @Override
    @Transactional
    public UserDataResponse updateUser(UserUpdateRequest data) {
    	User user = userRepository.findByEmail(data.getEmail());
    	if (user == null)
    		throw new NoSuchElementException("Account with login '%s' not found".formatted(data.getEmail()));
    	user.setName(data.getName());
    	user.setSurname(data.getSurname());
    	user.setBirthdate(data.getBirthdate());
    	userRepository.save(user);
    	
        return EntityDtoMapper.INSTANCE.userToUserDataResponse(user);
    }

    @Override
    @Transactional
    public void updatePassword(Principal user, PasswordUpdateRequest data) {
    	
    	//TODO

    }

    @Override
    @Transactional
    public UserDataResponse removeUser(String login) {
    	User user = userRepository.findByEmail(login);
    	if (user == null)
    		throw new NoSuchElementException("Account with login '%s' not found".formatted(login));
    	userRepository.delete(user);
    	return EntityDtoMapper.INSTANCE.userToUserDataResponse(user);
    }

    @Override
    @Transactional
    public UserDataResponse addRole(String login, UserRoleRequest data) {
    	User user = userRepository.findByEmail(login);
    	if (user == null)
    		throw new NoSuchElementException("Account with login '%s' not found".formatted(login));
    	user.getRoles().add(data.getRole());
    	userRepository.save(user);
        return EntityDtoMapper.INSTANCE.userToUserDataResponse(user);
    }

    @Override
    @Transactional
    public UserDataResponse removeRole(String login, UserRoleRequest data) {
    	User user = userRepository.findByEmail(login);
    	if (user == null)
    		throw new NoSuchElementException("Account with login '%s' not found".formatted(login));
    	user.getRoles().remove(data.getRole());
    	userRepository.save(user);
        return EntityDtoMapper.INSTANCE.userToUserDataResponse(user);
    }
}
