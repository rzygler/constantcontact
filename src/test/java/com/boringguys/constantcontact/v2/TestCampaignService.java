package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.campaigns.Campaign;
import com.constantcontact.v2.campaigns.CampaignStatus;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestCampaignService
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
    void testGetDraftCampaigns()
    {
        CampaignService campaignService = new CampaignService(apiKey, apiToken);
        List<Campaign> campaigns = campaignService.getDraftCampaigns();
        assertTrue(campaigns.size() > 0);
        assertNotNull(campaigns.get(0));
        assertTrue(campaigns.get(0).getName().length() >= 4);
        assertTrue(campaigns.get(0).getStatus().equals(CampaignStatus.DRAFT));
        // When we get the campaigns this way, they do not have every field populated
        // assertNotNull(campaigns.get(0).getCreatedDate());
        if(showDebug)
        {
            campaigns.forEach(a -> Helper.printCampaign(a));
        }
    }

    @Test
    void testGetSentCampaignsSince()
    {
        // public List<Campaign> getSentCampaigns(String sinceDate)
        CampaignService campaignService = new CampaignService(apiKey, apiToken);
        try
        {
            List<Campaign> campaigns = campaignService.getSentCampaigns("2019/01/01");
            assertTrue(campaigns.size() > 0);
            assertNotNull(campaigns.get(0));
            assertTrue(campaigns.get(0).getName().length() >= 4);
            assertTrue(campaigns.get(0).getStatus().equals(CampaignStatus.SENT));
            if(showDebug)
            {
                Helper.printCampaign(campaigns.get(0));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    void testGetDraftCampaignsSince()
    {
        // public List<Campaign> getSentCampaigns(String sinceDate)
        CampaignService campaignService = new CampaignService(apiKey, apiToken);
        try
        {
            List<Campaign> campaigns = campaignService.getDraftCampaigns("2019/01/01");
            assertTrue(campaigns.size() > 0);
            assertNotNull(campaigns.get(0));
            assertTrue(campaigns.get(0).getName().length() >= 4);
            assertTrue(campaigns.get(0).getStatus().equals(CampaignStatus.DRAFT));
            if(showDebug)
            {
                Helper.printCampaign(campaigns.get(0));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    void testGetAllCampaignsSince()
    {
        // public List<Campaign> getSentCampaigns(String sinceDate)
        CampaignService campaignService = new CampaignService(apiKey, apiToken);
        try
        {
            List<Campaign> campaigns = campaignService.getAllCampaigns("2019/01/01");
            assertTrue(campaigns.size() > 0);
            assertNotNull(campaigns.get(0));
            assertTrue(campaigns.get(0).getName().length() >= 4);
            assertTrue(campaigns.get(0).getStatus().equals(CampaignStatus.SENT));
            if(showDebug)
            {
                Helper.printCampaign(campaigns.get(0));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    @Test
    void testGetOneCampaignSince()
    {
        CampaignService campaignService = new CampaignService(apiKey, apiToken);
        try
        {
            List<Campaign> campaigns = campaignService.getSentCampaigns("2019/01/01");
            Campaign campaign = campaigns.get(0);

            Campaign campaignToCompare = campaignService.getCampaign(campaign.getId());
            assertNotNull(campaignToCompare);
            assertEquals(campaign.getName(), campaignToCompare.getName());
            assertNotNull(campaignToCompare.getCreatedDate());
            assertNotNull(campaignToCompare.getSubject());
            if(showDebug)
            {
                Helper.printCampaign(campaignToCompare);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    void testGetOneCampaign()
    {

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
