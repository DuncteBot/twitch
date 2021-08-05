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

package com.dunctebot.twitch.commands;

import com.dunctebot.twitch.AbstractCommand;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RaffleCommand extends AbstractCommand {
    // Using a set to prevent duplicates
    protected final Map<String, Set<String>> raffles = new HashMap<>();

    public RaffleCommand() {
        super("sraffle");
    }

    @Override
    public void execute(ChannelMessageEvent event, List<String> args) {
        if (!event.getPermissions().contains(CommandPermission.MODERATOR)) {
            return;
        }

        final String channel = event.getChannel().getName();
        final String messageId = event.getMessageEvent().getMessageId().get();

        if (this.raffles.containsKey(channel)) {
            event.getTwitchChat().sendMessage(channel, "A raffle has already started in this channel", null, messageId);
            return;
        }

        event.getTwitchChat().sendMessage(channel, "test", null, messageId);
    }

    public class JoinCommand extends AbstractCommand {
        public JoinCommand() {
            super("join");
        }

        @Override
        public void execute(ChannelMessageEvent event, List<String> args) {
            final String channel = event.getChannel().getName();

            if (!raffles.containsKey(channel)) {
                return;
            }

            raffles.get(channel).add(event.getUser().getName());
        }
    }
}
