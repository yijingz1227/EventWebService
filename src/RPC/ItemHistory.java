package RPC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/history")
public class ItemHistory extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ItemHistory() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId = request.getParameter("user_id");

        DBConnection conn = DBConnectionFactory.getConnection();
        Set<Item> items = conn.getFavoriteItems(userId);
        conn.close();

        JSONArray array = new JSONArray();
        for (Item item : items) {
            JSONObject object = item.toJSONObject();
            try {
                object.put("favorite", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(object);
        }
        System.out.println("Query received.");
        RpcHelper.writeJsonArray(response, array);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // input format: {��user_id��: ��1111��, ��favorite��: [��abcd��, ��efgh��]}
        JSONObject input = RpcHelper.readJsonObject(request);
        try {
            String userId = input.getString("user_id");
            JSONArray favorite = input.getJSONArray("favorite");

            List<String> itemIds = new ArrayList<>();
            for (int i = 0; i < favorite.length(); ++i) {
                itemIds.add(favorite.getString(i));
            }

            DBConnection conn = DBConnectionFactory.getConnection();
            conn.setFavoriteItems(userId, itemIds);
            conn.close();

            RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
     */
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // input format: {��user_id��: ��1111��, ��favorite��: [��abcd��, ��efgh��]}
        JSONObject input = RpcHelper.readJsonObject(request);
        try {
            String userId = input.getString("user_id");
            JSONArray favorite = input.getJSONArray("favorite");

            List<String> itemIds = new ArrayList<>();
            for (int i = 0; i < favorite.length(); ++i) {
                itemIds.add(favorite.getString(i));
            }

            DBConnection conn = DBConnectionFactory.getConnection();
            conn.unsetFavoriteItems(userId, itemIds);
            conn.close();

            RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
