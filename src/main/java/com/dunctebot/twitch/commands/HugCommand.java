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
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.List;
import java.util.regex.Pattern;

public class HugCommand extends AbstractCommand {
    private static final Pattern CUTIE = Pattern.compile("@?(duncte123|crrool)");

    public HugCommand() {
        super("hug");
    }

    @Override
    public void execute(ChannelMessageEvent event, List<String> args) {
        final String channel = event.getChannel().getName();
        final TwitchChat chat = event.getTwitchChat();


        if (args.isEmpty()) {
            final String messageId = event.getMessageEvent().getMessageId().get();
            chat.sendMessage(channel, "/me hugs back crroolHug", null, messageId);
            return;
        }

        final String target = args.get(0);

        if (CUTIE.matcher(target).matches()) {
            chat.sendMessage(channel, "/me crroolDuncteHug " + target);
            return;
        }

        chat.sendMessage(channel, "/me crroolHug " + target);
    }
}
