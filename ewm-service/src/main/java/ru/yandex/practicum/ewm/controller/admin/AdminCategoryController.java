package ru.yandex.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ewm.dto.category.CategoryDto;
import ru.yandex.practicum.ewm.dto.category.NewCategoryRequestDto;
import ru.yandex.practicum.ewm.service.CategoryService;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryRequestDto request) {
        return categoryService.createCategory(request);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@Valid @RequestBody NewCategoryRequestDto request,
                                   @PathVariable long catId) {
        return categoryService.updateCategory(catId,request);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delCategory(@PathVariable long catId) {
        categoryService.delCategory(catId);
    }
}
