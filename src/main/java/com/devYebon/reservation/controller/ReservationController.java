package com.devYebon.reservation.controller;

import com.devYebon.reservation.service.ReservationService;
import com.devYebon.reservation.model.Seat;
import com.devYebon.reservation.model.ReservationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<Seat> getAllSeats() {
        return reservationService.getAllSeats();
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserveSeat(@RequestBody ReservationRequest reservationRequest) {
        String seatNumber = reservationRequest.getSeatNumber();
        String userName = reservationRequest.getUserName();

        try {
            if (reservationService.reserveSeat(seatNumber, userName)) {
                return ResponseEntity.ok("예약 성공");
            } else {
                return ResponseEntity.status(400).body("좌석이 이미 예약되었습니다.");
            }
        } catch (IllegalArgumentException e) {
            // 잘못된 인자에 대한 예외 처리
            return ResponseEntity.status(400).body("잘못된 요청입니다: " + e.getMessage());
        } catch (Exception e) {
            // 기타 예외 처리
            return ResponseEntity.status(500).body("예약 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}