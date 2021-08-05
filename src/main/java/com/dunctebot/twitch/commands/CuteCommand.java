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

public class CuteCommand extends AbstractCommand  {
    public CuteCommand() {
        super("cute");
    }

    @Override
    public void execute(ChannelMessageEvent event, List<String> args) {
        final String channel = event.getChannel().getName();
        final TwitchChat chat = event.getTwitchChat();


        final String name;

        if (args.isEmpty()) {
            name = event.getUser().getName();
        } else {
            name = args.get(0);
        }

        chat.sendMessage(channel, "/me You are cute " + name + "! Everyone is cute!");
    }
}
