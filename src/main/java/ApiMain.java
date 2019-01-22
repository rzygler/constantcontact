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


        System.out.println("Creating contact list");
        System.out.println("--------------------------------");
        ContactService contactServiceForCreate = new ContactService(apiKey, apiToken);
        contactServiceForCreate.createContactList();


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
