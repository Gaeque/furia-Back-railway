package com.gaeque.furia.Controllers;

import com.gaeque.furia.Entity.Friendship;
import com.gaeque.furia.Entity.User;
import com.gaeque.furia.Repository.UserRepository;
import com.gaeque.furia.Service.FriendshipService;
import com.gaeque.furia.Utils.JwtUtil;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/friends"})
public class FriendshipController {
    @Autowired
    private FriendshipService friendshipService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    public FriendshipController() {
    }

    @PostMapping("/sendRequest")
    public ResponseEntity<?> sendFriendRequest(@RequestParam String userName, @RequestParam String friendUserName) {
        try {
            friendshipService.sendFriendRequest(userName, friendUserName);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {

            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

        @PostMapping({"/acceptRequest"})
    public ResponseEntity<Void> acceptFriendRequest(@RequestParam String userName, @RequestParam String friendUserName) {
        this.friendshipService.acceptFriendRequest(userName, friendUserName);
        return ResponseEntity.ok().build();
    }

    @PostMapping({"/rejectRequest"})
    public ResponseEntity<Void> rejectFriendRequest(@RequestParam String userName, @RequestParam String friendUserName) {
        this.friendshipService.rejectFriendRequest(userName, friendUserName);
        return ResponseEntity.ok().build();
    }

    @GetMapping({"/areFriends"})
    public ResponseEntity<Boolean> areFriends(@RequestParam String userName, @RequestParam String friendUserName) {
        boolean friends = this.friendshipService.areFriends(userName, friendUserName);
        return ResponseEntity.ok(friends);
    }

    @GetMapping({"/getPendingRequests"})
    public ResponseEntity<List<Map<String, Object>>> getPendingRequests(@RequestHeader("Authorization") String token) {
        String currentEmail = this.jwtUtil.getUsernameFromToken(token.substring(7));
        User currentUser = (User)this.userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        List<Friendship> pendingRequests = this.friendshipService.getPendingRequests(currentUser);
        List<Map<String, Object>> friendData = (List)pendingRequests.stream().map((friendship) -> {
            Map<String, Object> data = new HashMap();
            data.put("id", friendship.getUser().getId());
            data.put("userName", friendship.getUser().getUserName());
            return data;
        }).distinct().collect(Collectors.toList());
        return ResponseEntity.ok(friendData);
    }

    @GetMapping("/getFriends")
    public ResponseEntity<List<Map<String, Object>>> getFriends(@RequestHeader("Authorization") String token) {
        try {
            String currentEmail = this.jwtUtil.getUsernameFromToken(token.substring(7));
            User currentUser = this.userRepository.findByEmail(currentEmail)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            List<Friendship> friends = this.friendshipService.getFriends(currentUser);

            // Usar Set para evitar duplicatas
            Set<Long> addedIds = new HashSet<>();

            List<Map<String, Object>> friendData = friends.stream()
                    .map(friendship -> {
                        User friend = friendship.getUser().getId().equals(currentUser.getId())
                                ? friendship.getFriend()
                                : friendship.getUser();

                        Map<String, Object> data = new HashMap<>();
                        data.put("id", friend.getId());
                        data.put("userName", friend.getUserName());
                        return data;
                    })
                    .filter(data -> addedIds.add((Long) data.get("id"))) // só adiciona se o ID ainda não estiver no set
                    .collect(Collectors.toList());

            return ResponseEntity.ok(friendData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
