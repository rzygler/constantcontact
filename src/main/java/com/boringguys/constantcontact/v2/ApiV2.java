package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.CCApi2;


public class ApiV2
{
    private int millisToSleepBetweenRequests = 2000;
    private CCApi2 api;

    public CCApi2 getApiConn()
    {
        return api;
    }

    /**
     *
     * @param api
     */
    public void setApiConn(CCApi2 api)
    {
        this.api = api;
    }

    /**
     * Constructor
     * @param apiKey    Constant Contact developer api key
     * @param apiToken  Constant Contact developer token
     */
    public ApiV2(String key, String token)
    {
        this.api = getApiService(key, token);
    }

    /**
     * Empty Constructor
     */
    public ApiV2()
    {

    }

    /**
     *
     * @param apiKey    Constant Contact developer api key
     * @param apiToken  Constant Contact developer token
     * @return      CCApi2 API service object
     */
    public static CCApi2 getApiService(String key, String token)
    {
        return new CCApi2(key, token);
    }



}

