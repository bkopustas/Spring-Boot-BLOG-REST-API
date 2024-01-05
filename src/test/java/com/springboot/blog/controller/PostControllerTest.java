package com.springboot.blog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.springboot.blog.entity.Post;
import com.springboot.blog.entity.User;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.service.PostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan(basePackages = "com.springboot.blog.config")
@WebMvcTest(PostController.class)
public class PostControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModelMapper mapper;


    @MockBean
    private PostService postService;
    Post post1 = new Post();
    Post post2 = new Post();
    List<Post> posts = new ArrayList<>();

    User postCreator = new User();

    @BeforeEach
    void setUp() {

        postCreator.setId(1L);
        postCreator.setUsername("user1");
        postCreator.setPassword("user1");
        postCreator.setEmail("email@email.com");


        post1.setId(1L);
        post1.setTitle("Title1");
        post1.setContent("Content1");
        post1.setPostCreator(postCreator);

        post2.setId(2L);
        post2.setTitle("Title2");
        post2.setContent("Content2");

        posts.add(post1);
        posts.add(post2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @WithMockUser(username = "user1", password = "user1")
    void shouldCreatePostTest() throws Exception {

        PostDto newPost = new PostDto();
        newPost.setContent("Content");
        newPost.setTitle("Title123");

        when(postService.createPost(newPost))
                .thenReturn(newPost);
        this.mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newPost)))
                .andDo(print()).andExpect(status().isCreated());
    }

    @Test
    void shouldGetAllPostsTest() throws Exception {
        List<PostDto> postsList = posts.stream().map(post -> mapEntityToDTO(post)).toList();

        when(postService.getAllPosts())
                .thenReturn(postsList);
        this.mockMvc.perform(get("/api/posts"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void shouldGetPostByIdTest() throws Exception {
        PostDto postDto = mapEntityToDTO(post1);
        when(postService.getPostById(1))
                .thenReturn(postDto);
        this.mockMvc.perform(get("/api/posts/1"))
                .andDo(print()).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "user1", password = "user1")
    void shouldUpdatePostTest() throws Exception {

        PostDto updatedPost = new PostDto();
        updatedPost.setId(1L);
        updatedPost.setContent("Updated Content");
        updatedPost.setTitle("Updated Title123");

        when(postService.updatePost(updatedPost, 1L))
                .thenReturn(updatedPost);
        this.mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedPost)))
                .andDo(print()).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "user1", password = "user1")
    void shouldDeletePostTest() throws Exception {
        doNothing().when(postService).deletePost(1);
        this.mockMvc.perform(delete("/api/posts/1"))
                .andDo(print()).andExpect(status().isOk());
    }

    private PostDto mapEntityToDTO(Post post){


        return mapper.map(post, PostDto.class);
    }
}
