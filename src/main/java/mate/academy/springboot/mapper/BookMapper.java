package mate.academy.springboot.mapper;

import java.util.List;
import java.util.stream.Collectors;
import mate.academy.springboot.config.MapperConfig;
import mate.academy.springboot.dto.book.BookDto;
import mate.academy.springboot.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.springboot.dto.book.CreateBookRequestDto;
import mate.academy.springboot.model.Book;
import mate.academy.springboot.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        if (book != null && book.getCategories() != null) {
            List<Long> categoryIds = book.getCategories().stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());
            bookDto.setCategoryIds(categoryIds);
        }
    }
}
