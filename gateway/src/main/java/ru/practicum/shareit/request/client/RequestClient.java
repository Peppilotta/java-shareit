package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.RequestInputDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> save(long userId, RequestInputDto requestInputDto) {
        return post("", userId, requestInputDto);
    }

    public ResponseEntity<Object> getRequest(long userId, Long requestId) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "requestId", requestId
        ));

        return get("/" + requestId, userId, parameters);
    }

    public ResponseEntity<Object> getRequests(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getPartOfRequests(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all/?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> deleteItem(long userId, long requestId) {
        Map<String, Object> parameters = Map.of(
                "requestId", requestId
        );
        return delete("/" + requestId, userId, parameters);
    }
}