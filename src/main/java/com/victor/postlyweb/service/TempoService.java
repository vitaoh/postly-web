package com.victor.postlyweb.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TempoService {

    public String relativo(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        if (diff < 60_000L) {
            return "agora";
        }
        if (diff < 3_600_000L) {
            return (diff / 60_000L) + "min";
        }
        if (diff < 86_400_000L) {
            return (diff / 3_600_000L) + "h";
        }
        if (diff < 7L * 86_400_000L) {
            return (diff / 86_400_000L) + "d";
        }
        return new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date(timestamp));
    }
}
