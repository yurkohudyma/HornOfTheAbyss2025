package ua.hudyma.mapper;

import java.util.List;
import java.util.function.Function;

public abstract class BaseMapper<RESP_DTO, ENTITY, REQ_DTO> {

    public List<RESP_DTO> toDtoList(List<ENTITY> entities) {
        return mapList(entities, this::toDto);
    }
    public List<ENTITY> toEntityList(List<REQ_DTO> dtos) {
        return mapList(dtos, this::toEntity);
    }
    public abstract RESP_DTO toDto(ENTITY entity);
    public abstract ENTITY toEntity(REQ_DTO dto);

    protected <TYPE, RESULT> List<RESULT> mapList(
            List<TYPE> source,
            Function<TYPE, RESULT> mapper) {
        if (source == null || source.isEmpty()) {
            return List.of();
        }
        return source.stream().map(mapper).toList();
    }
    public <T> List<String> getEntityFieldList(
            List<T> entityList,
            Function<T, String> mapper) {
        return entityList.stream().map(mapper).toList();
    }
}

