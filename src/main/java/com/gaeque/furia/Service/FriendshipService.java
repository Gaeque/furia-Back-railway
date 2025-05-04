package com.gaeque.furia.Service;

import com.gaeque.furia.Entity.Friendship;
import com.gaeque.furia.Entity.Profile;
import com.gaeque.furia.Entity.User;
import com.gaeque.furia.Repository.FriendshipRepository;
import com.gaeque.furia.Repository.ProfileRepository;
import com.gaeque.furia.Repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendshipService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private UserRepository userRepository;

    public FriendshipService() {
    }

    public void sendFriendRequest(String userName, String friendUserName) {
        User sender = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User receiver = userRepository.findByUserName(friendUserName)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        if (sender.equals(receiver)) {
            throw new RuntimeException("Não é possível enviar solicitação para si mesmo.");
        }

        boolean alreadyExists = friendshipRepository.existsByUserAndFriendAndStatus(sender, receiver, "PENDING")
                || friendshipRepository.existsByUserAndFriendAndStatus(receiver, sender, "PENDING");

        if (alreadyExists) {
            throw new RuntimeException("Já existe uma solicitação de amizade pendente.");
        }

        Friendship request = new Friendship();
        request.setUser(sender);
        request.setFriend(receiver);
        request.setStatus("PENDING");
        friendshipRepository.save(request);
    }


    public void acceptFriendRequest(String userName, String friendUserName) {
        User user = ((Profile)this.profileRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"))).getUser();
        User friend = ((Profile)this.profileRepository.findByUserName(friendUserName)
                .orElseThrow(() -> new RuntimeException("Friend not found"))).getUser();

        // Atualizando a solicitação de amizade existente para "ACCEPTED"
        Friendship request = friendshipRepository.findByUserAndFriend(friend, user)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Atualiza o status da solicitação
        request.setStatus("ACCEPTED");
        friendshipRepository.save(request);

        // Agora, cria a amizade em ambas as direções se ainda não existir
        if (!friendshipRepository.existsByUserAndFriendAndStatus(user, friend, "ACCEPTED")) {
            Friendship friendship1 = new Friendship();
            friendship1.setUser(user);
            friendship1.setFriend(friend);
            friendship1.setStatus("ACCEPTED");
            friendshipRepository.save(friendship1);
        }

        if (!friendshipRepository.existsByUserAndFriendAndStatus(friend, user, "ACCEPTED")) {
            Friendship friendship2 = new Friendship();
            friendship2.setUser(friend);
            friendship2.setFriend(user);
            friendship2.setStatus("ACCEPTED");
            friendshipRepository.save(friendship2);
        }
    }


    public void rejectFriendRequest(String userName, String friendUserName) {
        this.updateRequestStatus(userName, friendUserName, "REJECTED");
    }

    public boolean areFriends(String userName, String friendUserName) {
        User user = ((Profile)this.profileRepository.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found"))).getUser();
        User friend = ((Profile)this.profileRepository.findByUserName(friendUserName).orElseThrow(() -> new RuntimeException("Friend not found"))).getUser();
        return this.friendshipRepository.existsByUserAndFriendAndStatus(user, friend, "ACCEPTED") || this.friendshipRepository.existsByUserAndFriendAndStatus(friend, user, "ACCEPTED");
    }

    private void updateRequestStatus(String userName, String friendUserName, String status) {
        User user = ((Profile)this.profileRepository.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found"))).getUser();
        User friend = ((Profile)this.profileRepository.findByUserName(friendUserName).orElseThrow(() -> new RuntimeException("Friend not found"))).getUser();
        Friendship request = (Friendship)this.friendshipRepository.findByUserAndFriend(friend, user).orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(status);
        this.friendshipRepository.save(request);
    }

    public List<Friendship> getPendingRequests(User user) {
        return this.friendshipRepository.findByFriendAndStatus(user, "PENDING");
    }

    public List<Friendship> getFriends(User user) {
        List<Friendship> friendships1 = friendshipRepository.findByUserAndStatus(user, "ACCEPTED");
        List<Friendship> friendships2 = friendshipRepository.findByFriendAndStatus(user, "ACCEPTED");
        friendships1.addAll(friendships2);
        return friendships1;
    }
}
