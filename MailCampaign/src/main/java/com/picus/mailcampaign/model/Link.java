package com.picus.mailcampaign.model;

import com.picus.mailcampaign.model.enums.LinkStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

@Entity
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
public class Link {
    @Id
    @Column(name = "pk", updatable = false, nullable = false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long pk;

    private LinkStatus status;

    @Column(unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contact_pk")
    private Contact contact;

    @Column(name="clicked_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Date clickedAt;

    @Column(name="created_at", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    public boolean isClickable(int hash) {
        return this.getStatus() == LinkStatus.PENDING
                && this.getContact().hashCode() == hash;
    }
}
