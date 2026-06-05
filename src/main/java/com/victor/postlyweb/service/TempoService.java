package com.victor.postlyweb.service;

import com.victor.postlyweb.modelo.ChatThread;

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

    public String dataHora(Long timestamp) {
        if (timestamp == null || timestamp <= 0L) {
            return "";
        }
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-BR")).format(new Date(timestamp));
    }

    public String dataHoraConversa(ChatThread chat) {
        if (chat == null) {
            return "";
        }
        Long timestamp = primeiroTimestampValido(chat.getLastTimestamp(), chat.getUpdatedAt(), chat.getCreatedAt());
        return dataHora(timestamp);
    }

    private Long primeiroTimestampValido(Long... timestamps) {
        for (Long timestamp : timestamps) {
            if (timestamp != null && timestamp > 0L) {
                return timestamp;
            }
        }
        return 0L;
    }
}
