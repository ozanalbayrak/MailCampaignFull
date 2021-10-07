package com.picus.mailcampaign.service.impl;

import com.picus.mailcampaign.common.Constant;
import com.picus.mailcampaign.model.Contact;
import com.picus.mailcampaign.model.Link;
import com.picus.mailcampaign.model.enums.LinkStatus;
import com.picus.mailcampaign.repository.LinkRepository;
import com.picus.mailcampaign.service.ILinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

@Service
@Transactional
public class LinkService implements ILinkService {

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void prepareLinkSendEmail(Iterable<Contact> contacts, String mailBody) {
        contacts.forEach(contact -> {
            Link link = new Link();
            link.setContact(contact);
            link.setStatus(LinkStatus.PENDING);
            link.setCode(UUID.randomUUID().toString());
            Iterable<Link> oldLinks = linkRepository.getAllByContact_PkAndStatus(contact.getPk(), LinkStatus.PENDING);
            oldLinks.forEach(oldLink -> oldLink.setStatus(LinkStatus.INVALID));
            Link savedLink = linkRepository.save(link);
            sendEmail(Constant.SENDER_EMAIL, contact.getEmail(), Constant.SUBJECT_EMAIL,
                    String.format(Constant.TEXT_EMAIL, mailBody, savedLink.getCode(), contact.hashCode()));
        });
    }

    @Override
    public void checkExpiration() {
        Calendar calendar = Calendar.getInstance();
        Instant instant = Instant.now();
        calendar.setTimeInMillis(instant.toEpochMilli());
        calendar.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Turkey")));
        linkRepository.expireLinks(calendar, LinkStatus.EXPIRED);
    }

    @Override
    public String checkLink(String code, int hash) {
        Link link = linkRepository.findLinkByCode(code);
        if (link == null || !link.isClickable(hash)) {
            return "Invalid Link";
        }
        link.setClickedAt(new Date());
        link.setStatus(LinkStatus.DONE);
        return "Success";
    }

    @Async
    public void sendEmail(String from, String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

}
