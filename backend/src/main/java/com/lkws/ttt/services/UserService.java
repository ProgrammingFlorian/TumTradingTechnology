package com.lkws.ttt.services;

import com.lkws.ttt.datatransferobjects.RegisterDTO;
import com.lkws.ttt.model.User;
import com.lkws.ttt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(format("User %s not found", username)));
    }

    @Transactional
    public User create(RegisterDTO user) {
        if (userRepository.findByUsername(user.username()).isPresent()) {
            throw new ValidationException("Username already exists");
        }

        String password = passwordEncoder.encode(user.password());
        var userToCreate = new User(user.username(), password);
        return userRepository.save(userToCreate);
    }

    public Optional<User> getUser(String username) {
        return userRepository.findByUsername(username);

    }

    public List<User> getAllUsers() {
        var users = userRepository.findAll();
        return StreamSupport.stream(users.spliterator(), true).toList();
    }
}
