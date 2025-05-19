package com.ua.unialgo.assignment.service;

import com.ua.unialgo.assignment.dto.SaveAssignmentRequestDto;
import com.ua.unialgo.assignment.entity.Assignment;
import com.ua.unialgo.assignment.repository.AssignmentRepository;
import com.ua.unialgo.user.entity.Teacher;
import com.ua.unialgo.user.repository.TeacherRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final TeacherRepository teacherRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, TeacherRepository teacherRepository) {
        this.assignmentRepository = assignmentRepository;
        this.teacherRepository = teacherRepository;
    }

    public ResponseEntity<?> getAllAssignments(Principal principal) {
        String userId = principal.getName();
        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + userId));

        Iterable<Assignment> assignments = assignmentRepository.findAllByTeacherId(teacher.getId());

        return ResponseEntity.ok(assignments);
    }

    public ResponseEntity<?> getAssignmentById(Long id) {
        Optional<Assignment> assignment = assignmentRepository.findById(id);

        return ResponseEntity.ok(assignment);
    }

    public ResponseEntity<?> createAssignment(SaveAssignmentRequestDto saveAssignmentRequest, Principal principal) {
        Assignment assignment = new Assignment();

        String userId = principal.getName();

        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + userId));

        assignment.setTitle(saveAssignmentRequest.title());
        assignment.setDescription(saveAssignmentRequest.description());
        assignment.setStartDate(saveAssignmentRequest.startDate());
        assignment.setEndDate(saveAssignmentRequest.endDate());
        assignment.setCreationDate(LocalDateTime.now());
        assignment.setTeacher(teacher);

        Assignment assignmentInDb = assignmentRepository.save(assignment);

        return ResponseEntity.ok(assignmentInDb.getId());
    }

    public ResponseEntity<?> updateAssignmentById(Long id, SaveAssignmentRequestDto saveAssignmentRequest) {
        Assignment assignmentInDb = assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found with id: " + id));

        assignmentInDb.setTitle(saveAssignmentRequest.title());
        assignmentInDb.setDescription(saveAssignmentRequest.description());
        assignmentInDb.setStartDate(saveAssignmentRequest.startDate());
        assignmentInDb.setEndDate(saveAssignmentRequest.endDate());

        assignmentRepository.save(assignmentInDb);

        return ResponseEntity.ok(assignmentInDb.getId());
    }

    public ResponseEntity<?> deleteAssignmentById(Long id) {
        assignmentRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
