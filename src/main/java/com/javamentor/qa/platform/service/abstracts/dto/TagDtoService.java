package com.javamentor.qa.platform.service.abstracts.dto;

import com.javamentor.qa.platform.models.dto.question.TagDto;

import java.util.List;

public interface TagDtoService {

    List<TagDto> getTrackedTagsByUserId(Long currentUserId);
}