package com.softwaresapiens.blog.controllers;

import com.softwaresapiens.blog.domain.dtos.PostDto;
import com.softwaresapiens.blog.domain.entities.Post;
import com.softwaresapiens.blog.mappers.PostMapper;
import com.softwaresapiens.blog.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;

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
}
