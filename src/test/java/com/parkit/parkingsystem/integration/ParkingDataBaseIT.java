package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

   //@Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
       // when(inputReaderUtil.readSelection()).thenReturn(1);
        // when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDEF");
        setField();
        ticketDAO.saveTicket(ticket);
    }

    @AfterAll
    private static void tearDown(){

    }

    public void setField() throws NoSuchFieldException, IllegalAccessException {
        Field reader = InputReaderUtil.class.getDeclaredField("scan");
        reader.setAccessible(true);
        InputReaderUtil input = new InputReaderUtil();
        Scanner scan = new Scanner("1");
        reader.set(input,scan);
    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
       // parkingService.processIncomingVehicle();

        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
        Ticket ticket =  ticketDAO.getTicket("ABCDEF");

        assertEquals(1,ticket.getId());
        assertEquals(false,ticket.getParkingSpot().isAvailable());

    }

   /* @Test
    public void testParkingLotExit(){
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        //TODO: check that the fare generated and out time are populated correctly in the database
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        LocalDateTime outTime = LocalDateTime.now();
        Duration duration = Duration.between(ticket.getInTime(), ticket.getOutTime());
        long minutesToPay = 0;

        if(duration.toMinutes()>30){
            minutesToPay = duration.minusMinutes(30).toMinutes();
        }

        double calculFare = (minutesToPay*(Fare.CAR_RATE_PER_HOUR/60));

        assertEquals(calculFare, ticket.getPrice());
        assertNotNull(ticket.getOutTime());
    }

    @Test
    public void recurringUser(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDEF");
        Ticket ticketdb = ticketDAO.getTicket("ABCDEF");

        if(ticket == ticketdb){
            double price = ticket.getPrice()*0.05;
            ticket.setPrice(price);
            System.out.println("yes");
        }

        System.out.println("no");
        assertEquals(ticket.getPrice(),ticketdb.getPrice());
    }
*/
}
