/*
 *     A twitch bot for personal use
 *     Copyright (C) 2022  Duncan "duncte123" Sterken
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

package com.dunctebot.twitch.db;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Connection getConnection() throws SQLException {
        final String jbdcUrl = String.format(
            "jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=utf8",
            System.getenv("SQL_HOST"),
            System.getenv("SQL_DB")
        );

        return DriverManager.getConnection(
            jbdcUrl,
            System.getenv("SQL_USER"),
            System.getenv("SQL_PASS")
        );
    }

    public void addPoints(String twitchId, String twitchDisplay, int points) {
        executor.submit(() -> {
            try (
                final Connection con = getConnection();
                final PreparedStatement st = con.prepareStatement(
                    "INSERT INTO points (`user_id`, `user_display`, `points`) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE `user_display` = ?, `points` = `points` + ?;"
                )
            ) {
                st.setString(1, twitchId);
                st.setString(2, twitchDisplay);
                st.setInt(3, points);
                st.setString(4, twitchDisplay);
                st.setInt(5, points);

                st.executeUpdate();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
