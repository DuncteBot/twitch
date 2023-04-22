package com.dunctebot.twitch.commands;

import com.dunctebot.twitch.AbstractCommand;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.List;

public class LeaderboardCommand extends AbstractCommand {

    public LeaderboardCommand() {
        super("leaderboard");
    }

    @Override
    public void execute(ChannelMessageEvent event, List<String> args) {
        event.getTwitchChat().sendMessage(
            event.getChannel().getName(),
            "You can find the current leadeboard here: https://twitch.duncte.bot/points.php"
        );
    }
}
