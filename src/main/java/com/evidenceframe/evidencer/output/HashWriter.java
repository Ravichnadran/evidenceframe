package com.evidenceframe.evidencer.output;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;

public final class HashWriter {

    public void write(Path evidenceRoot) throws IOException {
        List<Path> filesToHash = new ArrayList<>();

        Files.walk(evidenceRoot)
                .filter(Files::isRegularFile)
                .filter(p -> !p.getFileName().toString().equals("checksums.sha256"))
                .forEach(filesToHash::add);

        filesToHash.sort(Comparator.comparing(Path::toString));

        Path hashFile = evidenceRoot.resolve("checksums.sha256");

        StringBuilder sb = new StringBuilder();

        for (Path file : filesToHash) {
            sb.append(hash(file))
                    .append("  ")
                    .append(evidenceRoot.relativize(file))
                    .append("\n");
        }

        Files.writeString(hashFile, sb.toString());
    }

    private String hash(Path file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            try (InputStream in = Files.newInputStream(file)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) > 0) {
                    digest.update(buffer, 0, read);
                }
            }

            return HexFormat.of().formatHex(digest.digest());
        } catch (Exception e) {
            throw new IOException("Failed to hash file: " + file, e);
        }
    }
}
