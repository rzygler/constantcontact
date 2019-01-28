package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.contacts.*;
import org.junit.jupiter.api.*;
import retrofit2.Response;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class TestServiceContact
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
    void testGetContactsByListNameSucceeds()
    {
        ServiceContact serviceContact = new ServiceContact(apiKey, apiToken);
        List<ContactList> lists = serviceContact.getContactLists();
        ContactList list = lists.get(0);
        List<Contact> contacts = serviceContact.getContactsByListName(list.getName(), this.fetchLimit, this.dateCreated);

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
    void testGetContactsByListNameFails()
    {
        ServiceContact serviceContact = new ServiceContact(apiKey, apiToken);
        String str = Helper.generateRandomString(10);
        List<Contact> contacts = serviceContact.getContactsByListName(str, this.fetchLimit, this.dateCreated);
        assertTrue(contacts.size() == 0);


    }

    @Test
    void testGetContactsByList()
    {
        ServiceContact serviceContact = new ServiceContact(apiKey, apiToken);
        List<ContactList> lists = serviceContact.getContactLists();
        ContactList list = lists.get(0);
        List<Contact> contacts = serviceContact.getContactsByList(list.getId(), this.fetchLimit, this.dateCreated);

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
        ServiceContact serviceContact = new ServiceContact(apiKey, apiToken);
        List<Contact> contacts = serviceContact.getContactsByEmail("homer@gmail.com");

        assertTrue(contacts.size() > 0);
        assertNotNull(contacts.get(0));
        assertTrue(contacts.get(0).getFirstName().equals("Homer"));

        if (showDebug)
        {
            contacts.forEach(a -> Helper.printContact(a));
        }
    }



    @Test
    void testCreateContactMissingContactListFails() throws InterruptedException
    {
        Contact contact = new Contact();

        // set up test contact
        String emailName = Helper.generateRandomString(10);

        EmailAddress address = new EmailAddress();
        address.setEmailAddress(emailName + "@gmail.com");
        // add the email address to the array
        contact.setEmailAddresses(new EmailAddress[]{ address });


        // create the contact
        ServiceContact service = new ServiceContact(apiKey, apiToken);
        Response<Contact> response = service.createContactByOwner(contact);
        assertEquals(400, response.code());
        assertEquals("Bad Request", response.message());
        assertNull(response.body());
    }

    @Test
    void testCreateContactMissingEmailFails() throws InterruptedException
    {
        // First create a list
        ServiceContact service = new ServiceContact(apiKey, apiToken);
        String name = Helper.generateRandomString(10);
        ContactListStatus status = ContactListStatus.ACTIVE;
        Response<ContactList> response = service.createContactList(name, status);
        String listId = response.body().getId();

        // wait a bit
        Thread.sleep(4000);

        Contact contact = new Contact();
        ContactListMetaData contactListMetaData = new ContactListMetaData();
        contactListMetaData.setId(listId);
        // add the contact list to the array
        contact.setContactLists(new ContactListMetaData[]{ contactListMetaData });

        // create the contact
        Response<Contact> response2 = service.createContactByOwner(contact);
        assertEquals(400, response2.code());
        assertEquals("Bad Request", response2.message());
        assertNull(response2.body());

        // wait a bit
        Thread.sleep(4000);

        // delete the list
        ServiceContact service2 = new ServiceContact(apiKey, apiToken);
        Response deleteResponse = service2.deleteContactList(listId);
        assertEquals(204, deleteResponse.code());
    }

    @Test
    @Disabled
    void testCreateContactReturnsContact() throws InterruptedException
    {
        // First create a list
        ServiceContact service = new ServiceContact(apiKey, apiToken);
        String name = Helper.generateRandomString(10);
        ContactListStatus status = ContactListStatus.ACTIVE;
        Response<ContactList> response = service.createContactList(name, status);
        String listId = response.body().getId();

        // wait a bit
        Thread.sleep(4000);

        Contact contact = new Contact();

        // set up test contact
        String emailName = Helper.generateRandomString(10);

        EmailAddress address = new EmailAddress();
        address.setEmailAddress(emailName + "@gmail.com");
        // add the email address to the array
        contact.setEmailAddresses(new EmailAddress[]{ address });

        ContactListMetaData contactListMetaData = new ContactListMetaData();
        contactListMetaData.setId(listId);
        // add the contact list to the array
        contact.setContactLists(new ContactListMetaData[]{ contactListMetaData });
        // System.out.println(address.getEmailAddress());

        // create the contact
        Response<Contact> response2 = service.createContactByOwner(contact);
        assertEquals(201, response2.code());
        assertEquals("Created", response2.message());
        assertNotNull(response.body());

        // Contact is created and embedded in response.body
        assertTrue(response2.body() instanceof Contact);
        String contactId = response2.body().getId();
        assertEquals(response2.body().getEmailAddresses()[0].getEmailAddress(), address.getEmailAddress());

        // wait a bit
        Thread.sleep(4000);

        // cannot actually delete the contact via api

        // delete the list
        ServiceContact service2 = new ServiceContact(apiKey, apiToken);
        Response deleteResponse = service2.deleteContactList(listId);
        assertEquals(204, deleteResponse.code());

    }



    @Test
    @Disabled
    void testCreateDuplicateContactFails() throws InterruptedException
    {
        // First create a list
        ServiceContact service = new ServiceContact(apiKey, apiToken);
        String name = Helper.generateRandomString(10);
        ContactListStatus status = ContactListStatus.ACTIVE;
        Response<ContactList> response = service.createContactList(name, status);
        String listId = response.body().getId();

        // wait a bit
        Thread.sleep(4000);

        Contact contact = new Contact();

        // set up test contact
        String emailName = Helper.generateRandomString(10);

        EmailAddress address = new EmailAddress();
        address.setEmailAddress(emailName + "@gmail.com");
        // add the email address to the array
        contact.setEmailAddresses(new EmailAddress[]{ address });

        ContactListMetaData contactListMetaData = new ContactListMetaData();
        contactListMetaData.setId(listId);
        // add the contact list to the array
        contact.setContactLists(new ContactListMetaData[]{ contactListMetaData });

        // create the contact
        Response<Contact> response2 = service.createContactByOwner(contact);
        assertEquals(201, response2.code());
        assertEquals("Created", response2.message());
        assertNotNull(response.body());

        // wait a bit
        Thread.sleep(4000);

        // try to add the contact again
        Response<Contact> response3 = service.createContactByOwner(contact);

        assertEquals(409, response3.code());
        assertEquals("Conflict", response3.message());
        assertNull(response3.body());

        // wait a bit
        Thread.sleep(4000);

        // cannot actually delete the contact via api

        // delete the list
        ServiceContact service2 = new ServiceContact(apiKey, apiToken);
        Response deleteResponse = service2.deleteContactList(listId);
        assertEquals(204, deleteResponse.code());

    }



}