package epaw.lab1;

import epaw.lab1.util.DBManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/hello")
public class HelloWorld extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) //This method is called when the client sends a GET request to the server (e.g., when you open the page in the browser with the URL http://localhost:8080/hello)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8"); //Setting the content type of the response to "text/html", so the browser knows to interpret the response as an HTML document. The character encoding used in the response is UTF-8, which is a common encoding that supports a wide range of characters.
        PrintWriter out = response.getWriter(); //For writing the response back to the client, (in HTML format)
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Users List</title></head>");
        out.println("<body>");
        out.println("<h1>Users from Database</h1>");
        out.println("<table border='1'>"); //Table in HTML, with a border of 1 pixel
        /*out.println("<style>");
        out.println("table { border-collapse: collapse; }");
        out.println("td, th { padding: 8px; border: 1px solid black; }");
        out.println("body { font-family: Arial; }");
        out.println("</style>");*/
        out.println("<tr><th>ID</th><th>Name</th><th>Description</th></tr>"); //Header row of the table, with 3 columns: ID, Name and Description

        try (DBManager db = new DBManager()) {
            PreparedStatement stmt = db.prepareStatement("SELECT id, name, description FROM users"); //Creating a prepared statement to execute a SQL query that selects the id, name, and description columns from the users table in the database.
            ResultSet rs = stmt.executeQuery(); //For executing the SQL query and obtaining the results in a ResultSet object 
            
            while (rs.next()) { //Iterating through the ResultSet using a while loop, which continues as long as there are more rows to process. The rs.next() method moves the cursor to the next row and returns true if there is a row to process, or false if there are no more rows.
                int id = rs.getInt("id"); //For obtaining the data from the current row of the ResultSet, it reads the columns from the Database and stores them in variables
                String name = rs.getString("name");
                String description = rs.getString("description");
                
                out.println("<tr>"); //For showing the data in the HTML table.
                out.println("<td>" + id + "</td>");
                out.println("<td>" + name + "</td>");
                out.println("<td>" + description + "</td>");
                out.println("</tr>");
            }
        } catch (Exception e) {
            out.println("<tr><td colspan='3'>Error: " + e.getMessage() + "</td></tr>");
            e.printStackTrace();
        }

        out.println("</table>");

        out.println("<h2>New user</h2>");
        out.println("<form method='POST' action='/hello'>"); //Adding the FORM to the page (allows to send data to the server)
        out.println("<input type='text' name='name' placeholder='Name'>");
        out.println("<input type='text' name='description' placeholder='Description'>");
        out.println("<button type='submit'>Save</button>");

        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) //This method is called when the client sends a POST request to the server (e.g., when you submit the form on the page)
            throws ServletException, IOException {
                String name = request.getParameter("name"); //Takes the data from the form and stores it in variables
                String description = request.getParameter("description");

                try (DBManager db = new DBManager()){
                    PreparedStatement stmt = db.prepareStatement("INSERT INTO users (name, description) VALUES (?, ?)"); //Creating a prepared statement to execute a SQL query that inserts a new row into the users table in the database, with the name and description values provided by the user through the form.
                    stmt.setString(1, name); //Assigning the corresponding values
                    stmt.setString(2, description);
                    stmt.executeUpdate(); //Actually executing the SQL query to insert the new user into the database.
                } catch (Exception e) {
                    e.printStackTrace();
                    
                }
                response.sendRedirect("/hello"); //This is done to refresh the page and show the updated list of users, including the newly added user.
    }
}