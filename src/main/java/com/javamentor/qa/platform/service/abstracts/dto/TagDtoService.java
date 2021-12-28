package com.javamentor.qa.platform.service.abstracts.dto;


import com.javamentor.qa.platform.models.dto.TagDto;
import com.javamentor.qa.platform.models.dto.question.TagDto;


import java.util.List;

public interface TagDtoService {

    List<TagDto> getTagDtoServiceById(Long id);
    List<TagDto> getTrackedTagsByUserId(Long currentUserId);

}
