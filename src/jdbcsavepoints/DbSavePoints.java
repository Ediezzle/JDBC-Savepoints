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
public class DbSavePoints
{

    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws SQLException
    {
        // TODO code application logic here
        Connection connection = DbConnector.connectToDb();
			ResultSet resultSet = null;
			// we're using explicit finally blocks
			// instead of try-with-resources statement in this code
			try 
			{
				// for commit/rollback we first need to set auto-commit to false
				connection.setAutoCommit(false);
				Statement statement = connection.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE);
				resultSet = statement.executeQuery("SELECT * FROM familyGroup");
				System.out.println("Printing the contents of the table before inserting");
				
				while(resultSet.next()) 
				{
					System.out.println(resultSet.getInt("id") + " "
					+ resultSet.getString("nickName"));
				}
				
				System.out.println("Starting to insert rows");
				// first insert
				resultSet.moveToInsertRow();
				resultSet.updateString("nickName", "Tom");
				resultSet.insertRow();
				System.out.println("Inserted row for Tom");
				// our first savepoint is here. . .
				Savepoint firstSavePoint = connection.setSavepoint();
				
				// second insert
				resultSet.moveToInsertRow();
				resultSet.updateString("nickName", "Dick");
				resultSet.insertRow();
				System.out.println("Inserted row for Dick");
				// our second savepoint is here. . . after we inserted Dick
				// we can give a string name for savepoint
				Savepoint secondSavePoint = connection.setSavepoint("SavepointForDick");
				
				// third insert
				resultSet.moveToInsertRow();
				resultSet.updateString("nickName", "Harry");
				resultSet.insertRow();
				System.out.println("Inserted row for Harry");
				// our third savepoint is here. . . for "Harry"
				Savepoint thirdSavePoint = connection.setSavepoint("ForHarry");
				System.out.println("Table updation complete. . .");
				
				// rollback to the state when Dick was inserted;
				// so the insert for Harry will be lost
				System.out.println("Rolling back to the state where Tom and Dick were inserted");
				connection.rollback(secondSavePoint);
				// commit the changes now and see what happens to the contents of the table
				connection.commit();
				
				System.out.println("Printing the contents of the table after commit");
				resultSet = statement.executeQuery("SELECT * FROM familyGroup");
				
				while(resultSet.next()) 
				{
					System.out.println(resultSet.getInt("id") + " "
					+ resultSet.getString("nickName"));
				}
			} 
			
			catch (SQLException e) 
			{
				System.out.println("Something gone wrong, couldn't add a contact in family group");
				// roll back all the changes in the transaction since something has gone wrong
				connection.rollback();
				e.printStackTrace();
			}
			
			finally 
			{
				if(connection != null) connection.close();
				if(resultSet != null) resultSet.close();
			}
    }
    
}
