package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.CCApi2;
import com.constantcontact.v2.Paged;
import com.constantcontact.v2.QueryDate;
import com.constantcontact.v2.contacts.Contact;
import com.constantcontact.v2.contacts.ContactList;
import com.constantcontact.v2.contacts.ContactListStatus;
import com.constantcontact.v2.contacts.OptInSource;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


public class ContactService
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
    public ContactService(String apiKey, String apiToken)
    {
        this.service = new ApiV2(apiKey, apiToken);
        this.conn = service.getApiConn();
    }


    // Call<Paged<Contact>> getContacts(@Query("limit") int limit, @Query("status") ContactStatus status);
    // Call<Paged<Contact>> getContacts(@Query("limit") int limit, @Query("modified_since") QueryDate date, @Query("status") ContactStatus status);
    // Call<Contact> getContact(@Path("contactId") String contactId);
    //
    // Call<Contact> createContact(@Body Contact contact, @Query("action_by") OptInSource optInSource);
    // Call<Contact> updateContact(@Body Contact contact, @Path("contactId") String contactId, @Query("action_by") OptInSource optInSource);
    // Call<Response<Void>> unsubscribeContact(@Path("contactId") String contactId);
    //
    // Call<ContactList> updateContactList(@Body ContactList contactList, @Path("listId") String listId);
    //
    // Call<SignupFormResponse> createCustomSignupForm(@Body SignupFormRequest signupFormRequest);



    public ContactList getContactList(String listId)
    {
        ContactList list = new ContactList();

        try
        {
            com.constantcontact.v2.ContactService contactService = conn.getContactService();
            list = contactService.getContactList(listId).execute().body();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        // give back empty list if none found
        return list;
    }

    public ContactList getContactListByName(String name)
    {
        List<ContactList> lists = new ArrayList<>();
        Optional<ContactList> result = null;

        try
        {
            com.constantcontact.v2.ContactService contactService = conn.getContactService();

            // fetching the lists gets ALL your lists at once
            // no need to loop thru, oversight in their API design
            lists = contactService.getContactLists(null).execute().body();

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
            com.constantcontact.v2.ContactService contactService = conn.getContactService();

            // fetching the lists gets ALL your lists at once
            // no need to loop thru, oversight in their API design
            lists = contactService.getContactLists(null).execute().body();
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
            com.constantcontact.v2.ContactService contactService = conn.getContactService();

            Call<Response<Void>> call = contactService.deleteContactList(listId);
            response = call.execute();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return response;
    }

    public Response<Contact> createContactByOwner(Contact contact)
    {
        return createContact(contact, OptInSource.ACTION_BY_OWNER);
    }

    public Response<Contact> createContactByVisitor(Contact contact)
    {
        return createContact(contact, OptInSource.ACTION_BY_VISITOR);
    }


    public Response<Contact> createContact(Contact contact, OptInSource source)
    {
        if (source == null)
        {
            source = OptInSource.ACTION_BY_OWNER;
        }

        Response<Contact> response = null;
        try
        {
            com.constantcontact.v2.ContactService contactService = conn.getContactService();
            Call<Contact> call = contactService.createContact(contact, source);
            response = call.execute();
            // System.out.println(response);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return response;
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
            com.constantcontact.v2.ContactService contactService = conn.getContactService();
            Call<ContactList> call = contactService.createContactList(list);
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


    //TODO: get contacts by list name instead of id

    /**
     * Get the email contacts for the list
     *
     * @param listId        String id of the list
     * @param limit         how many to return
     * @param dateCreated   date the list was created
     * @return              a list of email contacts
     */
    public List<Contact> getContactsByList(String listId, int limit, String dateCreated)
    {
        List<Contact> contacts = new ArrayList<>();

        try
        {
            com.constantcontact.v2.ContactService contactService = conn.getContactService();
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
            Paged<Contact> pagedContacts = contactService.getContacts(listId,limit, new QueryDate(dated)).execute().body();

            if (pagedContacts.getResults().size() > 0)
            {
                contacts.addAll(pagedContacts.getResults());
            }

            // keep fetching another batch until there are no more
            while (pagedContacts.getNextLink() != null)
            {
                // System.out.println(pagedContacts.getNextLink());
                Thread.sleep(millisToSleepBetweenRequests);
                pagedContacts = contactService.getContacts(pagedContacts.getNextLink()).execute().body();
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
            com.constantcontact.v2.ContactService contactService = conn.getContactService();
            Paged<Contact> pagedContacts = contactService.getContactsByEmail(email).execute().body();
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
