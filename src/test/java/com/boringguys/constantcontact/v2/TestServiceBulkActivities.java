package com.boringguys.constantcontact.v2;

import com.boringguys.constantcontact.v2.exceptions.PayloadTooLargeException;
import com.boringguys.constantcontact.v2.exceptions.TooManyContactsException;
import com.constantcontact.v2.bulkactivities.Activity;
import com.constantcontact.v2.contacts.Contact;
import com.constantcontact.v2.contacts.ContactList;
import com.constantcontact.v2.contacts.ContactListStatus;
import com.constantcontact.v2.contacts.EmailAddress;
import org.junit.jupiter.api.*;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
    void TestBulkActivitiesImportSucceeds()
            throws IOException, PayloadTooLargeException, TooManyContactsException, InterruptedException
    {
        ServiceContact contactService = new ServiceContact(apiKey, apiToken);
        String name = Helper.generateRandomString(10);
        ContactListStatus status = ContactListStatus.ACTIVE;
        Response<ContactList> listResponse = contactService.createContactList(name, status);
        String id = listResponse.body().getId();

        // wait a bit
        Thread.sleep(4000);
        // one or more contact lists that we are adding these contacts to
        String[] contactLists = {
                id
        };

        // list of contacts that we are adding
        List<Contact> contacts = new ArrayList<>();

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

        ServiceBulkActivities service = new ServiceBulkActivities(apiKey, apiToken);
        Response<Activity> response = service.addContacts(contacts, contactLists, defaultColumns);
        assertEquals(201, response.code());
        assertEquals("Created", response.message());
        assertNotNull(response.body());

        // Activity is created and embedded in response.body
        assertTrue(response.body() instanceof Activity);
        String activityId = response.body().getId();

        // clean up
        Thread.sleep(4000);
        ServiceContact service2 = new ServiceContact(apiKey, apiToken);
        Response deleteResponse = service2.deleteContactList(id);
        assertEquals(204, deleteResponse.code());

    }


    @Test
    void TestBulkActivitiesBadListIdImportFails() throws IOException, PayloadTooLargeException, TooManyContactsException
    {
        // one or more contact lists that we are adding these contacts to
        String[] contactLists = {
                "111111111"
        };

        // list of contacts that we are adding
        List<Contact> contacts = new ArrayList<>();

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

        ServiceBulkActivities service = new ServiceBulkActivities(apiKey, apiToken);
        Response<Activity> response = service.addContacts(contacts, contactLists, defaultColumns);

        // {"error_key":"activity.field.list_id.invalid","error_message":"Contact List ID 111111111 does not exist."}]
        // System.out.println(response.errorBody().string());

        assertEquals(400, response.code());
        assertEquals("Bad Request", response.message());
        assertNull(response.body());

    }

    @Test
    void TestPayloadTooLargeBulkActivitiesImportFails()
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
    void Test40001BulkActivitiesImportFails()
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
