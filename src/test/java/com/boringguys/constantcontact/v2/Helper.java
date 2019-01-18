package com.boringguys.constantcontact.v2;

import com.constantcontact.v2.account.AccountAddress;
import com.constantcontact.v2.account.AccountSummaryInformation;
import com.constantcontact.v2.contacts.Contact;
import com.constantcontact.v2.contacts.ContactList;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Helper
{
    /**
     * Get the API config from config.properties file (key and token)
     *
     * @return              Map of key value pairs for API config
     * @throws Exception
     */
    public static Map<String,String> getApiConfig() throws Exception
    {
        Map<String, String> configs = new HashMap<>();
        Properties prop = new Properties();
        InputStream input = null;
        try {

            input = new FileInputStream("config.properties");
            prop.load(input);

            if (prop.getProperty("api_key").equals("your_key_here"))
            {
                throw new Exception("Please update your API key in config.properties");
            }
            configs.put("apiKey", prop.getProperty("api_key"));

            if (prop.getProperty("api_token").equals("your_token_here"))
            {
                throw new Exception("Please update your API token in config.properties");
            }
            configs.put("apiToken", prop.getProperty("api_token"));
            configs.put("dateCreated", prop.getProperty("date_created"));

            configs.put("showDebug", "false");
            if (prop.getProperty("show_debug").equals("true"))
            {
                configs.put("showDebug", "true");
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return configs;
    }

    public static void printAccountSummaryInformation(AccountSummaryInformation summary)
    {
        if (summary == null)
        {
            System.out.println("Can't get Account Summary -- check your API key and Token in properties file");
            return;
        }
        StringBuilder sb = new StringBuilder();

        sb.append("Fetching account info");
        sb.append(System.getProperty("line.separator"));
        sb.append("--------------------------------");
        sb.append(System.getProperty("line.separator"));
        sb.append("Organization Name: " + summary.getOrganizationName());
        sb.append(System.getProperty("line.separator"));
        sb.append("Org Contact Email: " + summary.getEmail());
        sb.append(System.getProperty("line.separator"));
        sb.append("Org Contact Name: " + summary.getFirstName() + " " + summary.getLastName());
        sb.append(System.getProperty("line.separator"));
        sb.append("Org Contact Phone: " + summary.getPhone());
        sb.append(System.getProperty("line.separator"));
        AccountAddress address = summary.getOrganizationAddresses()[0];
        sb.append("Org Address: " + address.getLine1());
        sb.append(System.getProperty("line.separator"));
        if (address.getLine2() != null)
        {
            sb.append(address.getLine2());
            sb.append(System.getProperty("line.separator"));
        }

        sb.append("             " + address.getCity() + ",");
        sb.append(address.getState()+ " ");
        sb.append(address.getPostalCode());
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));

        System.out.println(sb.toString());
    }

    public static void printContactLists(List<ContactList> lists)
    {
        if (lists == null || lists.size() == 0)
        {
            System.out.println("Can't find any Contact Lists -- check your API key and Token in properties file");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Found: " + lists.size() + " lists");
        sb.append(System.getProperty("line.separator"));
        sb.append("--------------------------------");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));

        for(ContactList list:lists)
        {
            sb.append("List name:    " + list.getName());
            sb.append(System.getProperty("line.separator"));
            sb.append("List size:    " + list.getContactCount());
            sb.append(System.getProperty("line.separator"));
            sb.append("List id:      " + list.getId());
            sb.append(System.getProperty("line.separator"));
            sb.append("List created: " + list.getCreatedDate());
            sb.append(System.getProperty("line.separator"));
            sb.append("List status:  " + list.getStatus());
            sb.append(System.getProperty("line.separator"));
            sb.append("--------------------------------");
            sb.append(System.getProperty("line.separator"));
        }

        System.out.println(sb.toString());
    }

    public static void printContact(Contact contact)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("line.separator"));
        sb.append(contact.getEmailAddresses()[0].getEmailAddress() + "," +
                contact.getFirstName() + ","
                + contact.getLastName() );
        System.out.println(sb.toString());
    }
}
