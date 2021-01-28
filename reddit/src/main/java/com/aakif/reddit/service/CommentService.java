package com.aakif.reddit.service;

import com.aakif.reddit.dto.CommentsDto;
import com.aakif.reddit.exceptions.PostNotFoundException;
import com.aakif.reddit.mapper.CommentMapper;
import com.aakif.reddit.model.Comment;
import com.aakif.reddit.model.NotificationEmail;
import com.aakif.reddit.model.Post;
import com.aakif.reddit.model.User;
import com.aakif.reddit.repository.CommentRepository;
import com.aakif.reddit.repository.PostRepository;
import com.aakif.reddit.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {

    //as we need postId and username to map commentsDto to comments entity (model)
    //we need postRepository and userRepository
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;

    public void save(CommentsDto commentsDto) {

        Post post = postRepository.findById(commentsDto.getPostId())
                            .orElseThrow(() -> new PostNotFoundException(commentsDto.getPostId().toString()));
        //mapping CommentDto to Post
        Comment comment = commentMapper.map(commentsDto, post, authService.getCurrentUser());
        commentRepository.save(comment);

        //TO:DO - POST_URL
        String message = mailContentBuilder.build(authService.getCurrentUser() + "posted a comment on your post.");
        sendCommentNotification(message, post.getUser());
    }

    public void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername() + "Commented on your post " + user.getEmail(), message));
    }

    public List<CommentsDto> getAllCommentsForPost(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId.toString()));

        //finding all comments associated with this post
        return commentRepository.findByPost(post)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public List<CommentsDto> getAllCommentsForUser(String userName) {

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));

        return commentRepository.findAllByUser(user)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());

    }
}
