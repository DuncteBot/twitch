/*
 *     A twitch bot for personal use
 *     Copyright (C) 2021  Duncan "duncte123" Sterken
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.dunctebot.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;

public class Main {
    private final TwitchClient client;

    private Main() {
        final OAuth2Credential chatCredential = new OAuth2Credential("twitch", System.getenv("OAUTH_TOKEN"));

        this.client = TwitchClientBuilder.builder()
            .withDefaultAuthToken(chatCredential)
            .withClientId(System.getenv("CLIENT_ID"))
            .withEnableTMI(true)
            .withEnableChat(true)
            .withChatAccount(chatCredential)
            .build();

        final EventHandler eventHandler = new EventHandler();
        this.client.getEventManager()
            .getEventHandler(SimpleEventHandler.class)
            .registerListener(eventHandler);

        // we need to join all channels
        this.client.getChat().joinChannel("duncte123");
    }

    public static void main(String[] args) {
        new Main();
    }

    public TwitchClient getClient() {
        return this.client;
    }
}
