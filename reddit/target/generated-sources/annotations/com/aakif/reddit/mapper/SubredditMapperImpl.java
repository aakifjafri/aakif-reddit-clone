package com.aakif.reddit.mapper;

import com.aakif.reddit.dto.SubredditDto;
import com.aakif.reddit.dto.SubredditDto.SubredditDtoBuilder;
import com.aakif.reddit.model.Subreddit;
import com.aakif.reddit.model.Subreddit.SubredditBuilder;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-01-15T21:29:41+0530",
    comments = "version: 1.4.1.Final, compiler: javac, environment: Java 11.0.7 (Amazon.com Inc.)"
)
@Component
public class SubredditMapperImpl implements SubredditMapper {

    @Override
    public SubredditDto mapSubredditToDto(Subreddit subreddit) {
        if ( subreddit == null ) {
            return null;
        }

        SubredditDtoBuilder subredditDto = SubredditDto.builder();

        subredditDto.id( subreddit.getId() );
        subredditDto.name( subreddit.getName() );
        subredditDto.description( subreddit.getDescription() );

        subredditDto.numberOfPosts( mapPosts(subreddit.getPosts()) );

        return subredditDto.build();
    }

    @Override
    public Subreddit mapDtoToSubreddit(SubredditDto subredditDto) {
        if ( subredditDto == null ) {
            return null;
        }

        SubredditBuilder subreddit = Subreddit.builder();

        subreddit.id( subredditDto.getId() );
        subreddit.name( subredditDto.getName() );
        subreddit.description( subredditDto.getDescription() );

        return subreddit.build();
    }
}
