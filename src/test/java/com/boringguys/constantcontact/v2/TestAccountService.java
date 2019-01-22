package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.account.AccountAddress;
import com.constantcontact.v2.account.AccountSummaryInformation;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

public class TestAccountService
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
    void testAccountSummaryInformationIsFetched()
    {
        AccountService accountService = new AccountService(apiKey, apiToken);
        AccountSummaryInformation summaryInfo = accountService.getAccountSummary();
        assertNotNull(summaryInfo != null);
        assertNotNull(summaryInfo.getFirstName());
        assertNotNull(summaryInfo.getLastName());
        assertTrue(summaryInfo.getFirstName().length() >= 2);
        assertTrue(summaryInfo.getLastName().length() >= 2);
        assertTrue(summaryInfo.getOrganizationName().length() >= 2);
        assertTrue(summaryInfo.getEmail().length() >= 6);
        assertTrue(summaryInfo.getPhone().length() >= 10);
        assertTrue(summaryInfo.getOrganizationAddresses().length >= 1);
        AccountAddress address = summaryInfo.getOrganizationAddresses()[0];
        assertTrue(address.getLine1().length() >= 4);
        assertTrue(address.getCity().length() >= 4);
        assertTrue(address.getState().length() >= 4);
        assertTrue(address.getPostalCode().length() >=5 );

        if (showDebug)
        {
            Helper.printAccountSummaryInformation(summaryInfo);
        }
    }

    @Test
    void testUpdateAccountSummaryInformation()
    {
        AccountService accountService = new AccountService(apiKey, apiToken);
        assertThrows(NotImplementedException.class, () -> accountService.updateAccountSummaryInformation() );
    }

    @Test
    void testCreateAccountEmailAddress()
    {
        AccountService accountService = new AccountService(apiKey, apiToken);
        assertThrows(NotImplementedException.class, () -> accountService.createAccountEmailAddress() );
    }

    @Test
    void testGetAccountEmailAddresses()
    {
        AccountService accountService = new AccountService(apiKey, apiToken);
        assertThrows(NotImplementedException.class, () -> accountService.getAccountEmailAddresses() );
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
