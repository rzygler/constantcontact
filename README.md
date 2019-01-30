# Constant Contact
This is a Java wrapper for Constant Contact API v2. The existing CC api (version 2) is pretty good, but I wrapped it here to add some convenience methods.  

Highlights (full examples further down page)

```java
// Add a contact quickly
ServiceContact service = new ServiceContact(apiKey, apiToken);
Response<Contact> response = service.createContact("homer.simpson@gmail.com", "Homer", "Simpson", "listId");
// Contact is saved and embedded in response.body
Contact savedContact = response.body();
/////

// Report on the "SENT" campaigns you can just do:
List<Campaign> campaigns = service.getSentCampaigns();
///// 

// Fetch a contact list by its name
ContactList list = service.getContactListByName(String name)
/////

```

## Build instructions
* The API v2 from Constant Contact uses Gradle for build so you will need to use that as well to build this code
* Update config.properties with your key and token from Constant Contact developer portal to run tests
* Enjoy!

## Help with the rest
* The Java API v2 from Constant Contact uses [Retrofit](https://square.github.io/retrofit/) as its framework so you will need to read up on how to handle the synchronous/async methods of Retrofit.  More help [here](https://futurestud.io/tutorials/retrofit-synchronous-and-asynchronous-requests) and [here](https://square.github.io/retrofit/2.x/retrofit/retrofit2/Call.html)

## Code examples


```java
// Fetch the info on your Constant Contact account
ServiceAccount service = new ServiceAccount(apiKey, apiToken);
AccountSummaryInformation summary = service.getAccountSummary();
//////

// Fetch all your existing contact lists
ServiceContact service = new ServiceContact(apiKey, apiToken);
List<ContactList> lists = service.getContactLists();
//////

// Fetch contact list by name
ServiceContact service = new ServiceContact(apiKey, apiToken);
ContactList list = service.getContactListByName(name);
//////

// Fetch all the contacts for list with id of 1
ServiceContact service = new ServiceContact(apiKey, apiToken);
List<Contact> contacts = service.getContactsByList("1", this.fetchLimit, this.dateCreated);
//////

// Create a contact list
ServiceContact service = new ServiceContact(apiKey, apiToken);
String listName = "Hello World List";
ContactListStatus status = ContactListStatus.ACTIVE;
Response<ContactList> response = service.createContactList(listName, status)
    // Contact list is created and embedded in response.body
    // OR errors will be in response.code, response,message  
ContactList list = response.body();
String newName = list.getName();  // should be equal to listName
String id = list.getId();  
//////

// Fetch a contact
ServiceContact service = new ServiceContact(apiKey, apiToken);
List<Contact> contacts = service.getContactsByEmail("homer.simpson@gmail.com");
contacts.forEach(a -> System.out.println(contact.getEmailAddresses()[0].getEmailAddress() + "," +
                contact.getFirstName() + ","
                + contact.getLastName() ) );
 ////// 
  
// Create a contact quickly, with just email, first, last, and list
ServiceContact service = new ServiceContact(apiKey, apiToken);
Response<Contact> response = service.createContact("homer.simpson@gmail.com", "Homer", "Simpson", "listId");
// Contact is saved and embedded in response.body
Contact savedContact = response.body();
/////

// Create a contact the long way
Contact contact = new Contact();
EmailAddress address = new EmailAddress();
address.setEmailAddress("homer.simpson@gmail.com");
// add the email address to the array
contact.setEmailAddresses(new EmailAddress[]{ address });
// add the contact to one or more lists
ContactListMetaData contactListMetaData = new ContactListMetaData();
contactListMetaData.setId(listId); // get listId from an existing Contact list
// add the contact list to the array
contact.setContactLists(new ContactListMetaData[]{ contactListMetaData });
// save the contact
ServiceContact service = new ServiceContact(apiKey, apiToken);
Response<Contact> response = service.createContactByOwner(contact)
// Contact is saved and embedded in response.body
Contact savedContact = response.body();
String contactId = savedContact.getId();
//////

// Fetch all draft campaigns
ServiceCampaign service = new ServiceCampaign(apiKey, apiToken);
List<Campaign> campaigns = service.getDraftCampaigns();
campaigns.forEach(a -> System.out.println(a.getName()));
//////

// Fetch all sent campaigns
ServiceCampaign service = new ServiceCampaign(apiKey, apiToken);
List<Campaign> campaigns = service.getSentCampaigns();
campaigns.forEach(a -> System.out.println(a.getName()));
//////

// Fetch sent campaigns since 1/1/2019
ServiceCampaign service = new ServiceCampaign(apiKey, apiToken);
List<Campaign> campaigns = service.getSentCampaigns("2019/01/01 00:00:01");
campaigns.forEach(a -> System.out.println(a.getName()));
//////

// Fetch a campaign
ServiceCampaign service = new ServiceCampaign(apiKey, apiToken);
Campaign campaign = service.getCampaign("campaign id");
System.out.println(campaign.getId() + "," +
                campaign.getName() + "," +
                campaign.getSubject() + "," +
                campaign.getCreatedDate())
//////
```


More examples in tests...
