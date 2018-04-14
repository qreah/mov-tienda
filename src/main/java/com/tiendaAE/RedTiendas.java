package com.tiendaAE;

	
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.mybusiness.v4.MyBusiness;
import com.google.api.services.mybusiness.v4.model.Account;
import com.google.api.services.mybusiness.v4.model.ListAccountsResponse;
import com.google.api.services.mybusiness.v4.model.ListLocationsResponse;
import com.google.api.services.mybusiness.v4.model.ListReviewsResponse;
import com.google.api.services.mybusiness.v4.model.Location;
import com.google.api.services.mybusiness.v4.model.Review;
	
public class RedTiendas {
	
		Connection conn;
		//private static final java.io.File DATA_STORE_DIR = new java.io.File("/home/rafa/we");
		private static final String APPLICATION_NAME = "Google My Business";
		//private static FileDataStoreFactory dataStoreFactory;
		private static HttpTransport httpTransport;
		protected Writer W = new StringWriter();
		//private static final JsonFactory JSON_FACTORY = JacksonFactory();
		static JsonFactory JSON_FACTORY = new JacksonFactory();
		private static MyBusiness mybusiness;
		private static String flagError = "1";
		private static int contError = 0;
		private static int contOK = 0;
		private static String SQL = "";
		private static String SQLold = "";
		static qhrea Q = new qhrea();
		
	public RedTiendas() {
			
	}
		
			
			
			
			/*
			 * Returns all locations for the specified account.
			 * @param accountName The account for which to return locations.
			 * @returns List A list of all locations for the specified account.
			 */
			public static List<Location> listLocations(String accountName, MyBusiness mybusiness) throws Exception {
			  com.google.api.services.mybusiness.v4.MyBusiness.Accounts.Locations.List locationsList =
			      mybusiness.accounts().locations().list(accountName);
			  //String filter = "location=MADRID";
			  //locationsList.setFilter(filter);
			  
			  //System.out.println("Número de tiendas (lo último): " + locationsList.size());
			  ListLocationsResponse responses = locationsList.execute();  
			 
			  List<Location> locations = responses.getLocations();
			//locations.batchGet(accountName, getLocationsRequest);
			locations = responses.getLocations(); //borrar si no estás probando
			  // Poner marca (*) aquí para no traerte toda la info
			  
				while (responses.getNextPageToken() != null)
			    	   {
			    	    locationsList.setPageToken(responses.getNextPageToken());
			    		System.out.println("Pasa " + responses.getNextPageToken());
			    	    responses = locationsList.execute();
			    	    locations.addAll(responses.getLocations());
			    	    
			    	   } 
			  
			  // Poner marca aaquí (*) para no traerte toda la info
			  
			  
			  return locations;
			}

			
			/**
			 * Returns a list of reviews.
			 * @param locationName Name of the location to retrieve reviews for.
			 * @return List A list of reviews.
			 * @throws Exception
			 */
	public static List<Review> listReviews(String locationName, MyBusiness mybusiness) throws Exception {
			  
			  MyBusiness.Accounts.Locations.Reviews.List reviewsList = 
			  mybusiness.accounts().locations().reviews().list(locationName);
			  ListReviewsResponse response = reviewsList.execute();
			  List<Review> reviews = response.getReviews();			 
			  return reviews;
	}
			
					
			
	public static StringBuilder buildString2CSV(List<Location> tiendas, MyBusiness mybusiness, Connection conn) throws Exception {
				
		StringBuilder sb = new StringBuilder();
		sb.append("Ciudad; ");
		sb.append("Dirección; ");
		sb.append("Localización; ");
		sb.append("Identificador; ");
		sb.append("Comentarios; ");
		sb.append("Valoración; ");
		sb.append("Fecha de creación; ");
		sb.append("Fecha de actuación; ");
		sb.append('\n');
				  
		for (int i = 1; i < tiendas.size(); i++) {
			String tienda_i = tiendas.get(i).getName();	
			List<Review> valoraciones = listReviews(tienda_i, mybusiness);
			for (int j = 0; j < valoraciones.size(); j++) {
				String ciudad = tiendas.get(i).getAddress().getLocality().replace(";", "").replace("'", "");
				String direccion = tiendas.get(i).getAddress().getAddressLines().toString().replace(";", "").replace("'", "");
				String localizacion = tiendas.get(i).getName().replace(";", "").replace("'", "");
				String ReviewId = valoraciones.get(j).getReviewId().replace(";", "").replace("'", "");
				String Comment = valoraciones.get(j).getComment().replaceAll("\n", "").replace("'", "");
				String StarRating = valoraciones.get(j).getStarRating().replace(";", "").replace("'", "");
				String CreateTime = valoraciones.get(j).getCreateTime().replace(";", "").replace("'", "");
				String UpdateTime = valoraciones.get(j).getUpdateTime().replace(";", "").replace("'", "");
						  
				sb.append(ciudad);
				sb.append("; ");
				sb.append(direccion);
				sb.append("; ");
				sb.append(localizacion);
				sb.append("; ");
				if (ReviewId != null) {sb.append(ReviewId);} else {sb.append(""); ReviewId = "";}
				sb.append("; ");
				if (Comment != null) {sb.append(Comment);} else {sb.append(""); Comment = "";}
				sb.append("; ");
				if (StarRating != null) {sb.append(StarRating);} else {sb.append(""); StarRating = "";}
				sb.append("; ");
				if (CreateTime != null) {sb.append(CreateTime);} else {sb.append(""); CreateTime = "";}
				sb.append("; ");
				if (UpdateTime != null) {sb.append(UpdateTime);} else {sb.append(""); UpdateTime = "";}
				sb.append('\n');
			}
				
		}
				 
		return sb;
	}
	
	public static void loadReviews2DB(List<Location> tiendas, MyBusiness mybusiness, Connection conn) {
		
		StringBuilder sb = new StringBuilder();
		erroresGMB errores = new erroresGMB();
		try {
			Statement secuencia = conn.createStatement(); 
			sb.append("Ciudad; ");
			sb.append("Dirección; ");
			sb.append("Localización; ");
			sb.append("Identificador; ");
			sb.append("Comentarios; ");
			sb.append("Valoración; ");
			sb.append("Fecha de creación; ");
			sb.append("Fecha de actuación; ");
			sb.append('\n');
		  
			for (int i = 1; i < tiendas.size(); i++) {
			  String tienda_i = tiendas.get(i).getName();	
			  try {
				 List<Review> valoraciones = listReviews(tienda_i, mybusiness);
				 for (int j = 0; j < valoraciones.size(); j++) {
				  String ciudad = tiendas.get(i).getAddress().getLocality().replace(";", "").replace("'", "");
				  String direccion = tiendas.get(i).getAddress().getAddressLines().toString().replace(";", "").replace("'", "");
				  String localizacion = tiendas.get(i).getName().replace(";", "").replace("'", "");
				  String ReviewId = valoraciones.get(j).getReviewId().replace(";", "").replace("'", "");
				  String Comment = valoraciones.get(j).getComment().replaceAll("\n", "").replace("'", "");
				  String StarRating = valoraciones.get(j).getStarRating().replace(";", "").replace("'", "");
				  String CreateTime = valoraciones.get(j).getCreateTime().replace(";", "").replace("'", "");
				  String UpdateTime = valoraciones.get(j).getUpdateTime().replace(";", "").replace("'", "");
				  
				  SQL = "INSERT INTO gmb_db.Valoraciones_temp (Ciudad, Dirección, Localización, Identificación, Comentarios, Valoración, Fechacreación, Fechaactuación) VALUES ('" 
				    + ciudad + "', '" + direccion + "', '" + localizacion 
				    + "', '" + ReviewId + "', '" + Comment + "', '" + StarRating + "', '"  
				    + CreateTime + "', '" + UpdateTime + "')";
				  secuencia.executeUpdate(SQL);
				 }
			  } catch (Exception e) {
				  
				// Si llega aqui solo puede ser porque no hay valoraciones, 
				// entonces relleno todo a cero 
				  						  
				  String ciudad = errores.esNull("Ciudad", tiendas.get(i), conn).replace(";", "").replace("'", "");
				  String direccion = errores.esNull("Dirección", tiendas.get(i), conn).toString().replace(";", "").replace("'", "");
				  String localizacion = errores.esNull("Localización", tiendas.get(i), conn).replace(";", "").replace("'", "");
				  
				  SQL = "INSERT INTO gmb_db.Valoraciones_temp (Ciudad, Dirección, Localización, Identificación, Comentarios, Valoración, Fechacreación, Fechaactuación) VALUES ('" 
						  + ciudad + "', '" + direccion + "', '" + localizacion 
						  + "', '', '', '', '', '')";
				  secuencia.executeUpdate(SQL);
				  
				  }
			  }
		  
		  SQL = "delete FROM gmb_db.Valoraciones_temp where Identificación = ''";
		  secuencia.executeUpdate(SQL);
		  
		  
		  SQL = "INSERT INTO gmb_db.Valoraciones (Ciudad, Dirección, Localización, Identificación, Comentarios, Valoración, Fechacreación, Fechaactuación) "
		  		+ "SELECT * FROM gmb_db.Valoraciones_temp "
		  		+ "WHERE (gmb_db.Valoraciones_temp.Identificación  NOT IN (SELECT gmb_db.Valoraciones.Identificación FROM gmb_db.Valoraciones))";
		  secuencia.executeUpdate(SQL);
		  Q.enviarMail("qhrear@gmail.com", "Actualiza gmb_db.Valoraciones de gmb_db.Valoraciones_temp.Identificación", SQL);
		  
		  SQL = "delete FROM gmb_db.Valoraciones_temp;";
		  secuencia.executeUpdate(SQL);
		  Q.enviarMail("qhrear@gmail.com", "Se borra gmb_db.Valoraciones_temp", "OK");	  
		  
		  
		  }  catch (Exception e) {
		  Q.enviarMail("qhrear@gmail.com", "Ha habido un problema", SQL + " Error: " + e);
		  System.out.println("Ha habido un problema");
		  System.out.println(e);
		  }

	}
	
	public static StringBuilder GestionValoraciones(List<Location> tiendas, MyBusiness mybusiness, Connection conn) {
		
		StringBuilder sb = new StringBuilder();
		erroresGMB errores = new erroresGMB();
		
			
		  try {
			  
			  Statement secuencia = conn.createStatement(); 
			  //SQL = "delete FROM gmb_db.Valoraciones_temp;";
			  //secuencia.executeUpdate(SQL);
			  //Q.enviarMail("qhrear@gmail.com", "Se borra gmb_db.Valoraciones_temp", "OK");
		
		  //Gestionar la lista reviews
		  sb.append("Ciudad; ");
		  sb.append("Dirección; ");
		  sb.append("Localización; ");
		  sb.append("Identificador; ");
		  sb.append("Comentarios; ");
		  sb.append("Valoración; ");
		  sb.append("Fecha de creación; ");
		  sb.append("Fecha de actuación; ");
		  sb.append('\n');
		  
		  System.out.println("Cuántas tiendas hay: " + tiendas.size());
		  for (int i = 1; i < tiendas.size(); i++) {
			  String tienda_i = tiendas.get(i).getName();	
			  
			  
			  try {
				 
			  List<Review> valoraciones = listReviews(tienda_i, mybusiness);
			  
			  for (int j = 0; j < valoraciones.size(); j++) {
				  
				  
				  String ciudad = tiendas.get(i).getAddress().getLocality().replace(";", "").replace("'", "");
				  
				  String direccion = tiendas.get(i).getAddress().getAddressLines().toString().replace(";", "").replace("'", "");
				  String localizacion = tiendas.get(i).getName().replace(";", "").replace("'", "");
				  String ReviewId = valoraciones.get(j).getReviewId().replace(";", "").replace("'", "");
				  String Comment = valoraciones.get(j).getComment().replaceAll("\n", "").replace("'", "");
				  String StarRating = valoraciones.get(j).getStarRating().replace(";", "").replace("'", "");
				  String CreateTime = valoraciones.get(j).getCreateTime().replace(";", "").replace("'", "");
				  String UpdateTime = valoraciones.get(j).getUpdateTime().replace(";", "").replace("'", "");
				  
				  sb.append(ciudad);
				  sb.append("; ");
				  sb.append(direccion);
				  sb.append("; ");
				  sb.append(localizacion);
				  sb.append("; ");
				  if (ReviewId != null) {sb.append(ReviewId);} else {sb.append(""); ReviewId = "";}
				  sb.append("; ");
				  if (Comment != null) {sb.append(Comment);} else {sb.append(""); Comment = "";}
				  sb.append("; ");
				  if (StarRating != null) {sb.append(StarRating);} else {sb.append(""); StarRating = "";}
				  sb.append("; ");
				  if (CreateTime != null) {sb.append(CreateTime);} else {sb.append(""); CreateTime = "";}
				  sb.append("; ");
				  if (UpdateTime != null) {sb.append(UpdateTime);} else {sb.append(""); UpdateTime = "";}
				  sb.append('\n');
				  
				  
				  
					  SQL = "INSERT INTO gmb_db.Valoraciones_temp (Ciudad, Dirección, Localización, Identificación, Comentarios, Valoración, Fechacreación, Fechaactuación) VALUES ('" 
						  + ciudad + "', '" + direccion + "', '" + localizacion 
						  + "', '" + ReviewId + "', '" + Comment + "', '" + StarRating + "', '"  
						  + CreateTime + "', '" + UpdateTime + "')";
					  
				  secuencia.executeUpdate(SQL);
				  
				
			  
			  }
			  } catch (Exception e) {
				  
				// Si llega aqui solo puede ser porque no hay valoraciones, 
				// entonces relleno todo a cero 
				  						  
				  String ciudad = errores.esNull("Ciudad", tiendas.get(i), conn).replace(";", "").replace("'", "");
				  String direccion = errores.esNull("Dirección", tiendas.get(i), conn).toString().replace(";", "").replace("'", "");
				  String localizacion = errores.esNull("Localización", tiendas.get(i), conn).replace(";", "").replace("'", "");
				  
				  SQL = "INSERT INTO gmb_db.Valoraciones_temp (Ciudad, Dirección, Localización, Identificación, Comentarios, Valoración, Fechacreación, Fechaactuación) VALUES ('" 
						  + ciudad + "', '" + direccion + "', '" + localizacion 
						  + "', '', '', '', '', '')";
				  secuencia.executeUpdate(SQL);
				  
				  }
			  }
		  
		  SQL = "delete FROM gmb_db.Valoraciones_temp where Identificación = ''";
		  secuencia.executeUpdate(SQL);
		  
		  
		  SQL = "INSERT INTO gmb_db.Valoraciones (Ciudad, Dirección, Localización, Identificación, Comentarios, Valoración, Fechacreación, Fechaactuación) "
		  		+ "SELECT * FROM gmb_db.Valoraciones_temp "
		  		+ "WHERE (gmb_db.Valoraciones_temp.Identificación  NOT IN (SELECT gmb_db.Valoraciones.Identificación FROM gmb_db.Valoraciones))";
		  secuencia.executeUpdate(SQL);
		  Q.enviarMail("qhrear@gmail.com", "Actualiza gmb_db.Valoraciones de gmb_db.Valoraciones_temp.Identificación", SQL);
		  
		  SQL = "delete FROM gmb_db.Valoraciones_temp;";
		  secuencia.executeUpdate(SQL);
		  Q.enviarMail("qhrear@gmail.com", "Se borra gmb_db.Valoraciones_temp", "OK");	  
		  
		  
		  }  catch (Exception e) {
		  Q.enviarMail("qhrear@gmail.com", "Ha habido un problema", SQL + " Error: " + e);
		  System.out.println("Ha habido un problema");
		  System.out.println(e);
		  }
		  
		  Q.enviarMail("qhrear@gmail.com", "Ok. Done", sb.toString());
		  System.out.println("Ok.Done");
		  return sb;
	}
		
		public StringBuilder ObtenerValoraciones(Connection conn) throws Exception {
			
			
			MyBusiness mybusiness = AccesoAPIs.Connect2GMB("/client_secrets.json"); 
			MyBusiness.Accounts.List accountsList = mybusiness.accounts().list();
			ListAccountsResponse response = accountsList.execute();
			List<Account> accounts = response.getAccounts();
			System.out.println(accounts.toString()); 
			List<Location> tiendas = listLocations("accounts/105070584962891673973", mybusiness);
			  
			return GestionValoraciones(tiendas, mybusiness, conn);
		}
		
		
		public StringBuilder listarTodasValoraciones(Connection conn) {
			
			StringBuilder sb = new StringBuilder();
			String selectQuery = "SELECT * FROM gmb_db.Valoraciones";
			String qrea = "";
			try {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(selectQuery);
                int numCols = rs.getMetaData().getColumnCount();
                int z = 0;
                
                
                while(rs.next()) {
                	z = z +1 ;
                	System.out.println("Por aquí: " + z);
                	System.out.println("rs: " + rs.getString(1));
                    for (int i = 1; i <= numCols; i++) {
                    	qrea = rs.getString(i).replace(";","");
                    	sb.append(qrea).append(";");
                    }
                    
                    sb.append('\n');
                }
                
            } catch (Exception e) {
			
            	sb.append(qrea);
            	sb.append(e);
            	sb.append("Existe algún problema con la base de datos");
            	
            }
		
			
			return sb;
			
		}
		
						
			public static void main(String[] args) throws Exception {
				
				//ExtraerValoraciones();
			  
			  }
	
	
}
