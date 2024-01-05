package com.springboot.blog.service;

import com.springboot.blog.payload.PostDto;

import java.util.List;
import java.util.Map;

public interface PostService {
    PostDto createPost(PostDto postDto);

    List<PostDto> getAllPosts();

    PostDto getPostById(long id);

    PostDto updatePost(PostDto postDto, long id);

    void deletePost(long id);

    Map<String, Integer> findMostCommonWords( int topNumberOfWords);

}
