package com.volley2.samples.bekup.farid.volleysample2;

import java.io.Serializable;

/**
 * Created by farid on 7/27/16.
 */
public class DataResult implements Serializable {
    private Object _data;
    private DATA_TYPE _type;

    public DataResult(DATA_TYPE type, Object data) {
        _data = data;
        _type = type;
    }

    public Object get_data(){
        return this._data;
    }

    public DATA_TYPE get_type() {
        return _type;
    }
}
