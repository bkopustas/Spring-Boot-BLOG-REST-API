package com.springboot.blog.service;

import com.springboot.blog.entity.Post;
import com.springboot.blog.entity.User;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setContent("Test Content");
        testPost.setPostCreator(testUser);
    }

    @Test
    void getAllPostsTest() {
        // Arrange
        when(postRepository.findAll()).thenReturn(Collections.singletonList(testPost));
        when(modelMapper.map(any(Post.class), eq(PostDto.class))).thenReturn(new PostDto());

        // Act
        List<PostDto> result = postService.getAllPosts();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findAll();
    }

    @Test
    void getPostByIdTest() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(modelMapper.map(any(Post.class), eq(PostDto.class))).thenReturn(new PostDto());

        // Act
        PostDto result = postService.getPostById(1L);

        // Assert
        assertNotNull(result);
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void getPostByIdShouldThrowResourceNotFoundExceptionTest() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.getPostById(1L));
    }


    @Test
    void updatePostShouldThrowResourceNotFoundExceptionTest() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.updatePost(new PostDto(), 1L));
    }

    @Test
    void updatePostShouldThrowBlogAPIExceptionWhenUserIsNotPostCreatorTest() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otherUser");

        testPost.setPostCreator(otherUser);
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        // Act and Assert
        assertThrows(BlogAPIException.class, () -> postService.updatePost(new PostDto(), 1L));
    }


    @Test
    void deletePostShouldThrowResourceNotFoundExceptionTest() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(1L));
    }

    @Test
    void deletePostShouldThrowBlogAPIExceptionWhenUserIsNotPostCreatorTest() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otherUser");

        testPost.setPostCreator(otherUser);
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        // Act and Assert
        assertThrows(BlogAPIException.class, () -> postService.deletePost(1L));
    }

}
