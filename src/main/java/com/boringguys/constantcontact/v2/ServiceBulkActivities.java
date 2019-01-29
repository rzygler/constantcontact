package com.boringguys.constantcontact.v2;

import com.boringguys.constantcontact.v2.exceptions.PayloadTooLargeException;
import com.boringguys.constantcontact.v2.exceptions.TooManyContactsException;
import com.constantcontact.v2.CCApi2;
import com.constantcontact.v2.BulkActivitiesService;
import com.constantcontact.v2.bulkactivities.Activity;
import com.constantcontact.v2.bulkactivities.AddContacts;
import com.constantcontact.v2.bulkactivities.ImportData;
import com.constantcontact.v2.contacts.Contact;
import com.constantcontact.v2.contacts.EmailAddress;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

public class ServiceBulkActivities
{
    private ApiV2 service;
    private CCApi2 conn;
    private String[] columns = {
            "EMAIL",            // Required
            "FIRST NAME",       // Required
            "LAST NAME",        // Required
            "BIRTHDAY_DAY",     // 1 to 31
            "BIRTHDAY_MONTH",   // 1 to 12
            "ANNIVERSARY",      // Accepts the following formats MM/DD/YYYY, M/D/YYYY, YYYY/MM/DD, YYYY/M/D, YYYY-MM-DD, YYYY-M-D,M-D-YYYY, M-DD-YYYY.
                                // The year must be greater than 1900 and cannot be more than 10 years in the future
                                //(with respect to the current year).
            "JOB TITLE",        // DONE
            "COMPANY NAME",
            "WORK PHONE",       // DONE
            "HOME PHONE",       // DONE
            "ADDRESS LINE 1",
            "ADDRESS LINE 2",
            "CITY",
            "STATE",
            "COUNTRY",
            "ZIP CODE",
            "POSTAL CODE",
            "CUSTOM FIELD 1",
            "CUSTOM FIELD 2",
            "CUSTOM FIELD 3",
            "CUSTOM FIELD 4",
            "CUSTOM FIELD 5",
            "CUSTOM FIELD 6",
            "CUSTOM FIELD 7",
            "CUSTOM FIELD 8",
            "CUSTOM FIELD 9",
            "CUSTOM FIELD 10",
            "CUSTOM FIELD 11",
            "CUSTOM FIELD 12",
            "CUSTOM FIELD 13",
            "CUSTOM FIELD 14",
            "CUSTOM FIELD 15"
    };

    /**
     * Constructor for Account Service
     *
     * @param apiKey    Constant Contact developer api key
     * @param apiToken  Constant Contact developer token
     */
    public ServiceBulkActivities(String apiKey, String apiToken)
    {
        this.service = new ApiV2(apiKey, apiToken);
        this.conn = service.getApiConn();
    }


    /**
     * Adds contacts via the bulk activity service
     * The import data object is more loosely typed
     * however, we shouldn't really allow anyone to go willy-nilly adding
     * these untyped pieces of data to it, otherwise that will incur errors
     *
     * @param contacts
     * @param contactLists
     * @param columns
     * @throws TooManyContactsException
     * @throws PayloadTooLargeException
     * @throws IOException
     */
    public Response<Activity> addContacts(List<Contact> contacts, String[] contactLists, String[] columns)
            throws TooManyContactsException,PayloadTooLargeException,IOException
    {
        int numOfBytes = 0;

        BulkActivitiesService service = conn.getBulkActivitiesService();

        AddContacts contactsToAdd = new AddContacts();
        contactsToAdd.setColumnNames(columns);
        contactsToAdd.setLists(contactLists);

        ImportData[] importData = new ImportData[contacts.size()];

        if (contacts.size() > 40000)
        {
            throw new TooManyContactsException("Contacts imported cannot 40,000");
        }

        // loop thru contacts list and make an importData object
        for (int i = 0; i < contacts.size(); i++)
        {
            Contact contact = contacts.get(i);
            ImportData imp = new ImportData();

            imp.setFirstName(contact.getFirstName());
            numOfBytes += contact.getFirstName().getBytes().length;

            imp.setLastName(contact.getLastName());
            numOfBytes += contact.getLastName().getBytes().length;

            // have to convert EmailAddress[] to String[]

            int numOfEmails = contact.getEmailAddresses().length;
            EmailAddress[] address = contact.getEmailAddresses();

            String[] emails = new String[numOfEmails];

            for (int j = 0; j < numOfEmails; j++)
            {
                emails[j] = address[j].getEmailAddress();
                numOfBytes += address[j].getEmailAddress().getBytes().length;
            }

            imp.setEmailAddresses(emails);

            // non-required contact fields
            // TODO: finish adding non-required contact fields to bulk import
            if (contact.getWorkPhone() != null)
            {
                imp.setWorkPhone(contact.getWorkPhone());
            }

            if (contact.getHomePhone() != null)
            {
                imp.setHomePhone(contact.getHomePhone());
            }

            if (contact.getJobTitle() != null)
            {
                imp.setJobTitle(contact.getJobTitle());
            }

            importData[i] = imp;
        }

        // System.out.println("bytes: " + numOfBytes);
        if (numOfBytes > 4000000)
        {
            throw new PayloadTooLargeException("Contacts import cannot exceed 4mb of data");
        }

        contactsToAdd.setImportData(importData);

        Response<Activity> response = null;

        try
        {
            Call<Activity> activity = service.addContacts(contactsToAdd);
            response = activity.execute();
        } catch (IOException e)
        {
            throw new IOException(e.getMessage());
        }

        return response;
    }

}
