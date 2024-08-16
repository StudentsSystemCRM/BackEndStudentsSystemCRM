package edutrack.service;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edutrack.dto.request.accounting.PasswordUpdateRequest;
import edutrack.dto.request.accounting.UserRegisterRequest;
import edutrack.dto.request.accounting.UserRoleRequest;
import edutrack.dto.request.accounting.UserUpdateRequest;
import edutrack.dto.response.accounting.LoginSuccessResponse;
import edutrack.dto.response.accounting.Role;
import edutrack.dto.response.accounting.UserDataResponse;
import edutrack.entity.accounting.User;
import edutrack.exception.AccessException;
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
	PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public LoginSuccessResponse registration(String invite, UserRegisterRequest data) {
    	
    	//TODO  invite check invite
    	
    	User user = userRepository.findByEmail(data.getEmail());
    	if (user != null)
			throw new ResourceExistsException("user with email " + data.getEmail() + "already exists");
		user = EntityDtoMapper.INSTANCE.userRegisterRequestToUser(data);
		user.setHashedPassword(passwordEncoder.encode(data.getPassword()));
		user.setRoles(new HashSet<Role>(Arrays.asList(Role.USER)));
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
        if (user == null) {
            throw new NoSuchElementException("Account with email '%s' not found".formatted(data.getEmail()));
        }
        if(user.getName().equals("ada@gmail.com"))
			throw new NoSuchElementException("you can't change default user Ada Lovelace, create new one");
    	
        
        checkAccessCurrentAccountChangeUser(user);

        user.setName(data.getName());
        user.setSurname(data.getSurname());
        user.setBirthdate(data.getBirthdate());
        userRepository.save(user);
       
        return EntityDtoMapper.INSTANCE.userToUserDataResponse(user);
    }


   

	@Override
    @Transactional
    public void updatePassword(Principal user, PasswordUpdateRequest data) {
		if(user.getName().equals("ada@gmail.com"))
			throw new NoSuchElementException("you can't change default user Ada Lovelace, create new one");
    	User account = userRepository.findByEmail(user.getName());
    	account.setHashedPassword(passwordEncoder.encode(data.getPassword()));
    	userRepository.save(account);
    }

    @Override
    @Transactional
    public UserDataResponse removeUser(String login) {
		if(login.equals("ada@gmail.com"))
			throw new NoSuchElementException("you can't change default user Ada Lovelace, create new one");
		
    	User user = userRepository.findByEmail(login);
    	if (user == null)
    		throw new NoSuchElementException("Account with login '%s' not found".formatted(login));
    	
    	
    	checkAccessCurrentAccountChangeUser(user);
    	
    	userRepository.delete(user);
    	return EntityDtoMapper.INSTANCE.userToUserDataResponse(user);
    }

    @Override
    @Transactional
    public UserDataResponse addRole(String login, UserRoleRequest data) {
    	User user = userRepository.findByEmail(login);
    	if (user == null)
    		throw new NoSuchElementException("Account with login '%s' not found".formatted(login));
    	
    	checkAccessChangeRoleUser(user);
    	
    	user.getRoles().add(Role.fromValue(data.getRole()));
    	userRepository.save(user);
        return EntityDtoMapper.INSTANCE.userToUserDataResponse(user);
    }

    @Override
    @Transactional
    public UserDataResponse removeRole(String login, UserRoleRequest data) {
    	User user = userRepository.findByEmail(login);
    	if (user == null)
    		throw new NoSuchElementException("Account with login '%s' not found".formatted(login));
    	
    	checkAccessChangeRoleUser(user);
    	
    	user.getRoles().remove(Role.fromValue(data.getRole()));
    	userRepository.save(user);
        return EntityDtoMapper.INSTANCE.userToUserDataResponse(user);
    }


	private void checkAccessCurrentAccountChangeUser(User user) {
    	// Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Get the roles of the currently authenticated user
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        boolean isCeo = authorities.stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_CEO"));
        
        // Check if the user is attempting to update someone else's profile
        if (!username.equals(user.getEmail())) {
            // Prevent changing data of a CEO
            if (user.getRoles().contains(Role.CEO)) {
                throw new AccessException("You don't have rules to update this user's profile.");
            }
            
            // Prevent update if the authenticated user is neither an ADMIN nor a CEO
            if (!isAdmin && !isCeo) {
                throw new AccessException("You don't have rules to  update this user's profile.");
            }
            
            // Prevent an ADMIN from updating another ADMIN's profile unless the authenticated user is a CEO
            if (user.getRoles().contains(Role.ADMIN) && !isCeo) {
                throw new AccessException("You don't have rules to  update this user's profile.");
            }
        }
		
	}
	
    
    
    private void checkAccessChangeRoleUser(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        boolean isCeo = authorities.stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_CEO"));
        
        if (username.equals(user.getEmail())) 
        	throw new AccessException("You can't change role for yourself");
        
        if (!isAdmin && !isCeo)
            throw new AccessException("You don't have rules to update this user's profile.");

        if (user.getRoles().contains(Role.CEO) && !isCeo)
                throw new AccessException("You don't have rules to update this user's profile.");

        if (user.getRoles().contains(Role.ADMIN) && !isCeo)
                throw new AccessException("You don't have rules to update this user's profile.");
        }

}
