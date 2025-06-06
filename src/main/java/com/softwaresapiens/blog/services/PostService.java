package com.softwaresapiens.blog.services;

import com.softwaresapiens.blog.domain.CreatePostRequest;
import com.softwaresapiens.blog.domain.PostStatus;
import com.softwaresapiens.blog.domain.UpdatePostRequest;
import com.softwaresapiens.blog.domain.entities.Category;
import com.softwaresapiens.blog.domain.entities.Post;
import com.softwaresapiens.blog.domain.entities.Tag;
import com.softwaresapiens.blog.domain.entities.User;
import com.softwaresapiens.blog.repositories.PostRepository;
import com.softwaresapiens.blog.repositories.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final TagService tagService;
    private static final int WORDS_PER_MINUTE = 200;
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<Post> getAllPosts(UUID categoryId, UUID tagId) {
        if(categoryId != null && tagId != null) {
            Category category = categoryService.getCategoryById(categoryId);
            Tag tag = tagService.getTagById(tagId);
            return postRepository.findAllByStatusAndCategoryAndTagsContaining(
                    PostStatus.PUBLISHED,
                    category,
                    tag);
        }

        if(categoryId != null){
            Category category = categoryService.getCategoryById(categoryId);
            return postRepository.findAllByStatusAndCategory(
                    PostStatus.PUBLISHED,
                    category);
        }

        if(tagId != null){
            Tag tag = tagService.getTagById(tagId);
            return postRepository.findAllByStatusAndTags(
                    PostStatus.PUBLISHED,
                    tag);
        }
        return postRepository.findAllByStatus(
                PostStatus.PUBLISHED);

    }

    public List<Post> getDraftPost(User user){
            return postRepository.findAllByAuthorAndStatus(user, PostStatus.DRAFT);
    }

    @Transactional
    public Post createPost(User user, CreatePostRequest createPostRequest){
        Post newPost = new Post();
        newPost.setTitle(createPostRequest.getTitle());
        newPost.setContent(createPostRequest.getContent());
        newPost.setStatus(createPostRequest.getStatus());
        newPost.setAuthor(user);
        newPost.setReadingTime(this.calculateReadingTime(createPostRequest.getContent()));
        Category category = categoryService.getCategoryById(createPostRequest.getCategoryId());
        newPost.setCategory(category);

        Set<UUID> tagIds = createPostRequest.getTagIds();
        List<Tag>  tags = tagService.getTagsById(tagIds);
        newPost.setTags(new HashSet<>(tags));

        return postRepository.save(newPost);
    }

    @Transactional
    public Post updatePost(UUID id, UpdatePostRequest updatePostRequest){
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        existingPost.setTitle(updatePostRequest.getTitle());
        existingPost.setContent(updatePostRequest.getContent());
        existingPost.setStatus(updatePostRequest.getStatus());
        existingPost.setReadingTime(this.calculateReadingTime(updatePostRequest.getContent()));

        UUID updatePostRequestCategoryId = updatePostRequest.getCategoryId();
        if(!existingPost.getCategory().getId().equals(updatePostRequestCategoryId) ) {
            Category category = categoryService.getCategoryById(updatePostRequest.getCategoryId());
            existingPost.setCategory(category);
        }
        Set<UUID> existingTagIds = existingPost.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
        Set<UUID> updatePostRequestTagIds = updatePostRequest.getTagIds();
        if(!existingTagIds.equals(updatePostRequestTagIds)) {
            List<Tag>  newTags = tagService.getTagsById(updatePostRequestTagIds);
            existingPost.setTags(new HashSet<>(newTags));
        }

        return postRepository.save(existingPost);
    }

    @Transactional
    public void deletePost(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        postRepository.delete(post);
    }

    public Post getPost(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
    }


    private Integer calculateReadingTime(String content) {
        // Assuming an average reading speed of 200 words per minute
        if(content == null || content.isEmpty()) {
            return 0;

        }
        int wordCount = content.trim().split("\\s+").length;
        return (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);

    }
}
