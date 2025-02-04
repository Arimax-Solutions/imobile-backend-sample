package com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl;

import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.User;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.UserRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceIMPL implements UserService {
    private final ModelMapper mapper;
    private final UserRepository userRepository;
    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public void save(User data) {
        //implement that
    }

    @Override
    public void update(User data) {
        try {
            Optional<User> userOptional = userRepository.findById(data.getUser_id());

            if (userOptional.isPresent() && !userOptional.get().is_deleted()) {
                User existingUser = userOptional.get();

                existingUser.setName(data.getName());
                existingUser.setEmail(data.getEmail());
                existingUser.setRole(data.getRole());
                existingUser.setContact_number(data.getContact_number());
                existingUser.setUsername(data.getUsername());
                //existingUser.setPassword(data.getPassword());

                userRepository.save(existingUser);
            }
        } catch (Exception e) {
            log.error("Error updating user: ", e);
            throw e;
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (!userOptional.isPresent()) {
                throw new RuntimeException("User not found");
            }
            User user = userOptional.get();
            user.set_deleted(true);
            userRepository.save(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    @Override
    public User findById(Integer id) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent() && !userOptional.get().is_deleted()) {
                return userOptional.get();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Error finding user by ID: ", e);
            throw e;
        }
    }

    @Override
    public List<User> findAll() {
        try {
            List<User> allUsers = userRepository.findAll();
            List<User> activeUsers = allUsers.stream()
                    .filter(user -> !user.is_deleted())
                    .collect(Collectors.toList());

            // Decode passwords for active users
            for (User user : activeUsers) {
                String decodedPassword = decodePassword(user.getPassword());
                if (decodedPassword != null) {
                    user.setPassword(decodedPassword);
                } else {
                    System.out.println("Failed to decode password for user: " + user.getUsername());
                }
            }

            return activeUsers;
        } catch (Exception e) {
            System.out.println("Error finding users: " + e.getMessage());
            throw e; // Or handle the exception as needed
        }
    }

    public String decodePassword(String encodedPassword) {
        try {
            if (encodedPassword != null && !encodedPassword.isEmpty()) {
                byte[] decodedBytes = Base64.getDecoder().decode(encodedPassword);
                return new String(decodedBytes);
            } else {
                System.out.println("Encoded password is null or empty");
                return null;
            }
        } catch (IllegalArgumentException e) {
            // Handle the error, perhaps log it and return an error message or null
            System.out.println("Error decoding password: " + e.getMessage());
            return null;
        }
    }
}
