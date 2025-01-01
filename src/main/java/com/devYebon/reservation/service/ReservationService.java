package com.devYebon.reservation.service;


import com.devYebon.reservation.model.Seat;
import com.devYebon.reservation.model.Reservation;
import com.devYebon.reservation.repository.SeatRepository;
import com.devYebon.reservation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(SeatRepository seatRepository, ReservationRepository reservationRepository) {
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    public boolean isSeatAvailable(String seatNumber) {
        Seat seat = seatRepository.findBySeatNumber(seatNumber);
        return seat != null && !seat.isReserved();
    }

    @Transactional
    public boolean reserveSeat(String seatNumber, String userName) {
        Seat seat = seatRepository.findBySeatNumber(seatNumber);
        if (seat != null && !seat.isReserved()) {
            seat.setReserved(true);
            seatRepository.save(seat);

            Reservation reservation = new Reservation();
            reservation.setSeatId(seat.getId());
            reservation.setUserName(userName);
            reservation.setReservationTime(LocalDateTime.now());
            reservationRepository.save(reservation);

            return true;
        }
        return false;
    }
}