## Setting up the project

```
git clone https://github.com/nvseshaiah2013/mongodb-connection-example-with-oci.git
```

#### Pre-Requisites for setting up the project
    1. An Oracle Cloud account (Free Tier is fine).
    2. A new vault is created in the Oracle cloud.
    2. An MongoDB Atlas account.
    3. A free instance of mongodb is created on the mongodb atlas.

#### Steps to set up the project
    1. Import the project into any one of your favourite IDE.
    2. Make a new folder with the name .oci
    3. Create new file with the name secrets-config (Make sure the file does not have any file extension)
    4. Create a pair of public and private keys along with passphrase.
        a. Follow https://docs.oracle.com/en-us/iaas/Content/API/Concepts/apisigningkey.htm for detailed 
           steps to generate keys based on your operating system.
        b. Upload the public key generated from above step in your Oracle profile using the steps given in 
            https://docs.oracle.com/en-us/iaas/Content/API/Concepts/apisigningkey.htm
        c. Copy the configuration output you get once you upload the public key and copy it inside secrets-config file.
    5. Open Vault service from list of Oracle Cloud Services.
    6. Create a new Vault, Generate the master Encryption Key.
    7. Then, create a new secret with key as MONGO_DB_URI and its value as the mongo db uri from your atlas account.
    8. Copy the OCID of the above secret and paste it in the secrets-config file in new line with key as mongo_db_secret
       and the value as the OCID which we copied earlier.
    9. Now, run the ConnectionApplication
