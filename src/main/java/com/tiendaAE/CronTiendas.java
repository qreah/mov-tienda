package com.tiendaAE;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CronTiendas
 */
//@WebServlet("/CronTiendas")
public class CronTiendas extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection conn;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CronTiendas() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		RedTiendas tiendas = new RedTiendas();
		qhrea Q = new qhrea();
		ServletContext contexto = request.getServletContext();
		String PropiedadesConexion = "/WEB-INF/classes/config.properties";
		BaseDatos DB = new BaseDatos();
		Connection conn = DB.ConectarDB(PropiedadesConexion, contexto);
		
		try {
			tiendas.ObtenerValoraciones(conn);
			Q.enviarMail("rafael.fayosoliver@telefonica.com", "Ha ido bien el Cron", "OK");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Q.enviarMail("rafael.fayosoliver@telefonica.com", "Ha ido mal el Cron", e.toString());
		}
		
	}

	@Override
	  public void init() throws ServletException {

	  }

}
