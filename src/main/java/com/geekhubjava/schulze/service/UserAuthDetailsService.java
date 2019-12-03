package com.geekhubjava.schulze.service;

import com.geekhubjava.schulze.model.User;
import com.geekhubjava.schulze.model.UserAuthDetails;
import com.geekhubjava.schulze.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAuthDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public UserAuthDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userByLogin = userRepository.getUserByLogin(username);
        User user = userByLogin.orElseThrow(() -> new UsernameNotFoundException("No user with provided login were found."));
        return new UserAuthDetails(user.getId(), user.getLogin(), user.getPasswordHash());
    }
}
