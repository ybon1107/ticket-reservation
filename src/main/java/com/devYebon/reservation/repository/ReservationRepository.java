package com.devYebon.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.devYebon.reservation.model.Reservation;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findBySeatId(Long seatId);  // 좌석 ID로 예약 조회
}