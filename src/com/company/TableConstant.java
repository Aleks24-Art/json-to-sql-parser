package com.company;

public final class TableConstant {
    public final static String TABLE_REPORTS =
            "CREATE TABLE REPORTS (\n" +
                    "    id uuid primary key,\n" +
                    "    start_date timestamp,\n" +
                    "    instance varchar(255)\n" +
                    ");";

    public final static String TABLE_NETWORKS =
            "CREATE TABLE NETWORKS (\n" +
                    "    ssid varchar(255),\n" +
                    "    capabilities varchar(255),\n" +
                    "    status varchar(255),\n" +
                    "    security varchar(255),\n" +
                    "    debug text,\n" +
                    "    level varchar(255),\n" +
                    "    bssid varchar(255),\n" +
                    "    report_id uuid CONSTRAINT FK_REPORT_ID REFERENCES REPORTS(ID)\n" +
                    ");";

}
