package com.example.demo;

import com.example.demo.dao.UserDAO;
import com.example.demo.dao.impl.UserDAOImpl;
import com.example.demo.model.User;
import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.demo.dao.PostDAO;
import com.example.demo.dao.impl.PostDAOImpl;
import com.example.demo.model.Post;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class Seeder {
    public static void seed() throws Exception {
        System.out.println("Seeding data...");
        UserDAO userDAO = new UserDAOImpl();
        User user1 = new User("John Doe", "john.doe@example.com",
                BCrypt.withDefaults().hashToString(12, "password123".toCharArray()));
        User user2 = new User("Jane Smith", "jane.smith@example.com",
                BCrypt.withDefaults().hashToString(12, "password456".toCharArray()));
        User user3 = new User("Alice Jones", "alice.jones@example.com",
                BCrypt.withDefaults().hashToString(12, "password789".toCharArray()));
        User user4 = new User("Bob Brown", "bob.brown@example.com",
                BCrypt.withDefaults().hashToString(12, "password101".toCharArray()));
        User user5 = new User("Charlie Davis", "charlie.davis@example.com",
                BCrypt.withDefaults().hashToString(12, "password121".toCharArray()));
        createUserIfNotExists(userDAO, user1);
        createUserIfNotExists(userDAO, user2);
        createUserIfNotExists(userDAO, user3);
        createUserIfNotExists(userDAO, user4);
        createUserIfNotExists(userDAO, user5);

        System.out.println("seed user posts");
        PostDAO postDAO = new PostDAOImpl();
        Post post1 = new Post(user1.getId(), "Hello, world!", "public");
        Post post2 = new Post(user2.getId(), "Hello, world!", "friends");
        Post post3 = new Post(user3.getId(), "Hello, world!", "friends");
        Post post4 = new Post(user4.getId(), "Hello, world!", "private");
        Post post5 = new Post(user5.getId(), "Hello, world!", "public");
        postDAO.createPost(post1);
        postDAO.createPost(post2);
        postDAO.createPost(post3);
        postDAO.createPost(post4);
        postDAO.createPost(post5);

    }

    /**
     * Creates user or, if duplicate, fetches existing so user.getId() is valid for
     * posts.
     */
    private static void createUserIfNotExists(UserDAO userDAO, User user) throws Exception {
        try {
            userDAO.createUser(user);
        } catch (Exception e) {
            Throwable t = e;
            while (t != null) {
                if (t instanceof SQLIntegrityConstraintViolationException
                        || (t instanceof SQLException && t.getMessage() != null
                                && t.getMessage().contains("Duplicate entry"))) {
                    User existing = userDAO.getUserByUsername(user.getUsername());
                    if (existing != null) {
                        user.setId(existing.getId());
                    }
                    return;
                }
                t = t.getCause();
            }
            throw new RuntimeException(e);
        }
    }

}

