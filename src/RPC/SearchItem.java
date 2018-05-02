package RPC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search") /*
                        * This will have a corresponding URL portion*; used for mapping; automated by
                        * TomCat
                        */
public class SearchItem extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */

    /* If the HTTP request has a Get request, this function will be called */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        // request is input; response is output
        // mandatory input
        double lat = Double.parseDouble(request.getParameter("lat"));
        double lon = Double.parseDouble(request.getParameter("lon"));
        Set<String> favItemIds = new HashSet<>();
        /*
         * TODO: find the userId for future uses, allow anonymous person to search in
         * the meanwhile
         */
        String userId = request.getParameter("user_id");
        if (userId != null) {
            DBConnection conn = DBConnectionFactory.getConnection();
            if (conn != null) {
                favItemIds = conn.getFavoriteItemIds(userId);
            }
        }

        // term can be empty
        String keyword = request.getParameter("term");

        DBConnection connection = DBConnectionFactory.getConnection();
        List<Item> items = connection.searchItems(lat, lon, keyword);
        connection.close();

        List<JSONObject> list = new ArrayList<>();

        try {
            for (Item item : items) {
                /*
                 * TODO: Check against the SQL to see if the item is already in the user's fav
                 * list.
                 */
                JSONObject obj = item.toJSONObject();
                if (!favItemIds.isEmpty()) {
                    obj.put("favorite", favItemIds.contains(item.getItemId()));
                }
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONArray array = new JSONArray(list);
        RpcHelper.writeJsonArray(response, array);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */

    /* If the HTTP request has a Post request, this function will be called */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
