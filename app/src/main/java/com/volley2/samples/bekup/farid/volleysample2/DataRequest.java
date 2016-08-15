package com.volley2.samples.bekup.farid.volleysample2;

import java.io.Serializable;

/**
 * Created by farid on 7/26/16.
 */
public class DataRequest implements Serializable {
    public DATA_TYPE type;
    private String _url;

    public DataRequest(DATA_TYPE type, String url){
        this.type = type;
        this._url = url;
    }

    public String get_url(){
        return this._url;
    }
}
