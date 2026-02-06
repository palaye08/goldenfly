package com.goldenfly.security;

import com.goldenfly.design.repositories.UtilisateurRepository;
import com.goldenfly.domain.entities.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + email));

        return User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getPassword())
                .authorities(Collections.singleton(
                        new SimpleGrantedAuthority("ROLE_" + utilisateur.getProfile().name())))
                .accountExpired(false)
                .accountLocked(!utilisateur.getActif())
                .credentialsExpired(false)
                .disabled(!utilisateur.getActif())
                .build();
    }
}

