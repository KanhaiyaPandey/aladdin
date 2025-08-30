package com.store.aladdin.controllers.admincontroller.orders;

import com.store.aladdin.utils.response.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.store.aladdin.routes.admin_routes.AdminOrderRoutes.*;


@RestController
@RequestMapping(ORDER_BASE)
public class CreateManualOrder {

    @PostMapping(value = CREATE_MANUAL_ORDER)
    public ResponseEntity<Map<String, Object>> create_manual_order(@RequestBody String reqJson){
        return ResponseUtil.buildResponse("jdshbyuw", true, reqJson, HttpStatus.CREATED);
    }

}
