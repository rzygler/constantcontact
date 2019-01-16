package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.CCApi2;
import com.constantcontact.v2.Paged;
import com.constantcontact.v2.campaigns.Campaign;
import com.constantcontact.v2.campaigns.CampaignStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CampaignService
{
    private ApiV2 service;
    private CCApi2 conn;
    int millisToSleepBetweenRequests = 4000;

    /**
     *
     * @param apiKey    Constant Contact developer api key
     * @param apiToken  Constant Contact developer token
     */
    public CampaignService(String apiKey, String apiToken)
    {
        this.service = new ApiV2(apiKey, apiToken);
        this.conn = service.getApiConn();
    }

    // @GET("v2/emailmarketing/campaigns/{campaignId}")
    // Call<Campaign> getCampaign(@Path("campaignId") String campaignId, @Query("updateSummary") boolean updateSummary);
    //
    //
    //

    /**
     *
     * @return  list of all campaigns
     */
    public List<Campaign> getAllCampaigns()
    {
        return getCampaigns(50, CampaignStatus.ALL);
    }

    /**
     *
     * @return  list of draft campaigns
     */
    public List<Campaign> getDraftCampaigns()
    {
        return getCampaigns(50, CampaignStatus.DRAFT);
    }

    /**
     *
     * @return  list of running Campaigns
     */
    public List<Campaign> getRunningCampaigns()
    {
        return getCampaigns(50, CampaignStatus.RUNNING);
    }

    /**
     *
     * @return  list of sent campaigns
     */
    public List<Campaign> getSentCampaigns()
    {
        return getCampaigns(50, CampaignStatus.SENT);
    }

    /**
     *
     * @return  list of scheduled campaigns
     */
    public List<Campaign> getScheduledCampaigns()
    {
        return getCampaigns(50, CampaignStatus.SCHEDULED);
    }

    /**
     *
     * @return  list of deleted campaigns
     */
    public List<Campaign> getDeletedCampaigns()
    {
        return getCampaigns(50,CampaignStatus.DELETED);
    }

    /**
     * GET("v2/emailmarketing/campaigns")
     * Call<Paged<Campaign>> getCampaigns(@Query("limit") int limit, @Query("status") CampaignStatus status);
     *
     * @param limit     size of page to return, max 50
     * @param status    enum of     ALL, DRAFT, RUNNING, SENT, SCHEDULED, DELETED
     * @return          list of campaigns
     */
    public List<Campaign> getCampaigns(int limit, CampaignStatus status)
    {
        if (limit > 50)
            limit = 50;


        List<Campaign> campaigns = new ArrayList<>();

        try
        {
            com.constantcontact.v2.CampaignService campaignService = conn.getCampaignService();

            // synchronous method
            Paged<Campaign> pagedCampaigns = campaignService.getCampaigns(limit, status).execute().body();

            if (pagedCampaigns.getResults().size() > 0)
            {
                campaigns.addAll(pagedCampaigns.getResults());
            }

            // keep fetching another batch until there are no more
            while (pagedCampaigns.getNextLink() != null)
            {
                System.out.println(pagedCampaigns.getNextLink());
                Thread.sleep(millisToSleepBetweenRequests);
                pagedCampaigns = campaignService.getCampaigns(pagedCampaigns.getNextLink()).execute().body();
                campaigns.addAll(pagedCampaigns.getResults());
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        // give back empty list if none found
        return campaigns;

    }


    // @GET("v2/emailmarketing/campaigns")
    // Call<Paged<Campaign>> getCampaigns(@Query("limit") int limit, @Query("modified_since") QueryDate date, @Query("status") CampaignStatus status);
    //
    // @GET
    // Call<Paged<Campaign>> getCampaigns(@Url String nextLink);
    //
    // @POST("v2/emailmarketing/campaigns")
    // Call<Campaign> createCampaign(@Body Campaign campaign);
    // @PUT("v2/emailmarketing/campaigns/{campaignId}")
    // Call<Campaign> updateCampaign(@Body Campaign campaign, @Path("campaignId") String campaignId);
    // @DELETE("v2/emailmarketing/campaigns/{campaignId}")
    // Call<Response<Void>> deleteCampaign(@Path("campaignId") String campaignId);
    // @POST("v2/emailmarketing/campaigns/{campaignId}/tests")
    // Call<TestSend> sendTestCampaign(@Body TestSend testSend, @Path("campaignId") String campaignId);
    // @GET("v2/emailmarketing/campaigns/{campaignId}/preview")
    // Call<CampaignPreview> getCampaignPreview(@Path("campaignId") String campaignId);
    // @POST("v2/emailmarketing/campaigns/{campaignId}/schedules")
    // Call<CampaignSchedule> scheduleCampaign(@Body CampaignSchedule campaignSchedule, @Path("campaignId") String campaignId);
    // @GET("v2/emailmarketing/campaigns/{campaignId}/schedules")
    // Call<List<CampaignSchedule>> getCampaignSchedules(@Path("campaignId") String campaignId);
    // @GET("v2/emailmarketing/campaigns/{campaignId}/schedules/{scheduleId}")
    // Call<CampaignSchedule> getCampaignSchedule(@Path("campaignId") String campaignId, @Path("scheduleId") String scheduleId);
    // @PUT("v2/emailmarketing/campaigns/{campaignId}/schedules/{scheduleId}")
    // Call<CampaignSchedule> updateCampaignSchedule(@Path("campaignId") String campaignId, @Path("scheduleId") String scheduleId);
    // @DELETE("v2/emailmarketing/campaigns/{campaignId}/schedules/{scheduleId}")
    // Call<Response<Void>> deleteCampaignSchedule(@Path("campaignId") String campaignId, @Path("scheduleId") String scheduleId);

}
