package posmy.interview.boot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import posmy.interview.boot.constants.UserRole;
import posmy.interview.boot.model.User;

import java.util.Collection;
import java.util.Collections;

@Service
public class AuthenticationService implements AuthenticationProvider {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userService.getUserByUserName(username);

        if (user == null) {
            throw new AuthenticationServiceException("Invalid credentials");
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean isCorrectPassword = bCryptPasswordEncoder.matches(password, user.getPassword());
        if (!isCorrectPassword) {
            throw new AuthenticationServiceException("Invalid credentials");
        }

        String roleName = "ROLE_" + UserRole.getRoleById(user.getUserRoleId());

        Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(roleName));
        User principal = new User(user.getId(), username, password, user.getUserRoleId());

        // need to pass 3 argument already will isAuthenticated = true
        return new UsernamePasswordAuthenticationToken(principal, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
