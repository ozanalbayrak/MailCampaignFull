package com.picus.mailcampaign.service;

import com.picus.mailcampaign.model.Contact;

public interface ILinkService {
    void prepareLinkSendEmail(Iterable<Contact> contacts, String mailBody);

    void checkExpiration();

    String checkLink(String code, int hash);
}
