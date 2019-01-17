import com.boringguys.constantcontact.v2.*;
import com.constantcontact.v2.account.AccountAddress;
import com.constantcontact.v2.account.AccountSummaryInformation;
import com.constantcontact.v2.campaigns.Campaign;
import com.constantcontact.v2.campaigns.CampaignStatus;
import com.constantcontact.v2.contacts.Contact;
import com.constantcontact.v2.contacts.ContactList;
import com.constantcontact.v2.tracking.TrackingSummary;

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

    public static void main(String[] args) throws Exception
    {
        ApiMain main = new ApiMain();
    }


    ApiMain() throws Exception
    {
        // TODO returning nulls from api is ugly

        getApiConfig();

        // Get the info on your Constant Contact account and print it
        System.out.println("Fetching account info");
        System.out.println("--------------------------------");
        AccountService accountService = new AccountService(apiKey, apiToken);
        AccountSummaryInformation summaryInfo = accountService.getAccountSummary();
        printAccountSummaryInformation(summaryInfo);


        ContactService contactService = new ContactService(apiKey, apiToken);

        // Get all your existing contact lists and print them
        System.out.println("Fetching contact lists");
        System.out.println("--------------------------------");
        List<ContactList> lists = contactService.getContactLists();
        printContactLists(lists);

        // Get all the contacts from your first list (0) and print them
        if (lists.size() > 0)
        {
            ContactList list = lists.get(0);
            System.out.println("Fetching list: " + list.getName() + " " + list.getContactCount());
            System.out.println("--------------------------------");
            List<Contact> contacts = contactService.getContactsByList(list.getId(), this.fetchLimit, this.dateCreated);
            contacts.forEach(a -> printContact(a));
        }


        // get Homer contact
        System.out.println("Fetching Homer: ");
        System.out.println("--------------------------------");
        List<Contact> contacts = contactService.getContactsByEmail("homer.simpson@gmail.com");
        contacts.forEach(a -> printContact(a));

        // Get Draft Campaigns (likewise, All, Deleted, Draft, Deleted, Running, Scheduled, Sent
        System.out.println("Fetching Draft Campaigns");
        System.out.println("--------------------------------");
        CampaignService campaignService = new CampaignService(apiKey, apiToken);
        List<Campaign> campaigns = campaignService.getDraftCampaigns();
        campaigns.forEach(a -> System.out.println(a.getName()));


        System.out.println("Fetching Sent Campaigns since 1/1/2019");
        System.out.println("--------------------------------");
        CampaignService campaignService2 = new CampaignService(apiKey, apiToken);
        List<Campaign> campaigns2 = campaignService2.getCampaigns(
                50, "2019/01/01 00:00:01", CampaignStatus.SENT);
        campaigns2.forEach(a -> printCampaign(a));

        // Get the details on a campaign
        System.out.println("Fetching campaign");
        System.out.println("--------------------------------");
        Campaign campaign = campaignService.getCampaign(campaigns2.get(0).getId());
        printCampaign(campaign);

        System.out.println("Fetching campaign stats...");
        System.out.println("--------------------------------");
        CampaignTrackingService tracking = new CampaignTrackingService(apiKey, apiToken);
        TrackingSummary summary = tracking.getTrackingSummary(campaigns2.get(0).getId());



    }

    private void printCampaignTrackingSummary(TrackingSummary summary)
    {
        System.out.println("Sends: " + summary.getSends());
        System.out.println("Opens: " + summary.getOpens());
        System.out.println("Clicks: " + summary.getClicks());
        System.out.println("Bounces: " + summary.getBounces());
        System.out.println("Forwards: " + summary.getForwards());
        System.out.println("Spams: " + summary.getSpamCount());
        System.out.println("Unsubs: " + summary.getUnsubscribes());

    }

    private void printCampaign(Campaign campaign)
    {
        System.out.println(campaign.getId() + "," +
                campaign.getName() + "," +
                campaign.getSubject() + "," +
                campaign.getPermalinkUrl() + "," +
                campaign.getCreatedDate());
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
    private void getApiConfig() throws Exception
    {
        Properties prop = new Properties();
        InputStream input = null;
        try {

            input = new FileInputStream("config.properties");
            prop.load(input);

            if (prop.getProperty("api_key").equals("your_key_here"))
            {
                throw new Exception("Please update your API key in config.properties");
            }
            this.apiKey = prop.getProperty("api_key");

            if (prop.getProperty("api_token").equals("your_token_here"))
            {
                throw new Exception("Please update your API token in config.properties");
            }
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
