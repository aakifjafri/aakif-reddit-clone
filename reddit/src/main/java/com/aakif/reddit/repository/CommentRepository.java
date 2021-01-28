package com.aakif.reddit.repository;


import com.aakif.reddit.model.Comment;
import com.aakif.reddit.model.Post;
import com.aakif.reddit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost(Post post);
    List<Comment> findAllByUser(User user);

}
