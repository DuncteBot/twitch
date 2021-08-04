package com.dunctebot.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;

public class Main {
    private Main() {
        final OAuth2Credential chatCredential = new OAuth2Credential("twitch", "oAuthTokenHere");

        final TwitchClient twitchClient = TwitchClientBuilder.builder()
            .withDefaultAuthToken(chatCredential)
            .withClientId("client id")
            .withEnableTMI(true)
            .withEnableChat(true)
            .withChatAccount(chatCredential)
            .build();
    }

    public static void main(String[] args) {
        //
    }
}
