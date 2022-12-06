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
import com.github.twitch4j.common.enums.CommandPermission;

import java.util.*;
import java.util.concurrent.*;

public class RaffleCommand extends AbstractCommand {
    private final Random random = ThreadLocalRandom.current();
    // Using a set to prevent duplicates
    protected final Map<String, Set<String>> raffles = new ConcurrentHashMap<>();
    // announce every 15 seconds
    private final Map<String, Integer> countdownMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor((r) -> {
        final Thread t = new Thread(r, "Raffle scheduler");
        t.setDaemon(true);

        return t;
    });

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
        final TwitchChat chat = event.getTwitchChat();

        if (this.raffles.containsKey(channel)) {
            event.reply(chat, "/me A raffle has already started in this channel crroolDerp");
            return;
        }

        event.reply(chat, "/me crroolSpin A raffle has started type !join to join it crroolSpin");

        this.raffles.put(channel, new HashSet<>());
        this.countdownMap.put(channel, 60);

        this.scheduler.scheduleAtFixedRate(() -> {
            // TODO: keep this map?
            final int value = this.countdownMap.get(channel) - 15;

            if (value <= 0) {
                this.countdownMap.remove(channel);
                final Set<String> names = this.raffles.get(channel);

                chat.sendMessage(channel, "crroolClap Congrats to @" + this.getRandom(names) + " for winning this raffle! crroolClap");
            } else {
                this.countdownMap.put(channel, value);
                chat.sendMessage(channel, "crroolHappy There are " + value + " seconds left! Type !join to join crroolPray");
            }
        }, 15L, 15L, TimeUnit.SECONDS);
    }
    // https://stackoverflow.com/questions/124671/picking-a-random-element-from-a-set
    private String getRandom(Set<String> names) {
        final int search = this.random.nextInt(names.size());
        int i = 0;

        for (final String name : names) {
            if (i == search) {
                return name;
            }

            i++;
        }

        return null; // how?
    }

    public static class JoinCommand extends AbstractCommand {
        private final RaffleCommand parent;

        public JoinCommand(RaffleCommand parent) {
            super("join");
            this.parent = parent;
        }

        @Override
        public void execute(ChannelMessageEvent event, List<String> args) {
            final String channel = event.getChannel().getName();

            if (!this.parent.raffles.containsKey(channel)) {
                return;
            }

            this.parent.raffles.get(channel).add(event.getUser().getName());
        }
    }
}
