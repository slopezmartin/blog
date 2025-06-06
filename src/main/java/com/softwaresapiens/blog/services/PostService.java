package com.softwaresapiens.blog.services;

import com.softwaresapiens.blog.domain.PostStatus;
import com.softwaresapiens.blog.domain.entities.Category;
import com.softwaresapiens.blog.domain.entities.Post;
import com.softwaresapiens.blog.domain.entities.Tag;
import com.softwaresapiens.blog.domain.entities.User;
import com.softwaresapiens.blog.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final TagService tagService;

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
}
