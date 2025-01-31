package minha_midia_fisica.controller;

import minha_midia_fisica.dto.LoginRequestDTO;
import minha_midia_fisica.model.User;
import minha_midia_fisica.repository.UserRepository;
import minha_midia_fisica.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthenticationController(JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody User user) {
        return userRepository.findByUsername(user.getUsername())
                .flatMap(existingUser -> Mono.just(ResponseEntity.badRequest().body("Usuário já existe")))
                .switchIfEmpty(
                        Mono.defer(() -> {
                            user.setPassword(passwordEncoder.encode(user.getPassword()));
                            return userRepository.save(user)
                                    .map(savedUser -> ResponseEntity.ok("Usuário registrado com sucesso"));
                        }));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Object>> login(@RequestBody LoginRequestDTO loginRequest) {
        return userRepository.findByUsername(loginRequest.username())
                .flatMap(user -> {
                    if (passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
                        String token = jwtUtil.generateToken(user.getUsername());
                        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(Duration.ofHours(1))
                                .build();
                        return Mono.just(ResponseEntity.ok()
                                .header("Set-Cookie", cookie.toString())
                                .build());
                    } else {
                        return Mono.just(ResponseEntity.status(401).build());
                    }
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(401).build()));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<String>> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return Mono.just(ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body("Logout realizado com sucesso!"));
    }

    @GetMapping("/verify-token")
    public Mono<ResponseEntity<Object>> verifyToken(ServerHttpRequest request) {
        return Mono.justOrEmpty(request.getCookies().getFirst("jwt"))
                .flatMap(cookie -> {
                    String token = cookie.getValue();
                    if (jwtUtil.validateToken(token)) {
                        return Mono.just(ResponseEntity.ok().build());
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                    }
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }
}