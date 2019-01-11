package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.CCApi2;
import com.constantcontact.v2.account.AccountSummaryInformation;

import java.io.IOException;

public class AccountService
{
    private ApiV2 service;
    private CCApi2 conn;

    public AccountService(String apiKey, String apiToken)
    {
        this.service = new ApiV2(apiKey, apiToken);
        this.conn = service.getApiConn();
    }

    /**
     *
     * @return
     */
    public AccountSummaryInformation getAccountSummary()
    {

        try
        {
            AccountSummaryInformation summary = conn.getAccountService()
                    .getAccountSummaryInformation()
                    .execute()
                    .body();

            if (summary == null)
            {
                return null;
            }
            return summary;
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;

    }
}
