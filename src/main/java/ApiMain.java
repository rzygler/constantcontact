import com.boringguys.constantcontact.v2.ApiV2;
import com.constantcontact.v2.account.AccountAddress;
import com.constantcontact.v2.account.AccountSummaryInformation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        ApiV2 api = new ApiV2(apiKey, apiToken);

        // get an API connection and pass it in
        AccountSummaryInformation summary = api.getAccountSummary(api.getApiConn());
        printAccountSummaryInformation(summary);

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
