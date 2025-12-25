package com.evidenceframe.evidencer.collector.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class GitHubClient {

    private static final String API_BASE = "https://api.github.com";
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final HttpClient httpClient;
    private final GitHubConfig config;
    private final ObjectMapper mapper = new ObjectMapper();

    public GitHubClient(GitHubConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
    }

    /**
     * Fetch repositories for org or user (depending on config).
     * Returns a list of raw JSON responses (one per page).
     */
    public List<String> fetchRepositories() throws IOException, InterruptedException {
        List<String> pages = new ArrayList<>();

        String path;
        if (config.organization() != null && !config.organization().isEmpty()) {
            path = "/orgs/" + config.organization() + "/repos";
        } else {
            path = "/users/" + config.user() + "/repos";
        }

        int perPage = Math.min(config.repoLimit(), 100);
        int remaining = config.repoLimit();
        int page = 1;
        int totalCollected = 0;

        while (remaining > 0) {
            String url = API_BASE + path
                    + "?per_page=" + perPage
                    + "&page=" + page;

            HttpResponse<String> response = get(url);

            if (response.statusCode() != 200) {
                throw new IOException(
                        "GitHub API error fetching repositories: HTTP "
                                + response.statusCode()
                );
            }

            // Parse the response to handle item limits precisely
            JsonNode rootNode = mapper.readTree(response.body());
            if (!rootNode.isArray()) {
                throw new IOException("GitHub API returned non-array response");
            }

            ArrayNode pageItems = mapper.createArrayNode();
            for (JsonNode repo : rootNode) {
                if (totalCollected >= config.repoLimit()) {
                    break;
                }
                pageItems.add(repo);
                totalCollected++;
            }

            if (pageItems.size() > 0) {
                pages.add(pageItems.toString());
            }

            if (totalCollected >= config.repoLimit()) {
                break;
            }

            // GitHub returns fewer items than per_page when done
            if (!hasNextPage(response) || rootNode.size() < perPage) {
                break;
            }

            remaining -= pageItems.size();
            page++;
        }

        return pages;
    }

    /**
     * Generic GET request with auth headers.
     */
    private HttpResponse<String> get(String url)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(TIMEOUT)
                .header("Authorization", "Bearer " + config.token())
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Detects pagination via Link header.
     */
    private boolean hasNextPage(HttpResponse<?> response) {
        return response.headers()
                .firstValue("Link")
                .map(link -> link.contains("rel=\"next\""))
                .orElse(false);
    }
}
