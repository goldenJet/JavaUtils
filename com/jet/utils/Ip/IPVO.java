package com.jet.mini.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qiniu.util.StringUtils;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: IPVO
 * @Description: IP 相关信息
 * @Author: Jet.Chen
 * @Date: 2019/2/2 15:01
 * @Version: 1.0
 **/
@Data
public class IPVO {

    private String ip;

    @ApiParam(value = "国家")
    private String country; // 国家

    @ApiParam(value = "地区")
    private String area; // 地区

    @ApiParam(value = "省")
    private String region; // 省

    @ApiParam(value = "市")
    private String city; // 市

    @ApiParam(value = "区")
    private String county; // 区

    @ApiParam(value = "运营商")
    private String isp; // 运营商，如：电信

    private String country_id;
    private String area_id;
    private String region_id;
    private String city_id;
    private String county_id;
    private String isp_id;

    public IPVO(){
        this.country = this.area = "";
    }

    public IPVO(String ip){
        this.ip = ip;
        this.country = this.area = "";
    }

    public IPVO(Map<String, String> taobao){
        this.ip = taobao.get("ip");

        this.country = taobao.get("country");
        this.area = taobao.get("area");
        this.region = taobao.get("region");
        this.city = taobao.get("city");
        this.county = taobao.get("county");
        this.isp = taobao.get("isp");

        this.country_id = taobao.get("country_id");
        this.area_id = taobao.get("area_id");
        this.region_id = taobao.get("region_id");
        this.city_id = taobao.get("city_id");
        this.county_id = taobao.get("county_id");
        this.isp_id = taobao.get("isp_id");
    }

    @JsonIgnore
    public synchronized IPVO getCopy() {
        IPVO ret = new IPVO();
        ret.country = country;
        ret.area = area;
        return ret;
    }

    public String getCity() {
        if (!StringUtils.isNullOrEmpty(city)) return this.city;
        if (this.country != null) {
            String[] array = this.country.split("省");
            if (array.length > 1) {
                this.city =  array[1];
            } else {
                this.city = this.country;
            }
            if (this.city.length() > 3) {
                this.city.replace("内蒙古", "");
            }
        }
        return this.city;
    }

    public void setArea(String area) {
        //如果为局域网，纯真IP地址库的地区会显示CZ88.NET,这里把它去掉
        if (area.trim().equals("CZ88.NET")) {
            this.area="本机或本网络";
        } else {
            this.area = area;
        }
    }

}
