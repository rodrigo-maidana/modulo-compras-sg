package modulocompras.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import modulocompras.jwt.JwtService;
import modulocompras.user.Role;
import modulocompras.user.User;
import modulocompras.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final JwtService jwtService;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;

        public AuthResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
                UserDetails user = userRepository.findByUsername(request.getUsername()).orElseThrow();
                String token = jwtService.getToken(user);
                return AuthResponse.builder()
                                .token(token)
                                .build();
        }

        public AuthResponse register(RegisterRequest request) {
                User user = User.builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .firstName(request.getFirstname())
                                .lastName(request.getLastname())
                                .role(Role.USER)
                                .build();

                userRepository.save(user);
                System.out.println("User registered: " + user);

                String token = jwtService.getToken(user);
                System.out.println("Generated Token: " + token);

                return AuthResponse.builder()
                                .token(token)
                                .build();
        }
}
