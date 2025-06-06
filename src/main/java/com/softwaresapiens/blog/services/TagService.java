package com.softwaresapiens.blog.services;

import com.softwaresapiens.blog.domain.entities.Tag;
import com.softwaresapiens.blog.repositories.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<Tag> getTags(){
        return tagRepository.findAllWithPostCount();
    }

    @Transactional
    public List<Tag> createTags(Set<String> tagNames){
        List<Tag> existingTags = tagRepository.findByNameIn(tagNames);
        Set<String> existingTagNames = existingTags
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
        List<Tag> newTags = tagNames
                .stream()
                .filter(name-> !existingTagNames.contains(name))
                .map(name -> Tag.builder()
                        .name(name)
                        .build())
                .collect(Collectors.toList());

        List<Tag> savedTags = tagRepository.saveAll(newTags);
        if(!newTags.isEmpty()){
            savedTags = tagRepository.saveAll(newTags);
        }
        return savedTags;
    }

    @Transactional
    public void deleteTag(UUID id){
        tagRepository.findById(id).ifPresent(tag -> {
            if (!tag.getPosts().isEmpty()){
                throw new IllegalArgumentException("Cannot delete tag with posts");
            }
        });
        tagRepository.deleteById(id);
    }

    public Tag getTagById(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + id));
    }
}

