/*
 *     A twitch bot for personal use
 *     Copyright (C) 2021  Duncan duncte123 Sterken
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation either version 3 of the License or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not see <https://www.gnu.org/licenses/>.
 */

package com.dunctebot.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.helix.domain.UserList;
import org.slf4j.LoggerFactory;

import java.util.List;


public class Main {
    public static final String BOT_USER_LOGIN = "dunctebot";

    private final TwitchClient client;
    private final OAuth2Credential credential;

    private Main() {
        // scopes: chat:read chat:edit channel:moderate whispers:read whispers:edit channel_editor
        // token gen: https://twitchapps.com/tokengen/
        
        this.credential = new OAuth2Credential("twitch", getOauthToken());

        this.client = TwitchClientBuilder.builder()
            .withDefaultAuthToken(this.credential)
            .withClientId(System.getenv("CLIENT_ID"))
            // .withClientSecret(System.getenv("CLIENT_SECRET"))
            .withEnableTMI(true)
            .withEnableChat(true)
            .withEnableHelix(true)
            .withEnablePubSub(true)
            .withChatAccount(this.credential)
            .build();

        final UserList selfRequest = this.client.getHelix()
            .getUsers(null, null, List.of(BOT_USER_LOGIN))
            .execute();
        final String selfId = selfRequest.getUsers().get(0).getId();

        final EventHandler eventHandler = new EventHandler(this, selfId);
        this.client.getEventManager()
            .getEventHandler(SimpleEventHandler.class)
            .registerListener(eventHandler);

        // we need to join all channels
        final String[] channels = System.getenv("CHANNELS").split(",");
        final TwitchChat chat = this.client.getChat();

        for (final String channel : channels) {
            chat.joinChannel(channel);
        }
    }

    public static void main(String[] args) {
        if (args.length > 0 && "--export-commands".equals(args[0])) {
            new CommandExporter();
            return;
        }

        LoggerFactory.getLogger(Main.class).info("Booting bot");

        new Main();
    }

    public TwitchClient getClient() {
        return this.client;
    }

    public OAuth2Credential getCredential() {
        return credential;
    }

    public static String getOauthToken() {
        return System.getenv("OAUTH_TOKEN").replace("oauth:", "");
    }
}
