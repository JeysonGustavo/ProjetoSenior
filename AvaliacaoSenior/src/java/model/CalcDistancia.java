package model;

import java.math.BigDecimal;

/**
 * @author Jeyson Gomes
 */
public class CalcDistancia {
    
    private int ibge_id;
    private String name;
    private String uf;
    private java.math.BigDecimal lat;
    private java.math.BigDecimal lon;
    private String micro_region;
    private String meso_region;

    public int getIbge_id() {
        return ibge_id;
    }

    public void setIbge_id(int ibge_id) {
        this.ibge_id = ibge_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }

    public String getMicro_region() {
        return micro_region;
    }

    public void setMicro_region(String micro_region) {
        this.micro_region = micro_region;
    }

    public String getMeso_region() {
        return meso_region;
    }

    public void setMeso_region(String meso_region) {
        this.meso_region = meso_region;
    }
    
    
    
}
