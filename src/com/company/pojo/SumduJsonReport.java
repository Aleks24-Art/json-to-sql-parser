package com.company.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SumduJsonReport {
    @JsonProperty("networks")
    private List<Network> network;
    @JsonProperty("startDate")
    private String startDate;
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("instance")
    private String instance;
}
