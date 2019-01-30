package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.CCApi2;
import com.constantcontact.v2.CampaignTrackingService;
import com.constantcontact.v2.campaigns.Campaign;
import com.constantcontact.v2.tracking.TrackingSummary;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public void printTrackingSummaries(List<Campaign> campaigns, List<TrackingSummary> summaries) throws IOException
    {
        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get("test.csv"));

                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader(
                                "CAMPAIGN NAME",
                                "SUBJECT",
                           //     "DATE SENT",
                                "EMAILS SENT",
                                "OPENED",
                                "CLICKED",
                                "BOUNCED",
                                "UNSUBSCRIBED"
                        ));
        ) {
            for(int i = 0; i < campaigns.size(); i++)
            {
                int emailsSent, emailsOpened, emailsClicked, emailsBounced, emailsUnsubscribed;
                int calcSentMinusBounces, calcOpenRate, calcBounceRate, calcClickRate, calcUnsubscribeRate;
                Campaign campaign = campaigns.get(i);
                TrackingSummary summary = summaries.get(i);

                String name = campaign.getName();
                String subject = campaign.getSubject();
                // String dateSent = campaign.getLastRunDate().toString();


                emailsSent = summary.getSends();
                emailsOpened = summary.getOpens();
                emailsClicked = summary.getClicks();
                emailsBounced = summary.getBounces();
                emailsUnsubscribed = summary.getUnsubscribes();
                calcSentMinusBounces = emailsSent - emailsBounced;

                calcOpenRate = Math.round((emailsOpened / calcSentMinusBounces) *100);
                calcBounceRate = Math.round((emailsBounced / emailsSent) * 100);
                calcClickRate = Math.round((emailsClicked / calcSentMinusBounces) * 100);
                calcUnsubscribeRate = Math.round((emailsUnsubscribed / calcSentMinusBounces) * 100);

                csvPrinter.printRecord(
                        name,
                        subject,
                      //  dateSent,
                        emailsSent,
                        emailsOpened,
                        emailsClicked,
                        emailsBounced,
                        emailsUnsubscribed

                );
            }

            csvPrinter.flush();
        }
    }
}
