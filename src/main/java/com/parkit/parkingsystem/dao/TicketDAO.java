package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();


    // Fonction pour sauvegarder un ticket dans la base de données
    public boolean saveTicket(Ticket ticket){
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);

            long entry = Timestamp.valueOf(ticket.getInTime()).getTime();

            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            //ps.setInt(1,ticket.getId());
            ps.setInt(1,ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(entry));
            ps.setTimestamp(5, null);
            return ps.execute();
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
            return false;
        }
    }

    // Fonction qui retourne un ticket avec le numéro de véhicule entré dans le parametre
    public Ticket getTicket(String vehicleRegNumber) {
        Connection con = null;
        Ticket ticket = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1,vehicleRegNumber);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4).toLocalDateTime());
                if(rs.getTimestamp(5)!=null) {
                    ticket.setOutTime(rs.getTimestamp(5).toLocalDateTime());
                }
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
            return ticket;
        }
    }

    // Fonction pour mettre à jour un ticket
    public boolean updateTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            long exit = Timestamp.valueOf(ticket.getOutTime()).getTime();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);

            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, new Timestamp(exit));
            ps.setInt(3,ticket.getId());
            ps.execute();
            return true;
        }catch (Exception ex){
            logger.error("Error saving ticket info",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }

    // Fonction pour vérifier si le vehicule a été déjà enregistrer dans la base de données
    public int checkCountByVehicleRegNumber(String vehicleRegNumber)  {
        int result = 0;
        Connection con = null;
        try  {
            con = dataBaseConfig.getConnection();
            final PreparedStatement ps = con.prepareStatement(DBConstants.GET_COUNT);
            ps.setString(1, vehicleRegNumber);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt("countIfExistingClient");
            }
        }catch (Exception ex){
            logger.error("Error counting tickets by Vehicle Reg Number",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        return result;
    }
}
