package com.evidenceframe.evidencer.collector.github;

import java.util.Map;
import java.util.Objects;

public final class GitHubConfig {

    private final String token;
    private final String organization;
    private final String user;
    private final int repoLimit;

    private GitHubConfig(
            String token,
            String organization,
            String user,
            int repoLimit
    ) {
        this.token = token;
        this.organization = organization;
        this.user = user;
        this.repoLimit = repoLimit;
    }

    public static GitHubConfig fromEnv() {
        Map<String, String> env = System.getenv();

        String token = trim(env.get("GITHUB_TOKEN"));
        String org = trim(env.get("GITHUB_ORG"));
        String user = trim(env.get("GITHUB_USER"));
        String repoLimitRaw = trim(env.get("GITHUB_REPO_LIMIT"));

        if (token == null || token.isEmpty()) {
            throw new IllegalStateException(
                    "GITHUB_TOKEN is required for GitHub collector"
            );
        }

        if ((org == null || org.isEmpty())
                && (user == null || user.isEmpty())) {
            throw new IllegalStateException(
                    "Either GITHUB_ORG or GITHUB_USER must be set"
            );
        }

        int repoLimit = 50; // default
        if (repoLimitRaw != null && !repoLimitRaw.isEmpty()) {
            try {
                repoLimit = Integer.parseInt(repoLimitRaw);
                if (repoLimit <= 0) {
                    throw new IllegalStateException(
                            "GITHUB_REPO_LIMIT must be greater than zero"
                    );
                }
            } catch (NumberFormatException e) {
                throw new IllegalStateException(
                        "GITHUB_REPO_LIMIT must be a valid number"
                );
            }
        }

        return new GitHubConfig(token, org, user, repoLimit);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    public String token() {
        return token;
    }

    public String organization() {
        return organization;
    }

    public String user() {
        return user;
    }

    public int repoLimit() {
        return repoLimit;
    }

    @Override
    public String toString() {
        return "GitHubConfig{" +
                "organization='" + organization + '\'' +
                ", user='" + user + '\'' +
                ", repoLimit=" + repoLimit +
                '}';
    }
}
