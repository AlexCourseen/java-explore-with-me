package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.category.CategoryDto;
import ru.yandex.practicum.ewm.dto.category.NewCategoryRequestDto;
import ru.yandex.practicum.ewm.exception.ConflictedDataException;
import ru.yandex.practicum.ewm.exception.NotFoundException;
import ru.yandex.practicum.ewm.mapper.CategoryMapper;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.repository.CategoryRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

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
            throw new ConflictedDataException("Категория " + request.getName() + " уже существует");
        }
        Category category = CategoryMapper.mapNewCategoryRequestToCategory(request);
        categoryRepository.save(category);
        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(long catId, NewCategoryRequestDto request) {
        Category category = checkCategory(catId);
        Category existing = categoryRepository.findByName(request.getName());
        if (existing != null && existing.getId() != catId) {
            throw new ConflictedDataException("Категория " + request.getName() + " уже существует");
        }
        category.setName(request.getName());
        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    public void delCategory(long catId) {
        Category category = checkCategory(catId);
        if (!eventRepository.findByCategoryId(catId).isEmpty()) {
            throw new ConflictedDataException("У категории есть связанные события");
        }
        categoryRepository.delete(category);
    }

    private Category checkCategory(long catId) {
        return categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Категория с " + catId + " не найдена"));
    }
}
