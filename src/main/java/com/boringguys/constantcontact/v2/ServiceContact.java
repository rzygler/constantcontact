package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.CCApi2;
import com.constantcontact.v2.ContactService;
import com.constantcontact.v2.Paged;
import com.constantcontact.v2.QueryDate;
import com.constantcontact.v2.contacts.*;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


public class ServiceContact
{
    private ApiV2 service;
    private CCApi2 conn;
    int millisToSleepBetweenRequests = 4000;

    /**
     * Constructor for Contact Service
     *
     * @param apiKey    Constant Contact developer api key
     * @param apiToken  Constant Contact developer token
     */
    public ServiceContact(String apiKey, String apiToken)
    {
        this.service = new ApiV2(apiKey, apiToken);
        this.conn = service.getApiConn();
    }

    // Call<Paged<Contact>> getContacts(@Query("limit") int limit, @Query("status") ContactStatus status);
    // Call<Paged<Contact>> getContacts(@Query("limit") int limit, @Query("modified_since") QueryDate date, @Query("status") ContactStatus status);
    // Call<Contact> getContact(@Path("contactId") String contactId);
    //
    // Call<Response<Void>> unsubscribeContact(@Path("contactId") String contactId);
    //
    // Call<ContactList> updateContactList(@Body ContactList contactList, @Path("listId") String listId);
    //
    // Call<SignupFormResponse> createCustomSignupForm(@Body SignupFormRequest signupFormRequest);


    /**
     * Get contact list identified by listId
     * @param listId
     * @return
     */
    public ContactList getContactList(String listId)
    {
        ContactList list = new ContactList();

        try
        {
            ContactService service = conn.getContactService();
            list = service.getContactList(listId).execute().body();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        // give back empty list if none found
        return list;
    }

    /**
     * Get contact list by name
     * @param name
     * @return
     */
    public ContactList getContactListByName(String name)
    {
        List<ContactList> lists = new ArrayList<>();
        Optional<ContactList> result = null;

        try
        {
            ContactService service = conn.getContactService();

            // fetching the lists gets ALL your lists at once
            // no need to loop thru, oversight in their API design
            lists = service.getContactLists(null).execute().body();

            result = lists.stream()
                    .filter(item -> item.getName().equals(name))
                    .findFirst();
                    //.collect(Collectors.toList());

            if (result.isPresent())
            {
                return getContactList(result.get().getId());
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Get all the contact lists
     *
     * @return              List of contact lists
     */
    public List<ContactList> getContactLists()
    {
        List<ContactList> lists = new ArrayList<>();

        try
        {
            ContactService service = conn.getContactService();

            // fetching the lists gets ALL your lists at once
            // no need to loop thru, oversight in their API design
            lists = service.getContactLists(null).execute().body();
            if (lists != null && lists.size() != 0)
            {
                return lists;
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        // give back empty list if none found
        return lists;
    }


    /**
     * Delete a contact list
     *
     * @param listId
     * @return
     */
    public Response deleteContactList(String listId)
    {
        Response response = null;

        try
        {
            ContactService service = conn.getContactService();

            Call<Response<Void>> call = service.deleteContactList(listId);
            response = call.execute();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return response;
    }


    /**
     * Update an existing contact
     * @param contact
     * @return
     */
    public Response<Contact> updateContactByOwner(Contact contact)
    {
        return updateContact(contact, OptInSource.ACTION_BY_OWNER);
    }

    /**
     * Update an existing contact
     * @param contact
     * @return
     */
    public Response<Contact> updateContactByVisitor(Contact contact)
    {
        return updateContact(contact, OptInSource.ACTION_BY_VISITOR);
    }

    // TODO: update contact using hashmap<string, string>, email=test@test.com, field-to-update=new value

    /**
     * Update an existing contact
     * @param contact
     * @param source
     * @return
     */
    public Response<Contact> updateContact(Contact contact, OptInSource source)
    {
        Response<Contact> response = null;
        try
        {
            ContactService service = conn.getContactService();
            Call<Contact> call = service.updateContact(contact, contact.getId(), source);
            response = call.execute();
            // System.out.println(response);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return response;
    }



    /**
     * Create a new contact
     * @param contact
     * @return
     */
    public Response<Contact> createContactByOwner(Contact contact)
    {
        return createContact(contact, OptInSource.ACTION_BY_OWNER);
    }

    /**
     * Create a new contact
     * @param contact
     * @return
     */
    public Response<Contact> createContactByVisitor(Contact contact)
    {
        return createContact(contact, OptInSource.ACTION_BY_VISITOR);
    }

    /**
     * Create a new contact
     * @param contact
     * @param source
     * @return
     */
    public Response<Contact> createContact(Contact contact, OptInSource source)
    {
        Response<Contact> response = null;
        try
        {
            ContactService service = conn.getContactService();
            Call<Contact> call = service.createContact(contact, source);
            response = call.execute();
            // System.out.println(response);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Create a contact with basic info
     * @param email
     * @param first
     * @param last
     * @param listId
     * @return
     */
    public Response<Contact> createContactBySource(String email, String first, String last, String listId, OptInSource source)
    {
        Contact contact = new Contact();

        contact.setFirstName(first);
        contact.setLastName(last);

        EmailAddress address = new EmailAddress();
        address.setEmailAddress(email);
        // add the email address to the array
        contact.setEmailAddresses(new EmailAddress[]{ address });

        ContactListMetaData contactListMetaData = new ContactListMetaData();
        contactListMetaData.setId(listId);
        // add the contact list to the array
        contact.setContactLists(new ContactListMetaData[]{ contactListMetaData });

        return createContact(contact, source);
    }

    /**
     * Create contact with basic info
     * @param email
     * @param first
     * @param last
     * @param listId
     * @return
     */
    public Response<Contact> createContact(String email, String first, String last, String listId)
    {
        return createContactBySource(email, first, last, listId, OptInSource.ACTION_BY_OWNER);
    }

    /**
     * Create contact with basic info
     * @param email
     * @param first
     * @param last
     * @param listId
     * @return
     */
    public Response<Contact> createContactByOwner(String email, String first, String last, String listId)
    {
        return createContactBySource(email, first, last, listId, OptInSource.ACTION_BY_OWNER);
    }

    /**
     * Create contact with basic info
     * @param email
     * @param first
     * @param last
     * @param listId
     * @return
     */
    public Response<Contact> createContactByVisitor(String email, String first, String last, String listId)
    {
        return createContactBySource(email, first, last, listId, OptInSource.ACTION_BY_VISITOR);
    }


    /**
     * Create a new contact list
     *
     * @param listName
     * @param status        we default all of our lists to public
     * @return
     */
    public Response<ContactList> createContactList(String listName, ContactListStatus status)
    {
        Response<ContactList> response = null;
        ContactList list = new ContactList();
        ContactList newList = null;
        list.setName(listName);
        list.setStatus(status);

        try
        {
            ContactService service = conn.getContactService();
            Call<ContactList> call = service.createContactList(list);
            response = call.execute();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return response;
    }


    /**
     * Get the contacts from a list by the list name
     * @param listName
     * @param limit         how many to return in a page
     * @param dateCreated   date the list was modified
     * @return              a list of email contacts
     */
    public List<Contact> getContactsByListName(String listName, int limit, String dateCreated)
    {
        List<Contact> contacts = new ArrayList<>();

        ContactList list = this.getContactListByName(listName);
        if (list == null)
        {
            return contacts;
        }

        return this.getContactsByList(list.getId(), limit, dateCreated);

    }

    /**
     * Get the email contacts for the list
     *
     * @param listId        String id of the list
     * @param limit         how many to return in a page
     * @param dateCreated   date the list was modified
     * @return              a list of email contacts
     */
    public List<Contact> getContactsByList(String listId, int limit, String dateCreated)
    {
        List<Contact> contacts = new ArrayList<>();

        try
        {
            ContactService service = conn.getContactService();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyymmdd");
            Date dated =null;
            try
            {
                dated = formatter.parse(dateCreated);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            // synchronous method
            Paged<Contact> pagedContacts = service.getContacts(listId,limit, new QueryDate(dated)).execute().body();

            if (pagedContacts.getResults().size() > 0)
            {
                contacts.addAll(pagedContacts.getResults());
            }

            // keep fetching another batch until there are no more
            while (pagedContacts.getNextLink() != null)
            {
                // System.out.println(pagedContacts.getNextLink());
                Thread.sleep(millisToSleepBetweenRequests);
                pagedContacts = service.getContacts(pagedContacts.getNextLink()).execute().body();
                contacts.addAll(pagedContacts.getResults());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        // give back empty list if none found
        return contacts;
    }


    /**
     * Get the contacts by email address
     *
     * @param email         email address we are querying for
     * @return              a list of email contacts
     */
    public List<Contact> getContactsByEmail(String email)
    {
        List<Contact> contacts = new ArrayList<>();

        try
        {
            ContactService service = conn.getContactService();
            Paged<Contact> pagedContacts = service.getContactsByEmail(email).execute().body();
            if (pagedContacts.getResults().size() > 0)
            {
                contacts.addAll(pagedContacts.getResults());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contacts;
    }
}
