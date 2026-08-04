package com.prgrammingtechie.demo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;

public record ProductResponse(String id, String name, String description, @JsonSerialize(using = ToStringSerializer.class) BigDecimal price) {
}
