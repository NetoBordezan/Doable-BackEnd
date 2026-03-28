package com.dev.doable.service;

import com.dev.doable.dto.TagDto;
import com.dev.doable.model.Tag;
import com.dev.doable.model.User;
import com.dev.doable.repository.TagRepository;
import com.dev.doable.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public List<TagDto> findAll() {
        return tagRepository.findByUser(getCurrentUser())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TagDto create(TagDto dto) {
        if (tagRepository.existsByNameAndUser(dto.getName(), getCurrentUser())) {
            throw new RuntimeException("Tag já existe");
        }

        Tag tag = Tag.builder()
                .name(dto.getName())
                .color(dto.getColor())
                .user(getCurrentUser())
                .build();

        return toDTO(tagRepository.save(tag));
    }

    public void delete(Long id) {
        tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag não encontrada"));
        tagRepository.deleteById(id);
    }

    public TagDto toDTO(Tag tag) {
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .build();
    }
}