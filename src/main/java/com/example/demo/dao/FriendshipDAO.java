package com.example.demo.dao;

import com.example.demo.model.Friendship;
import java.util.List;

public interface FriendshipDAO {
    Friendship createFriendship(Friendship friendship) throws Exception;
    Friendship getFriendshipById(int id) throws Exception;
    List<Friendship> getFriendsByUserId(int userId) throws Exception;
    boolean areFriends(int userId, int friendId) throws Exception;
    boolean deleteFriendship(int id) throws Exception;
    boolean deleteFriendshipByUsers(int userId, int friendId) throws Exception;
}
