package mate.academy.springboot.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.academy.springboot.dto.category.CategoryDto;
import mate.academy.springboot.exception.EntityNotFoundException;
import mate.academy.springboot.mapper.CategoryMapper;
import mate.academy.springboot.model.Category;
import mate.academy.springboot.repository.category.CategoryRepository;
import mate.academy.springboot.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    public void setup() {
        category = new Category();
        category.setId(1L);
        category.setName("Category");
        category.setDescription("Description");

        categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
    }

    @Test
    @DisplayName("""
            Given available categories, retrieve the available list of category
            """)
    void findAll_ReturnCategoryList() {
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        when(categoryRepository.findAll()).thenReturn(categories);
        List<CategoryDto> categoryList = categoryService.findAll();
        assertEquals(1, categoryList.size());
    }

    @Test
    @DisplayName("Verify the correct book was returned when book exists")
    void getById_WithValidCategoryId_ShouldReturnValidCategory() {
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);
        CategoryDto actual = categoryService.getById(category.getId());
        assertEquals(category.getId(), actual.getId());
    }

    @Test
    @DisplayName("Given incorrect id, check if returns exception")
    void getById_WithNotExistingCategoryId_ShouldThrowException() {
        Long categoryId = 100L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(categoryId)
        );
        String expected = "Can`t find category by id " + categoryId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify the correct book was saved")
    void save() {
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);
        when(categoryMapper.toEntity(categoryDto)).thenReturn(category);

        CategoryDto actual = categoryService.save(categoryDto);

        assertNotNull(actual);
        assertEquals(category.getId(), actual.getId());
    }

//    @Test
//    @DisplayName("""
//    Verify the correct book was returned when updated book
//     """)
//    void update() {
//        Long categoryId = 1L;
//        String updatedDescription = "Description updated";
//
//        category.setDescription(updatedDescription);
//        categoryDto.setDescription(updatedDescription);
//
//        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
//        when(categoryMapper.toEntity(categoryDto)).thenReturn(category);
//        when(categoryRepository.save(category)).thenReturn(category);
//
//        CategoryDto actual = categoryService.update(categoryId, categoryDto);
//
//        assertEquals(updatedDescription, actual.getDescription());
//    }

    @Test
    @DisplayName("Verify the correct book was returned when updated book")
    void deleteById_VerifyDeletion() {
        Long categoryId = 1L;
        categoryService.deleteById(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }
}
