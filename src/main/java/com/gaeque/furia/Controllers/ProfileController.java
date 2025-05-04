package com.gaeque.furia.Controllers;

import com.gaeque.furia.DTOs.ProfileDTO;
import com.gaeque.furia.Entity.User;
import com.gaeque.furia.Repository.UserRepository;
import com.gaeque.furia.Service.FriendshipService;
import com.gaeque.furia.Service.ProfileService;
import com.gaeque.furia.Utils.JwtUtil;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/profile"})
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipService friendshipService;

    public ProfileController() {
    }

    @PostMapping
    public ResponseEntity<?> createOrUpdateProfile(@RequestBody ProfileDTO dto, @RequestHeader("Authorization") String token) {
        String rawToken = token.replace("Bearer ", "");
        String email = this.jwtUtil.getUsernameFromToken(rawToken);
        this.profileService.saveOrUpdateProfile(email, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<ProfileDTO> getProfile(@RequestHeader("Authorization") String token) {
        String rawToken = token.replace("Bearer ", "").trim();
        String email = this.jwtUtil.getUsernameFromToken(rawToken);
        ProfileDTO profileDTO = this.profileService.getProfileByEmail(email);
        return ResponseEntity.ok(profileDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUser(@RequestParam String userName, @RequestHeader("Authorization") String token) {
        String currentEmail = jwtUtil.getUsernameFromToken(token.substring(7));
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (currentUser.getUserName().equalsIgnoreCase(userName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Você não pode se adicionar como amigo");
        }

        Optional<User> userOptional = userRepository.findByUserNameIgnoreCase(userName);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        boolean alreadyFriends = friendshipService.areFriends(currentUser.getUserName(), userName);
        if (alreadyFriends) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Vocês já são amigos");
        }

        return ResponseEntity.ok(userOptional.get().getUserName());
    }
}
