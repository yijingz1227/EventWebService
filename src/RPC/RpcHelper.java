package RPC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class RpcHelper {

    public static void writeJsonObject(HttpServletResponse response, JSONObject obj) {
        try {
            response.setContentType("application/json");
            response.addHeader("Access-Control-Allow-Origin", "*"); /* For front-end */
            PrintWriter out = response.getWriter();
            out.print(obj);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Writes a JSONArray to http response.
    public static void writeJsonArray(HttpServletResponse response, JSONArray array) {
        try {
            response.setContentType("application/json");
            response.addHeader("Access-Control-Allow-Origin", "*");
            PrintWriter out = response.getWriter();
            out.print(array);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject readJsonObject(HttpServletRequest request) {
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
            reader.close();
            return new JSONObject(jb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
