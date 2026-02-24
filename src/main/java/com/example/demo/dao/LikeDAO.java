package com.example.demo.dao;

import com.example.demo.model.Like;
import java.util.List;

public interface LikeDAO {
    Like createLike(Like like) throws Exception;
    Like getLikeById(int id) throws Exception;
    List<Like> getLikesByPostId(int postId) throws Exception;
    List<Like> getLikesByUserId(int userId) throws Exception;
    int getLikeCountByPostId(int postId) throws Exception;
    boolean deleteLike(int id) throws Exception;
    boolean deleteLikeByUserAndPost(int userId, int postId) throws Exception;
}
