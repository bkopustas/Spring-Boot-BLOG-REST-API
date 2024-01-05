package com.springboot.blog.payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CommentDto {
    private long id;
    @NotEmpty
    private String body;
}
