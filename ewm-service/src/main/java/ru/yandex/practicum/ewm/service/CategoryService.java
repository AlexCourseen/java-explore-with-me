package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.category.CategoryDto;
import ru.yandex.practicum.ewm.dto.category.NewCategoryRequestDto;

import java.util.Collection;

public interface CategoryService {
    Collection<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(long catId);

    CategoryDto createCategory(NewCategoryRequestDto request);

    CategoryDto updateCategory(long catId, NewCategoryRequestDto request);

    void delCategory(long catId);
}
