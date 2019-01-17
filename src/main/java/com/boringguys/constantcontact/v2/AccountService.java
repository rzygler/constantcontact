package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.CCApi2;
import com.constantcontact.v2.account.AccountEmailAddress;
import com.constantcontact.v2.account.AccountSummaryInformation;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.util.List;

public class AccountService
{

    private ApiV2 service;
    private CCApi2 conn;

    /**
     * Constructor for Account Service
     *
     * @param apiKey    Constant Contact developer api key
     * @param apiToken  Constant Contact developer token
     */
    public AccountService(String apiKey, String apiToken)
    {
        this.service = new ApiV2(apiKey, apiToken);
        this.conn = service.getApiConn();
    }


    /**
     * Get the account summary information
     *
     * @return  The account summary information
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

    /**
     * Update the account summary information
     *
     * @return  The account summary information
     * @throws NotImplementedException
     */
    public AccountSummaryInformation updateAccountSummaryInformation() throws NotImplementedException
    {
        throw new NotImplementedException("Not implemented yet.");
    }

    /**
     * List the account email addresses
     *
     * @return  The account summary information
     * @throws NotImplementedException
     */
    public List<AccountEmailAddress> getAccountEmailAddresses() throws NotImplementedException
    {
        throw new NotImplementedException("Not implemented yet.");
    }

    /**
     * Create an account email address
     *
     * @return  The account summary information
     * @throws NotImplementedException
     */
    public List<AccountEmailAddress> createAccountEmailAddress() throws NotImplementedException
    {
        throw new NotImplementedException("Not implemented yet.");
    }
}


