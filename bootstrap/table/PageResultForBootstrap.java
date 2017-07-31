package com.wailian.page;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/10/26.
 */
@Data
public class PageResultForBootstrap<T> {

    private long total;

    private List<T> rows;

    public PageResultForBootstrap() {

    }


    public PageResultForBootstrap(Page<T> page) {
        this.total = page.getTotalElements();
        this.rows = new ArrayList();
        Iterator<T> iterator = page.iterator();
        while (iterator.hasNext()) {
            rows.add(iterator.next());
        }
    }



    @Override
    public String toString() {
        return "PageResultForBootstrap{" +
                "total=" + total +
                ", rows=" + rows +
                '}';
    }
}
