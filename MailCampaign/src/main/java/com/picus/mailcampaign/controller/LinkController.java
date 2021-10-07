package com.picus.mailcampaign.controller;

import com.picus.mailcampaign.service.ILinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LinkController {

    @Autowired
    private ILinkService iLinkService;

    @GetMapping("/code/{code}/mail/{hash}")
    public String checkLink(@PathVariable("code") String code,
                                    @PathVariable("hash") int hash) {
        return iLinkService.checkLink(code, hash);
    }

}
