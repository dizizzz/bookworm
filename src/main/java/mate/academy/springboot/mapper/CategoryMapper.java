package mate.academy.springboot.mapper;

import mate.academy.springboot.config.MapperConfig;
import mate.academy.springboot.dto.category.CategoryDto;
import mate.academy.springboot.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDto);
}
