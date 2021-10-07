package com.picus.mailcampaign.model.dto;

import com.picus.mailcampaign.model.Contact;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SendEmailRequestDto {
    List<Contact> contacts;
    String mailBody;
}
