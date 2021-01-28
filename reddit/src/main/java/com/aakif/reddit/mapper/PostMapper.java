package com.aakif.reddit.mapper;

import com.aakif.reddit.dto.PostRequest;
import com.aakif.reddit.dto.PostResponse;
import com.aakif.reddit.model.*;
import com.aakif.reddit.repository.CommentRepository;
import com.aakif.reddit.repository.VoteRepository;
import com.aakif.reddit.service.AuthService;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.aakif.reddit.model.VoteType.DOWNVOTE;
import static com.aakif.reddit.model.VoteType.UPVOTE;

// we are changing this interface to abstract class bcz we added 4 new fields inside our dto
// and we need some dependencies to fill these details..


@Mapper(componentModel = "spring")
public abstract class PostMapper {

    @Autowired
    private CommentRepository commentRepository; //for commentCount
    @Autowired
    private VoteRepository voteRepository; //for voteCount
    @Autowired
    private AuthService authService; //for calculating the vote count

    // mapping method to create a post from the postRequest object
    // To create a post we need Subreddit and User details also
    //This value will be mapped to the created date field inside Post
    //same for others

    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target="description", source = "postRequest.description")
    @Mapping(target = "subreddit", source = "subreddit")
    //newly added
    @Mapping(target = "voteCount", constant = "0") //default voteCount = 0
    @Mapping(target= "user", source = "user")
    public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);

    //mapping from dto to post response

    @Mapping(target="id", source = "postId")
    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "commentCount", expression = "java(commentCount(post))")
    @Mapping(target = "duration", expression = "java(getDuration(post))")
    public abstract PostResponse maptoDto(Post post);

    Integer commentCount(Post post) {
        return commentRepository.findByPost(post).size();
    }

    String getDuration(Post post) {
        return TimeAgo.using(post.getCreatedDate().toEpochMilli());
    }

    boolean isPostUpVoted(Post post) {
        return checkVoteType(post, UPVOTE);
    }

    boolean isPostDownVoted(Post post) {
        return checkVoteType(post, DOWNVOTE);
    }

    private boolean checkVoteType(Post post, VoteType voteType) {
        if (authService.isLoggedIn()) {
            Optional<Vote> voteForPostByUser =
                    voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post,
                            authService.getCurrentUser());
            return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType))
                    .isPresent();
        }
        return false;
    }
}
