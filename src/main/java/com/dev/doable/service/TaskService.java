package com.dev.doable.service;

import com.dev.doable.dto.TagDto;
import com.dev.doable.dto.TaskDto;
import com.dev.doable.model.Tag;
import com.dev.doable.model.Task;
import com.dev.doable.model.User;
import com.dev.doable.repository.TagRepository;
import com.dev.doable.repository.TaskRepository;
import com.dev.doable.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public List<TaskDto> findAll() {
        return taskRepository.findByUserOrderByCreatedAtDesc(getCurrentUser())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TaskDto findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        return toDTO(task);
    }

    public TaskDto create(TaskDto dto) {
        Task task = toEntity(dto);
        task.setUser(getCurrentUser());

        if (dto.getTags() != null) {
            List<Tag> tags = dto.getTags().stream()
                    .map(tagDto -> tagRepository.findById(tagDto.getId())
                            .orElseThrow(() -> new RuntimeException("Tag não encontrada")))
                    .collect(Collectors.toList());
            task.setTags(tags);
        }

        return toDTO(taskRepository.save(task));
    }

    public TaskDto update(Long id, TaskDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setIsDone(dto.getIsDone());
        task.setDeadline(dto.getDeadline());

        if (dto.getTags() != null) {
            List<Tag> tags = dto.getTags().stream()
                    .map(tagDto -> tagRepository.findById(tagDto.getId())
                            .orElseThrow(() -> new RuntimeException("Tag não encontrada")))
                    .collect(Collectors.toList());
            task.setTags(tags);
        }

        return toDTO(taskRepository.save(task));
    }

    public void delete(Long id) {
        taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        taskRepository.deleteById(id);
    }

    private TaskDto toDTO(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .isDone(task.getIsDone())
                .deadline(task.getDeadline())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .tags(task.getTags().stream()
                        .map(tag -> TagDto.builder()
                                .id(tag.getId())
                                .name(tag.getName())
                                .color(tag.getColor())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private Task toEntity(TaskDto dto) {
        return Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .isDone(dto.getIsDone() != null ? dto.getIsDone() : false)
                .deadline(dto.getDeadline())
                .build();
    }
}