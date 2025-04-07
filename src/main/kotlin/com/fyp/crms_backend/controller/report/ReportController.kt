package com.fyp.crms_backend.controller.report

import com.fyp.crms_backend.dto.room.AddRoomRequest
import com.fyp.crms_backend.dto.room.EditRoomRequest
import com.fyp.crms_backend.dto.room.GetRoomRequest
import com.fyp.crms_backend.service.RoomService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.fyp.crms_backend.controller.ApiController
import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.dto.room.DeleteRoomRequest
import com.fyp.crms_backend.service.ReportService

@RestController
@RequestMapping("/api")
class ReportController(private val service: ReportService) :ApiController(){

//    過期冇還嘢
//    show entity record （room, user）

//    針對某學生嘅借野record



}