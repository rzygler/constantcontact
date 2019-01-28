package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.CCApi2;
import com.constantcontact.v2.CampaignTrackingService;
import com.constantcontact.v2.tracking.TrackingSummary;

import java.io.IOException;

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
}
