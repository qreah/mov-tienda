package com.tiendaAE;

	
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import com.google.api.client.http.HttpTransport;
	import com.google.api.client.json.JsonFactory;
	import com.google.api.client.json.jackson2.JacksonFactory;
	
	import com.google.api.services.mybusiness.v3.MyBusiness;
	import com.google.api.services.mybusiness.v3.model.Account;
	import com.google.api.services.mybusiness.v3.model.ListAccountsResponse;
	import com.google.api.services.mybusiness.v3.model.ListLocationsResponse;
	import com.google.api.services.mybusiness.v3.model.ListReviewsResponse;
	import com.google.api.services.mybusiness.v3.model.Location;
	import com.google.api.services.mybusiness.v3.model.Review;

	//import com.sun.javaws.Main;

	public class RedTiendas3 {
	
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
		
		public RedTiendas3() {
			// TODO Auto-generated constructor stub
		}
		
			
			
			
			/*
			 * Returns all locations for the specified account.
			 * @param accountName The account for which to return locations.
			 * @returns List A list of all locations for the specified account.
			 */
			public static List<Location> listLocations(String accountName, MyBusiness mybusiness) throws Exception {
			  com.google.api.services.mybusiness.v3.MyBusiness.Accounts.Locations.List locationsList =
			      mybusiness.accounts().locations().list(accountName);
			  //String filter = "location=MADRID";
			  //locationsList.setFilter(filter);
			  
			  //System.out.println("Número de tiendas (lo último): " + locationsList.size());
			  ListLocationsResponse responses = locationsList.execute();  
			 
			  List<Location> locations = responses.getLocations();
			//locations.batchGet(accountName, getLocationsRequest);
			locations = responses.getLocations(); //borrar si no estás probando
			  // Poner marca (*) aquí para no traerte toda la info
			  
				if (responses != null) {
			    while (responses.getNextPageToken() != null)
			    	   {
			    	    locationsList.setPageToken(responses.getNextPageToken());
			    		System.out.println("Pasa " + responses.getNextPageToken());
			    	    responses = locationsList.execute();
			    	    locations.addAll(responses.getLocations());
			    	    
			    	   }                                                                                                                                                                                                                                                    
			    
			  } else {
			    System.out.printf("Account '%s' has no locations.", accountName);
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
			  //int tamaño = reviewsList.size();
			  //System.out.println("Tamaño= " + tamaño);
			  ListReviewsResponse response = reviewsList.execute();
			  List<Review> reviews = response.getReviews();			 
			  return reviews;
			}
			
			
			
			
			public static StringBuilder GestionValoraciones(List<Location> tiendas, MyBusiness mybusiness, Connection conn) {
				
				StringBuilder sb = new StringBuilder();
				
				
					
				  try {
					  
					  Statement secuencia = conn.createStatement(); 
					  
				
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
				  int t = 0;
				  
				  System.out.println("Cuántas tiendas hay: " + tiendas.size());
				  for (int i = 1; i < tiendas.size(); i++) {
					  String tienda_i = tiendas.get(i).getName();	
					  
					  
					  try {
						 
					  List<Review> valoraciones = listReviews(tienda_i, mybusiness);
					  
					  	
					 	
					  	
					  for (int j = 0; j < valoraciones.size(); j++) {
						  
						  contOK = contOK + 1;
						  System.out.println("contOK: " + contOK);
						  System.out.println("j: " + j);
						  System.out.println("i: " + i);
						  
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
						  
						  
						  
							  SQL = "INSERT INTO gmb_db.Valoraciones (Ciudad, Dirección, Localización, Identificación, Comentarios, Valoración, Fechacreación, Fechaactuación) VALUES ('" 
								  + ciudad + "', '" + direccion + "', '" + localizacion 
								  + "', '" + ReviewId + "', '" + Comment + "', '" + StarRating + "', '"  
								  + CreateTime + "', '" + UpdateTime + "')";
							  
						  secuencia.executeUpdate(SQL);
						  
						  
						flagError = "0";
					  
					  }
					  } catch (Exception e) {
						  
						// Si llega aqui solo puede ser porque no hay valoraciones, 
						// entonces relleno todo a cero 
						  
						  
						  
						  
						  String ciudad = tiendas.get(i).getAddress().getLocality().replace(";", "").replace("'", "");
						  
						  
						  
						  sb.append(ciudad);
						  sb.append("; ");
						  
						  String Debugger = "INSERT INTO gmb_db.Debugger (Mensaje, Descripción) VALUES ('" + ciudad + "', '" + contError + "')";
						  System.out.println("Debugger: " + Debugger);
						  secuencia.executeUpdate(Debugger);
						  System.out.println("Dirección: " + tiendas.get(i).getAddress().getAddressLines());
						  String direccion ="";
						  if (tiendas.get(i).getAddress().getAddressLines()==null) {
							  
						  }
						  else {
							  direccion = tiendas.get(i).getAddress().getAddressLines().toString().replace(";", "").replace("'", "");
						  }
						  try {sb.append(direccion);} catch (Exception e2) {sb.append("");}
						  
						  sb.append("; ");
						  
						  
						  
						  String localizacion = tiendas.get(i).getName().replace(";", "").replace("'", "");
						  sb.append(localizacion);
						  sb.append("; ");
						  sb.append("");
				
						  
						  
						  
						  try {sb.append(tiendas.get(i).getPrimaryPhone().replace(";", ""));} catch (Exception e3) {sb.append("");}
						  sb.append("; ");
						  sb.append("");
						  sb.append("; ");
						  sb.append("");
						  sb.append("; ");
						  sb.append("");
						  sb.append("; ");
						  sb.append("");
						  sb.append("; ");
						  sb.append("");
						  sb.append('\n');
						  
						  
						  
						  
						  
						  SQL = "INSERT INTO gmb_db.Valoraciones (Ciudad, Dirección, Localización, Identificación, Comentarios, Valoración, Fechacreación, Fechaactuación) VALUES ('" 
								  + ciudad + "', '" + direccion + "', '" + localizacion 
								  + "', '', '', '', '', '')";
						  System.out.println("SQL: " + SQL);
						  secuencia.executeUpdate(SQL);
						  flagError = "1";
						  contError = contError +1;
						  
						  }
					  }
				  System.out.println("Flag: " + flagError);
				  System.out.println("Número de valoraciones: " + t);
				  System.out.println("contError: " + contError);
				  System.out.println("contOK: " + contOK);
				  System.out.println("SQL: " + SQL);
				  
				  }  catch (Exception e) {
					  
				  System.out.println("Ha salido por un problema");
				  System.out.println("SQL: " + SQL);}
				  
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
		
						
			public static void main(String[] args) throws Exception {
				
				//ExtraerValoraciones();
			  
			  }
	
	
}
