package com.gaeque.furia.Service;

import com.gaeque.furia.DTOs.AuthRequest;
import com.gaeque.furia.DTOs.AuthResponse;
import com.gaeque.furia.DTOs.UserDTO;
import com.gaeque.furia.Entity.User;
import com.gaeque.furia.Repository.UserRepository;
import com.gaeque.furia.Utils.JwtUtil;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService() {
    }

    public void registerUser(UserDTO userDTO) {
        if (this.userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        } else {
            User newUser = new User();
            newUser.setUserName(userDTO.getUserName());
            newUser.setEmail(userDTO.getEmail());
            newUser.setPassword(this.passwordEncoder.encode(userDTO.getPassword()));
            this.userRepository.save(newUser);
        }
    }

    public AuthResponse login(AuthRequest request) {
        Optional<User> userOpt = this.userRepository.findByEmail(request.getEmail());
        if (!userOpt.isEmpty() && this.passwordEncoder.matches(request.getPassword(), ((User)userOpt.get()).getPassword())) {
            User user = (User)userOpt.get();
            String token = JwtUtil.generateToken(user.getEmail());
            return new AuthResponse(true, user.getId(), user.getEmail(), token, user.getUserName());
        } else {
            throw new RuntimeException("Credenciais inválidas.");
        }
    }
}
