package ru.practicum.shareit.request.dto;

import lombok.Builder;

@Builder
public class ProposalDto {

    private final Long id;

    private final Long itemId;

    private final String itemName;

    private final Long requesterId;
}