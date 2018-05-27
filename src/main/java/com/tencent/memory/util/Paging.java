package com.tencent.memory.util;

import com.tencent.memory.config.Attrs;
import com.tencent.memory.model.MyException;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

public class Paging {

    public static final int defaultSize = 30;

    public int start;
    public int size;

    public Paging(int start, int size) {
        this.start = start;
        this.size = size;
    }

    public static Paging parse(HttpServletRequest req) {
        int pageInt;
        int pageSizeInt;

        String page = req.getParameter(Attrs.page);
        if (page == null || page.length() == 0) {
            pageInt = 1;
        } else {
            if (TextUtils.isDigitsOnly(page)) {
                pageInt = Integer.parseInt(page);
            } else {
                throw new MyException(HttpStatus.BAD_REQUEST, "page 参数要为数字");
            }
        }

        String pageSize = req.getParameter(Attrs.size);
        if (pageSize == null || pageSize.length() == 0) {
            pageSizeInt = defaultSize;
        } else {
            if (TextUtils.isDigitsOnly(pageSize)) {
                pageSizeInt = Integer.parseInt(pageSize);
            } else {
                throw new MyException(HttpStatus.BAD_REQUEST, "size 参数要为数字");
            }
        }


        if (pageInt <= 0) {
            throw new MyException(HttpStatus.BAD_REQUEST, "page参数要大于0");
        }

        if (pageSizeInt <= 0) {
            throw new MyException(HttpStatus.BAD_REQUEST, "size参数要大于0");
        }

        int start = (pageInt - 1) * pageSizeInt;

        return new Paging(start, pageSizeInt);
    }
}
