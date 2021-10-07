package com.picus.mailcampaign.repository;

import com.picus.mailcampaign.model.Link;
import com.picus.mailcampaign.model.enums.LinkStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public interface LinkRepository extends CrudRepository<Link, Long> {
    Iterable<Link> getAllByContact_PkAndStatus(Long pk, LinkStatus status);

    Link findLinkByCode(String code);

    @Modifying
    @Query(value = "UPDATE Link SET status = " +
            "CASE WHEN extract(epoch from created_at - :currentDate / 60) > 5 THEN :expiredStatus " +
            "ELSE status END", nativeQuery=true)
    void expireLinks(@Param("currentDate") Calendar currentDate, @Param("expiredStatus") LinkStatus expiredStatus);
}
