package com.springboot.blog.repository;


import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {

    @MockBean
    private CommentRepository commentRepository;

    @Test
    void findByPostIdShouldReturnCommentsForGivenPostId() {

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Post1");
        post.setContent("Some text");

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setBody("First comment");
        comment1.setPost(post);

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setBody("Second comment");
        comment2.setPost(post);

        List<Comment> expectedComments = Arrays.asList(comment1, comment2);

        when(commentRepository.findByPostId(1L)).thenReturn(expectedComments);

        List<Comment> actualComments = commentRepository.findByPostId(1L);

        assertEquals(expectedComments.size(), actualComments.size());
        assertEquals(expectedComments, actualComments);
    }

    @Test
    void findByPostIdShouldReturnEmptyListWhenNoCommentsForGivenPostId() {
        when(commentRepository.findByPostId(99L)).thenReturn(Arrays.asList());

        List<Comment> comments = commentRepository.findByPostId(99L); // Assuming post with ID 99 does not exist

        assertEquals(0, comments.size());
    }

}
