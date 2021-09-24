package com.gs.pi4.api.app.service;

import java.util.Date;

import com.gs.pi4.api.app.vo.user.UserRegistrationVO;
import com.gs.pi4.api.core.User;
import com.gs.pi4.api.core.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(8);

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserDetails loadByEmail(String email) throws UsernameNotFoundException {
        UserDetails user = repository.findByEmail(email);
        if (user != null) {
            return user;
        } else {
            throw new UsernameNotFoundException("Email " + email + " not found.");
        }
    }

    public User findByEmail(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email);
        if (user != null) {
            return user;
        } else {
            throw new UsernameNotFoundException("Email " + email + " not found.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return loadByEmail(email);
    }

    public User createUser(UserRegistrationVO vo) {
        User entity = parseUserRegistrationVOToEntity(vo);
        entity = repository.save(entity);
        return entity;
    }

    private User parseUserRegistrationVOToEntity(UserRegistrationVO vo) {
        return User.builder()
            .id(0L)
            .firstName(vo.getFirstName())
            .lastName(vo.getLastName())
            .email(vo.getEmail())
            .password(bCryptPasswordEncoder.encode(vo.getPassword()))
            .birthday(vo.getBirthday())
            .gender(vo.getGender())
            .createdAt(new Date())
            .profileImage(0L)
            .build();
    }

    public boolean hasEmail(String email) {
        var user = repository.findByEmail(email);
        return (user != null);
    }
}
