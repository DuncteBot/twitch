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
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Inspired by: https://github.com/Vuurvos1/stayHydratedFox
public class StayHydrated {
    private static final Logger LOG = LoggerFactory.getLogger(StayHydrated.class);
    private static final int HOUR_IN_MILIS = 60 * 60 * 1000;

    private final TwitchHelix helix;
    private final TwitchChat chat;
    private final List<String> userIds = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private final List<String> liveChannels = new ArrayList<>();
    private final List<String> messageQueue = new ArrayList<>();

    public StayHydrated(TwitchClient client) {
        this.helix = client.getHelix();
        this.chat = client.getChat();

        final String[] users = Objects.requireNonNullElse(System.getenv("HYDRATE_USERS"), "").split(",");

        if (users.length > 0) {
            this.loadUserIds(users);

            this.scheduler.scheduleAtFixedRate(this::pingStreamUp, 0L, 1L, TimeUnit.HOURS);
        }
    }

    private void loadUserIds(String[] userNames) {
        final List<String> userIds = this.helix.getUsers(null, null, List.of(userNames))
            .execute()
            .getUsers()
            .stream()
            .map(User::getId)
            .toList();

        this.userIds.clear();
        this.userIds.addAll(userIds);
    }

    private void pingStreamUp() {
        final List<Stream> streams = this.helix.getStreams(
                null,
                null,
                null,
                100,
                null,
                null,
                this.userIds,
                null
            )
            .execute()
            .getStreams();

        this.liveChannels.clear();

        final Instant now = Instant.now();

        for (final Stream stream : streams) {
            final String login = stream.getUserLogin();

            this.liveChannels.add(login);

            if (this.messageQueue.contains(login)) {
                continue;
            }

            final Instant start = stream.getStartedAtInstant();
            final Duration diff = Duration.between(start, now);
            final long streamTime = diff.toMillis();
            final long timeTilReminder = HOUR_IN_MILIS - (streamTime % HOUR_IN_MILIS);
            final long hoursLive = diff.toHours();

            // LOG.info("toHours: {}, toHoursPart: {}, ceil: {}", hoursLive, diff.toHoursPart(), streamTime / HOUR_IN_MILIS);

            this.messageQueue.add(login);
            LOG.info(
                "Sending reminder to {} in {} min, {} hour live",
                login,
                timeTilReminder / 60000,
                hoursLive
            );

            this.scheduler.schedule(() -> {
                if (!this.messageQueue.remove(login)) {
                    return;
                }

                if (this.liveChannels.contains(login)) {
                    final long currentHoursLive = hoursLive + 1;
                    final String hourWord = currentHoursLive == 1 ? "hour" : "hours";

                    LOG.info("Sending reminder to {}, {} {} live", login, currentHoursLive, hourWord);

                    this.chat.sendMessage(
                        login,
                        "You have been live for %s %s and should have consumed at least %sml of water to maintain optimal hydration! \uD83D\uDCA6"
                            .formatted(currentHoursLive, hourWord, currentHoursLive * 120)
                    );
                }
            }, timeTilReminder, TimeUnit.MILLISECONDS);
        }


        LOG.info("Channels current live: {}", String.join(", ", this.liveChannels));
    }
}
