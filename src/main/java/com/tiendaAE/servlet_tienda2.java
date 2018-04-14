package com.tiendaAE;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class servlet_tienda
 */
//@WebServlet("/servlet_tienda")
public class servlet_tienda2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection conn;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servlet_tienda2() {
        super();
        // TODO Auto-generated constructor stub
    }

    
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		RedTiendas tiendas = new RedTiendas();
		String codigo = request.getParameter("codigo");
		qhrea Q = new qhrea();
		
		if (codigo.equals("34RT6")) {
			//Se ejecuta clase tienda
			String url;
		      
		      Properties properties = new Properties();
		      try {
		        properties.load(
		            getServletContext().getResourceAsStream("/WEB-INF/classes/config.properties"));
		        
		        url = properties.getProperty("sqlUrl");
		      } catch (IOException e) {
		        log("no property", e);  // Servlet Init should never fail.
		        return;
		      }

		      log("connecting to: " + url);
		      try {
		        Class.forName("com.mysql.cj.jdbc.Driver");
		        conn = DriverManager.getConnection(url);
		        Q.enviarMail("qhrear@gmail.com", "Se ha conectado la BD", "");
				  
		      } catch (ClassNotFoundException e) {
		        throw new ServletException("Error loading JDBC Driver", e);
		      } catch (SQLException e) {
		    	  
		        throw new ServletException("Unable to connect to PostGre", e);
		      }

		   
		
			try {
				response.setHeader("Content-Disposition",
	                     "attachment;filename=valoraciones.csv");
				//StringBuilder sb = tiendas.ObtenerValoraciones(conn);
				
				StringBuilder sb = tiendas.listarTodasValoraciones(conn);
				
				out.println(sb.toString());
				conn.close();
				Q.enviarMail("qhrear@gmail.com", "Se ha cerrado la conexión", "");
				
				
			} catch (Exception e) {
				out.println("Error");
				e.printStackTrace();
				
			}
		 
		} else {
			out.println("El código es incorrecto");
			
			
		}
	}
	
	

}
