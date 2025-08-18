package models;

import java.util.Map;

public record Conversor(
        String date,
        String base,
        Map<String, String> rates
) {
}