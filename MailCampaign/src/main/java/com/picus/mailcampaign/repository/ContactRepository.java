package com.picus.mailcampaign.repository;

import com.picus.mailcampaign.model.Contact;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContactRepository extends PagingAndSortingRepository<Contact, Long> {
}
