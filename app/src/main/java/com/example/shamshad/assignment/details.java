package com.example.shamshad.assignment;

/**
 * Created by shamshad on 5/6/18.
 */

public class details {
    String audio;
    String Address;

    public details(String audio, String address) {
        this.audio = audio;
        Address = address;
    }

    public details() {
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
