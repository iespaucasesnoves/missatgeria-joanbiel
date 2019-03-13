package com.example.joanbiel.quepassaeh;

public class Missatge {
    long codi;
    String msg;
    String dataHora;//Intentar canvair-lo a DATETIME.
    long codiUsuari;

    public Missatge() {
        this.codi = -1;
    }

    public long getCodi() {
        return codi;
    }

    public void setCodi(long codi) {
        this.codi = codi;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public long getCodiUsuari() {
        return codiUsuari;
    }

    public void setCodiUsuari( long codiUsuari) {
        this.codiUsuari = codiUsuari;
    }
}
