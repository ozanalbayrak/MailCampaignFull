package com.picus.mailcampaign.service;

import com.picus.mailcampaign.model.Contact;
import com.picus.mailcampaign.model.dto.ListContactsResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IContactService {
    Contact saveContact(Contact contact);

    Iterable<Contact> saveContacts(List<Contact> contacts);

    Contact updateContact(Contact contact);

    ListContactsResponseDto listContacts(Pageable pageable);

    void deleteContact(Long pk);

    void sendEmailToContacts(List<Contact> contacts, String mailBody);
}
