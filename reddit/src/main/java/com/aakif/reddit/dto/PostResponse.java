package com.aakif.reddit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private Long id;
    private String postName;
    private String url;
    private String description;
    private String userName;
    private String subredditName;
    //new fields
    private Integer voteCount;
    private Integer commentCount;
    private String duration; //shows relative time of duration of the post creation time. using library called timeago
    //like commented 4 days ago or minutes ago etc.
}
