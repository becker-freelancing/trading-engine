package com.becker.freelance.backtest.commons;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class BacktestResultZipper {

    private static final Logger logger = LoggerFactory.getLogger(BacktestResultZipper.class);

    public static void registerOnShutdown(Path path) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.warn("Starting Zipping Result File.... (!!!DO NOT FORCE STOP THE APPLICATION!!!)");
            new BacktestResultZipper(path).zipFile();
            logger.info("Result File zipped. Stopping the application...");
        }, "ResultFile-Zipper-0"));
    }


    private final Path readPath;
    private final Path writePath;

    public BacktestResultZipper(Path path) {
        if (path.toString().endsWith(".zst")){
            this.readPath = path;
            this.writePath = Path.of(readPath.toString().replace(".zst", ""));
        } else {
            this.readPath = path;
            this.writePath = Path.of(readPath + ".zst");
        }
    }


    public void zipFile() {
        logger.info("Saving Result file to {}", writePath);
        try {
            if (Files.exists(writePath)){
                Files.delete(writePath);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not Zip File " + readPath, e);
        }
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
            throw new IllegalStateException("Could not Zip File " + readPath, e);
        }

        try {
            Files.delete(readPath);
        } catch (IOException e) {
            throw new IllegalStateException("Could not delete File " + readPath, e);
        }
    }

    public Path unzipFile(){
        logger.info("Unzipping file {}...", readPath);
        try {
            if (!Files.exists(writePath)){
                Files.createDirectories(writePath.getParent());
            } else {
                Files.delete(writePath);
            }
            Files.createFile(writePath);
        } catch (IOException e){
            throw new IllegalStateException("Could not Unzip File " + readPath, e);
        }
        try (
                FileInputStream fis = new FileInputStream(readPath.toFile());
                ZstdCompressorInputStream zis = new ZstdCompressorInputStream(fis);
                FileOutputStream fos = new FileOutputStream(writePath.toFile());
                BufferedReader reader = new BufferedReader(new InputStreamReader(zis))
        ) {

            zis.transferTo(fos);
        } catch (IOException e) {
            throw new IllegalStateException("Could not Unzip File " + readPath, e);
        }

        return writePath;
    }
}
