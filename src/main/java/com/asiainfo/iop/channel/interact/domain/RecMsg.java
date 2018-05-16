package com.asiainfo.iop.channel.interact.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

public class RecMsg {
    public RecMsg() {
    }
    private ObjectMapper mapper = new ObjectMapper();
    public String toJson() {
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
