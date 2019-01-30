package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.campaigns.Campaign;
import com.constantcontact.v2.tracking.TrackingSummary;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestServiceCampaignTracking
{
    private String apiKey;
    private String apiToken;
    private String dateCreated;
    private boolean showDebug = false;

    @BeforeAll
    static void initAll()
    {

    }

    // TODO: save campaign tracking report to csv

    @BeforeEach
    void init() throws Exception
    {
        Map<String, String> configs = Helper.getApiConfig();
        this.apiKey = configs.get("apiKey");
        this.apiToken = configs.get("apiToken");
        this.dateCreated = configs.get("dateCreated");
        this.showDebug = Boolean.parseBoolean(configs.get("showDebug"));
    }

    @Test
    void testGetTrackingSummary()
    {
        // get the campaigns and take the first one
        ServiceCampaign serviceCampaign = new ServiceCampaign(apiKey, apiToken);

        try
        {
            List<Campaign> campaigns = serviceCampaign.getSentCampaigns("2019/01/01");
            Campaign campaign = campaigns.get(0);
            ServiceCampaignTracking tracking = new ServiceCampaignTracking(apiKey, apiToken);
            TrackingSummary summary = tracking.getTrackingSummary(campaign.getId());
            assertTrue(summary.getSends() > 0);
            assertTrue(summary.getOpens() > 0);
            // assertTrue(summary.getClicks() > 0 );
            Helper.printCampaignTrackingSummary(summary);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    void testPrintTrackingSummariesSucceeds() throws IOException
    {
        ServiceCampaign serviceCampaign = new ServiceCampaign(apiKey, apiToken);
        ServiceCampaignTracking serviceTracking = new ServiceCampaignTracking(apiKey, apiToken);

        List<TrackingSummary> summaries = new ArrayList<>();
        // TODO: get full campaign, then loop thru summaries
        // TODO: maybe make method printCampaignSummariesSince(date) or getCampaignSummariesSince(date)

        try
        {
            List<Campaign> campaigns = serviceCampaign.getSentCampaigns("2019/01/01");
            for(Campaign campaign : campaigns)
            {
                Thread.sleep(4000);
                summaries.add(serviceTracking.getTrackingSummary(campaign.getId()));
            }

            serviceTracking.printTrackingSummaries(campaigns, summaries);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @AfterEach
    void tearDown()
    {
    }

    @AfterAll
    static void tearDownAll()
    {
    }

}
