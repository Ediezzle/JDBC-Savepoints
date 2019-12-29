/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdbcsavepoints;

import java.sql.*;
/**
 *
 * @author Dev Ops
 */
public class DbConnector
{
    public static Connection connectToDb() throws SQLException 
	{
		String url = "jdbc:mysql://localhost:3306/";
		String database = "addressBook";
		String userName = "root";
		String password = "";
		return DriverManager.getConnection(url + database, userName, password);
	}
    
}
