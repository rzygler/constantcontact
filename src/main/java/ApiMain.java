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

        getApiConfig();

        // Get Draft Campaigns (likewise, All, Deleted, Draft, Deleted, Running, Scheduled, Sent
        System.out.println("Fetching Draft Campaigns");
        System.out.println("--------------------------------");
        CampaignService campaignService = new CampaignService(apiKey, apiToken);
        List<Campaign> campaigns = campaignService.getDraftCampaigns();
        campaigns.forEach(a -> System.out.println(a.getName()));


        System.out.println("Fetching Sent Campaigns since 1/1/2019");
        System.out.println("--------------------------------");
        CampaignService campaignService2 = new CampaignService(apiKey, apiToken);
        List<Campaign> campaigns2 = campaignService2.getSentCampaigns("2019/01/01");
        campaigns2.forEach(a -> printCampaign(a));

        // Get the details on a campaign
        System.out.println("Fetching campaign");
        System.out.println("--------------------------------");
        Campaign campaign = campaignService2.getCampaign(campaigns2.get(0).getId());
        printCampaign(campaign);

        System.out.println("Fetching campaign stats...");
        System.out.println("--------------------------------");
        CampaignTrackingService tracking = new CampaignTrackingService(apiKey, apiToken);
        TrackingSummary summary = tracking.getTrackingSummary(campaigns2.get(0).getId());
        printCampaignTrackingSummary(summary);

        System.out.println("Creating contact list");
        System.out.println("--------------------------------");
        ContactService contactServiceForCreate = new ContactService(apiKey, apiToken);
        contactServiceForCreate.createContactList();


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
