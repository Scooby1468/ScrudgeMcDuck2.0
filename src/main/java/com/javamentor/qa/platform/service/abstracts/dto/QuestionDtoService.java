package com.javamentor.qa.platform.service.abstracts.dto;

import com.javamentor.qa.platform.models.dto.PageDTO;
import com.javamentor.qa.platform.models.dto.QuestionDto;
import com.javamentor.qa.platform.models.dto.question.QuestionCommentDto;
import com.javamentor.qa.platform.models.dto.question.TagDto;
import com.javamentor.qa.platform.models.entity.pagination.PaginationData;
import com.javamentor.qa.platform.service.abstracts.model.ReadWriteService;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface QuestionDtoService{
    Optional<QuestionDto> getQuestionDtoServiceById(Long id);

    List<QuestionCommentDto> getQuestionByIdComment(Long id);

    Map<Long, List<TagDto>> getTagsByQuestionIds(List<Long> questionIds);

    PageDTO<QuestionDto> getPageQuestionsByTagId(Long id, PaginationData data);
}
