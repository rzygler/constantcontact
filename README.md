# Constant Contact
Java wrapper for Constant Contact API v2

Update config.properties with your key and token from Constant Contact developer portal and run the ApiMain code


**Fetch the info on your Constant Contact account**
```java
ApiV2 api = new ApiV2(apiKey, apiToken);
AccountSummaryInformation summary = api.getAccountSummary();
```

**Fetch all your existing contact lists**
```java
ApiV2 api = new ApiV2(apiKey, apiToken);
List<ContactList> lists = api.getContactLists();
```

**Fetch all the contacts for list 1**
```java
ApiV2 api = new ApiV2(apiKey, apiToken);
List<Contact> contacts = api.getContactsByList("1", this.fetchLimit, this.dateCreated);
```
