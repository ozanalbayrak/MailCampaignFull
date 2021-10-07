package com.picus.mailcampaign.quartz;

import com.picus.mailcampaign.service.ILinkService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LinkExpirationJob implements Job {

    @Autowired
    private ILinkService iLinkService;

    public void execute(JobExecutionContext context) throws JobExecutionException {
//        iLinkService.checkExpiration();
    }
}
