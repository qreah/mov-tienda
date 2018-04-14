package com.tiendaAE;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class BaseDatos {
	Connection conn;
	
	public void EjecutarSQL(String SQL, Connection conn) throws SQLException {
		
		Statement secuencia = conn.createStatement(); 
		String CabeceraConsulta = SQL.substring(0, 6);
		if (CabeceraConsulta.equals("INSERT")) {
			secuencia.executeUpdate(SQL);
		}
		if (CabeceraConsulta.equals("SELECT")) {
			secuencia.executeQuery(SQL);
		}
		
	}
	
	public Connection ConectarDB(String PropiedadesConexion, ServletContext contexto) {
		//Método cuyo objetivo es conectarse a una base de datos desde un servlet
		
		String url = "";
	      
	    Properties properties = new Properties();
	    InputStream IS;
	    try {
	        properties.load(contexto.getResourceAsStream(PropiedadesConexion));
	        url = properties.getProperty("sqlUrl");
	    } catch (IOException e) {
	    	System.out.println("No hay configuración del JDBC en el fichero de propiedades");
	        System.out.println(e);
	        
	    }

	    System.out.println("connecting to: " + url);
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        conn = DriverManager.getConnection(url);
	        
			  
	    } catch (ClassNotFoundException e) {
	    	System.out.println("Error loading JDBC Driver");
	        System.out.println(e);
	        
	    } catch (SQLException e) {
	    	System.out.println("Unable to connect to PostGre");
	        System.out.println(e);  
	        
	    }
	    return conn;
	}

}
