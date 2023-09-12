package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.request.model.Request;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface RequestMapper {

    Request toRequest(RequestDto requestDto);

    RequestWithProposalsDto toRequestWithProposalDto(Request request);

    RequestDto toDto(Request request);
}