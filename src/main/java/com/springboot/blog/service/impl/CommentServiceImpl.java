package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.repository.CommentRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.CommentService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;
    private PostRepository postRepository;

    private ModelMapper mapper;

    @Override
    public CommentDto createComment(long postId, CommentDto commentDto) {

        Comment comment = mapDTOToEntity(commentDto);

        Post post = findPostById(postId);

        comment.setPost(post);

        Comment newComment = commentRepository.save(comment);

        return mapEntityToDTO(newComment);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);

        return comments.stream().map(comment -> mapEntityToDTO(comment)).collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long postId, Long commentId) {
        Post post = findPostById(postId);

        Comment comment = findCommentById(commentId, post);

        return mapEntityToDTO(comment);
    }

    @Override
    public CommentDto updateComment(Long postId, long commentId, CommentDto commentRequest) {

        Post post = findPostById(postId);

        Comment comment = findCommentById(commentId, post);

        comment.setBody(commentRequest.getBody());

        Comment updatedComment = commentRepository.save(comment);
        return mapEntityToDTO(updatedComment);
    }


    @Override
    public void deleteComment(Long postId, Long commentId) {
        Post post = findPostById(postId);

        Comment comment = findCommentById(commentId, post);

        commentRepository.delete(comment);
    }

    private CommentDto mapEntityToDTO(Comment comment){
        return mapper.map(comment, CommentDto.class);
    }

    private Comment mapDTOToEntity(CommentDto commentDto){
        return mapper.map(commentDto, Comment.class);
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId));
    }

    private Comment findCommentById(Long commentId, Post post) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getPost().getId().equals(post.getId())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");
        }

        return comment;
    }
}
