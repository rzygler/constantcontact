package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.CCApi2;
import com.constantcontact.v2.CampaignTrackingService;
import com.constantcontact.v2.campaigns.Campaign;
import com.constantcontact.v2.campaigns.CampaignStatus;
import com.constantcontact.v2.tracking.TrackingSummary;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceCampaignTracking
{
    private ApiV2 service;
    private CCApi2 conn;
    int millisToSleepBetweenRequests = 4000;

    /**
     *
     * @param apiKey    Constant Contact developer api key
     * @param apiToken  Constant Contact developer token
     */
    public ServiceCampaignTracking(String apiKey, String apiToken)
    {
        this.service = new ApiV2(apiKey, apiToken);
        this.conn = service.getApiConn();
    }

    /**
     * GET("v2/emailmarketing/campaigns/{campaignId}/tracking/reports/summary?updateSummary=true")
     * Call<TrackingSummary> getTrackingSummary(@Path("campaignId") String campaignId);
     *
     * @param campaignId
     * @return
     */
    public TrackingSummary getTrackingSummary(String campaignId)
    {
        TrackingSummary summary = null;
        try
        {
            CampaignTrackingService service = conn.getCampaignTrackingService();

            // synchronous method
            summary = service.getTrackingSummary(campaignId).execute().body();

            return summary;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return summary;
    }

    /**
     * Print the full Campaign Summary since {Date} to a CSV file
     *
     * @param theDate
     * @param file
     */
    public void printSentCampaignSummariesSince(String theDate, String file)
    {
        List<Campaign> campaigns = new ArrayList<>();


        try
        {
            ServiceCampaign service = new ServiceCampaign(this.service.getApiKey(), this.service.getApiToken());

            // this call gets us the campaigns but doens't return the tracking summary details
            // so we need to loop thru all the campaigns to get their details
            List<Campaign> sentCampaigns = service.getSentCampaigns(theDate);

            for(Campaign tempCampaign : sentCampaigns)
            {
                // System.out.println("getting campaign: " + tempCampaign.getName());
                Thread.sleep(1000);
                Campaign campaign = service.getCampaign(tempCampaign.getId());
                campaigns.add(campaign);
            }
            List<TrackingSummary> summaries = getTrackingSummaries(campaigns);

            printTrackingSummaries(campaigns, summaries, file);

        } catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    /**
     * Get the campaign tracking summaries for a list of campaigns
     *
     * @param campaigns
     * @return
     * @throws InterruptedException
     */
    public List<TrackingSummary> getTrackingSummaries(List<Campaign> campaigns) throws InterruptedException
    {
        List<TrackingSummary> summaries = new ArrayList<>();

        for(Campaign tempCampaign : campaigns)
        {
            Thread.sleep(1000);
            TrackingSummary summary = getTrackingSummary(tempCampaign.getId());
            summaries.add(summary);
        }
        return summaries;

    }

    /**
     * Print the campaign tracking summarys for a list of campaigns (and matching list of summaries)
     *
     * @param campaigns
     * @param summaries
     * @param file
     * @throws IOException
     */
    public void printTrackingSummaries(List<Campaign> campaigns, List<TrackingSummary> summaries, String file) throws IOException
    {
        if ( file == null)
        {
            file  = "summary.csv";
        }

        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(file));

                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader(
                                "CAMPAIGN NAME",
                                "SUBJECT",
                                "DATE SENT",
                                "EMAILS SENT",
                                "OPENED",
                                "OPEN RATE",
                                "CLICKED",
                                "CLICK RATE",
                                "BOUNCED",
                                "BOUNCE RATE",
                                "UNSUBSCRIBED",
                                "UNSUB RATE"
                        ));
        ) {
            for(int i = 0; i < campaigns.size(); i++)
            {
                int emailsSent, emailsOpened, emailsClicked, emailsBounced, emailsUnsubscribed;
                float calcSentMinusBounces, calcOpenRate, calcBounceRate, calcClickRate, calcUnsubscribeRate;
                Campaign campaign = campaigns.get(i);
                TrackingSummary summary = summaries.get(i);

                String name = campaign.getName();
                String subject = campaign.getSubject();
                String dateSent = campaign.getLastRunDate().toString();


                emailsSent = summary.getSends();
                emailsOpened = summary.getOpens();
                emailsClicked = summary.getClicks();
                emailsBounced = summary.getBounces();
                emailsUnsubscribed = summary.getUnsubscribes();
                calcSentMinusBounces = emailsSent - emailsBounced;

                calcOpenRate = (emailsOpened / calcSentMinusBounces) *100;
                calcBounceRate = (emailsBounced / emailsSent) * 100;
                calcClickRate = (emailsClicked / calcSentMinusBounces) * 100;
                calcUnsubscribeRate = (emailsUnsubscribed / calcSentMinusBounces) * 100;

                csvPrinter.printRecord(
                        name,
                        subject,
                        dateSent,
                        emailsSent,
                        emailsOpened,
                        String.format("%.1f",calcOpenRate),
                        emailsClicked,
                        String.format("%.1f", calcClickRate),
                        emailsBounced,
                        String.format("%.1f", calcBounceRate),
                        emailsUnsubscribed,
                        String.format("%.1f", calcUnsubscribeRate)

                );
            }

            csvPrinter.flush();
        }
    }
}
