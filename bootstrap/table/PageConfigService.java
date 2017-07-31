package com.wailian.util;

import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Created by jet.chen on 2017/4/24.
 * bootstrap table 请求参数封装
 */
@Log4j
public class PageConfigService {
    /**
     * 根据传入的参数封装为PageRequest对象
     *
     * @param map
     * @return
     */
    public static PageRequest genericPageRequestByRequest(Map map) {
        PageRequest pageRequest = null;
        Integer pageSize = (Integer) map.get("pageSize");
        Integer pageNumber = (Integer) map.get("pageNumber");
        String sortName = (String) map.get("sortName");
        String sortOrder = (String) map.get("sortOrder");
        if (StringUtils.isEmpty(sortName)){
            sortName = "id";
        }
        try {
            Sort.Direction direction = Sort.Direction.DESC;
            if ("asc".equalsIgnoreCase(sortOrder)) {
                direction = Sort.Direction.ASC;
            }
            Sort.Order order = new Sort.Order(direction, sortName);
            pageRequest = new PageRequest(
                    pageNumber - 1,
                    pageSize,
                    new Sort(order));
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        }
        return pageRequest;
    }
}
