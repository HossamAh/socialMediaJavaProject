package com.example.demo.dao;

import com.example.demo.model.Post;
import java.util.List;

public interface PostDAO {
    Post createPost(Post post) throws Exception;
    Post getPostById(int id) throws Exception;
    List<Post> getPostsByUserId(int userId) throws Exception;
    List<Post> getPostsCreatedByUserId(int userId) throws Exception;
    List<Post> getPostsByUserIdPaginated(int userId, int page, int pageSize) throws Exception;

    List<Post> getAllPosts() throws Exception;
    List<Post> getPublicPosts() throws Exception;
    Post updatePost(Post post) throws Exception;
    boolean deletePost(int id) throws Exception;
}
