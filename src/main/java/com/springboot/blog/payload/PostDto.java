package com.springboot.blog.payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;


@Data
public class PostDto {
    private long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
    private Set<CommentDto> comments;
}
