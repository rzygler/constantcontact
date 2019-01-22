package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.campaigns.Campaign;
import com.constantcontact.v2.tracking.TrackingSummary;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestCampaignTrackingService
{
    private String apiKey;
    private String apiToken;
    private String dateCreated;
    private boolean showDebug = false;

    @BeforeAll
    static void initAll()
    {

    }

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
        CampaignService campaignService = new CampaignService(apiKey, apiToken);

        try
        {
            List<Campaign> campaigns = campaignService.getSentCampaigns("2019/01/01");
            Campaign campaign = campaigns.get(0);
            CampaignTrackingService tracking = new CampaignTrackingService(apiKey, apiToken);
            TrackingSummary summary = tracking.getTrackingSummary(campaign.getId());
            assertTrue(summary.getSends() > 0);
            assertTrue(summary.getOpens() > 0);
            assertTrue(summary.getClicks() > 0 );
            Helper.printCampaignTrackingSummary(summary);

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
