import java.sql.*;
import java.util.Vector;

/*
 * This class is handle mysql require.
 * Include create table, Insert, delete, update and select.
 */
public class MysqlHandler {
    Connection connection = null ;
    Statement statement = null ;

    /* Create table */
    /* I think need first create a customer in Person then can create the Poised */
    String createProjectTableSQL = "CREATE TABLE IF NOT EXISTS Project ("
        + " PersonRole ENUM('Architect',"
        + " 'Contractor', 'Customer', 'ProjectManager',"
        + " 'StructuralEngineer') NOT NULL,"
        + " ProjectNumber int(6) ZEROFILL NOT NULL,"
        + " PersonId int(6) UNSIGNED, "
        + " FOREIGN KEY (PersonId) REFERENCES Person(id),"
        + " PRIMARY KEY (ProjectNumber, PersonRole))";

    String createPersonTableSQL = "CREATE TABLE IF NOT EXISTS Person ("
        + " id int(6) ZEROFILL NOT NULL AUTO_INCREMENT,"
        + " FirstName varchar(50),"       // First name
        + " SurName varchar(50) NOT NULL,"     // Surname name.
        + " Telephone varchar(50),"        // Telephone name.
        + " EmailAddress varchar(50),"     // Architect Email Address name.
        + " PhysicalAddress varchar(50),"  // Architect Physical Address name.
        + " PRIMARY KEY (id))";

    String createPoisedTableSQL = "CREATE TABLE IF NOT EXISTS Poised ("
        + " ProjectNumber int(6) ZEROFILL NOT NULL,"  // Project number.
        + " ProjectName varchar(50),"  // Project name.
        + " TypeBuilding ENUM('House', 'Apartment',"
        + " 'Block', 'Store') NOT NULL," // What type of building is being designed?
                                       // E.g. House, apartment block or store, etc.
        + " PhysicalAddress varchar(200),"   // The physical address for the project.
        + " ERFNumber int(6),"         // ERF number.
        + " FeeChargedProject int(6)," // The total fee being charged for the project.
        + " PaidToDate int(6),"        // The total amount paid to date.
        + " Deadline DATE,"          // The total amount paid to date.
        + " PRIMARY KEY (ProjectNumber))";

    String createTriggerSQL = "CREATE TRIGGER IF NOT EXISTS has_customer BEFORE INSERT ON Poised"
        + " FOR EACH ROW"
        + " BEGIN"
        + " IF NOT EXISTS"
        + " (SELECT 1 FROM Project WHERE"
        + " ProjectNumber = new.ProjectNumber and PersonRole = 'Customer' ) THEN"
        + " BEGIN"
        + " SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Project does not have Customer.';"
        + " END;"
        + " END IF;"
        + " END;" ;
        
    String createTriggerPersonSurNameInsertSQL = "CREATE TRIGGER IF NOT EXISTS insert_has_surname BEFORE INSERT ON Person"
        + " FOR EACH ROW"
        + " BEGIN"
        + " IF EXISTS"
        + " (SELECT 1 FROM Person WHERE"
        + " new.SurName = '' ) THEN"
        + " BEGIN"
        + " SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Person does not have SurName.';"
        + " END;"
        + " END IF;"
        + " END;";

    String createTriggerPersonSurNameUpdateSQL = "CREATE TRIGGER IF NOT EXISTS update_has_surname BEFORE UPDATE ON Person"
        + " FOR EACH ROW"
        + " BEGIN"
        + " IF EXISTS"
        + " (SELECT 1 FROM Person WHERE"
        + " new.SurName = '' ) THEN"
        + " BEGIN"
        + " SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Person does not have SurName.';"
        + " END;"
        + " END IF;"
        + " END;" ;

    String createTriggerProjectName = "CREATE TRIGGER IF NOT EXISTS project_name BEFORE INSERT ON Poised"
        + " FOR EACH ROW"
        + " BEGIN"
        + " IF new.ProjectName IS NULL THEN"
        + " BEGIN"
        + " SET new.ProjectName = "
        + " ( SELECT CONCAT ("
        + " new.TypeBuilding"
        + " , ' ', "
        + " (SELECT SurName from Person where"
        + " id = ( select PersonId from Project where ProjectNumber = new.ProjectNumber "
        + " and PersonRole = 'Customer' )"
        + " )));"
        + " SET new.ProjectName ="
        + " ( SELECT CONCAT (new.ProjectName,"
        + "  ' ',"
        + " ( SELECT count(ProjectName) FROM Poised"
        + " WHERE ProjectName LIKE CONCAT ( new.ProjectName , '%') ) ));"
        + " END;"
        + " END IF;"
        + " END;" ;

    String insertPersonSQL = "INSERT INTO Person ( SurName )"
        + " VALUES ( 'Chow' ), ( 'Chan' )";
    String insertProjectSQL = "INSERT INTO Project (PersonRole, ProjectNumber, PersonId) "
        + " VALUES ( 'Customer', 1, 1 ), ( 'Customer', 2, 2 )";
    String insertSQL = "INSERT INTO Poised ( TypeBuilding, ProjectNumber ) VALUES "
        + " ( 'House', 1 ), ( 'House', 2 )";
        
    String selectPerson = "SELECT  id, FirstName, SurName, Telephone, EmailAddress, PhysicalAddress"
        + " FROM Person";

    MysqlHandler () {
        try {
            // Connect to the library_db database, via the jdbc:mysql: channel on localhost (this PC)
            // Use username "otheruser", password "swordfish".
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/PoisePMS?useSSL=false",
                    "otheruser",
                    "swordfish"
                    );
            // Create a direct line to the database for running our queries
            this.statement = this.connection.createStatement();

            this.statement.executeUpdate(this.createPersonTableSQL);
            this.statement.executeUpdate(this.createProjectTableSQL);
            this.statement.executeUpdate(this.createPoisedTableSQL);
            this.statement.executeUpdate(this.createTriggerSQL);
            this.statement.executeUpdate(this.createTriggerProjectName);
            this.statement.executeUpdate(this.createTriggerPersonSurNameInsertSQL);
            this.statement.executeUpdate(this.createTriggerPersonSurNameUpdateSQL);
        } catch (SQLException e) {
            // We only want to catch a SQLException - anything else is off-limits for now.
            e.printStackTrace();
        }
    }

    /* Insert the base five records. */
    void insertIntRecord() {
        try {
            this.statement.executeUpdate(this.insertPersonSQL);
            this.statement.executeUpdate(this.insertProjectSQL);
            this.statement.executeUpdate(this.insertSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    void insertPersonRecord (
            String firstName,
            String surName,
            String telePhone,
            String emailAddress,
            String physicalAddress ) throws SQLException {

        String sqlStr = "INSERT INTO Person ( FirstName, SurName, "
            + "Telephone, EmailAddress, PhysicalAddress ) "
            + "VALUES (" 
            + "'" + firstName + "', "
            + "'" + surName + "', "
            + "'" + telePhone + "', "
            + "'" + emailAddress + "', "
            + "'" + physicalAddress + "' "
            + ")";
        this.statement.executeUpdate(sqlStr);
    }
    
    void deletePerson (String id) throws SQLException {
        String sqlStr = "DELETE FROM Person"
            + " WHERE id ="
            + " '" + id + "'" ;
        this.statement.executeUpdate(sqlStr); 
    }
    
    void updatePersonRecord (
            String id,
            String firstName,
            String surName,
            String telePhone,
            String emailAddress,
            String physicalAddress    
        ) throws SQLException {
        String sqlStr = "UPDATE Person SET"
            + " FirstName = '" + firstName + "',"
            + " SurName = '" + surName + "',"
            + " Telephone = '" + telePhone + "',"
            + " EmailAddress = '" + emailAddress + "',"
            + " PhysicalAddress = '" + physicalAddress + "'"
            + " WHERE id = '" + id + "'";
        this.statement.executeUpdate(sqlStr); 
    }
    
    Vector<Vector<String>> searchPersonRecord(
            String firstName,
            String surName,
            String telePhone,
            String emailAddress,
            String physicalAddress      
        ) {
        return makeRow( "SELECT * FROM Person WHERE"
                    + " FirstName like '%" + firstName + "%'"
                    + " AND SurName like '%" + surName + "%' "
                    + " AND Telephone like '%" + telePhone + "%' "
                    + " AND EmailAddress like '%" + emailAddress + "%' "
                    + " AND PhysicalAddress like '%" + physicalAddress + "%' "
            );
    }
    
    Vector<Vector<String>> selectPersonRecord() {
        return this.makeRow( this.selectPerson );
    }
    
    Vector<Vector<String>> makeRow(String sqlString) {
        Vector<Vector<String>> resultVector = new Vector<Vector<String>>();

        try {
            ResultSet rs = this.statement.executeQuery( sqlString );
            
            while( rs.next() ){
                Vector<String> resultRow = new Vector<>();
                resultRow.add( Integer.toString(rs.getInt("id")) );
                resultRow.add( rs.getString("FirstName") );
                resultRow.add( rs.getString("SurName") );
                resultRow.add( rs.getString("Telephone") );
                resultRow.add( rs.getString("EmailAddress") );
                resultRow.add( rs.getString("PhysicalAddress") );
                resultVector.add(resultRow);
            }
            
        }catch ( SQLException e) {
            e.printStackTrace();
        }
        return resultVector;
    }
    
    public static void main(String[] args) {
        MysqlHandler sqlHandler = new MysqlHandler();
        sqlHandler.insertIntRecord();
        // System.out.println(sqlHandler);
    }
}
