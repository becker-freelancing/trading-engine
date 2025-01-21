package com.becker.freelance.backtest;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BacktestResultZipper {

    public static void registerOnShutdown(Path path) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> new BacktestResultZipper(path).zipFile()));
    }


    private final Path readPath;
    private final Path writePath;

    public BacktestResultZipper(Path path) {
        this.readPath = path;
        this.writePath = Path.of(readPath + ".zst");
    }


    public void zipFile() {
        try (
                FileOutputStream fos = new FileOutputStream(writePath.toFile());
                ZstdCompressorOutputStream zos = new ZstdCompressorOutputStream(fos);
                FileInputStream fis = new FileInputStream(readPath.toFile());
        ) {
            byte[] buffer = new byte[8192];
            int length;

            while ((length = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not Zip File " + readPath);
        }

        try {
            Files.delete(readPath);
        } catch (IOException e) {
            throw new IllegalStateException("Could not delete File " + readPath);
        }
    }
}
