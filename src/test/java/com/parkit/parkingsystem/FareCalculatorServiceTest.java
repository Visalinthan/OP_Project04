package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCarWithLessThan30MinutesPark() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusMinutes(30);

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        System.out.println(ticket.getInTime());
        System.out.println(ticket.getOutTime());

        assertEquals(0, ticket.getPrice());
    }

    @Test
    public void calculateFareBikeWithLessThan30MinutesPark() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusMinutes(30);

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        System.out.println(ticket.getInTime());
        System.out.println(ticket.getOutTime());

        assertEquals(0, ticket.getPrice());
    }

    @Test
    public void calculateFareCar() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusMinutes(150);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        Duration duration = Duration.between(ticket.getInTime(), ticket.getOutTime());
        long minutesToPay = 0;

        if(duration.toMinutes()>30){
            minutesToPay = duration.minusMinutes(30).toMinutes();
        }

        double calculFare = (minutesToPay*(Fare.CAR_RATE_PER_HOUR/60));

        assertEquals(calculFare, ticket.getPrice());

    }

    @Test
    public void calculateFareBike() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusMinutes(15);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        Duration duration = Duration.between(ticket.getInTime(), ticket.getOutTime());
        long minutesToPay = 0;

        if(duration.toMinutes()>30){
             minutesToPay = duration.minusMinutes(30).toMinutes();
        }

        double calculFare = (minutesToPay*(Fare.BIKE_RATE_PER_HOUR/60));

        assertEquals(calculFare,ticket.getPrice());
    }

    @Test
    public void calculateFareUnkownType() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusMinutes(75);
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareCarWithFutureInTime() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.minusMinutes(10);

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.minusMinutes(10);

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusMinutes(45);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        Duration duration = Duration.between(ticket.getInTime(), ticket.getOutTime());
        long minutesToPay = 0;

        if(duration.toMinutes()>30){
            minutesToPay = duration.minusMinutes(30).toMinutes();
        }

        double calculFare = (minutesToPay*(Fare.CAR_RATE_PER_HOUR/60));

        assertEquals(calculFare, ticket.getPrice());
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusMinutes(45);

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        Duration duration = Duration.between(ticket.getInTime(), ticket.getOutTime());

        long minutesToPay = 0;

        if(duration.toMinutes()>30){
            minutesToPay = duration.minusMinutes(30).toMinutes();
        }

        double calculFare = (minutesToPay*(Fare.BIKE_RATE_PER_HOUR/60));

        assertEquals(calculFare, ticket.getPrice());
    }


    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusDays(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        Duration duration = Duration.between(ticket.getInTime(), ticket.getOutTime());
        long minutesToPay = 0;

        if(duration.toMinutes()>30){
            minutesToPay = duration.minusMinutes(30).toMinutes();
        }

        double calculFare = (minutesToPay*(Fare.CAR_RATE_PER_HOUR/60));

        assertEquals(calculFare, ticket.getPrice());
    }

    @Test
    public void calculateFareBikeWithMoreThanADayParkingTime() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusDays(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        Duration duration = Duration.between(ticket.getInTime(), ticket.getOutTime());
        long minutesToPay = 0;

        if(duration.toMinutes()>30){
            minutesToPay = duration.minusMinutes(30).toMinutes();
        }

        double calculFare = (minutesToPay*(Fare.BIKE_RATE_PER_HOUR/60));

        assertEquals(calculFare, ticket.getPrice());
    }



}
