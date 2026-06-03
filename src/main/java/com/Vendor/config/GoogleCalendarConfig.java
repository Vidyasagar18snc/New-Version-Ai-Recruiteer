package com.Vendor.config;
 
import com.google.api.client.auth.oauth2.Credential;

import com.google.api.client.auth.oauth2.TokenResponseException;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

import com.google.api.client.http.javanet.NetHttpTransport;

import com.google.api.client.json.JsonFactory;

import com.google.api.client.json.gson.GsonFactory;

import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.calendar.Calendar;

import com.google.api.services.calendar.CalendarScopes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
 
import java.io.File;

import java.io.FileInputStream;

import java.io.InputStream;

import java.io.InputStreamReader;

import java.util.Collections;
 
@Configuration
@Slf4j
public class GoogleCalendarConfig {
 
    private static final String APPLICATION_NAME = "AI-Recruiter-Calendar";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final String TOKENS_DIRECTORY_PATH = "tokens";
 
    @Bean

    public Calendar getCalendarService() throws Exception {
 
        final NetHttpTransport HTTP_TRANSPORT =

                GoogleNetHttpTransport.newTrustedTransport();
 
        // Load credentials from Docker mounted volume

//        String credentialsPath = "/resources/secrets/credentials.json";

        String credentialsPath = System.getenv("GOOGLE_CREDENTIALS_PATH") != null
                ? System.getenv("GOOGLE_CREDENTIALS_PATH")
                : "./src/main/resources/secrets/credentials.json";

        log.info("CredentialsPath: {}", credentialsPath);
        File file = new File(credentialsPath);
        log.info("File Path: {}", file);
 
        if (!file.exists()) {

            throw new RuntimeException(

                    "credentials.json not found: " + credentialsPath);

        }
 
        InputStream in = new FileInputStream(file);
 
        GoogleClientSecrets clientSecrets =

                GoogleClientSecrets.load(

                        JSON_FACTORY,

                        new InputStreamReader(in));
 
        System.out.println(

                "CLIENT ID: " +

                        clientSecrets.getDetails().getClientId());
 
        GoogleAuthorizationCodeFlow flow =

                new GoogleAuthorizationCodeFlow.Builder(

                        HTTP_TRANSPORT,

                        JSON_FACTORY,

                        clientSecrets,

                        Collections.singleton(CalendarScopes.CALENDAR))

                        .setDataStoreFactory(

                                new FileDataStoreFactory(

                                        new File(TOKENS_DIRECTORY_PATH)))

                        .setAccessType("offline")

                        .build();
 
        LocalServerReceiver receiver =

                new LocalServerReceiver.Builder()

                        .setPort(8888)

                        .build();
 
        Credential credential;
 
        try {

            credential =

                    new AuthorizationCodeInstalledApp(

                            flow,

                            receiver)

                            .authorize("user");
 
        } catch (TokenResponseException e) {
 
            System.out.println(

                    "ERROR STATUS: " +

                            e.getStatusCode());
 
            System.out.println(

                    "ERROR DETAILS: " +

                            e.getDetails());
 
            throw e;

        }
 
        return new Calendar.Builder(

                HTTP_TRANSPORT,

                JSON_FACTORY,

                credential)

                .setApplicationName(APPLICATION_NAME)

                .build();

    }

}
 
