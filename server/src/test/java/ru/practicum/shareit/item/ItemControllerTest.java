package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    private final ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    private CommentService commentService;

    @Test
    void create_ReturnsCreatedItem() throws Exception {
        ItemDto itemDto = createSampleItemDto();
        when(itemService.create(anyInt(), any())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));
    }

    @Test
    void update_ReturnsUpdatedItem() throws Exception {
        ItemDto itemDto = createSampleItemDto();
        when(itemService.update(anyInt(), anyInt(), any())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));
    }

    @Test
    void getItemById_ReturnsItem() throws Exception {
        ItemDto itemDto = createSampleItemDto();
        when(itemService.getItemById(anyInt(), anyInt())).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));
    }

    @Test
    void getItemsByUser_ReturnsListOfItems() throws Exception {
        List<ItemDto> itemDtos = Collections.singletonList(createSampleItemDto());
        when(itemService.getItemsByUser(anyInt(), anyInt(), anyInt())).thenReturn(itemDtos);

        mockMvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDtos.get(0).getId()));
    }

    @Test
    void search_ReturnsListOfItems() throws Exception {
        List<ItemDto> itemDtos = Collections.singletonList(createSampleItemDto());
        when(itemService.search(any(), anyInt(), anyInt())).thenReturn(itemDtos);

        mockMvc.perform(get("/items/search")
                        .param("text", "search_text")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDtos.get(0).getId()));
    }

    @Test
    void postComment_ReturnsPostedComment() throws Exception {
        CommentResponse commentResponse = createSampleCommentResponse();
        when(commentService.saveComment(any())).thenReturn(commentResponse);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentResponse))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentResponse.getId()));
    }

    private ItemDto createSampleItemDto() {
        return ItemDto.builder()
                .id(1)
                .ownerId(1)
                .name("name")
                .description("description")
                .available(true)
                .build();
    }

    private CommentResponse createSampleCommentResponse() {
        return CommentResponse.builder()
                .id(1)
                .text("text")
                .authorName("authorName")
                .created(LocalDateTime.of(2023, 8, 10, 10, 0))
                .build();
    }
}