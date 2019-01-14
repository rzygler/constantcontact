package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.CCApi2;
import com.constantcontact.v2.Paged;
import com.constantcontact.v2.QueryDate;
import com.constantcontact.v2.contacts.Contact;
import com.constantcontact.v2.contacts.ContactList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ContactService
{
    private ApiV2 service;
    private CCApi2 conn;
    int millisToSleepBetweenRequests = 4000;

    /**
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
    // //   Call<Paged<Contact>> getContacts(@Path("listId") String listId, @Query("limit") int limit, @Query("modified_since") QueryDate date);
    // Call<Contact> getContact(@Path("contactId") String contactId);
    //
    // Call<Contact> createContact(@Body Contact contact, @Query("action_by") OptInSource optInSource);
    // Call<Contact> updateContact(@Body Contact contact, @Path("contactId") String contactId, @Query("action_by") OptInSource optInSource);
    // Call<Response<Void>> unsubscribeContact(@Path("contactId") String contactId);
    //
    // Call<ContactList> createContactList(@Body ContactList contactList);
    // Call<ContactList> getContactList(@Path("listId") String listId);
    // Call<ContactList> updateContactList(@Body ContactList contactList, @Path("listId") String listId);
    // Call<Response<Void>> deleteContactList(@Path("listId") String listId);
    //
    // Call<SignupFormResponse> createCustomSignupForm(@Body SignupFormRequest signupFormRequest);

    /**
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
