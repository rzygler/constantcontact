package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.contacts.*;
import org.junit.jupiter.api.*;
import retrofit2.Response;

import java.util.HashMap;
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
    void testGetHomerByEmail()
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
    void testGetHomer2ByEmail()
    {
        ServiceContact serviceContact = new ServiceContact(apiKey, apiToken);
        List<Contact> contacts = serviceContact.getContactsByEmail("homer.simpson@gmail.com");

        assertTrue(contacts.size() > 0);
        assertNotNull(contacts.get(0));
        Contact contact = contacts.get(0);
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
    void testUpdateContactReturnsUpdatedContact() throws InterruptedException
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
        String firstName = Helper.generateRandomString(10);
        String lastName = Helper.generateRandomString(10);

        contact.setFirstName(firstName);
        contact.setLastName(lastName);

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
        assertNotNull(response2.body());
        Contact savedContact = response2.body();

        // Contact is created and embedded in response.body
        assertTrue(response2.body() instanceof Contact);
        String contactId = response2.body().getId();
        assertEquals(response2.body().getEmailAddresses()[0].getEmailAddress(), address.getEmailAddress());

        // wait a bit
        Thread.sleep(4000);

        // lets update the contact
        String emailName2 = Helper.generateRandomString(10);
        String firstName2 = Helper.generateRandomString(10);
        String lastName2 = Helper.generateRandomString(10);
        savedContact.setFirstName(firstName2);
        savedContact.setLastName(lastName2);

        EmailAddress address2 = new EmailAddress();
        address2.setEmailAddress(emailName2 + "@gmail.com");
        // add the email address to the array
        savedContact.setEmailAddresses(new EmailAddress[]{ address2 });


        // System.out.println(savedContact.getId() + " " + savedContact.getFirstName());

        Response<Contact> response3 = service.updateContactByOwner(savedContact);
        assertEquals(200, response3.code());
        assertEquals("OK", response3.message());
        assertNotNull(response2.body());
        Contact updatedContact = response3.body();
        assertEquals(firstName2, updatedContact.getFirstName());
        assertEquals(lastName2, updatedContact.getLastName());


        // cannot actually delete the contact via api

        // delete the list
        ServiceContact service2 = new ServiceContact(apiKey, apiToken);
        Response deleteResponse = service2.deleteContactList(listId);
        assertEquals(204, deleteResponse.code());
    }

    @Test
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
    void testQuickCreateContactReturnsContact() throws InterruptedException
    {
        // First create a list
        ServiceContact service = new ServiceContact(apiKey, apiToken);
        String name = Helper.generateRandomString(10);
        ContactListStatus status = ContactListStatus.ACTIVE;
        Response<ContactList> response = service.createContactList(name, status);
        String listId = response.body().getId();

        // wait a bit
        Thread.sleep(4000);

        // set up test contact
        String email = Helper.generateRandomString(10) + "@gmail.com";
        String first = Helper.generateRandomString(11);
        String last = Helper.generateRandomString(12);


        // create the contact
        Response<Contact> response2 = service.createContact(email, first, last, listId);
        assertEquals(201, response2.code());
        assertEquals("Created", response2.message());
        assertNotNull(response.body());

        // Contact is created and embedded in response.body
        assertTrue(response2.body() instanceof Contact);
        String contactId = response2.body().getId();
        Contact contact = response2.body();
        assertEquals(email, contact.getEmailAddresses()[0].getEmailAddress());
        assertEquals(first, contact.getFirstName());
        assertEquals(last, contact.getLastName());

        // wait a bit
        Thread.sleep(4000);

        // cannot actually delete the contact via api

        // delete the list
        ServiceContact service2 = new ServiceContact(apiKey, apiToken);
        Response deleteResponse = service2.deleteContactList(listId);
        assertEquals(204, deleteResponse.code());

    }

    @Test
    void testIsAllowedContactFieldSucceeds()
    {
        ServiceContact service = new ServiceContact(apiKey, apiToken);
        assertTrue(service.isAllowedContactField("first_name"));
        assertTrue(service.isAllowedContactField("first_name"));
    }

    @Test
    void testIsAllowedContactFieldFails()
    {
        ServiceContact service = new ServiceContact(apiKey, apiToken);
        String name = Helper.generateRandomString(10);
        assertFalse(service.isAllowedContactField(name));

    }

    @Test
    void testUpdateContactNamesSucceeds() throws InterruptedException
    {
        // First create a list
        ServiceContact service = new ServiceContact(apiKey, apiToken);
        String name = Helper.generateRandomString(10);
        ContactListStatus status = ContactListStatus.ACTIVE;
        Response<ContactList> response = service.createContactList(name, status);
        String listId = response.body().getId();

        // wait a bit
        Thread.sleep(4000);

        // set up test contact
        String email = Helper.generateRandomString(10) + "@gmail.com";
        String first = Helper.generateRandomString(11);
        String last = Helper.generateRandomString(12);


        // create the contact
        Response<Contact> response2 = service.createContact(email, first, last, listId);
        assertEquals(201, response2.code());
        assertEquals("Created", response2.message());
        assertNotNull(response.body());

        first = Helper.generateRandomString(6);
        last = Helper.generateRandomString(6);
        HashMap<String,String> fields = new HashMap<>();
        fields.put("first_name",first);
        fields.put("last_name", last);

        // wait a bit
        Thread.sleep(4000);

        Response<Contact> response3 = service.updateContact(email,fields );
        assertEquals(200,response3.code());
        assertEquals("OK", response3.message());

        Contact updatedContact = response3.body();
        assertEquals(first, updatedContact.getFirstName());
        assertEquals(last, updatedContact.getLastName());


        // wait a bit
        Thread.sleep(4000);

        // cannot actually delete the contact via api

        // delete the list
        ServiceContact service2 = new ServiceContact(apiKey, apiToken);
        Response deleteResponse = service2.deleteContactList(listId);
        assertEquals(204, deleteResponse.code());

    }

    @Test
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