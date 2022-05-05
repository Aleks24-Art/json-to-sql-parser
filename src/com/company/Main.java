package com.company;

import com.company.pojo.Network;
import com.company.pojo.SumduJsonReport;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private final static String REPORT_INSERT_PATTERN = "INSERT INTO REPORTS VALUES ('%s', TO_TIMESTAMP('%s', 'YYYY-MM-DD HH24:MI:SS:MS'), '%s');";
    private final static String NETWORK_INSERT_PATTERN = "INSERT INTO NETWORKS VALUES ('%s', '%s', '%s', '%s', '%s ', '%s', '%s', '%s');";
    private final static String SOURCE_FOLDER = "json";
    private final static String EMPTY_STRING = "";

    private final static Path SQL_FILE = Paths.get("sql/sumdu_reports.sql");

    static {
        creteFile();
    }


    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();

        try (Stream<Path> paths = Files.list(Path.of(SOURCE_FOLDER))) {
            final List<SumduJsonReport> sumduJsonReports = paths.map(file ->
                    {
                        try {
                            String content = Files.lines(file).findFirst().orElseThrow();
                            return mapper.readValue(content, SumduJsonReport.class);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to read file: " + file.toFile().getName());
                        }
                    }
            ).collect(Collectors.toList());

            // There may be duplicates in the folder
            var uniqueReports = removeDuplicateReports(sumduJsonReports);

            generateSqlScript(uniqueReports);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from folder: " + SOURCE_FOLDER);
        }
    }

    private static void generateSqlScript(List<SumduJsonReport> reports) {
        try {
            for (SumduJsonReport report : reports) {
                // In DB we have MANY(network)-TO-(report)ONE relation
                // So first we generate report insert and then use report PK in many network inserts
                String uuid = Optional.ofNullable(checkField(report.getUuid())).orElse(UUID.randomUUID().toString());
                String startDate = Optional.ofNullable(checkField(report.getStartDate())).orElse(EMPTY_STRING);
                String instance = Optional.ofNullable(checkField(report.getInstance())).orElse(EMPTY_STRING);
                List<Network> networks = report.getNetwork();
                // Format report SQL insert request
                String reportRequest = String.format(REPORT_INSERT_PATTERN, uuid, startDate, instance);
                Files.write(SQL_FILE, Collections.singleton(reportRequest), StandardCharsets.UTF_8, StandardOpenOption.APPEND);

                for (Network network : networks) {
                    String ssid = Optional.ofNullable(checkField(network.getSsid())).orElse(EMPTY_STRING);
                    String capabilities = Optional.ofNullable(checkField(network.getCapabilities())).orElse(EMPTY_STRING);
                    String status = Optional.of(checkField(network.getStatus())).orElse(EMPTY_STRING);
                    String security = Optional.ofNullable(checkField(network.getSecurity())).orElse(EMPTY_STRING);
                    String debug = Optional.ofNullable(checkField(network.getDebug())).orElse(EMPTY_STRING);
                    String level = Optional.ofNullable(checkField(network.getLevel())).orElse(EMPTY_STRING);
                    String bssid = Optional.ofNullable(checkField(network.getBssid())).orElse(EMPTY_STRING);
                    // Format network SQL insert request
                    String networkRequest = String.format(NETWORK_INSERT_PATTERN, ssid, capabilities, status, security, debug, level, bssid, uuid);
                    Files.write(SQL_FILE, Collections.singleton(networkRequest), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file: " + SQL_FILE.getFileName());
        }
    }

    private static List<SumduJsonReport> removeDuplicateReports(List<SumduJsonReport> reports) {
        var uniqueReports = reports.stream().distinct().collect(Collectors.toList());
        System.out.println("Duplicates removed = " + (reports.size() - uniqueReports.size()));
        return uniqueReports;
    }

    private static String checkField(String field) {
        if (Objects.isNull(field)) {
            return field;
        }

        // Escape character to prevent SQL script crushed
        if (field.contains("'")) {
            return field.replaceAll("'", "''");
        }

        return field;
    }

    @SneakyThrows
    private static void creteFile() {
        SQL_FILE.toFile().createNewFile();
    }
}
