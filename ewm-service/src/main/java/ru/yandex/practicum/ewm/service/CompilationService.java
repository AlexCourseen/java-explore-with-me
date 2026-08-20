package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.compilation.CompilationDto;
import ru.yandex.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.ewm.dto.compilation.UpdateCompilationRequest;

public interface CompilationService {

    CompilationDto createCompilation(NewCompilationDto request);

    CompilationDto updateCompilation(long compId, UpdateCompilationRequest request);

    void delCompilation(long compId);
}
