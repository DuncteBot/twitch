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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class PerspectiveApi {
    private static final Logger LOG = LoggerFactory.getLogger(PerspectiveApi.class);

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // TODO: get a new token for this application
    private static String getUrl() {
        return "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=" + System.getenv("PERSPECTIVE_KEY");
    }

    public float getScore(String comment, String model) {
        if (comment.isBlank()) {
            return 0f;
        }

        try {
            final JsonNode json = this.makeRequest(comment, model);

            System.out.println(json);

            if (json == null) {
                return 0f;
            }

            if (json.has("error")) {
                final String error = json.get("error").get("message").asText();

                if ("Unable to detect language.".equals(error)) {
                    return 0f;
                }

                LOG.error("Error while handling perspective api request: " + json);

                return 0f;
            }

            final JsonNode score = json.get("attributeScores").get(model).get("summaryScore");

            return Float.parseFloat(score.get("score").asText());
        } catch (final IOException e) {
            LOG.error("Failed to fetch perspective api data", e);
            return 0f;
        }
    }

    // SEVERE_TOXICITY
    private byte[] buildRequest(String comment, String model) throws JsonProcessingException {
        final ObjectNode mainNode = this.mapper.createObjectNode();
        final ObjectNode commentNode = mapper.createObjectNode()
            .put("text", comment);

        final ObjectNode requestedAttrs = this.mapper.createObjectNode();
        requestedAttrs.set(model, this.mapper.createObjectNode());

        mainNode.set("comment", commentNode);
        mainNode.set("requestedAttributes", requestedAttrs);

        return this.mapper.writeValueAsBytes(mainNode);
    }

    @Nullable
    private JsonNode makeRequest(String comment, String model) throws IOException {
        final Request request = new Request.Builder()
            .url(getUrl())
            .post(RequestBody.create(
                buildRequest(comment, model),
                MediaType.get("application/json")
            ))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("User-Agent", "DuncteBot twitch extension (+https://www.duncte.bot/)")
            .build();

        try (final Response response = this.client.newCall(request).execute()) {
            if (response.code() != 200) {
                // handle error
                return null;
            }

            try (final ResponseBody body = response.body()) {
                try (final InputStream inputStream = body.byteStream()) {
                    return this.mapper.readTree(inputStream);
                }
            }
        }
    }
}
