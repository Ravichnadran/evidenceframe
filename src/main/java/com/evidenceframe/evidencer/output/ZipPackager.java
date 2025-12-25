package com.evidenceframe.evidencer.output;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ZipPackager {

    public Path createZip(Path evidenceRoot) throws IOException {

//        Path zipFile = evidenceRoot.getParent()
//                .resolve(evidenceRoot.getFileName().toString() + ".zip");
        Path parent = evidenceRoot.toAbsolutePath().getParent();
        if (parent == null) {
            parent = Path.of(".");
        }

        Path zipFile = parent.resolve(
                evidenceRoot.getFileName().toString() + ".zip"
        );


        try (OutputStream fos = Files.newOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            Files.walk(evidenceRoot)
                    .filter(Files::isRegularFile)
                    .forEach(file -> zip(file, evidenceRoot, zos));
        }

        return zipFile;
    }

    private void zip(Path file, Path root, ZipOutputStream zos) {
        try {
            ZipEntry entry = new ZipEntry(root.relativize(file).toString());
            zos.putNextEntry(entry);
            Files.copy(file, zos);
            zos.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException("Failed to zip file: " + file, e);
        }
    }
}
