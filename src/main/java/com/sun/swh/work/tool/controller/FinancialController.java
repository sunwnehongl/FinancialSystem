package com.sun.swh.work.tool.controller;

import com.sun.swh.work.tool.bean.Parameter;
import com.sun.swh.work.tool.bean.Purchase;
import com.sun.swh.work.tool.service.*;
import com.sun.swh.work.tool.service.impl.TurnoverServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: swh
 * @Date: 2019/11/2 21:53
 * @Description:
 */
@RestController
public class FinancialController {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private ExcelToolService excelToolService;

    @Autowired
    private TurnoverService turnoverService;

    @Autowired
    private RoyaltyService royaltyService;

    @Autowired
    private RepetitionService repetitionService;
    @RequestMapping(value = "/createMothExcel",method = {RequestMethod.POST,RequestMethod.GET})
    public String createMothExcel(@RequestBody Parameter parameter) {
        excelToolService.createMothExcel(parameter);
        return parameter.getPath();
    }

    @RequestMapping(value = "/createPurchaseExcel",method = {RequestMethod.POST,RequestMethod.GET})
    public String createPurchaseExcel(@RequestBody Parameter parameter) {
        purchaseService.establish(parameter.getPath());
        return "true";
    }

    @RequestMapping(value = "/createTurnoverExcel",method = {RequestMethod.POST,RequestMethod.GET})
    public String createTurnoverExcel(@RequestBody Parameter parameter) {
        turnoverService.establish(parameter.getPath());
        return "true";
    }

    @RequestMapping(value = "/createRoyaltyExcel",method = {RequestMethod.POST,RequestMethod.GET})
    public String createRoyaltyExcel(@RequestBody Parameter parameter) {
        royaltyService.computationalRoyalty(parameter.getPath());
        return "true";
    }

    @RequestMapping(value = "/writeRepetition",method = {RequestMethod.POST,RequestMethod.GET})
    public String writeRepetition(@RequestBody Parameter parameter){
        repetitionService.writeRepetition(parameter.getPath(),parameter.getFileName());
        return "true";
    }



}
