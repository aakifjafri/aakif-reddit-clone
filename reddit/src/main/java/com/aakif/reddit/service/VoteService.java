package com.aakif.reddit.service;

import com.aakif.reddit.dto.VoteDto;
import com.aakif.reddit.exceptions.PostNotFoundException;
import com.aakif.reddit.exceptions.SpringRedditException;
import com.aakif.reddit.model.Post;
import com.aakif.reddit.model.Vote;
import com.aakif.reddit.repository.PostRepository;
import com.aakif.reddit.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import static com.aakif.reddit.model.VoteType.UPVOTE;

@Service
@AllArgsConstructor
public class VoteService {

    private final PostRepository postRepository;
    private final VoteRepository voteRepository;
    private final AuthService authService;

    @Transactional
    public void vote(VoteDto voteDto) {

        //retrieve the post which needs to upvote
        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID - " + voteDto.getPostId()));

        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());

        //this is validation if user has upvoted then can't upvote again.
        if (voteByPostAndUser.isPresent() &&
                            voteByPostAndUser.get().getVoteType()
                            .equals(voteDto.getVoteType())) {
            throw new SpringRedditException("You have already " + voteDto.getVoteType() + "'d for this post");
        }

        if (UPVOTE.equals(voteDto.getVoteType())) {
            post.setVoteCount(post.getVoteCount() + 1);
        } else {
            post.setVoteCount(post.getVoteCount() - 1);
        }

        //mapping voteDto to vote
        voteRepository.save(mapToVote(voteDto, post));
        postRepository.save(post);
    }

    private Vote mapToVote(VoteDto voteDto, Post post) {
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}
