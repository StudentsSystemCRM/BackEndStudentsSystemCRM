package edutrack.security.services;

import edutrack.user.entity.UserEntity;
import edutrack.user.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = accountRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found with email: " + email);
        }
        return UserDetailsImpl.build(user);
    }
}
