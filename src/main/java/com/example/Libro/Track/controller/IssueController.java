package com.example.Libro.Track.controller;

import com.example.Libro.Track.dto.IssueRequest;
import com.example.Libro.Track.dto.IssueResponse;
import com.example.Libro.Track.service.IssueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    public ResponseEntity<IssueResponse> issueBook(@Valid @RequestBody IssueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.issueBook(request));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<IssueResponse> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.returnBook(id));
    }

    @GetMapping
    public ResponseEntity<List<IssueResponse>> getAllIssues() {
        return ResponseEntity.ok(issueService.getAllIssues());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getIssue(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.getIssue(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<IssueResponse>> getStudentIssues(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        return ResponseEntity.ok(issueService.getIssuesByStudent(studentId, activeOnly));
    }
}
