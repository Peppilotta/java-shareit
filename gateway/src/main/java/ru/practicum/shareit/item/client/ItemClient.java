package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.ItemInputDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItems(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "from", from,
                "size", size
        ));
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItem(long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> createItem(long userId, ItemInputDto itemInputDto) {
        return post("", userId, itemInputDto);
    }

    public ResponseEntity<Object> deleteItem(long userId, long itemId) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "itemId", itemId
        ));
        return delete("/" + itemId, userId, parameters);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, Map<String, Object> itemUpdates) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "itemId", itemId
        ));
        parameters.putAll(itemUpdates);
        return patch("/" + itemId, userId, parameters);
    }

    public ResponseEntity<Object> searchItem(long userId, String text, Integer from, Integer size) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "text", text,
                "from", from,
                "size", size
        ));
        return get("/search/?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> saveComment(long itemId, long userId, CommentInputDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
