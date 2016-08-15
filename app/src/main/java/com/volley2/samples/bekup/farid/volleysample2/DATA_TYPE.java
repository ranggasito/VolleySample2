package com.volley2.samples.bekup.farid.volleysample2;

/**
 * Created by farid on 7/26/16.
 */
public enum DATA_TYPE {
    BITMAP("bitmap"), TEXT("text"), XML("xml"), JSON("json");

    private String value;
    private DATA_TYPE(String name){
        this.value = name;
    }

    public String getValue(){
        return this.value;
    }
}
