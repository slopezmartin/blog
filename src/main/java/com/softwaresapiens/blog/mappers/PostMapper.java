package com.softwaresapiens.blog.mappers;

import com.softwaresapiens.blog.domain.CreatePostRequest;
import com.softwaresapiens.blog.domain.UpdatePostRequest;
import com.softwaresapiens.blog.domain.dtos.CreatePostRequestDto;
import com.softwaresapiens.blog.domain.dtos.PostDto;
import com.softwaresapiens.blog.domain.dtos.UpdatePostRequestDto;
import com.softwaresapiens.blog.domain.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PostMapper {


    @Mapping(target = "author", source = "author")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "tags", source = "tags")
    PostDto toDto(Post post);

    CreatePostRequest toCreatePostRequest(CreatePostRequestDto createPostRequestDto);

    UpdatePostRequest toUpdatePostRequest(UpdatePostRequestDto updatePostRequestDto);

}
