package com.hepl.protocol.requests;

import com.hepl.protocol.interfaces.Request;

public class requestSecure implements Request {
    private byte[] data1;  // clé de session cryptée asymétriquement
    private byte[] data2;  // message crypté symétriquement

    public void setData1(byte[] d) { data1 = d; }
    public void setData2(byte[] d) { data2 = d; }
    public byte[] getData1() { return data1; }
    public byte[] getData2() { return data2; }
}
