package com.picus.mailcampaign.service.impl;

import com.picus.mailcampaign.model.Contact;
import com.picus.mailcampaign.model.Link;
import com.picus.mailcampaign.model.dto.ListContactsResponseDto;
import com.picus.mailcampaign.model.enums.LinkStatus;
import com.picus.mailcampaign.repository.ContactRepository;
import com.picus.mailcampaign.service.IContactService;
import com.picus.mailcampaign.service.ILinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContactService implements IContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ILinkService iLinkService;

    @Override
    public Contact saveContact(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public Iterable<Contact> saveContacts(List<Contact> contacts) {
        return contactRepository.saveAll(contacts);
    }

    @Override
    public Contact updateContact(Contact contact) {
        Optional<Contact> foundContact = contactRepository.findById(contact.getPk());
        foundContact.ifPresent(value -> contact.setLink(value.getLink()));
        return contactRepository.save(contact);
    }

    @Override
    public ListContactsResponseDto listContacts(Pageable pageable) {
        Iterable<Contact> contacts = contactRepository.findAll(pageable);

        List<ListContactsResponseDto.ListContactsResponseItem> data = new ArrayList<>();
        Comparator<Link> compareByCreatedDate = Comparator.comparing(Link::getCreatedAt);

        contacts.forEach(contact -> {
            boolean isEmailDone = contact.getLink().stream().anyMatch(link -> link.getStatus() == LinkStatus.DONE);
            boolean emailSent = contact.getLink().stream().anyMatch(link -> link.getStatus() != LinkStatus.INVALID);
            String howLong = "";
            if (isEmailDone) {
                List<Link> clickedLinks = contact.getLink().stream()
                        .filter(link -> link.getStatus() == LinkStatus.DONE)
                        .sorted(compareByCreatedDate)
                        .collect(Collectors.toList());
                Link lastClickedLink = clickedLinks.get(clickedLinks.size()-1);
                long howLongInMillis = lastClickedLink.getClickedAt().getTime() -
                        lastClickedLink.getCreatedAt().getTime();

                if (howLongInMillis > 0 ) {
                    long hours = TimeUnit.MILLISECONDS.toHours(howLongInMillis);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(howLongInMillis);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(howLongInMillis);
                    howLong = String.format("%02d:%02d:%02d:%02d",
                            hours,
                            minutes - TimeUnit.HOURS.toMinutes(hours), // The change is in this line
                            seconds - TimeUnit.MINUTES.toSeconds(minutes),
                            howLongInMillis - TimeUnit.SECONDS.toMillis(seconds));
                }
            }
            ListContactsResponseDto.ListContactsResponseItem item = ListContactsResponseDto.ListContactsResponseItem
                    .builder()
                    .pk(contact.getPk())
                    .email(contact.getEmail())
                    .fullName(contact.getFullName())
                    .isEmailDone(isEmailDone)
                    .emailSent(emailSent)
                    .howLong(howLong)
                    .build();

            data.add(item);
        });
        return ListContactsResponseDto
                .builder()
                .data(data)
                .totalElements(((PageImpl) contacts).getTotalElements())
                .build();
    }

    @Override
    public void deleteContact(Long pk) {
        contactRepository.deleteById(pk);
    }

    @Override
    public void sendEmailToContacts(List<Contact> contacts, String mailBody) {
        Set<Long> pkSet = contacts.stream().map(Contact::getPk).collect(Collectors.toSet());
        Iterable<Contact> contactListFromDb = contactRepository.findAllById(pkSet);
        iLinkService.prepareLinkSendEmail(contactListFromDb, mailBody);
    }
}
