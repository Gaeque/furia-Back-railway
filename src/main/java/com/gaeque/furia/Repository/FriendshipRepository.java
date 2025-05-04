package com.gaeque.furia.Repository;

import com.gaeque.furia.Entity.Friendship;
import com.gaeque.furia.Entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findByUserAndFriend(User user, User friend);

    boolean existsByUserAndFriendAndStatus(User user, User friend, String status);
    List<Friendship> findByFriendAndStatus(User friend, String status);

    List<Friendship> findByUserAndStatus(User user, String status);
}
