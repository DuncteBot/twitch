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

import com.github.twitch4j.TwitchClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CommandExporter {
    private static final String COMMAND_TEMPLATE = "./cmdhtml/template.html";
    private static final String COMMAND_OUTPUT = "./cmdhtml/index.html";

    public CommandExporter() {
        try {
            final StringBuilder sb = new StringBuilder();
            final File template = new File(COMMAND_TEMPLATE);
            final File output = new File(COMMAND_OUTPUT);

            if (output.exists()) {
                output.delete();
            }

            final String tmp = Files.readString(template.toPath());

            CommandHandler commandsHandler = new CommandHandler(null);
            commandsHandler.getCommands().forEach((name, cmd) -> {
                sb.append("<tr><td>")
                    .append(name)
                    .append("</td><td>")
                    .append("EVERYONE") // TODO
                    .append("</td></tr>");
            });

            Files.write(
                output.toPath(),
                tmp.replaceAll("\\{\\{ITEMS}}", sb.toString()).getBytes()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
