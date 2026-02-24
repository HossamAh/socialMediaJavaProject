package com.example.demo.dao;

import com.example.demo.model.Comment;
import java.util.List;

public interface CommentDAO {
    Comment createComment(Comment comment) throws Exception;
    Comment getCommentById(int id) throws Exception;
    List<Comment> getCommentsByPostId(int postId) throws Exception;
    List<Comment> getCommentsByUserId(int userId) throws Exception;
    Comment updateComment(Comment comment) throws Exception;
    boolean deleteComment(int id) throws Exception;
}
