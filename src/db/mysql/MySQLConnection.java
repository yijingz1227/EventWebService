package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterAPI;

public class MySQLConnection implements DBConnection {
    private Connection conn;

    @Override
    public void close() {
        // TODO close the connection with the data base
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public MySQLConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").getConstructor().newInstance();
            conn = DriverManager.getConnection(MySQLDBUtil.URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setFavoriteItems(String userId, List<String> itemIds) {
        if (conn == null) {
            return;
        }
        String sql = "INSERT IGNORE INTO history (user_id, item_id) VALUES (?,?)";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            for (String itemId : itemIds) {
                statement.setString(2, itemId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void unsetFavoriteItems(String userId, List<String> itemIds) {
        // TODO unset User's Favorite items by deleting entries in history with user id
        // as primary key and corresponding itemIds.

        if (conn == null) {
            return;
        }
        String query = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
        try {
            for (String id : itemIds) {
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, userId);
                stmt.setString(2, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<String> getFavoriteItemIds(String userId) {
        Set<String> ItemIds = new HashSet<>();
        if (conn == null) {
            return ItemIds;
        }
        String sql = "SELECT item_id FROM history WHERE user_id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);

            ResultSet rs = statement.executeQuery();
            /*
             * [ -1 <- rs (iteratorish, next method) {"item_id":"GXXXXXXXXX"}, ]
             */
            while (rs.next()) {
                ItemIds.add(rs.getString("item_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ItemIds;
    }

    @Override
    public Set<Item> getFavoriteItems(String userId) {
        Set<Item> items = new HashSet<>();
        if (conn == null) {
            return items;
        }
        // TODO: 1. get item_id from user_id
        Set<String> itemIds = getFavoriteItemIds(userId);
        String sql = "SELECT * from items WHERE item_id = ?";
        // TODO: 2. execute sql query from every corresponding itemId
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            for (String id : itemIds) {
                statement.setString(1, id);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    ItemBuilder builder = new ItemBuilder();
                    builder.setItemId(rs.getString("item_id"));
                    builder.setName(rs.getString("name"));
                    builder.setRating(rs.getDouble("rating"));
                    builder.setAddress(rs.getString("address"));
                    builder.setImageUrl(rs.getString("image_url"));
                    builder.setUrl(rs.getString("url"));
                    builder.setDistance(rs.getDouble("distance"));
                    builder.setCategories(getCategories(id));

                    items.add(builder.build());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    @Override
    public Set<String> getCategories(String itemId) {
        Set<String> categories = new HashSet<>();
        if (conn == null) {
            return categories;
        }
        String sql = "SELECT category from categories WHERE item_id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, itemId);
            ResultSet rs = statement.executeQuery();
            /*
             * [ -1 <- rs (iteratorish, next method) {"category":"Music"},
             * {"category":"Music"}, {"category":"Sports"}, {"category":"Drama"},
             * {"category":"Limited"} ]
             */
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    @Override
    public List<Item> searchItems(double lat, double lon, String term) {
        TicketMasterAPI tmAPI = new TicketMasterAPI();
        List<Item> items = tmAPI.search(lat, lon, term);
        for (Item item : items) {
            saveItem(item);
        }
        return items;
    }

    @Override
    public void saveItem(Item item) {
        if (conn == null) {
            return;
        }
        try {
            // First, insert into items table; IGNORE to prevent duplicate insertions and
            // following exception cats
            String sql = "INSERT IGNORE INTO items VALUES(?,?,?,?,?,?,?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, item.getItemId());
            statement.setString(2, item.getName());
            statement.setDouble(3, item.getRating());
            statement.setString(4, item.getAddress());
            statement.setString(5, item.getImageUrl());
            statement.setString(6, item.getUrl());
            statement.setDouble(7, item.getDistance());
            statement.executeUpdate();

            // Second, update categories table for each category
            sql = "INSERT IGNORE INTO categories VALUES(?,?)";
            PreparedStatement statement2 = conn.prepareStatement(sql);
            statement2.setString(1, item.getItemId());
            Set<String> categories = item.getCategories();
            for (String category : categories) {
                statement2.setString(1, item.getItemId());
                statement2.setString(2, category);
                statement2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getFullname(String userId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean verifyLogin(String userId, String password) {
        // TODO Auto-generated method stub
        return false;
    }

}
