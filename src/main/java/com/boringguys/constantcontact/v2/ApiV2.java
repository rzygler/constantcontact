package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.CCApi2;
import com.constantcontact.v2.ContactService;
import com.constantcontact.v2.Paged;
import com.constantcontact.v2.QueryDate;
import com.constantcontact.v2.account.AccountSummaryInformation;
import com.constantcontact.v2.contacts.Contact;
import com.constantcontact.v2.contacts.ContactList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApiV2
{
    private int millisToSleepBetweenRequests = 2000;
    private CCApi2 api;

    public CCApi2 getApiConn()
    {
        return api;
    }

    /**
     *
     * @param api
     */
    public void setApiConn(CCApi2 api)
    {
        this.api = api;
    }

    /**
     * Constructor
     * @param key
     * @param token
     */
    public ApiV2(String key, String token)
    {
        this.api = getApiService(key, token);
    }

    /**
     * Empty Constructor
     */
    public ApiV2()
    {

    }

    /**
     *
     * @param key   API key issued by Constant Contact
     * @param token API token issued by Constant Contact
     * @return      CCApi2 API service object
     */
    public static CCApi2 getApiService(String key, String token)
    {
        return new CCApi2(key, token);
    }



    /**
     *
     * @return
     */
    public List<ContactList> getContactLists()
    {
        List<ContactList> lists = new ArrayList<>();

        try
        {
            ContactService contactService = api.getContactService();

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


    public List<Contact> getContactByEmail(String email)
    {
        List<Contact> contacts = new ArrayList<>();

        try
        {
            ContactService contactService = api.getContactService();
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


    /**
     *
     * @param listId
     * @param limit
     * @param dateCreated
     * @return
     */
    public List<Contact> getContactsByList(String listId, int limit, String dateCreated)
    {
        List<Contact> contacts = new ArrayList<>();

        try
        {
            ContactService contactService = api.getContactService();
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
}

