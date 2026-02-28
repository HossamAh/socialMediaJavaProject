package com.example.demo.dao;

import com.example.demo.model.User;
import java.util.List;

public interface UserDAO {
    User createUser(User user) throws Exception;
    User getUserById(int id) throws Exception;
    User getUserByUsername(String username) throws Exception;
    User getUserByEmail(String email) throws Exception;
    List<User> getAllUsers() throws Exception;
    User updateUser(User user) throws Exception;
    boolean deleteUser(int id) throws Exception;
    boolean authenticate(String username, String password) throws Exception;
    List<User> searchUsers(String keyword) throws Exception;
}
