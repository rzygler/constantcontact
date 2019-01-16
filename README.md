# Constant Contact
Java wrapper for Constant Contact API v2. Sparse, but working.

## Build instructions
* The API v2 from Constant Contact uses Gradle for build so you will need to use that as well to build this code
* Update config.properties with your key and token from Constant Contact developer portal
* Enjoy!

## Help with the rest
* The Java API v2 from Constant Contact uses [Retrofit](https://square.github.io/retrofit/) as its framework so you will need to read up on how to handle the synchronous/async methods of Retrofit.  More help [here](https://futurestud.io/tutorials/retrofit-synchronous-and-asynchronous-requests) and [here](https://square.github.io/retrofit/2.x/retrofit/retrofit2/Call.html)

## Some code examples

**Fetch the info on your Constant Contact account**
```java
AccountService accountService = new AccountService(apiKey, apiToken);
AccountSummaryInformation summary = accountService.getAccountSummary();
```

**Fetch all your existing contact lists**
```java
ContactService contactService = new ContactService(apiKey, apiToken);
List<ContactList> lists = contactService.getContactLists();
```

**Fetch all the contacts for list with id of 1**
```java
ContactService contactService = new ContactService(apiKey, apiToken);
List<Contact> contacts = contactService.getContactsByList("1", this.fetchLimit, this.dateCreated);
```
**Fetch a contact**
```java
ContactService contactService = new ContactService(apiKey, apiToken);
List<Contact> contacts = contactService.getContactsByEmail("homer.simpson@gmail.com");
contacts.forEach(a -> System.out.println(contact.getEmailAddresses()[0].getEmailAddress() + "," +
                contact.getFirstName() + ","
                + contact.getLastName() ) );
```
**Fetch all draft campaigns**
```java
CampaignService campaignService = new CampaignService(apiKey, apiToken);
List<Campaign> campaigns = campaignService.getDraftCampaigns();
campaigns.forEach(a -> System.out.println(a.getName()));
```

**Fetch all sent campaigns**
```java
CampaignService campaignService = new CampaignService(apiKey, apiToken);
List<Campaign> campaigns = campaignService.getSentCampaigns();
campaigns.forEach(a -> System.out.println(a.getName()));
```

**Fetch a campaign**
```java
CampaignService campaignService = new CampaignService(apiKey, apiToken);
Campaign campaign = campaignService.getCampaign("campaign id");
System.out.println(campaign.getId() + "," +
                campaign.getName() + "," +
                campaign.getSubject() + "," +
                campaign.getCreatedDate())
```

More examples in [console example](https://github.com/rzygler/constantcontact/blob/master/src/main/java/ApiMain.java)

