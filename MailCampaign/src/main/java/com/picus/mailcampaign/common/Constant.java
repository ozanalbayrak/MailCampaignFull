package com.picus.mailcampaign.common;

public class Constant {
    public static final String API_BASE_URL = "http://localhost:8080"; // Config dosyasina cikilmali.
    public static final String SENDER_EMAIL = "testpicustask@gmail.com";
    public static final String SUBJECT_EMAIL = "Link";
    public static final String TEXT_EMAIL = "%s \n Your link: \n" + API_BASE_URL + "/code/%s/mail/%s";
}
