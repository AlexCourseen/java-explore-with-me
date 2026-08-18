package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.category.CategoryDto;
import ru.yandex.practicum.ewm.dto.category.NewCategoryRequestDto;
import ru.yandex.practicum.ewm.exception.DuplicatedDataException;
import ru.yandex.practicum.ewm.exception.NotFoundException;
import ru.yandex.practicum.ewm.mapper.CategoryMapper;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.repository.CategoryRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Collection<CategoryDto> getCategories(int from, int size) {
        return categoryRepository.getCategories(PageRequest.of(from, size)).stream()
                .map(CategoryMapper::mapToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(long catId) {
        return CategoryMapper.mapToCategoryDto(checkCategory(catId));
    }


    @Override
    public CategoryDto createCategory(NewCategoryRequestDto request) {
        if (categoryRepository.findByName(request.getName()) != null) {
            throw new DuplicatedDataException("Категория " + request.getName() + " уже существует");
        }
        Category category = CategoryMapper.mapNewCategoryRequestToCategory(request);
        categoryRepository.save(category);
        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(long catId, NewCategoryRequestDto request) {
        Category category = checkCategory(catId);
        if (categoryRepository.findByName(request.getName()) != null) {
            throw new DuplicatedDataException("Категория " + request.getName() + " уже существует");
        }
        category.setName(request.getName());
        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    public void delCategory(long catId) {
        Category category = checkCategory(catId);
        categoryRepository.delete(category);
    }

    private Category checkCategory(long catId) {
        return categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Категория с " + catId + " не найдена"));
    }
}
