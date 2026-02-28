package com.example.demo.dao;

import java.util.List;
import com.example.demo.model.FriendRequest;

public interface FriendDAO {

    void sendFriendRequest(int userId, int friendId) throws Exception;

    void acceptFriendRequest(int requestId) throws Exception;

    void rejectFriendRequest(int requestId) throws Exception;

    List<String> getFriends(int userId) throws Exception;

    List<FriendRequest> getPendingRequests(int userId) throws Exception;

    boolean areFriends(int user1, int user2) throws Exception;

    boolean isRequestPending(int senderId, int receiverId) throws Exception;
}

