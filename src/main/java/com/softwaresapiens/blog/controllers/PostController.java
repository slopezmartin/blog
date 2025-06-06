package com.softwaresapiens.blog.controllers;

import com.softwaresapiens.blog.domain.CreatePostRequest;
import com.softwaresapiens.blog.domain.UpdatePostRequest;
import com.softwaresapiens.blog.domain.dtos.CreatePostRequestDto;
import com.softwaresapiens.blog.domain.dtos.PostDto;
import com.softwaresapiens.blog.domain.dtos.UpdatePostRequestDto;
import com.softwaresapiens.blog.domain.entities.Post;
import com.softwaresapiens.blog.domain.entities.User;
import com.softwaresapiens.blog.mappers.PostMapper;
import com.softwaresapiens.blog.services.PostService;
import com.softwaresapiens.blog.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID tagId
    ){
        List<Post> allPosts = postService.getAllPosts(categoryId, tagId);
        List<PostDto> postDtos = allPosts
                .stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(postDtos);
    }

    @GetMapping(path="/drafts")
    public ResponseEntity<List<PostDto>> getDrafts(@RequestAttribute UUID userId) {
        User loggedInUser = userService.getUserById(userId);
        List<Post> draftPost = postService.getDraftPost(loggedInUser);
        List<PostDto> postDtos = draftPost
                .stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(postDtos);
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @Valid @RequestBody CreatePostRequestDto createPostRequestDto,
            @RequestAttribute UUID userId){

        User loggedInUser = userService.getUserById(userId);
        CreatePostRequest createPostRequest = postMapper.toCreatePostRequest(createPostRequestDto);
        Post createdPost = postService.createPost(loggedInUser,createPostRequest);

        PostDto postDto = postMapper.toDto(createdPost);
        return new ResponseEntity<>(postDto, HttpStatus.CREATED);
    }

    @PutMapping(path= "/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePostRequestDto updatePostRequestDto,
            @RequestAttribute UUID userId) {

        User loggedInUser = userService.getUserById(userId);
        UpdatePostRequest updatePostRequest = postMapper.toUpdatePostRequest(updatePostRequestDto);
        Post updatedPost = postService.updatePost(id, updatePostRequest);
        PostDto postDto = postMapper.toDto(updatedPost);
        return ResponseEntity.ok(postDto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable UUID id) {
        Post post = postService.getPost(id);
        PostDto postDto = postMapper.toDto(post);
        return ResponseEntity.ok(postDto);
    }
}
