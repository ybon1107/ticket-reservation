package com.devYebon.reservation.controller;

import com.devYebon.reservation.model.ReservationRequest;
import com.devYebon.reservation.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/reservations")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/reserve")
    public ResponseEntity<String> reserveTicket(@RequestBody ReservationRequest request) {
        try {
            CompletableFuture<String> result = ticketService.reserveTicket(request.getSeatNumber(), request.getUserName());
            String responseMessage = result.join();

            // 성공 메시지 확인
            if (responseMessage.startsWith("예약 성공")) {
                return ResponseEntity.ok(responseMessage); // 200 OK
            } else if (responseMessage.contains("이미 예약된 좌석입니다")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage); // 400 Bad Request
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage); // 500 Internal Server Error
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }
}
