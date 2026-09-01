package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.compilation.CompilationDto;
import ru.yandex.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.ewm.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(NewCompilationDto request);

    CompilationDto updateCompilation(long compId, UpdateCompilationRequest request);

    List<CompilationDto> getCompilations(int from, int size, Boolean pinned);

    CompilationDto getCompilationById(long compId);

    void delCompilation(long compId);
}
