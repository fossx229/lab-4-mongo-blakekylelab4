package umm3601.todo;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import spark.Request;
import spark.Response;

public class TodoRequestHandler {

    private final TodoController todoController;

    public TodoRequestHandler(TodoController todoController){
        this.todoController = todoController;
    }
    /**Method called from Server when the 'api/users/:id' endpoint is received.
     * Get a JSON response with a list of all the users in the database.
     *
     * @param req the HTTP request
     * @param res the HTTP response
     * @return one user in JSON formatted string and if it fails it will return text with a different HTTP status code
     */
    public String getTodoJSON(Request req, Response res) {
        res.type("application/json");
        String id = req.params("id");
        String todo;
        try
        {
            todo = todoController.getTodo(id);
        }
        catch (IllegalArgumentException e)
        {
            // This is thrown if the ID doesn't have the appropriate
            // form for a Mongo Object ID.
            // https://docs.mongodb.com/manual/reference/method/ObjectId/
            res.status(400);
            res.body("The requested todo id " + id + " wasn't a legal Mongo Object ID.\n" +
                "See 'https://docs.mongodb.com/manual/reference/method/ObjectId/' for more info.");
            return "";
        }
        if (todo != null)
        {
            return todo;
        }
        else
        {
            res.status(404);
            res.body("The requested todo with id " + id + " was not found");
            return "";
        }
    }



    /**Method called from Server when the 'api/todos' endpoint is received.
     * This handles the request received and the response
     * that will be sent back.
     *@param req the HTTP request
     * @param res the HTTP response
     * @return an array of todos in JSON formatted String
     */
    public String getTodos(Request req, Response res)
    {
        res.type("application/json");
        return todoController.getTodos(req.queryMap().toMap());
    }

    public String getTodoSummary(Request req, Response res) {


        return todoController.getTodoSummary();
    }


    /**Method called from Server when the 'api/users/new'endpoint is recieved.
     * Gets specified user info from request and calls addNewUser helper method
     * to append that info to a document
     *
     * @param req the HTTP request
     * @param res the HTTP response
     * @return a boolean as whether the user was added successfully or not
     */
    public String addNewTodo(Request req, Response res)
    {
        res.type("application/json");
        Object o = JSON.parse(req.body());
        try {
            if(o.getClass().equals(BasicDBObject.class))
            {
                try {
                    BasicDBObject dbO = (BasicDBObject) o;

                    String owner = dbO.getString("owner");
                    //For some reason age is a string right now, caused by angular.
                    //This is a problem and should not be this way but here ya go
                    String category = dbO.getString("category");
                    String body = dbO.getString("body");
                    String status = dbO.getString("status");

                    System.err.println("Adding new todo [owner=" + owner + ", category=" + category + " body=" + body + " status=" + status + ']');
                    return todoController.addNewTodo(owner, status, body, body).toString();
                }
                catch(NullPointerException e)
                {
                    System.err.println("A value was malformed or omitted, new user request failed.");
                    return null;
                }

            }
            else
            {
                System.err.println("Expected BasicDBObject, received " + o.getClass());
                return null;
            }
        }
        catch(RuntimeException ree)
        {
            ree.printStackTrace();
            return null;
        }
    }
}
