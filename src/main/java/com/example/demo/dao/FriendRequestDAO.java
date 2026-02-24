package com.example.demo.dao;

import com.example.demo.model.FriendRequest;
import java.util.List;

public interface FriendRequestDAO {
    FriendRequest createFriendRequest(FriendRequest friendRequest) throws Exception;
    FriendRequest getFriendRequestById(int id) throws Exception;
    List<FriendRequest> getReceivedRequests(int receiverId) throws Exception;
    List<FriendRequest> getSentRequests(int senderId) throws Exception;
    boolean deleteFriendRequest(int id) throws Exception;
    boolean existsRequest(int senderId, int receiverId) throws Exception;
}
