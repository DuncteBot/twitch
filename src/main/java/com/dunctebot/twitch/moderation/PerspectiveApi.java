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

package com.dunctebot.twitch.moderation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;

public class PerspectiveApi {
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // SEVERE_TOXICITY
    // TODO: language needed?
    private static final String BASE_REQUEST = "{comment: {text: \"%s\"},languages: [\"en\"],requestedAttributes: {%s:{}} }";

    private static String genUrl(String apiKey) {
        return "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=" + apiKey;
    }

    public void getScore(String comment, String model, String apiKey) {
    }

    private JsonNode makeRequest(String comment, String model, String apiKey) throws IOException {
        final String jsonBody = String.format(BASE_REQUEST, comment, model);

        final Request request = new Request.Builder()
            .url(genUrl(apiKey))
            .post(RequestBody.create(jsonBody, MediaType.get("application/json")))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("User-Agent", "DuncteBot twitch extension (+https://www.duncte.bot/)")
            .build();

        try (final Response response = this.client.newCall(request).execute()) {
            if (response.code() != 200) {
                // handle error
            }

            try (final ResponseBody body = response.body()) {
                try (final InputStream inputStream = body.byteStream()) {
                    return this.mapper.readTree(inputStream);
                }
            }
        }
    }
}
