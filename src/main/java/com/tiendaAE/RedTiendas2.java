package com.tiendaAE;

	
	import java.io.*; 
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.Cookie;

import org.mortbay.jetty.Main;

	import com.google.api.client.auth.oauth2.Credential;
	import com.google.api.client.auth.oauth2.TokenResponse;
	import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
	import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
	import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
	import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
	import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
	import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
	import com.google.api.client.http.HttpTransport;
	import com.google.api.client.json.JsonFactory;
	import com.google.api.client.json.jackson2.JacksonFactory;
	//import com.google.api.client.util.store.FileDataStoreFactory;
	import com.google.api.services.mybusiness.v3.MyBusiness;
	import com.google.api.services.mybusiness.v3.model.Account;
	import com.google.api.services.mybusiness.v3.model.ListAccountsResponse;
	import com.google.api.services.mybusiness.v3.model.ListLocationsResponse;
	import com.google.api.services.mybusiness.v3.model.ListReviewsResponse;
	import com.google.api.services.mybusiness.v3.model.Location;
	import com.google.api.services.mybusiness.v3.model.Review;

	//import com.sun.javaws.Main;

	public class RedTiendas2 {
	
		Connection conn;
		//private static final java.io.File DATA_STORE_DIR = new java.io.File("/home/rafa/we");
		private static final String APPLICATION_NAME = "Google My Business";
		//private static FileDataStoreFactory dataStoreFactory;
		private static HttpTransport httpTransport;
		protected Writer W = new StringWriter();
		//private static final JsonFactory JSON_FACTORY = JacksonFactory();
		static JsonFactory JSON_FACTORY = new JacksonFactory();
		private static MyBusiness mybusiness;
		
		public RedTiendas2() {
			// TODO Auto-generated constructor stub
		}
		
			/**
			 * Demonstrates the authentication flow to use
			 * with the Google My Business API Java client library.
			 * @return AuthorizationCodeInstalledApp
			 */
			private static Credential authorize() throws Exception {
			  // Creates an InputStream to hold the client ID and secret.
			  InputStream secrets = RedTiendas2.class.getResourceAsStream("client_secrets.json");

			  // Prompts the user if no credential is found.
			  if (secrets == null) {
			    System.out.println(
			      "Enter Client ID and Secret from Google API Console "
			        + "into google-my-business-api-sample/src/main/resources/client_secrets.json");
			    System.exit(1);
			  }

			  // Uses the InputStream to create an instance of GoogleClientSecrets.
			  GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
			      new InputStreamReader(secrets));
			  if (clientSecrets.getDetails().getClientId().startsWith("Enter")
			      || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
			    System.out.println(
			        "Enter Client ID and Secret from Google API Console "
			            + "into google-my-business-api-sample/src/main/resources/client_secrets.json");
			    System.exit(1);
			  }

			  // Sets up the authorization code flow.
			  GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
			      httpTransport, JSON_FACTORY, clientSecrets,
			      Collections.singleton("https://www.googleapis.com/auth/plus.business.manage"))
			      //.setDataStoreFactory(dataStoreFactory)
			      .build();
			  // Returns the credential.
			  return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
			}
			
			public static void Identificacion_GMB() throws Exception {
				
				  httpTransport = GoogleNetHttpTransport.newTrustedTransport();
				  String refreshToken = "1/xcMBF3Aw-0fsZGloJ4xGdnCzXxm0u-uhi1j_56akUkQ";
				
				  //Leer el secrets 
				  InputStream in = RedTiendas2.class.getResourceAsStream("/client_secrets.json");
				  BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			        StringBuilder out = new StringBuilder();
			        String line;
			        while ((line = reader.readLine()) != null) {
			            out.append(line);
			        }
			        System.out.println(out.toString());   //Prints the string content read from input stream
			        reader.close();
			        
				  InputStream secrets = RedTiendas2.class.getResourceAsStream("/client_secrets.json");
				  System.out.println(secrets.toString());
				  GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
					      new InputStreamReader(secrets));
				  
				  JSON_FACTORY = JacksonFactory.getDefaultInstance();
			      TokenResponse tokenResponse = new TokenResponse();
			      tokenResponse.setRefreshToken(refreshToken);
			      tokenResponse.getAccessToken();
			      
			      GoogleCredential credential = new GoogleCredential.Builder()
			                .setTransport(httpTransport)
			                .setJsonFactory(JSON_FACTORY)
			                .setClientSecrets(clientSecrets.getDetails().getClientId(), clientSecrets.getDetails().getClientSecret())
			                .build()
			                .setFromTokenResponse(tokenResponse);
			      
				  		  
				  // Calls Mybusiness.Builder to create a new instance named 'mybusiness'.
				  mybusiness = new MyBusiness.Builder(httpTransport, JSON_FACTORY, credential)
				      .setApplicationName("App_Rafa").build();
				}
			
			
			/*
			 * Returns all locations for the specified account.
			 * @param accountName The account for which to return locations.
			 * @returns List A list of all locations for the specified account.
			 */
			public static List<Location> listLocations(String accountName) throws Exception {
			  com.google.api.services.mybusiness.v3.MyBusiness.Accounts.Locations.List locationsList =
			      mybusiness.accounts().locations().list(accountName);
			  ListLocationsResponse responses = locationsList.execute();  
			  List<Location> locations = responses.getLocations();
			
			locations = responses.getLocations(); //borrar si no estás probando
			  /*
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
			  */
			  //System.out.println("Numero de tiendas " + locations.size());
			   
			   
			  
			  return locations;
			}

			
			/**
			 * Returns a list of reviews.
			 * @param locationName Name of the location to retrieve reviews for.
			 * @return List A list of reviews.
			 * @throws Exception
			 */
			public static List<Review> listReviews(String locationName) throws Exception {
			  
			  MyBusiness.Accounts.Locations.Reviews.List reviewsList = 
			  mybusiness.accounts().locations().reviews().list(locationName);
			  ListReviewsResponse response = reviewsList.execute();
			  List<Review> reviews = response.getReviews();
			  //System.out.println("Reviews= " + reviews.toString());
			 
			  return reviews;
			}
			
			
			
			
			public static StringBuilder GestionValoraciones(List<Location> tiendas) {
				
				StringBuilder sb = new StringBuilder();
			  	  
				  //Gestionar la lista reviews
				  sb.append("Ciudad; ");
				  sb.append("Direccion; ");
				  sb.append("Localizacion; ");
				  sb.append("Identificador; ");
				  sb.append("Comentarios; ");
				  sb.append("Valoracion; ");
				  sb.append("Fecha de creacion; ");
				  sb.append("Fecha de actuacion; ");
				  sb.append('\n');
				  
				  for (int i = 1; i < tiendas.size(); i++) {
					  String tienda_i = tiendas.get(i).getName();	
					  
					  try {
					  List<Review> valoraciones = listReviews(tienda_i);
					  
					  for (int j = 0; j < valoraciones.size(); j++) {
						  //System.out.println("Valoraci�n= " + valoraciones.get(j).getStarRating());
						  //System.out.println("reviews= " + valoraciones.get(j));
						  
						  //List<String> telefonosAdicionales = tiendas.get(i).getAdditionalPhones();
						  String ciudad = tiendas.get(i).getAddress().getLocality().replace(";", "");
						  //System.out.println("Ciudad= " + ciudad);
						  String direccion = tiendas.get(i).getAddress().getAddressLines().toString().replace(";", "");
						  //System.out.println("Direcci�n= " + direccion);
						  String localizacion = tiendas.get(i).getName().replace(";", "");
						  //System.out.println("Localizaci�n= " + localizacion);
						  String ReviewId = valoraciones.get(j).getReviewId().replace(";", "");
						  //System.out.println("ReviewId= " + ReviewId);
						  String Comment = valoraciones.get(j).getComment().replaceAll("\n", "");
						  //System.out.println("Comment= " + Comment);
						  String StarRating = valoraciones.get(j).getStarRating().replace(";", "");
						  //System.out.println("StarRating= " + StarRating);
						  /*switch (StarRating) {
							  case "ONE": StarRating = "1";
							  break;
							  case "TWO": StarRating = "2";
							  break;
							  case "THREE": StarRating = "3";
							  break;
							  case "FOUR": StarRating = "4";
							  break;
							  case "FIVE": StarRating = "5";
							  break;
						  }*/
						  String CreateTime = valoraciones.get(j).getCreateTime().replace(";", "");
						  String UpdateTime = valoraciones.get(j).getUpdateTime().replace(";", "");
						  				  
						  sb.append(ciudad);
						  
						  sb.append("; ");
						  sb.append(direccion);
						  sb.append("; ");
						  sb.append(localizacion);
						  sb.append("; ");
						  if (ReviewId != null) {sb.append(ReviewId);} else {sb.append("NULL");}
						  sb.append("; ");
						  if (Comment != null) {sb.append(Comment);} else {sb.append("NULL");}
						  sb.append("; ");
						  if (StarRating != null) {sb.append(StarRating);} else {sb.append("NULL");}
						  sb.append("; ");
						  if (CreateTime != null) {sb.append(CreateTime);} else {sb.append("NULL");}
						  sb.append("; ");
						  if (UpdateTime != null) {sb.append(UpdateTime);} else {sb.append("NULL");}
						  sb.append('\n');
						  
					  
					  }
					  } catch (Exception e) {
						  
							  // Si llega aqui solo puede ser porque no hay valoraciones, 
						      // entonces relleno todo a cero 
						  //System.out.println(e.toString());
						  sb.append(tiendas.get(i).getAddress().getLocality().replace(";", ""));
						  sb.append("; ");
						  try {sb.append(tiendas.get(i).getAddress().getAddressLines().toString().replace(";", ""));} catch (Exception e2) {sb.append("null");}
						  sb.append("; ");
						  sb.append(tiendas.get(i).getName().replace(";", ""));
						  sb.append("; ");
						  sb.append("null");
						  try {sb.append(tiendas.get(i).getPrimaryPhone().replace(";", ""));} catch (Exception e3) {sb.append("null");}
						  sb.append("; ");
						  sb.append("null");
						  sb.append("; ");
						  sb.append("null");
						  sb.append("; ");
						  sb.append("null");
						  sb.append("; ");
						  sb.append("null");
						  sb.append("; ");
						  sb.append("null");
						  sb.append('\n');
						  
						  }
					  }
				  
				  return sb;
			}
			

		
		public StringBuilder ObtenerValoraciones() throws Exception {
			Identificacion_GMB(); 
			MyBusiness.Accounts.List accountsList = mybusiness.accounts().list();
			ListAccountsResponse response = accountsList.execute();
			List<Account> accounts = response.getAccounts();
			System.out.println(accounts.toString()); 
			List<Location> tiendas = listLocations("accounts/105070584962891673973");
			  
			return GestionValoraciones(tiendas);
		}
		
						
			public static void main(String[] args) throws Exception {
				
				//ExtraerValoraciones();
			  
			  }
	
	
}
