package com.java.bmart.global.jwt.dto;

import java.util.List;

public record Claims(Long userId, List<String> authorities) {

}
