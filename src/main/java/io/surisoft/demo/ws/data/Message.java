package io.surisoft.demo.ws.data;

import lombok.Data;

@Data
public class Message {

    public Message(String from, String text, String time) {
        this.from = from;
        this.text = text;
        this.time = time;
    }

    private String from;
    private String text;
    private String time;
}
