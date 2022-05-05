package com.company.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Network {
    @JsonProperty("SSID")
    private String ssid;
    @JsonProperty("capabilities")
    private String capabilities;
    @JsonProperty("status")
    private String status;
    @JsonProperty("security")
    private String security;
    @JsonProperty("debug")
    private String debug;
    @JsonProperty("level")
    private String level;
    @JsonProperty("BSSID")
    private String bssid;
}
