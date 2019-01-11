import com.boringguys.constantcontact.v2.AccountService;
import com.boringguys.constantcontact.v2.ApiV2;
import com.constantcontact.v2.account.AccountAddress;
import com.constantcontact.v2.account.AccountSummaryInformation;
import com.constantcontact.v2.contacts.Contact;
import com.constantcontact.v2.contacts.ContactList;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class ApiMain
{
    private String apiKey;
    private String apiToken;
    private String dateCreated;
    private int fetchLimit;
    private int millisToSleepBetweenRequests = 2000;

    public static void main(String[] args)
    {
        ApiMain main = new ApiMain();
    }


    ApiMain()
    {
        getApiConfig();

        // Get the info on your Constant Contact account and print it
        AccountService accountService = new AccountService(apiKey, apiToken);
        AccountSummaryInformation summary = accountService.getAccountSummary();
        printAccountSummaryInformation(summary);

        // TODO make CCApiV2 a single instance to re-use
        // TODO split out contact service
        // TODO split out campaign service

        ApiV2 api = new ApiV2(apiKey, apiToken);

        // Get all your existing contact lists and print them
        List<ContactList> lists = api.getContactLists();
        printContactLists(lists);

        // Get all the contacts from your first list (0) and print them
        if (lists.size() > 0)
        {
            ContactList list = lists.get(0);
            System.out.println("Fetching list: " + list.getName() + " " + list.getContactCount());
            System.out.println("--------------------------------");
            List<Contact> contacts = api.getContactsByList(list.getId(), this.fetchLimit, this.dateCreated);
            contacts.forEach(a -> printContact(a));
        }


    }

    /**
     *
     * @param contact
     */
    private void printContact(Contact contact)
    {
        System.out.println(contact.getEmailAddresses()[0].getEmailAddress() + "," +
                contact.getFirstName() + ","
                + contact.getLastName() );
    }



    /**
     *
     * @param lists
     */
    private void printContactLists(List<ContactList> lists)
    {
        if (lists == null || lists.size() == 0)
        {
            System.out.println("Can't find any Contact Lists -- check your API key and Token in properties file");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Found: " + lists.size() + " lists");
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

    /**
     *
     * @param summary
     */
    private void printAccountSummaryInformation(AccountSummaryInformation summary)
    {
        if (summary == null)
        {
            System.out.println("Can't get Account Summary -- check your API key and Token in properties file");
            return;
        }
        StringBuilder sb = new StringBuilder();

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

    /**
     * sets config values for API key and token
     */
    private void getApiConfig()
    {
        Properties prop = new Properties();
        InputStream input = null;
        try {

            input = new FileInputStream("config.properties");
            prop.load(input);
            this.apiKey = prop.getProperty("api_key");
            this.apiToken = prop.getProperty("api_token");
            this.dateCreated = prop.getProperty("date_created");
            this.fetchLimit = Integer.parseInt(prop.getProperty("fetch_limit"));

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
    }
}
