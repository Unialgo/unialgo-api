package com.ua.unialgo.list.service;

import com.ua.unialgo.user.entity.Teacher;
import com.ua.unialgo.user.repository.TeacherRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.ua.unialgo.list.dto.SaveListRequestDto;
import com.ua.unialgo.list.entity.List;
import com.ua.unialgo.list.repository.ListRepository;

@Service
public class ListService {

    private final ListRepository listRepository;
    private final TeacherRepository teacherRepository;

    public ListService(ListRepository listRepository, TeacherRepository teacherRepository) {
        this.listRepository = listRepository;
        this.teacherRepository = teacherRepository;
    }

    public ResponseEntity<?> getAllLists() {
        Iterable<List> lists = listRepository.findAll();

        return ResponseEntity.ok(lists);
    }

    public ResponseEntity<?> getListById(Long id) {
        Optional<List> list = listRepository.findById(id);

        return ResponseEntity.ok(list);
    }

    public ResponseEntity<?> createList(SaveListRequestDto saveListRequest, Principal principal) {
        List list = new List();

        String userId = principal.getName();

        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + userId));

        list.setTitle(saveListRequest.title());
        list.setDescription(saveListRequest.description());
        list.setStartDate(saveListRequest.startDate());
        list.setEndDate(saveListRequest.endDate());
        list.setCreationDate(LocalDateTime.now());
        list.setTeacher(teacher);

        List listInDb = listRepository.save(list);

        return ResponseEntity.ok(listInDb.getId());
    }

    public ResponseEntity<?> updateListById(Long id, SaveListRequestDto saveListRequest) {
        List listInDb = listRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("List not found with id: " + id));

        listInDb.setTitle(saveListRequest.title());
        listInDb.setDescription(saveListRequest.description());
        listInDb.setStartDate(saveListRequest.startDate());
        listInDb.setEndDate(saveListRequest.endDate());

        listRepository.save(listInDb);

        return ResponseEntity.ok(listInDb.getId());
    }

    public ResponseEntity<?> deleteListById(Long id) {
        listRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
