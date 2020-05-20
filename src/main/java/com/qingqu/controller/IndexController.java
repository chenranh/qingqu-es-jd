package com.qingqu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * <p>
 *
 * </p>
 *
 * @author Administrator
 * @since 2020/5/9
 */
@Controller
public class IndexController {

    @GetMapping()
    public String index(){
        return "index";
    }
}
