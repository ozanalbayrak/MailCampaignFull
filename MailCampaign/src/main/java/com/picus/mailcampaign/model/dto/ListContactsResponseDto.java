package com.picus.mailcampaign.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ListContactsResponseDto {
    List<ListContactsResponseItem> data;
    long totalElements;

    @Getter
    @Setter
    @Builder
    public static class ListContactsResponseItem {
        private Long pk;
        private String email;
        private String fullName;
        private boolean emailSent;
        private String howLong;
        private boolean isEmailDone;
    }
}
