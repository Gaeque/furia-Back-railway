package com.gaeque.furia.Service;

import com.gaeque.furia.DTOs.ProfileDTO;
import com.gaeque.furia.Entity.Profile;
import com.gaeque.furia.Entity.User;
import com.gaeque.furia.Repository.ProfileRepository;
import com.gaeque.furia.Repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserRepository userRepository;

    public ProfileService() {
    }

    public void saveOrUpdateProfile(String email, ProfileDTO dto) {
        User user = (User)this.userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Profile profile = (Profile)this.profileRepository.findByUser(user).orElse(new Profile());
        profile.setUser(user);
        profile.setUserName(dto.getUserName());
        profile.setBio(dto.getBio());
        profile.setPhone(dto.getPhone());
        profile.setBirthDate(dto.getBirthDate());
        profile.setInstagram(dto.getInstagram());
        profile.setGamersClub(dto.getGamersClub());
        profile.setTwitch(dto.getTwitch());
        this.profileRepository.save(profile);
    }

    public ProfileDTO getProfileByEmail(String email) {
        User user = (User)this.userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Profile profile = (Profile)this.profileRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Perfil não encontrado"));
        ProfileDTO dto = new ProfileDTO();
        dto.setUserName(profile.getUserName());
        dto.setBio(profile.getBio());
        dto.setPhone(profile.getPhone());
        dto.setBirthDate(profile.getBirthDate());
        dto.setInstagram(profile.getInstagram());
        dto.setGamersClub(profile.getGamersClub());
        dto.setTwitch(profile.getTwitch());
        return dto;
    }

    public Optional<Profile> findByUserNameIgnoreCase(String userName) {
        return this.profileRepository.findByUserNameIgnoreCase(userName);
    }
}
