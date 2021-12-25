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

import com.dunctebot.twitch.commands.CuteCommand;
import com.dunctebot.twitch.commands.HangmanCommand;
import com.dunctebot.twitch.commands.HugCommand;
import com.dunctebot.twitch.commands.RaffleCommand;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandHandler {
    public static final String PREFIX = "!";

    private final Map<String, AbstractCommand> commands = new HashMap<>();

    public CommandHandler(TwitchClient client) {
        this.addCommand(new CuteCommand());

        final HangmanCommand hangmanCommand = new HangmanCommand(client);

        this.addCommand(hangmanCommand);
        this.addCommand(new HangmanCommand.GuessCommand(hangmanCommand));
        this.addCommand(new HugCommand());
        final RaffleCommand raffleCommand = new RaffleCommand();
        this.addCommand(raffleCommand);
        this.addCommand(new RaffleCommand.JoinCommand(raffleCommand));
    }

    private void addCommand(AbstractCommand cmd) {
        final String name = cmd.getName();

        this.commands.put(name, cmd);
    }

    public Map<String, AbstractCommand> getCommands() {
        return commands;
    }

    @Nullable
    private AbstractCommand getCommand(String name) {
        return this.commands.get(name);
    }

    // TODO: run commands on their own threads
    void handle(ChannelMessageEvent event) {
        final String message = event.getMessage();

        if (!message.startsWith(PREFIX) || message.length() < 2) {
            return;
        }

        final String[] split = message.substring(1).split("\\s+", 2);
        final String invoke = split[0].toLowerCase();

        final AbstractCommand command = this.getCommand(invoke);

        if (command != null) {
            List<String> args = new ArrayList<>();

            if (split.length > 1 && !split[1].isBlank()) {
                args = List.of(
                    split[1].split("\\s+")
                );
            }

            command.execute(event, args);
        }
    }

}
