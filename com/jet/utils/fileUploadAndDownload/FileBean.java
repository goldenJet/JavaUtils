package com.jet.pojo;

import lombok.Data;

/**
 * Created by jet.chen on 2017/5/10.
 */
@Data
public class FileBean {
    byte[] bytes;
    long size;
    String fileName;
}
