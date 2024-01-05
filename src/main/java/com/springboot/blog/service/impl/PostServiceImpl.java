package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Post;
import com.springboot.blog.entity.User;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.service.PostService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private ModelMapper mapper;


    @Override
    public PostDto createPost(PostDto postDto) {

        Post post = mapDTOToEntity(postDto);

        User creator = getLoggedInUser();
        post.setPostCreator(creator);

        Post newPost = postRepository.save(post);

        return mapEntityToDTO(newPost);
    }

    @Override
    public List<PostDto> getAllPosts() {

        List<Post> posts = postRepository.findAll();

        return posts.stream().map(post -> mapEntityToDTO(post)).collect(Collectors.toList());
    }

    @Override
    public PostDto getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return mapEntityToDTO(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        validatePostOwner(post);

        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        Post updatedPost = postRepository.save(post);
        return mapEntityToDTO(updatedPost);
    }
    
    @Override
    public void deletePost(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        validatePostOwner(post);

        postRepository.delete(post);
    }



    @Override
    public Map<String, Integer> findMostCommonWords(int topNumberOfWords) {

        List<PostDto> posts = getAllPosts();
        String aggregatedContent = aggregatePostContent(posts);

        Map<String, Integer> wordFrequency = new HashMap<>();
        String[] words = aggregatedContent.toLowerCase().split("\\s+");

        for(String word : words){
            wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
        }
        return wordFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue()
                        .reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(topNumberOfWords)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (entry1, entry2) -> entry1, LinkedHashMap::new));
    }

    private PostDto mapEntityToDTO(Post post){

        return mapper.map(post, PostDto.class);
    }

    private Post mapDTOToEntity(PostDto postDto){

        return mapper.map(postDto, Post.class);
    }

    private User getLoggedInUser(){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication != null && authentication.getName() != null) {
            return userRepository.findByUsername(authentication.getName()).orElse(null);
        }
        return null;
    }

    public void validatePostOwner(Post post){
        User postCreator = post.getPostCreator();
        User loggedInUser = getLoggedInUser();

        if(loggedInUser == null || !Objects.equals(postCreator.getId(), loggedInUser.getId()))
            throw new BlogAPIException(HttpStatus.UNAUTHORIZED, "You are not the creator of this post");
    }

    private String aggregatePostContent(List<PostDto> posts) {
        return posts.stream()
                .map(PostDto::getContent)
                .collect(Collectors.joining(" "));
    }
}
