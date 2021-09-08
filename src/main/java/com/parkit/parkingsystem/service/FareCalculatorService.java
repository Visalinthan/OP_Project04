package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;

public class FareCalculatorService {

    // Fonction qui permet de calculer le prix d'un ticket pour tout type de véhicule
    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        Duration duration = Duration.between(ticket.getInTime(), ticket.getOutTime());

        long minutesToPay = 0;

        TicketDAO ticketDAO = new TicketDAO();

        int countClientPassages = ticketDAO.checkCountByVehicleRegNumber(ticket.getVehicleRegNumber());

        if(duration.toMinutes() > 30){

            minutesToPay = duration.minusMinutes(30).toMinutes();

            switch (ticket.getParkingSpot().getParkingType()){
                case CAR: {
                    if(countClientPassages >= 1){
                        ticket.setPrice((minutesToPay*(Fare.CAR_RATE_PER_HOUR/60))*0.95);
                    }else{
                        ticket.setPrice((minutesToPay*(Fare.CAR_RATE_PER_HOUR/60)));
                    }
                    break;
                }
                case BIKE: {
                    if(countClientPassages >= 1){
                        ticket.setPrice((minutesToPay*(Fare.BIKE_RATE_PER_HOUR/60))*0.95);
                    }else{
                        ticket.setPrice((minutesToPay*(Fare.BIKE_RATE_PER_HOUR/60)));
                    }
                    break;
                }
                default: throw new IllegalArgumentException("Unkown Parking Type");
            }

        }else{
            ticket.setPrice(0);
        }
    }
}