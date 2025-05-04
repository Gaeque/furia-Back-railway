package com.gaeque.furia.Repository;

import com.gaeque.furia.Entity.Profile;
import com.gaeque.furia.Entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser(User user);

    Optional<Profile> findByUserName(String userName);

    Optional<Profile> findByUserNameIgnoreCase(String userName);
}
