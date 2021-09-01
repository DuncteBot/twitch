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

import com.dunctebot.twitch.moderation.PerspectiveApi;
import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(EventHandler.class);

    private final PerspectiveApi perspective = new PerspectiveApi();
    private final Set<String> modInChannels = new HashSet<>();
    private final Main main;
    private final CommandHandler commandHandler;

    public EventHandler(Main main) {
        this.main = main;
        this.commandHandler = new CommandHandler(main);
    }

    // we know the chat and can start listening for events
    @EventSubscriber
    public void onUserState(UserStateEvent event) {
        final EventChannel channel = event.getChannel();
        final IRCMessageEvent messageEvent = event.getMessageEvent();
        final String channelName = channel.getName();
        final String botName = event.getDisplayName().get();

        LOG.info("[USERSTATE][" + channelName + "][" + botName +"] " + messageEvent.getClientPermissions());

        if (this.modInChannels.contains(channelName) && !event.isModerator()) {
            this.modInChannels.remove(channelName);
        } else if (event.isModerator()) {
            this.modInChannels.add(channelName);
        }

        /*final TwitchHelix helix = this.main.getClient().getHelix();

        final UserList userList = helix.getUsers(null, null, List.of(channelName, botName)).execute();
        final List<User> users = userList.getUsers();

        System.out.println(users.get(0));
        System.out.println(users.get(1));
        System.out.println("========================");

        final String channelId = users.get(0).getId();
        final String botId = users.get(1).getId();

        this.main.getClient()
            .getPubSub()
            .listenForModerationEvents(this.main.getCredential(), botId, channelId);*/
    }

    @EventSubscriber
    public void onChannelMod(ChannelModEvent event) {
        if (event.isMod()) {
            this.modInChannels.add(event.getChannel().getName());
        } else {
            this.modInChannels.remove(event.getChannel().getName());
        }

        System.out.println(event);
    }

    @EventSubscriber
    public void onChannelMessage(ChannelMessageEvent event) {
        final String channelName = event.getChannel().getName();
        final String username = event.getUser().getName();

        // event.getTwitchChat().sendMessage(channelName, "/mods");

        if (
            /*!event.getPermissions().contains(CommandPermission.MODERATOR) &&*/
            this.modInChannels.contains(channelName)
        ) {
            // https://support.perspectiveapi.com/s/about-the-api-attributes-and-languages
            float score = this.perspective.getScore(event.getMessage(), "THREAT");
            System.out.println("Score: " + score);

            if (score >= 0.79f) {
                event.getTwitchChat().timeout(
                    channelName,
                    username,
                    Duration.ofHours(1),
                    "Perspective API score: " + score
                );
                return;
            }
        }

        this.commandHandler.handle(event);
        LOG.info("[" + channelName + "]["+event.getPermissions()+"] " + username + ": " + event.getMessage());
    }
}
