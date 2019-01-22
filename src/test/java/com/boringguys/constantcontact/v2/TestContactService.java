package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.contacts.Contact;
import com.constantcontact.v2.contacts.ContactList;
import com.constantcontact.v2.contacts.ContactListMetaData;
import com.constantcontact.v2.contacts.EmailAddress;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
    void testGetContactsByList()
    {
        ContactService contactService = new ContactService(apiKey, apiToken);
        List<ContactList> lists = contactService.getContactLists();
        ContactList list = lists.get(0);
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

    // TODO: test creating a contact

    @Test
    void testCreateContactReturnsContact()
    {
        ContactService contactService = new ContactService(apiKey, apiToken);
        Contact contact = new Contact();

        // set up test contact
        String listName = Helper.generateRandomString(10);

        EmailAddress address = new EmailAddress();
        address.setEmailAddress(listName + "@gmail.com");
        // add the email address to the array
        contact.setEmailAddresses(new EmailAddress[]{ address });

        // TODO: figure out how to set a list for a contact
        // 1111111111
        ContactListMetaData contactListMetaData = new ContactListMetaData();
        contactListMetaData.setId("1111111111");
        // add the contact list to the array
        contact.setContactLists(new ContactListMetaData[]{ contactListMetaData });
        System.out.println(address.getEmailAddress());

        // send it
        Response<Contact> response = contactService.createContactByOwner(contact);
        assertEquals(201, response.code());
        assertEquals("Created", response.message());
        assertNotNull(response.body());

        // Contact is created and embedded in response.body
        assertTrue(response.body() instanceof Contact);
        String id = response.body().getId();
        assertEquals(response.body().getEmailAddresses()[0].getEmailAddress(), address.getEmailAddress());

    }





}