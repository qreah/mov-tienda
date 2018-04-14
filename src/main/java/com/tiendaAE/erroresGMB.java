package com.tiendaAE;

import java.sql.Connection;
import java.sql.SQLException;

import com.google.api.services.mybusiness.v4.model.Location;

public class erroresGMB {

	private BaseDatos BD = new BaseDatos();
	//private qhrea Qhrea = new qhrea();
	
	public String esNull(String TipoCampo, Location tienda, Connection conn) throws SQLException {
		
		try {
		
		if (TipoCampo.equals("Ciudad")) {
			
			return tienda.getAddress().getLocality();
		}
		if (TipoCampo.equals("Dirección")) {
			return tienda.getAddress().getAddressLines().toString(); 
		}
		if (TipoCampo.equals("Localización")) {
			
			return tienda.getName();
		}
		
		return "";
		
		} catch (Exception e) {
			
			String Mensaje = "Falta el dato básico: " + TipoCampo;
			String SQL = "INSERT INTO gmb_db.Errores (Fecha, Identificador, Mensaje) VALUES (NOW(), '" + tienda.getName() + "', '" + Mensaje + "')";
			System.out.println("SQL: " + SQL);
			BD.EjecutarSQL(SQL, conn);
			
			
			return "";
			
		}
	}
	
	
	
}
