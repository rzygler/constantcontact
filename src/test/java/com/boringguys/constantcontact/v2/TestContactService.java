package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.account.AccountAddress;
import com.constantcontact.v2.account.AccountSummaryInformation;
import com.constantcontact.v2.contacts.Contact;
import com.constantcontact.v2.contacts.ContactList;
import com.constantcontact.v2.contacts.ContactListStatus;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class TestContactService
{
    private String apiKey;
    private String apiToken;
    private String dateCreated;
    private int fetchLimit = 500;
    private boolean showDebug = false;

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
    void testGetContactLists()
    {
        ContactService contactService = new ContactService(apiKey, apiToken);
        List<ContactList> lists = contactService.getContactLists();
        assertNotNull(lists);
        assertTrue(lists.size() > 0);

        for(ContactList list:lists)
        {
            assertTrue(list.getName().length() > 2);
        }

        if(showDebug)
        {
            Helper.printContactLists(lists);
        }
    }

    @Test
    void testGetContactsByList()
    {
        ContactService contactService = new ContactService(apiKey, apiToken);
        List<ContactList> lists = contactService.getContactLists();
        ContactList list = lists.get(1);
        List<Contact> contacts = contactService.getContactsByList(list.getId(), this.fetchLimit, this.dateCreated);

        assertTrue(contacts.size() > 0);
        Contact contact = contacts.get(0);
        assertNotNull(contact.getEmailAddresses()[0].getEmailAddress());
        assertTrue(contact.getEmailAddresses()[0].getEmailAddress().length() >= 2);
        assertTrue(contact.getFirstName().length() >= 2);
        assertTrue(contact.getLastName().length() >= 2);

        if (showDebug)
        {
            contacts.forEach(a -> Helper.printContact(a));
        }
    }


    @Test
    void testGetContactsByEmail()
    {
        ContactService contactService = new ContactService(apiKey, apiToken);
        List<Contact> contacts = contactService.getContactsByEmail("homer@gmail.com");

        assertTrue(contacts.size() > 0);
        assertNotNull(contacts.get(0));
        assertTrue(contacts.get(0).getFirstName().equals("Homer"));

        if (showDebug)
        {
            contacts.forEach(a -> Helper.printContact(a));
        }
    }

    @Test
    void testCreateContactListWithMissingNameIsBadRequest()
    {
        ContactService contactServiceForCreate = new ContactService(apiKey, apiToken);
        String listName = null;
        ContactListStatus status = ContactListStatus.ACTIVE;
        Response<ContactList> response = contactServiceForCreate.createContactList(listName, status);
        assertEquals(response.code(), 400);
        assertEquals(response.message(), "Bad Request");
        assertNull(response.body());
    }

    @Test
    void testCreateContactListWithMissingStatusIsBadRequest()
    {
        ContactService contactServiceForCreate = new ContactService(apiKey, apiToken);
        String listName = Helper.generateRandomString(10);
        ContactListStatus status = null;
        Response<ContactList> response = contactServiceForCreate.createContactList(listName, status);
        assertEquals(response.code(), 400);
        assertEquals(response.message(), "Bad Request");
        assertNull(response.body());
    }

    @Test
    void testCreateContactListWithDuplicateNameIsBadRequest() throws InterruptedException
    {
        ContactService contactServiceForCreate = new ContactService(apiKey, apiToken);
        String listName = Helper.generateRandomString(10);
        ContactListStatus status = ContactListStatus.ACTIVE;
        Response<ContactList> response = contactServiceForCreate.createContactList(listName, status);
        String id = response.body().getId();
        Thread.sleep(4000);

        // duplicate contact list
        Response<ContactList> response2 = contactServiceForCreate.createContactList(listName, status);

        assertEquals(response2.code(), 409);
        assertEquals(response2.message(), "Conflict");
        assertNull(response2.body());

        // clean up
        Thread.sleep(4000);
        ContactService service2 = new ContactService(apiKey, apiToken);
        service2.deleteContactList(id);
    }



    @Test
    void testCreateContactListIsGoodRequest() throws InterruptedException
    {
        ContactService service = new ContactService(apiKey, apiToken);
        String listName = Helper.generateRandomString(10);
        ContactListStatus status = ContactListStatus.ACTIVE;
        Response<ContactList> response = service.createContactList(listName, status);
        assertEquals(response.code(), 201);
        assertEquals(response.message(), "Created");
        assertNotNull(response.body());

        // Contact list is created and embedded in response.body
        assertTrue(response.body() instanceof ContactList);
        assertEquals(listName, response.body().getName());
        String id = response.body().getId();

        // clean up
        Thread.sleep(4000);
        ContactService service2 = new ContactService(apiKey, apiToken);
        service2.deleteContactList(id);
    }

    @Test
    void testCreatedContactIsDeleted() throws InterruptedException
    {
        ContactService service = new ContactService(apiKey, apiToken);
        String listName = Helper.generateRandomString(10);
        ContactListStatus status = ContactListStatus.ACTIVE;
        Response<ContactList> response = service.createContactList(listName, status);
        String id = response.body().getId();

        // clean up
        Thread.sleep(4000);
        ContactService service2 = new ContactService(apiKey, apiToken);
        Response deleteResponse = service2.deleteContactList(id);
        assertEquals(deleteResponse.code(), 204);
    }
}