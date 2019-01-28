package com.boringguys.constantcontact.v2;

import com.boringguys.constantcontact.v2.exceptions.PayloadTooLargeException;
import com.boringguys.constantcontact.v2.exceptions.TooManyContactsException;
import com.constantcontact.v2.contacts.Contact;
import com.constantcontact.v2.contacts.EmailAddress;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestServiceBulkActivities
{
    private String apiKey;
    private String apiToken;
    private String dateCreated;
    private boolean showDebug = false;
    private String[] defaultColumns = {
            "EMAIL ADDRESS",
            "FIRST NAME",
            "LAST NAME"
    };

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
    @Disabled
    void TestBulkActivitiesImportSucceeds()
    {
        // one or more contact lists that we are adding these contacts to
        String[] contactLists = {
                "111",
                "222"
        };

        // list of contacts that we are adding
        List<Contact> contacts = new ArrayList<Contact>();

        Contact homer = new Contact();
        EmailAddress homerAddress = new EmailAddress();
        homerAddress.setEmailAddress("homer.simpson@gmail.com");
        homer.setEmailAddresses(new EmailAddress[]{ homerAddress });
        homer.setFirstName("Homer");
        homer.setLastName("Simpson");
        contacts.add(homer);

        Contact marge = new Contact();
        EmailAddress margeAddress = new EmailAddress();
        margeAddress.setEmailAddress("marge.simpson@gmail.com");
        marge.setEmailAddresses(new EmailAddress[]{ margeAddress });
        marge.setFirstName("Marge");
        marge.setLastName("Simpson");
        contacts.add(marge);

        String[] columns = {
                "EMAIL ADDRESS",
                "FIRST NAME",
                "LAST NAME"
        };

        ServiceBulkActivities service = new ServiceBulkActivities(apiKey, apiToken);
        // service.addContacts(contacts, contactLists, columns);

    }

    @Test
    void TestPayloadTooLargeBulkActivitiesImportFails() throws Exception
    {
        // one or more contact lists that we are adding these contacts to
        String[] contactLists = {
                "111",
                "222"
        };

        // list of contacts that we are adding
        List<Contact> contacts = new ArrayList<Contact>();

        for(int i = 0; i < 40000; i++)
        {
            Contact c = new Contact();
            c.setFirstName(Helper.generateRandomString(40));
            c.setLastName(Helper.generateRandomString(40));
            EmailAddress email = new EmailAddress();
            email.setEmailAddress(Helper.generateRandomString(40) + "@gmail.com");
            c.setEmailAddresses(new EmailAddress[]{ email });
            contacts.add(c);
        }

        ServiceBulkActivities service = new ServiceBulkActivities(apiKey, apiToken);
        assertThrows(PayloadTooLargeException.class, () -> {
            service.addContacts(contacts, contactLists, defaultColumns);
        });
    }

    @Test
    void Test40001BulkActivitiesImportFails() throws Exception
    {
        // one or more contact lists that we are adding these contacts to
        String[] contactLists = {
                "111",
                "222"
        };

        // list of contacts that we are adding
        List<Contact> contacts = new ArrayList<Contact>();

        for(int i = 0; i < 40001; i++)
        {
            Contact c = new Contact();
            c.setFirstName(Helper.generateRandomString(10));
            c.setLastName(Helper.generateRandomString(10));
            EmailAddress email = new EmailAddress();
            email.setEmailAddress(Helper.generateRandomString(20) + "@gmail.com");
            c.setEmailAddresses(new EmailAddress[]{ email });
            contacts.add(c);
        }

        ServiceBulkActivities service = new ServiceBulkActivities(apiKey, apiToken);
        assertThrows(TooManyContactsException.class, () -> {
            service.addContacts(contacts, contactLists, defaultColumns);
        });
    }

}
