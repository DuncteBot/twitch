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

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class EventHandler {

    private final Main main;
    private final CommandHandler commandHandler;

    public EventHandler(Main main) {
        this.main = main;
        this.commandHandler = new CommandHandler(main);
    }

    @EventSubscriber
    public void printChannelMessage(ChannelMessageEvent event) {
        this.commandHandler.handle(event);
        System.out.println("[" + event.getChannel().getName() + "]["+event.getPermissions()+"] " + event.getUser().getName() + ": " + event.getMessage());
    }
}
