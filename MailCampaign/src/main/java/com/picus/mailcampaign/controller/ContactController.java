package com.picus.mailcampaign.controller;

import com.picus.mailcampaign.model.Contact;
import com.picus.mailcampaign.model.dto.ContactDto;
import com.picus.mailcampaign.model.dto.ListContactsResponseDto;
import com.picus.mailcampaign.model.dto.SendEmailRequestDto;
import com.picus.mailcampaign.service.IContactService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contact")
@CrossOrigin(origins = "http://localhost:4200")
public class ContactController {

    @Autowired
    private IContactService iContactService;

    @GetMapping
    public ResponseEntity<ListContactsResponseDto> listContacts(@RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                                @RequestParam(name = "pageSize", required = false, defaultValue = "5") int pageSize) {
        return ResponseEntity.ok(iContactService.listContacts(PageRequest.of(page, pageSize, Sort.by("pk").descending())));
    }

    @PostMapping
    public ResponseEntity<Contact> addContact(@Valid @RequestBody ContactDto contactDto) {
        return ResponseEntity.ok(iContactService.saveContact(contactDto.toContact()));
    }

    @PostMapping("/file")
    public ResponseEntity<Iterable<Contact>> addContactsFromFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        InputStream inputStream = multipartFile.getInputStream();
        List<Contact> contacts = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .map(line -> {
                    String fullName = line.substring(0, line.indexOf(" <"));
                    String email = StringUtils.substringBetween(line, "<", ">");
                    return new Contact(fullName, email);
                }).collect(Collectors.toList());
        return ResponseEntity.ok(iContactService.saveContacts(contacts));
    }

    @PutMapping("/{pk}")
    public ResponseEntity<Contact> updateContact(@PathVariable("pk") Long pk, @Valid @RequestBody ContactDto contactDto) {
        Contact contact = contactDto.toContact();
        contact.setPk(pk);
        return ResponseEntity.ok(iContactService.updateContact(contact));
    }

    @DeleteMapping("/{pk}")
    public ResponseEntity<Contact> deleteContact(@PathVariable("pk") Long pk) {
        iContactService.deleteContact(pk);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/email")
    public ResponseEntity<Contact> sendMail(@Valid @RequestBody SendEmailRequestDto dto) {
        iContactService.sendEmailToContacts(dto.getContacts(), dto.getMailBody());
        return ResponseEntity.ok(null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityExceptions(
            DataIntegrityViolationException ex) {
        return ex.getCause().getCause().getMessage();
    }
}
