package com.javamentor.qa.platform.dao.abstracts.dto;

import com.javamentor.qa.platform.models.dto.GroupChatDto;
import com.javamentor.qa.platform.models.dto.SingleChatDto;

import java.util.List;
import java.util.Optional;

public interface ChatDtoDao {
    Optional<GroupChatDto> getGroupChatDto(long chatId);
    List<SingleChatDto> getAllSingleChatDtoByUserId(Long userId);

}
