package com.monkeypenthouse.core.security.service;

import com.monkeypenthouse.core.dao.LoginType;
import com.monkeypenthouse.core.dao.User;
import com.monkeypenthouse.core.repository.UserRepository;
import com.monkeypenthouse.core.security.dto.UserAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class MonkPentUserDetailsService implements UserDetailsService {

    private  final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("ClubUserDetailsService loadUserByUsername " + username);

        Optional<User> result = userRepository.findByEmail(username, LoginType.LOCAL);

        if (result.isEmpty()) {
            throw new UsernameNotFoundException("Check Email or LoginType");
        }

        User user = result.get();

        log.info("-------------------------------------");
        log.info(user);

        UserAuthDTO userAuthDTO = new UserAuthDTO(
                user.getEmail(),
                user.getPassword(),
                user.getLoginType(),
                user.getRoleSet().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_"+role.name())).collect(Collectors.toSet())
        );

        userAuthDTO.setName(user.getName());
        userAuthDTO.setLoginType(user.getLoginType());

        return userAuthDTO;
    }
}
