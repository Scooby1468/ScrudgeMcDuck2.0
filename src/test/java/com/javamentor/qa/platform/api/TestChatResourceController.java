package com.javamentor.qa.platform.api;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.javamentor.qa.platform.AbstractClassForDRRiderMockMVCTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class TestChatResourceController extends AbstractClassForDRRiderMockMVCTests {
    //Тесты для SingleChatDTO авторизированного пользователя
    @Test
    @DataSet(cleanBefore = true, value = "dataset/ChatResourceController/getAllSingleChatDtoByUserId.yml"
            , strategy = SeedStrategy.REFRESH)

    public void shouldGetAllSingleChatDtoByUserId() throws Exception {
        //Проверка что API доступно
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user/chat/single")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + getToken("user1@mail.ru", "user1")))
                .andDo(print())
                .andExpect(status().isOk());

        // Проверка на количество всех сингл чатов
        String sqlAll = "select CAST(count(sc.id) as int) from SingleChat sc";
        int countAllSingleChat = (int) entityManager.createQuery(sqlAll).getSingleResult();
        Assertions.assertTrue(countAllSingleChat == 5);

        // Проверка на количество сингл чатов у авторизированного пользователя c id= 1
        String sql = "select CAST(count(sc.id) as int) from SingleChat sc where sc.userOne.id =: userId OR sc.useTwo.id =: userId";
        int countSingleChat = (int) entityManager.createQuery(sql).setParameter("userId", 1L).getSingleResult();
        Assertions.assertTrue(countSingleChat == 3);

        //Проверка на соответствие полей в выборке
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user/chat/single")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + getToken("user1@mail.ru", "user1")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(1)) // id single chat
                .andExpect(jsonPath("$.[0].name").value("user_3")) // nickname not auth
                .andExpect(jsonPath("$.[0].image").value("avatar3.png")) // image not auth
                .andExpect(jsonPath("$.[0].lastMessage").value("Test message №1")) // last message
                .andExpect(jsonPath("$.[0].persistDateTimeLastMessage").value("2022-06-23T23:02:51.654")) // date message
                .andExpect(jsonPath("$.[1].id").value(2))
                .andExpect(jsonPath("$.[1].name").value("user_2"))
                .andExpect(jsonPath("$.[1].image").value("avatar2.png"))
                .andExpect(jsonPath("$.[1].lastMessage").value("Test message №3"))
                .andExpect(jsonPath("$.[1].persistDateTimeLastMessage").value("2022-04-07T23:02:51.654"))
                .andExpect(jsonPath("$.[2].id").value(5))
                .andExpect(jsonPath("$.[2].name").value("user_3"))
                .andExpect(jsonPath("$.[2].image").value("avatar3.png"))
                .andExpect(jsonPath("$.[2].lastMessage").value("Test message №5"))
                .andExpect(jsonPath("$.[2].persistDateTimeLastMessage").value("2022-06-22T23:02:51.654"));
    }

    @Test
    @DataSet(cleanBefore = true,
            value = {
                    "dataset/ChatResourceController/roles.yml",
                    "dataset/ChatResourceController/users.yml",
                    "dataset/ChatResourceController/group_chat.yml",
                    "dataset/ChatResourceController/chat.yml",
                    "dataset/ChatResourceController/messagesAreEmpty.yml"
            }
    )
    public void testGetGroupChatDtoByIdMustReturnGroupChatWithId101AndEmptyMessages() throws Exception {
        String USER_TOKEN = "Bearer " + getToken("test101@mail.ru", "test101");
        mockMvc.perform(get("/api/user/chat/group/101")
                        .header(AUTHORIZATION, USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("101"))
                .andExpect(jsonPath("$.messages.totalResultCount").value(0))
                .andExpect(jsonPath("$.messages.itemsOnPage").value(0))
                .andExpect(jsonPath("$.messages.items.length()").value(0));
    }

    @Test
    @DataSet(cleanBefore = true,
            value = {
                    "dataset/ChatResourceController/roles.yml",
                    "dataset/ChatResourceController/users.yml",
                    "dataset/ChatResourceController/group_chat.yml",
                    "dataset/ChatResourceController/chat.yml",
                    "dataset/ChatResourceController/messages5.yml"
            }
    )
    public void testGetGroupChatDtoByIdMustReturnGroupChatWithId101AndFiveMessages() throws Exception {
        String USER_TOKEN = "Bearer " + getToken("test101@mail.ru", "test101");
        mockMvc.perform(get("/api/user/chat/group/101")
                        .header(AUTHORIZATION, USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("101"))
                .andExpect(jsonPath("$.messages.totalResultCount").value(5))
                .andExpect(jsonPath("$.messages.itemsOnPage").value(5))
                .andExpect(jsonPath("$.messages.items.length()").value(5));
    }

    @Test
    @DataSet(cleanBefore = true,
            value = {
                    "dataset/ChatResourceController/roles.yml",
                    "dataset/ChatResourceController/users.yml",
                    "dataset/ChatResourceController/group_chat.yml",
                    "dataset/ChatResourceController/chat.yml",
                    "dataset/ChatResourceController/messages25.yml"
            }
    )
    public void testGetGroupChatDtoByIdMustReturnGroupChatWithId101AndFirstPageOfThreeAvailable() throws Exception {
        String USER_TOKEN = "Bearer " + getToken("test101@mail.ru", "test101");
        mockMvc.perform(get("/api/user/chat/group/101")
                        .header(AUTHORIZATION, USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("101"))
                .andExpect(jsonPath("$.messages.totalResultCount").value(25))
                .andExpect(jsonPath("$.messages.totalPageCount").value(3))
                .andExpect(jsonPath("$.messages.currentPageNumber").value(1))
                .andExpect(jsonPath("$.messages.itemsOnPage").value(10))
                .andExpect(jsonPath("$.messages.items.length()").value(10))
                .andExpect(jsonPath("$.messages.items[0].id").value(125))
                .andExpect(jsonPath("$.messages.items[9].id").value(116));
    }

    @Test
    @DataSet(cleanBefore = true,
            value = {
                    "dataset/ChatResourceController/roles.yml",
                    "dataset/ChatResourceController/users.yml",
                    "dataset/ChatResourceController/group_chat.yml",
                    "dataset/ChatResourceController/chat.yml",
                    "dataset/ChatResourceController/messages25.yml"
            }
    )
    public void testGetGroupChatDtoByIdMustReturnGroupChatWithId101AndThirdPageOfThreeAvailable() throws Exception {
        String USER_TOKEN = "Bearer " + getToken("test101@mail.ru", "test101");
        mockMvc.perform(get("/api/user/chat/group/101?currentPage=3")
                        .header(AUTHORIZATION, USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("101"))
                .andExpect(jsonPath("$.messages.totalResultCount").value(25))
                .andExpect(jsonPath("$.messages.totalPageCount").value(3))
                .andExpect(jsonPath("$.messages.currentPageNumber").value(3))
                .andExpect(jsonPath("$.messages.itemsOnPage").value(5))
                .andExpect(jsonPath("$.messages.items.length()").value(5))
                .andExpect(jsonPath("$.messages.items[0].id").value(105))
                .andExpect(jsonPath("$.messages.items[4].id").value(101));
    }

    @Test
    @DataSet(cleanBefore = true,
            value = {
                    "dataset/ChatResourceController/roles.yml",
                    "dataset/ChatResourceController/users.yml",
                    "dataset/ChatResourceController/group_chat.yml",
                    "dataset/ChatResourceController/chat.yml",
                    "dataset/ChatResourceController/messages25.yml"
            }
    )
    public void testGetGroupChatDtoByIdMustReturnGroupChatWithId101AndFirstPageWithItemsOnPageValueIsFifteen() throws Exception {
        String USER_TOKEN = "Bearer " + getToken("test101@mail.ru", "test101");
        mockMvc.perform(get("/api/user/chat/group/101?itemsOnPage=15")
                        .header(AUTHORIZATION, USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("101"))
                .andExpect(jsonPath("$.messages.totalResultCount").value(25))
                .andExpect(jsonPath("$.messages.totalPageCount").value(2))
                .andExpect(jsonPath("$.messages.currentPageNumber").value(1))
                .andExpect(jsonPath("$.messages.itemsOnPage").value(15))
                .andExpect(jsonPath("$.messages.items.length()").value(15))
                .andExpect(jsonPath("$.messages.items[0].id").value(125))
                .andExpect(jsonPath("$.messages.items[14].id").value(111));
    }

    @Test
    @DataSet(cleanBefore = true,
            value = {
                    "dataset/ChatResourceController/roles.yml",
                    "dataset/ChatResourceController/users.yml",
                    "dataset/ChatResourceController/group_chat.yml",
                    "dataset/ChatResourceController/chat.yml",
                    "dataset/ChatResourceController/messages25.yml"
            }
    )
    public void testGetGroupChatDtoByIdMustReturnGroupChatWithId101AndSecondPageWithItemsOnPageValueIsFifteen() throws Exception {
        String USER_TOKEN = "Bearer " + getToken("test101@mail.ru", "test101");
        mockMvc.perform(get("/api/user/chat/group/101?itemsOnPage=15&currentPage=2")
                        .header(AUTHORIZATION, USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("101"))
                .andExpect(jsonPath("$.messages.totalResultCount").value(25))
                .andExpect(jsonPath("$.messages.totalPageCount").value(2))
                .andExpect(jsonPath("$.messages.currentPageNumber").value(2))
                .andExpect(jsonPath("$.messages.itemsOnPage").value(10))
                .andExpect(jsonPath("$.messages.items.length()").value(10))
                .andExpect(jsonPath("$.messages.items[0].id").value(110))
                .andExpect(jsonPath("$.messages.items[9].id").value(101));
    }

    @Test
    @DataSet(cleanBefore = true,
            value = {
                    "dataset/ChatResourceController/roles.yml",
                    "dataset/ChatResourceController/users.yml",
                    "dataset/ChatResourceController/group_chat.yml",
                    "dataset/ChatResourceController/chat.yml",
                    "dataset/ChatResourceController/messages25.yml"
            }
    )
    public void testGetGroupChatDtoByIdWithIncorrectChatId() throws Exception {
        String USER_TOKEN = "Bearer " + getToken("test101@mail.ru", "test101");
        mockMvc.perform(get("/api/user/chat/group/102")
                        .header(AUTHORIZATION, USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DataSet(cleanBefore = true,
            value = "dataset/ChatResourceController/testGetPagedMessagesOfSingleChat/testSingleChatWithTenMessages.yml"
    )
    public void testGetPagedMessagesOfSingleChatWithTenMessages() throws Exception {
        String USER_TOKEN = "Bearer " + getToken("test102@mail.ru", "test102");
        mockMvc.perform(get("/api/user/chat/101/single/message")
                        .header(AUTHORIZATION, USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPageNumber").value(1))
                .andExpect(jsonPath("$.totalResultCount").value(10));
    }

    @Test
    @DataSet(cleanBefore = true,
            value = "dataset/ChatResourceController/testGetPagedMessagesOfSingleChat/testGetPagedMessagesOfSingleChatWithThreeChatsAndThreeUsers.yml"
    )
    public void testGetPagedMessagesOfSingleChatWithThreeChatsAndThreeUsers() throws Exception {
        String USER_TOKEN_101 = "Bearer " + getToken("test101@mail.ru", "test101");
        mockMvc.perform(get("/api/user/chat/101/single/message")
                        .header(AUTHORIZATION, USER_TOKEN_101)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPageNumber").value(1))
                .andExpect(jsonPath("$.totalResultCount").value(5));
        String USER_TOKEN_102 = "Bearer " + getToken("test102@mail.ru", "test102");
        mockMvc.perform(get("/api/user/chat/102/single/message")
                        .header(AUTHORIZATION, USER_TOKEN_102)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPageNumber").value(1))
                .andExpect(jsonPath("$.totalResultCount").value(5));
        String USER_TOKEN_103 = "Bearer " + getToken("test103@mail.ru", "test103");
        mockMvc.perform(get("/api/user/chat/103/single/message")
                        .header(AUTHORIZATION, USER_TOKEN_103)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPageNumber").value(1))
                .andExpect(jsonPath("$.totalResultCount").value(5));
    }
}
