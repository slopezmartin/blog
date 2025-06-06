package com.softwaresapiens.blog.controllers;

import com.softwaresapiens.blog.domain.dtos.PostDto;
import com.softwaresapiens.blog.domain.entities.Post;
import com.softwaresapiens.blog.domain.entities.User;
import com.softwaresapiens.blog.mappers.PostMapper;
import com.softwaresapiens.blog.services.PostService;
import com.softwaresapiens.blog.services.UserService;
import lombok.RequiredArgsConstructor;
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
}
