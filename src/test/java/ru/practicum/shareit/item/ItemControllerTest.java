package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getItem_StandardBehavior() {
        ReflectionTestUtils.setField(itemController, "itemService", itemService);

        ItemDto itemDto = createItemDto();
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemDto);

        ItemDto expected = itemController.getItem(33L, 33L);
        assertThat((expected.getId()), equalTo(itemDto.getId()));
        assertThat((expected.getName()), equalTo(itemDto.getName()));
        assertThat((expected.getDescription()), equalTo(itemDto.getDescription()));
    }

    @Test
    void getItems_ShouldReturnList() throws Exception {
        ReflectionTestUtils.setField(itemController, "itemService", itemService);

        ItemDto itemDto = createItemDto();
        List<ItemDto> expectedItems = List.of(itemDto);
        when(itemService.getItems(anyLong(), any(), any())).thenReturn(expectedItems);
        mockMvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .param("from", "3")
                        .param("size", "5")
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", equalTo(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", equalTo(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].id").value(equalTo(itemDto.getId()), Long.class));
    }

    @Test
    void create_StandardBehavior() throws Exception {

        ItemDto itemDto = createItemDto();
        when(itemService.createItem(anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(itemDto.getName())))
                .andExpect(jsonPath("$.description", equalTo(itemDto.getDescription())))
                .andExpect(jsonPath("$.id").value(equalTo(itemDto.getId()), Long.class));
    }

    @Test
    void delete_StandardBehavior() throws Exception {
        ItemDto itemDto = createItemDto();
        when(itemService.deleteItem(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(delete("/items/{id}", 33L)
                        .content(mapper.writeValueAsString(itemDto.getId()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(itemDto.getName())))
                .andExpect(jsonPath("$.description", equalTo(itemDto.getDescription())))
                .andExpect(jsonPath("$.id").value(equalTo(itemDto.getId()), Long.class));
    }

    @Test
    void update_StandardBehavior() throws Exception {
        ItemDto itemDtoWithUpdates = createItemDto();

        Map<String, Object> updates = Map.of("name", "супервелотренажёр", "description", "для похудения");

        itemDtoWithUpdates.setName((String) updates.get("name"));
        itemDtoWithUpdates.setName((String) updates.get("description"));
        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(itemDtoWithUpdates);

        mockMvc.perform(patch("/items/{id}", 33L)
                        .content(mapper.writeValueAsString(updates))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(itemDtoWithUpdates.getName())))
                .andExpect(jsonPath("$.description", equalTo(itemDtoWithUpdates.getDescription())))
                .andExpect(jsonPath("$.id").value(equalTo(itemDtoWithUpdates.getId()), Long.class));
    }

    private UserDto createOwnerDto() {
        return UserDto.builder()
                .id(99L)
                .name("Tigran")
                .email("tigran@rubytech.ru")
                .build();
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(33L)
                .name("велотренажёр")
                .description("очень тяжёлый")
                .available(true)
                .owner(createOwnerDto())
                .requestId(100L)
                .build();
    }
}