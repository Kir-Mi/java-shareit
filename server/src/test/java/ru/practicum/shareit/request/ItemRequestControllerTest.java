package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    private final ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void addRequest_ReturnsAddedRequest() throws Exception {
        ItemRequestResponse requestResponse = createSampleRequestResponse();
        when(itemRequestService.addItemRequest(any())).thenReturn(requestResponse);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestResponse))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestResponse.getId()));

        verify(itemRequestService).addItemRequest(ItemRequestDto.builder().description("description").requestorId(1).build());
    }

    @Test
    void getRequestsOfUser_ReturnsListOfRequests() throws Exception {
        List<ItemRequestResponse> requestResponses = Collections.singletonList(createSampleRequestResponse());
        when(itemRequestService.getItemRequestsOfUser(anyInt())).thenReturn(requestResponses);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestResponses.get(0).getId()));
    }

    @Test
    void getAllRequests_ReturnsListOfRequests() throws Exception {
        List<ItemRequestResponse> requestResponses = Collections.singletonList(createSampleRequestResponse());
        when(itemRequestService.getItemRequestsNotOfUser(anyInt(), anyInt(), anyInt())).thenReturn(requestResponses);

        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestResponses.get(0).getId()));
    }

    @Test
    void getRequest_ReturnsRequest() throws Exception {
        ItemRequestResponse requestResponse = createSampleRequestResponse();
        when(itemRequestService.getItemRequestById(anyInt(), anyInt())).thenReturn(requestResponse);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestResponse.getId()));
    }

    private ItemRequestResponse createSampleRequestResponse() {
        return ItemRequestResponse.builder()
                .id(1)
                .created(LocalDateTime.of(2023, 8, 10, 10, 0))
                .description("description")
                .items(Collections.singletonList(ItemResponse.builder()
                        .id(1)
                        .name("name")
                        .build()))
                .build();
    }
}
