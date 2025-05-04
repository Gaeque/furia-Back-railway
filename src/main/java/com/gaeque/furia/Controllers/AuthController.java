package com.gaeque.furia.Controllers;

import com.gaeque.furia.DTOs.AuthRequest;
import com.gaeque.furia.DTOs.AuthResponse;
import com.gaeque.furia.Entity.Profile;
import com.gaeque.furia.Entity.User;
import com.gaeque.furia.Repository.ProfileRepository;
import com.gaeque.furia.Repository.UserRepository;
import com.gaeque.furia.Service.UserService;
import com.gaeque.furia.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/auth"})
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthController() {
    }

    @PostMapping({"/register"})
    public ResponseEntity<String> register(@RequestBody AuthRequest authRequest) {
        if (this.userRepository.findByEmail(authRequest.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email já registrado");
        } else if (this.userRepository.findByUserName(authRequest.getUserName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nome de usuário já está em uso");
        } else {
            User newUser = new User();
            newUser.setUserName(authRequest.getUserName());
            newUser.setEmail(authRequest.getEmail());
            newUser.setPassword(this.passwordEncoder.encode(authRequest.getPassword()));
            this.userRepository.save(newUser);
            Profile profile = new Profile();
            profile.setUser(newUser);
            profile.setUserName(newUser.getUserName());
            this.profileRepository.save(profile);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registrado com sucesso");
        }
    }

    @PostMapping({"/login"})
    public AuthResponse login(@RequestBody User loginRequest) {
        User user = (User)this.userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (!this.passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciais inválidas");
        } else {
            String token = JwtUtil.generateToken(user.getEmail());
            return new AuthResponse(true, user.getId(), user.getEmail(), token, user.getUserName());
        }
    }
}
