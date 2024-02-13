package com.example.mongodb.oci.connection.configuration;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.secrets.SecretsClient;
import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;
import com.oracle.bmc.secrets.requests.GetSecretBundleRequest;
import com.oracle.bmc.secrets.responses.GetSecretBundleResponse;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

@AutoConfigureBefore({MongoDataAutoConfiguration.class, MongoAutoConfiguration.class})
@Configuration
public class MongoConnectionConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MongoConnectionConfiguration.class);

    private final ConfigFileReader.ConfigFile secretsConfig;

    public MongoConnectionConfiguration() throws IOException {
        this.secretsConfig = ConfigFileReader.parse(".oci/secrets-config");
    }

    @Bean
    @Primary
    public MongoProperties mongoProperties() {
        var mongoProperties = new MongoProperties();
        mongoProperties.setUri(getMongoDBConnectionUri());
        return mongoProperties;
    }

    private String getMongoDBConnectionUri() {
        log.info("Started fetching mongodb connection uri vault secret");
        return getSecretFromVault(secretsConfig.get("region"),
                secretsConfig.get("mongo_db_secret"), authenticationDetailsProvider());
    }

    public AuthenticationDetailsProvider authenticationDetailsProvider() {
        return new ConfigFileAuthenticationDetailsProvider(secretsConfig);
    }

    public static String getSecretFromVault(String region, String secretOcid, AuthenticationDetailsProvider provider) {
        log.info("Fetching the vault secret from the region : {}", region);
        GetSecretBundleResponse getSecretBundleResponse;
        try (SecretsClient secretsClient = SecretsClient.builder().build(provider)) {
            secretsClient.setRegion(region);
            GetSecretBundleRequest getSecretBundleRequest = GetSecretBundleRequest
                    .builder()
                    .secretId(secretOcid)
                    .build();
            getSecretBundleResponse = secretsClient.getSecretBundle(getSecretBundleRequest);
        }
        log.debug("Started decoding the base64 secret bundle");
        Base64SecretBundleContentDetails base64SecretBundleContentDetails =
                (Base64SecretBundleContentDetails) getSecretBundleResponse.getSecretBundle().getSecretBundleContent();
        byte[] secretValueDecoded = Base64.decodeBase64(base64SecretBundleContentDetails.getContent());
        log.debug("Finished decoding the base64 secret bundle");
        return new String(secretValueDecoded);
    }
}
