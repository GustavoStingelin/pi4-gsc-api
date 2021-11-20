package com.gs.pi4.api.app.service;

import java.util.Date;

import com.gs.pi4.api.app.vo.user.UserBasicVO;
import com.gs.pi4.api.app.vo.user.UserRegistrationVO;
import com.gs.pi4.api.core.user.IUserBasic;
import com.gs.pi4.api.core.user.User;
import com.gs.pi4.api.core.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(8);

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserDetails loadByEmail(@NonNull String email) throws UsernameNotFoundException {
        UserDetails user = repository.findByEmail(email);
        if (user != null) {
            return user;
        } else {
            throw new UsernameNotFoundException("Email " + email + " not found.");
        }
    }

    public User findByEmail(@NonNull String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email);
        if (user != null) {
            return user;
        } else {
            throw new UsernameNotFoundException("Email " + email + " not found.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        return loadByEmail(email);
    }

    public User createUser(@NonNull UserRegistrationVO vo) {
        User entity = parseUserRegistrationVO2Entity(vo);
        entity = repository.save(entity);
        return entity;
    }

    private User parseUserRegistrationVO2Entity(@NonNull UserRegistrationVO vo) {
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

    public boolean hasEmail(@NonNull String email) {
        var user = repository.findByEmail(email);
        return (user != null);
    }

    private UserBasicVO parseIUserBasic2UserBasicVO(@NonNull IUserBasic entity) {
        return UserBasicVO.builder()
            .firstName(entity.getFirstName())
            .email(entity.getEmail())
            .profileImage(entity.getProfileImage())
            .build();
    }

    public UserBasicVO parseUser2UserBasicVO(@NonNull User entity) {
        return UserBasicVO.builder()
            .firstName(entity.getFirstName())
            .email(entity.getEmail())
            .profileImage(entity.getProfileImage())
            .build();
    }

    public UserBasicVO findUserBasicById(@NonNull Long id) {
        IUserBasic entity = repository.findUserBasicById(id);
        return parseIUserBasic2UserBasicVO(entity);
    }

}
