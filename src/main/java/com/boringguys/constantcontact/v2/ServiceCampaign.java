package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.CCApi2;
import com.constantcontact.v2.CampaignService;
import com.constantcontact.v2.Paged;
import com.constantcontact.v2.QueryDate;
import com.constantcontact.v2.campaigns.Campaign;
import com.constantcontact.v2.campaigns.CampaignStatus;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class ServiceCampaign
{
    private ApiV2 service;
    private CCApi2 conn;
    int millisToSleepBetweenRequests = 4000;

    /**
     * Constructor for ServiceCampaign
     *
     * @param apiKey    Constant Contact developer api key
     * @param apiToken  Constant Contact developer token
     */
    public ServiceCampaign(String apiKey, String apiToken)
    {
        this.service = new ApiV2(apiKey, apiToken);
        this.conn = service.getApiConn();
    }


    /**
     * Get one campaign by the campaign id
     *
     * @param campaignId    string of the campaign id
     * @return              campaign
     */
    public Campaign getCampaign(String campaignId)
    {
        Campaign campaign = null;

        try
        {
            CampaignService campaignService = conn.getCampaignService();

            // synchronous method
            campaign = campaignService.getCampaign(campaignId, true).execute().body();

            return campaign;
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return campaign;
    }


    /**
     * Get all the email campaigns
     *
     * @return  list of all campaigns
     */
    public List<Campaign> getAllCampaigns()
    {
        return getCampaigns(50, CampaignStatus.ALL);
    }

    /**
     * Get all the email campaigns since date
     *
     * @param sinceDate     string of time to search after yyyy/MM/dd HH:mm:ss
     * @return              list of all campaigns
     */
    public List<Campaign> getAllCampaigns(String sinceDate) throws Exception
    {
        return getCampaigns(50, sinceDate, CampaignStatus.ALL);
    }

    /**
     * Get the "draft" email campaigns
     *
     * @return  list of draft campaigns
     */
    public List<Campaign> getDraftCampaigns()
    {
        return getCampaigns(50, CampaignStatus.DRAFT);
    }

    /**
     * Get the "draft" email campaigns since date
     *
     * @param sinceDate     string of time to search after yyyy/MM/dd HH:mm:ss
     * @return              list of all campaigns
     */
    public List<Campaign> getDraftCampaigns(String sinceDate) throws Exception
    {
        return getCampaigns(50, sinceDate, CampaignStatus.DRAFT);
    }

    /**
     * Get the "running" email campaigns
     *
     * @return  list of running Campaigns
     */
    public List<Campaign> getRunningCampaigns()
    {
        return getCampaigns(50, CampaignStatus.RUNNING);
    }

    /**
     * Get the "running" email campaigns since date
     *
     * @param sinceDate     string of time to search after yyyy/MM/dd HH:mm:ss
     * @return              list of all campaigns
     */
    public List<Campaign> getRunningCampaigns(String sinceDate) throws Exception
    {
        return getCampaigns(50, sinceDate, CampaignStatus.RUNNING);
    }

    /**
     * Get the "sent" email campaigns
     *
     * @return  list of sent campaigns
     */
    public List<Campaign> getSentCampaigns()
    {
        return getCampaigns(50, CampaignStatus.SENT);
    }

    /**
     * Get the "sent" email campaigns since date
     *
     * @param sinceDate     string of time to search after yyyy/MM/dd HH:mm:ss
     * @return              list of all campaigns
     */
    public List<Campaign> getSentCampaigns(String sinceDate) throws Exception
    {
        return getCampaigns(50, sinceDate, CampaignStatus.SENT);
    }


    /**
     * Get the "scheduled" email campaigns
     *
     * @return  list of scheduled campaigns
     */
    public List<Campaign> getScheduledCampaigns()
    {
        return getCampaigns(50, CampaignStatus.SCHEDULED);
    }

    /**
     * Get the "scheduled" email campaigns since date
     *
     * @param sinceDate     string of time to search after yyyy/MM/dd HH:mm:ss
     * @return              list of all campaigns
     */
    public List<Campaign> getScheduledCampaigns(String sinceDate) throws Exception
    {
        return getCampaigns(50, sinceDate, CampaignStatus.SCHEDULED);
    }


    /**
     * Get the "deleted" email campaigns
     *
     * @return  list of deleted campaigns
     */
    public List<Campaign> getDeletedCampaigns()
    {
        return getCampaigns(50,CampaignStatus.DELETED);
    }

    /**
     * Get the "deleted" email campaigns since date
     *
     * @param sinceDate     string of time to search after yyyy/MM/dd HH:mm:ss
     * @return              list of all campaigns
     */
    public List<Campaign> getDeletedCampaigns(String sinceDate) throws Exception
    {
        return getCampaigns(50, sinceDate, CampaignStatus.DELETED);
    }


    /**
     * Get the email campaigns by their campaign status
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
            CampaignService campaignService = conn.getCampaignService();

            // synchronous method
            Paged<Campaign> pagedCampaigns = campaignService.getCampaigns(limit, status).execute().body();

            if (pagedCampaigns == null)
            {
                return campaigns;
            }

            if (pagedCampaigns.getResults().size() > 0)
            {
                campaigns.addAll(pagedCampaigns.getResults());
            }

            // keep fetching another batch until there are no more
            while (pagedCampaigns.getNextLink() != null)
            {
                // System.out.println(pagedCampaigns.getNextLink());
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

    private Date checkDateFormat(String theDate)
    {
        List<String> formatStrings = Arrays.asList(
                "yyyy/MM/dd HH:mm:ss",
                "yyyy/MM/dd"
        );

        for (String formatString : formatStrings)
        {
            try
            {
                return new SimpleDateFormat(formatString).parse(theDate);
            }
            catch (ParseException e) {}
        }

        return null;

    }


    /**
     * Get the email campaigns by their campaign status and date since
     *
     * @param limit     size of page to return, max 50
     * @param theDate   string of time to search after yyyy/MM/dd HH:mm:ss
     * @param status    enum of     ALL, DRAFT, RUNNING, SENT, SCHEDULED, DELETED
     * @return          list of campaigns
     */
    public List<Campaign> getCampaigns(int limit, String theDate, CampaignStatus status) throws Exception
    {
        if (limit > 50)
            limit = 50;

        Date date = checkDateFormat(theDate);
        if (date == null)
        {
            throw new Exception("Date format needs to be:   yyyy/MM/dd HH:mm:ss  OR   yyyy/MM/dd");
        }

        QueryDate queryDate = new QueryDate(date);

        List<Campaign> campaigns = new ArrayList<>();

        try
        {
            CampaignService campaignService = conn.getCampaignService();

            // synchronous method
            Paged<Campaign> pagedCampaigns = campaignService.getCampaigns(limit, queryDate, status).execute().body();

            if (pagedCampaigns == null)
            {
                return campaigns;
            }

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

        return campaigns;

    }

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
