package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.account.AccountAddress;
import com.constantcontact.v2.account.AccountSummaryInformation;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestAccountService
{
    private String apiKey;
    private String apiToken;
    private String dateCreated;
    private int fetchLimit;

    /**
     * sets config values for API key and token
     */
    private void getApiConfig() throws Exception
    {
        Properties prop = new Properties();
        InputStream input = null;
        try {

            input = new FileInputStream("config.properties");
            prop.load(input);

            if (prop.getProperty("api_key").equals("your_key_here"))
            {
                throw new Exception("Please update your API key in config.properties");
            }
            this.apiKey = prop.getProperty("api_key");

            if (prop.getProperty("api_token").equals("your_token_here"))
            {
                throw new Exception("Please update your API token in config.properties");
            }
            this.apiToken = prop.getProperty("api_token");
            this.dateCreated = prop.getProperty("date_created");
            this.fetchLimit = Integer.parseInt(prop.getProperty("fetch_limit"));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @BeforeAll
    static void initAll()
    {

    }

    @BeforeEach
    void init() throws Exception
    {
        getApiConfig();
    }

    @Test
    void TestAccountSummaryInformationIsFetched()
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
    }

    @Test
    void TestUpdateAccountSummaryInformation()
    {
        AccountService accountService = new AccountService(apiKey, apiToken);
        assertThrows(NotImplementedException.class, () -> accountService.updateAccountSummaryInformation() );
    }

    @Test
    void TestCreateAccountEmailAddress()
    {
        AccountService accountService = new AccountService(apiKey, apiToken);
        assertThrows(NotImplementedException.class, () -> accountService.createAccountEmailAddress() );
    }

    @Test
    void TestGetAccountEmailAddresses()
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
