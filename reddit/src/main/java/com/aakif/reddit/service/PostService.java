package com.aakif.reddit.service;


import com.aakif.reddit.dto.PostRequest;
import com.aakif.reddit.dto.PostResponse;
import com.aakif.reddit.exceptions.PostNotFoundException;
import com.aakif.reddit.exceptions.SubredditNotFoundException;
import com.aakif.reddit.mapper.PostMapper;
import com.aakif.reddit.model.Post;
import com.aakif.reddit.model.Subreddit;
import com.aakif.reddit.model.User;

import com.aakif.reddit.repository.PostRepository;
import com.aakif.reddit.repository.SubredditRepository;
import com.aakif.reddit.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {


    private final SubredditRepository subredditRepository;
    private final AuthService authService;
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void save(PostRequest postRequest) {
        //Mapping from PostRequest to PostEntity
        //we need subreddit and user details
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SubredditNotFoundException(postRequest.getSubredditName()));

        postRepository.save(postMapper.map(postRequest, subreddit, authService.getCurrentUser()));
    }

    //get post by id
    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id.toString()));

        return postMapper.maptoDto(post); //returning the post response back to the controller
    }

    //get all posts
    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::maptoDto)
                .collect(Collectors.toList());
    }

    //get posts by subreddit
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new SubredditNotFoundException(subredditId.toString()));

        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.stream().map(postMapper::maptoDto).collect(Collectors.toList());
    }

    //get post by username
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return postRepository.findByUser(user)
                .stream()
                .map(postMapper::maptoDto)
                .collect(Collectors.toList());
    }

}
