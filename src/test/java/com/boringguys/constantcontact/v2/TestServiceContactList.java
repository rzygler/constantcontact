package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.contacts.ContactList;
import com.constantcontact.v2.contacts.ContactListStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestServiceContactList
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

    /**
     * Get one contact list
     */
    @Test
    void testGetContactList()
    {
        // first get all the lists, then pick one
        ServiceContact serviceContact = new ServiceContact(apiKey, apiToken);
        List<ContactList> lists = serviceContact.getContactLists();
        ContactList list = lists.get(0);
        String listId = list.getId();

        ContactList list2 = serviceContact.getContactList(listId);
        assertEquals(list2.getName(), list.getName());
        assertEquals(list2.getId(), list.getId());
    }

    @Test
    void testGetBadContactListFails()
    {
        ServiceContact serviceContact = new ServiceContact(apiKey, apiToken);
        ContactList list = serviceContact.getContactList("aaaaaaaa");
        assertNull(list);
    }

    @Test
    void testGetContactLists()
    {
        ServiceContact serviceContact = new ServiceContact(apiKey, apiToken);
        List<ContactList> lists = serviceContact.getContactLists();
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
    void testCreateContactListWithMissingNameIsBadRequest()
    {
        ServiceContact serviceContactForCreate = new ServiceContact(apiKey, apiToken);
        String listName = null;
        ContactListStatus status = ContactListStatus.ACTIVE;
        Response<ContactList> response = serviceContactForCreate.createContactList(listName, status);
        assertEquals(400, response.code());
        assertEquals("Bad Request", response.message());
        assertNull(response.body());
    }

    @Test
    void testCreateContactListWithMissingStatusIsBadRequest()
    {
        ServiceContact serviceContactForCreate = new ServiceContact(apiKey, apiToken);
        String listName = Helper.generateRandomString(10);
        ContactListStatus status = null;
        Response<ContactList> response = serviceContactForCreate.createContactList(listName, status);
        assertEquals(400, response.code());
        assertEquals("Bad Request", response.message());
        assertNull(response.body());
    }

    @Test
    void testCreateContactListWithDuplicateNameIsBadRequest() throws InterruptedException
    {
        ServiceContact serviceContactForCreate = new ServiceContact(apiKey, apiToken);
        String listName = Helper.generateRandomString(10);
        ContactListStatus status = ContactListStatus.ACTIVE;
        Response<ContactList> response = serviceContactForCreate.createContactList(listName, status);
        String id = response.body().getId();
        Thread.sleep(4000);

        // duplicate contact list
        Response<ContactList> response2 = serviceContactForCreate.createContactList(listName, status);

        assertEquals(409, response2.code());
        assertEquals("Conflict", response2.message());
        assertNull(response2.body());

        // clean up
        Thread.sleep(4000);
        ServiceContact service2 = new ServiceContact(apiKey, apiToken);
        service2.deleteContactList(id);
    }

    @Test
    void testCreateContactListIsGoodRequest() throws InterruptedException
    {
        ServiceContact service = new ServiceContact(apiKey, apiToken);
        String listName = Helper.generateRandomString(10);
        ContactListStatus status = ContactListStatus.ACTIVE;
        Response<ContactList> response = service.createContactList(listName, status);
        assertEquals(201, response.code());
        assertEquals("Created", response.message());
        assertNotNull(response.body());

        // Contact list is created and embedded in response.body
        assertTrue(response.body() instanceof ContactList);
        assertEquals(listName, response.body().getName());
        String id = response.body().getId();

        // clean up
        Thread.sleep(4000);
        ServiceContact service2 = new ServiceContact(apiKey, apiToken);
        service2.deleteContactList(id);
    }


    @Test
    void testCreatedContactListIsDeleted() throws InterruptedException
    {
        ServiceContact service = new ServiceContact(apiKey, apiToken);
        String listName = Helper.generateRandomString(10);
        ContactListStatus status = ContactListStatus.ACTIVE;
        Response<ContactList> response = service.createContactList(listName, status);
        String id = response.body().getId();

        // clean up
        Thread.sleep(4000);
        ServiceContact service2 = new ServiceContact(apiKey, apiToken);
        Response deleteResponse = service2.deleteContactList(id);
        assertEquals(204, deleteResponse.code());
    }

    //1645435044    zzz
    // 1705803815   yyy

    @Test
    @Disabled
    void testGetContactListByNameZZZ() throws InterruptedException
    {
        ServiceContact service = new ServiceContact(apiKey, apiToken);
        ContactList list = service.getContactListByName("yyy");

        if(showDebug)
        {
            Helper.printContactList(list);
        }

    }

    @Test
    void testGetContactListByName() throws InterruptedException
    {
        ServiceContact service = new ServiceContact(apiKey, apiToken);
        String name = Helper.generateRandomString(10);
        ContactListStatus status = ContactListStatus.ACTIVE;
        Response<ContactList> response = service.createContactList(name, status);
        String id = response.body().getId();

        // wait a bit
        Thread.sleep(4000);

        // now go find that list by name
        ContactList list = service.getContactListByName(name);

        if(showDebug)
        {
            Helper.printContactList(list);
        }
        assertNotNull(list);
        assertEquals(name, list.getName());
        assertNotNull(list.getCreatedDate());

        // clean up
        Thread.sleep(4000);
        ServiceContact service2 = new ServiceContact(apiKey, apiToken);
        Response deleteResponse = service2.deleteContactList(id);
        assertEquals(204, deleteResponse.code());
    }

    @Test
    void testGetContactListByNameFailsOnBadName()
    {
        String name = Helper.generateRandomString(10);
        ServiceContact service = new ServiceContact(apiKey, apiToken);

        // now go find that list by name
        ContactList list = service.getContactListByName(name);
        assertNull(list);
    }
}
