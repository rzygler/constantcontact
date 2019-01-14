package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.campaigns.Campaign;

import java.util.List;

public class CampaignService
{
    private ApiV2 api;

    public CampaignService(String apiKey, String apiToken)
    {
        this.api = new ApiV2(apiKey, apiToken);
    }

    //public List<Campaign> getCampaigns()
    //{

    //}

    /*
Call<Paged<Campaign>> getCampaigns(@Query("limit") int limit,
@Query("modified_since") QueryDate date, @Query("status") CampaignStatus status);

    ApiV2 api = new ApiV2(apiKey, apiToken);

        // Get the info on your Constant Contact account and print it
        AccountSummaryInformation summary = api.getAccountSummary();
        printAccountSummaryInformation(summary);
     */
}
