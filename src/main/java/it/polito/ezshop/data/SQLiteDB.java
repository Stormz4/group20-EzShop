package it.polito.ezshop.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SQLiteDB {
    static final String JDBC_DB_NAME = "EZShopDB.db";
    static final String JDBC_DB_URL = "jdbc:sqlite:src/" + JDBC_DB_NAME;
    static final int defaultID = -1;
    static final int defaultValue = 0;
    private static final int INTEGER = 4; // see https://docs.oracle.com/javase/8/docs/api/constant-values.html#java.sql.Types.INTEGER
    Connection dbConnection = null;

    /**
     ** Connect to the DB
     */
    public boolean connect() {
        try {
            // create a connection to the database
            this.dbConnection = DriverManager.getConnection(JDBC_DB_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            this.createNewDatabase();
        }

        return this.dbConnection != null;
    }

    public void closeConnection() {
        try {
            if (this.dbConnection != null) {
                this.dbConnection.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     ** Create the DB
     */
    private void createNewDatabase() {
        if (this.dbConnection == null)
            return;

        try {
            this.dbConnection = DriverManager.getConnection(JDBC_DB_URL);
            if (this.dbConnection != null) {
                DatabaseMetaData meta = this.dbConnection.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Initialize DB
     */
    public void initDatabase() {
        // Create tables if they do not exist already
        this.createBalanceOperationsTable();//
        this.createCardsTable();//
        this.createCustomersTable();//
        this.createOrdersTable();
        this.createProductsPerSaleTable();
        this.createProductTypesTable();
        this.createTransactionsTable();
        this.createUsersTable();
    }

    public boolean clearDatabase() {
        if (this.dbConnection == null)
            return false;

        this.clearTable("BalanceOperations");
        this.clearTable("Orders");
        this.clearTable("ProductsPerSale");
        this.clearTable("ProductTypes");
        this.clearTable("Transactions");
        // this.clearTable("ReturnTransactions"); // TODO: need this?

        return true;
  }

  private void clearTable(String tableName) {
      String sql =  "DELETE FROM " + tableName + " ;";

      try{
          Statement stmt = this.dbConnection.createStatement();
          stmt.execute(sql);
      } catch (SQLException e) {
          System.out.println(e.getMessage());
      }
  }

    /**
     ** Returns the id of the last inserted row, no matter the table
     */
    private int lastInsertRowId() {
        if (this.dbConnection == null)
            return defaultID;

        String sql = "SELECT last_insert_rowid() AS id;";
        int lastId = defaultID;

        try{
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            if (rs.next())
                lastId = rs.getInt("id");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return lastId;
    }

    /**
     ** Create a new Customers table
     ** EZCustomer(Integer id, String customerName, String customerCard)
     */
    private void createCustomersTable() {
        if (this.dbConnection == null)
            return;

        // SQL statement for creating a new Customer table
        String sql = "CREATE TABLE IF NOT EXISTS Customers (\n"
                   + " id integer PRIMARY KEY,\n"
                   + " name text NOT NULL,\n"
                   + " card integer UNIQUE,\n"
                   + "FOREIGN KEY(card) REFERENCES Cards(id)\n"
                   + ");";

        try{
            Statement stmt = this.dbConnection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Select all Customers records
     */
    public HashMap<Integer, EZCustomer> selectAllCustomers(){
        HashMap<Integer, EZCustomer> customers = new HashMap<>();
        String sql = "SELECT Customers.id, name, card, Cards.points \n"
                   + "FROM Customers \n"
                   + "LEFT JOIN Cards ON Customers.card = Cards.id;";

        try {
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String name = rs.getString("name");
                Integer card = rs.getInt("card");
                String cardCode = card != 0 ? String.format("%10d", card).replace(' ', '0') : "";
                Integer points = rs.getInt("points");
                customers.put(id, new EZCustomer(id, name, cardCode, points));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return customers;
    }

    /**
     ** Insert new Customer record
     */
    public Integer insertCustomer(String customerName, String customerCard) {
        if (this.dbConnection == null || customerName == null)
            return defaultID;

        if (customerCard == null)
            customerCard = "";

        String sql = "INSERT INTO Customers(name, card) VALUES(?,?)";
        int customerId = defaultID;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setString(1, customerName);
            if (customerCard.isEmpty())
                pstmt.setNull(2, INTEGER);
            else
                pstmt.setInt(2, Integer.parseInt(customerCard));
            pstmt.executeUpdate();

            customerId = this.lastInsertRowId();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return customerId;
    }

    /**
     ** Delete Customer record with given id
     */
    public boolean deleteCustomer(Integer id) {
        if (this.dbConnection == null || id == null)
            return false;

        String sql = "DELETE FROM Customers WHERE id=?";
        boolean deleted = false;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Update Customer record
     */
    public boolean updateCustomer(Integer customerId, String customerName, String customerCard) {
        if (this.dbConnection == null || customerId == null)
            return false;

        if (customerCard == null)
            customerCard = "";

        boolean updated = false;
        String sql = "UPDATE Customers\n" +
                     "SET name = ?, card = ?\n" +
                     "WHERE id = ?;";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setString(1, customerName);
            if (customerCard.isEmpty())
                pstmt.setNull(2, INTEGER);
            else
                pstmt.setInt(2, Integer.parseInt(customerCard));
            pstmt.setInt(3, customerId);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }

    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new BalanceOperations table
     ** EZBalanceOperation (int balanceId, LocalDate date, double money, String type)
     */
    private void createBalanceOperationsTable() {
        if (this.dbConnection == null)
            return;

        // SQL statement for creating a new BalanceOperations table
        String sql = "CREATE TABLE IF NOT EXISTS BalanceOperations (\n"
                + " id integer PRIMARY KEY,\n"
                + " date text,\n"
                + " money real,\n"
                + " type text\n"
                + ");";

        try{
            Statement stmt = this.dbConnection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Select all BalanceOperations records
     */
    public HashMap<Integer, EZBalanceOperation> selectAllBalanceOperations(){
        HashMap<Integer, EZBalanceOperation> balanceOperations = new HashMap<>();
        String sql = "SELECT * FROM BalanceOperations";

        try {
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                int id = rs.getInt("id");
                String strDate = rs.getString("date");
                LocalDate date = (strDate != null) ? LocalDate.parse(strDate) : null;
                double money = rs.getDouble("money");
                String type = rs.getString("type");
                balanceOperations.put(id, new EZBalanceOperation(id, date, money, type));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return balanceOperations;
    }

    /**
     ** Insert new BalanceOperation record
     */
    public Integer insertBalanceOperation(LocalDate date, double money, String type) {
        if (this.dbConnection == null)
            return null;

        String sql = "INSERT INTO BalanceOperations(date, money, type) VALUES(?,?,?)";
        int balanceOperationId = defaultID;

        String strDate = null;
        if (date != null)
            strDate = date.toString();

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setString(1, strDate);
            pstmt.setDouble(2, money);
            pstmt.setString(3, type);
            pstmt.executeUpdate();

            balanceOperationId = this.lastInsertRowId();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return balanceOperationId;
    }

    /**
     ** Delete BalanceOperation record with given id
     */
    public boolean deleteBalanceOperation(Integer id) {
        if (this.dbConnection == null || id == null)
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM BalanceOperations WHERE id=?";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Update BalanceOperation record
     */
    public boolean updateBalanceOperation(Integer id, LocalDate date, double money, String type) {
        if (this.dbConnection == null || id == null)
            return false;

        boolean updated = false;
        String sql = "UPDATE BalanceOperations\n" +
                     "SET date = ?, money = ?, type = ?\n" +
                     "WHERE id = ?;";

        String strDate = null;
        if (date != null)
            strDate = date.toString();

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setString(1, strDate);
            pstmt.setDouble(2, money);
            pstmt.setString(3, type);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }

    /**
     ** Select totalBalance
     */
    public double selectTotalBalance() {
        String sql = "SELECT SUM(money) as totalBalance\n" +
                     "FROM BalanceOperations;";

        if (this.dbConnection == null)
            return defaultValue;

        double totalBalance = 0;

        try{
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            if (rs.next())
                totalBalance = rs.getDouble("totalBalance");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return totalBalance;
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new Orders table
     ** EZOrder (Integer orderId, Integer balanceId, String productCode, double pricePerUnit, int quantity, String status)
     */
    private void createOrdersTable() {
        if (this.dbConnection == null)
            return;

        // SQL statement for creating a new Orders table
        String sql = "CREATE TABLE IF NOT EXISTS Orders (\n"
                + " id integer PRIMARY KEY,\n"
                + " balanceId integer,\n"
                + " productCode text,\n"
                + " pricePerUnit real,\n"
                + " quantity integer,\n"
                + " status text\n"
                + ");";

        try{
            Statement stmt = this.dbConnection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Select all Orders records
     */
    public HashMap<Integer, EZOrder> selectAllOrders(){
        HashMap<Integer, EZOrder> orders = new HashMap<>();
        String sql = "SELECT * FROM Orders";

        try {
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                Integer id = rs.getInt("id");
                Integer balanceId = rs.getInt("balanceId");
                String productCode = rs.getString("productCode");
                double pricePerUnit = rs.getDouble("pricePerUnit");
                Integer quantity = rs.getInt("quantity");
                String status = rs.getString("status");
                orders.put(id, new EZOrder(id, balanceId, productCode, pricePerUnit, quantity, status));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return orders;
    }

    /**
     ** Insert new Order record
     */
    public Integer insertOrder(Integer balanceId, String productCode, double pricePerUnit, int quantity, String status) {
        if (this.dbConnection == null)
            return defaultID;

        String sql = "INSERT INTO Orders(balanceId, productCode, pricePerUnit, quantity, status) VALUES(?,?,?,?,?)";
        int orderId = defaultID;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, balanceId != null ? balanceId : defaultID);
            pstmt.setString(2, productCode != null ? productCode : "");
            pstmt.setDouble(3, pricePerUnit);
            pstmt.setInt(4, quantity);
            pstmt.setString(5, status != null ? status : "");
            pstmt.executeUpdate();

            orderId = this.lastInsertRowId();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return orderId;
    }

    /**
     ** Delete Order record with given id
     */
    public boolean deleteOrder(Integer id) {
        if (this.dbConnection == null || id == null)
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM Orders WHERE id=?";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Update Order record
     */
    public boolean updateOrder(Integer id, Integer balanceId, String productCode, double pricePerUnit, int quantity, String status) {
        if (this.dbConnection == null || id == null)
            return false;

        boolean updated = false;
        String sql = "UPDATE Orders\n" +
                     "SET balanceId = ?, productCode = ?, pricePerUnit = ?, quantity = ?, status = ?\n" +
                     "WHERE id = ?;";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, balanceId);
            pstmt.setString(2, productCode);
            pstmt.setDouble(3, pricePerUnit);
            pstmt.setInt(4, quantity);
            pstmt.setString(5, status);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new Users table
     ** EZUser (Integer id, String userName, String password, String role)
     */
    private void createUsersTable() {
        if (this.dbConnection == null)
            return;

        // SQL statement for creating a new Orders table
        String sql = "CREATE TABLE IF NOT EXISTS Users (\n"
                + " id integer PRIMARY KEY,\n"
                + " userName text NOT NULL UNIQUE,\n"
                + " password text NOT NULL,\n"
                + " role text NOT NULL\n"
                + ");";

        try{
            Statement stmt = this.dbConnection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Select all Users records
     */
    public HashMap<Integer, EZUser> selectAllUsers(){
        HashMap<Integer, EZUser> users = new HashMap<>();
        String sql = "SELECT * FROM Users";

        try {
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String userName = rs.getString("userName");
                String password = rs.getString("password");
                String role = rs.getString("role");
                users.put(id, new EZUser(id, userName, password, role));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return users;
    }

    /**
     ** Insert new User record
     */
    public Integer insertUser(String userName, String password, String role) {
        if (this.dbConnection == null)
            return null;

        int userId = defaultID;
        String sql = "INSERT INTO Users(userName, password, role) VALUES(?,?,?)";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.executeUpdate();

            userId = this.lastInsertRowId();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return userId;
    }

    /**
     ** Delete User record with given id
     */
    public boolean deleteUser(Integer id) {
        if (this.dbConnection == null || id == null)
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM Users WHERE id=?";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Update User record
     */
    public boolean updateUser(Integer id, String userName, String password, String role) {
        if (this.dbConnection == null || id == null)
            return false;

        boolean updated = false;
        String sql = "UPDATE Users\n" +
                     "SET userName = ?, password = ?, role = ?\n" +
                     "WHERE id = ?;";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new Cards table
     ** Card (String cardCode, Integer customerId, Integer points)
     */
    private void createCardsTable() {
        if (this.dbConnection == null)
            return;

        // SQL statement for creating a new Cards table
        String sql = "CREATE TABLE IF NOT EXISTS Cards (\n"
                + " id integer PRIMARY KEY,\n"
                + " points integer\n"
                + ");";

        try{
            Statement stmt = this.dbConnection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Select all Cards records
     */
    public List<String> selectAllCards(){
        List<String> cards = new LinkedList<>();
        String sql = "SELECT id FROM Cards";

        try {
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String strId = String.format("%10d", id);
                cards.add(strId);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return cards;
    }

    /**
     ** Insert new Card record
     */
    public String insertCard(Integer points) {
        if (this.dbConnection == null)
            return "";

        String sql = "INSERT INTO Cards(points) VALUES(?)";
        String cardCode = "";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, points != null ? points : defaultValue);
            pstmt.executeUpdate();

            Integer cardId = this.lastInsertRowId();
            cardCode = String.format("%10d", cardId).replace(' ', '0');
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return cardCode;
    }

    /**
     ** Delete Card record with given cardCode
     */
    public boolean deleteCard(String cardCode) {
        if (this.dbConnection == null || cardCode == null || cardCode.length() == 0)
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM Cards WHERE id=?";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(cardCode));
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Update Card record
     */
    public boolean updateCard(String cardCode, Integer points) {
        if (this.dbConnection == null || cardCode == null || cardCode.isEmpty())
            return false;

        boolean deleted = false;
        int cardId = Integer.parseInt(cardCode);
        String sql = "UPDATE Cards\n" +
                "SET points = ?\n" +
                "WHERE id = ?;";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, points);
            pstmt.setInt(2, cardId);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new ProductTypes table
     ** Card EZProductType (Integer id, Integer quantity, String location, String note, String productDescription, String barCode, double pricePerUnit)
     */
    private void createProductTypesTable() {
        // SQL statement for creating a new ProductTypes table
        String sql = "CREATE TABLE IF NOT EXISTS ProductTypes (\n"
                + " id integer PRIMARY KEY,\n"
                + " quantity integer,\n"
                + " location text,\n"
                + " note text,\n"
                + " productDescription text,\n"
                + " barCode text,\n"
                + " pricePerUnit real\n"
                + ");";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return;

        try{
            Statement stmt = this.dbConnection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Select all ProductTypes records
     */
    public HashMap<Integer, EZProductType> selectAllProductTypes(){
        HashMap<Integer, EZProductType> productTypes = new HashMap<>();
        String sql = "SELECT * FROM ProductTypes";

        try {
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                Integer id = rs.getInt("id");
                Integer quantity = rs.getInt("quantity");
                String location = rs.getString("location");
                String note = rs.getString("note");
                String productDescription = rs.getString("productDescription");
                String barCode = rs.getString("barCode");
                double pricePerUnit = rs.getDouble("pricePerUnit");

                productTypes.put(id, new EZProductType(id, quantity, location, note, productDescription, barCode, pricePerUnit));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return productTypes;
    }

    /**
     ** Insert new ProductType record
     */
    public Integer insertProductType(Integer quantity, String location, String note, String productDescription, String barCode, double pricePerUnit) {
        if (this.dbConnection == null)
            return defaultID;

        int id = defaultID;
        String sql = "INSERT INTO ProductTypes(quantity, location, note, productDescription, barCode, pricePerUnit) \n"
                   + "VALUES(?,?,?,?,?,?);";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, quantity);
            pstmt.setString(2, location);
            pstmt.setString(3, note);
            pstmt.setString(4, productDescription);
            pstmt.setString(5, barCode);
            pstmt.setDouble(6, pricePerUnit);
            pstmt.executeUpdate();

            id = this.lastInsertRowId();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return id;
    }

    /**
     ** Delete ProductType record with given id
     */
    public boolean deleteProductType(Integer id) {
        if (this.dbConnection == null || id == null)
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM ProductTypes WHERE id=?";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Update ProductType record
     */
    public boolean updateProductType(Integer id, Integer quantity, String location, String note, String productDescription, String barCode, double pricePerUnit) {
        if (this.dbConnection == null || id == null)
            return false;

        boolean updated = false;
        String sql = "UPDATE ProductTypes\n" +
                     "SET quantity = ?, location = ?, note = ?, productDescription = ?, barCode = ?, pricePerUnit = ?\n" +
                     "WHERE id = ?;";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, quantity);
            pstmt.setString(2, location);
            pstmt.setString(3, note);
            pstmt.setString(4, productDescription);
            pstmt.setString(5, barCode);
            pstmt.setDouble(6, pricePerUnit);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new Transactions table
     ** EZSaleTransaction (Integer ticketNumber, List<TicketEntry> entries, double discountRate, double price)
     */
    private void createTransactionsTable() {
        if (this.dbConnection == null)
            return;

        // SQL statement for creating a new Transactions table
        String sql = "CREATE TABLE IF NOT EXISTS Transactions (\n"
                + " id integer PRIMARY KEY,\n"
                + " discountRate real,\n"
                + " price real\n"
                + " saleID integer\n"
                + ");";

        try{
            Statement stmt = this.dbConnection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Select all SaleTransactions records
     */
    public HashMap<Integer, EZSaleTransaction> selectAllSaleTransactions(){
        HashMap<Integer, EZSaleTransaction> saleTransactions = new HashMap<>();
        String sql = "SELECT id, discountRate, price FROM Transactions";

        try {
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs1    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs1.next()) {
                Integer transactionID = rs1.getInt("id");
                double saleDiscountRate = rs1.getDouble("discountRate");
                double price = rs1.getDouble("price");

                // Internal query to retrieve ticketEntry (productPerSale)
                LinkedList<TicketEntry> entries = new LinkedList<>();
                String sql2 = "SELECT barCode, amount, discountRate, productDescription, pricePerUnit \n"
                            + "FROM ProductsPerSale \n"
                            + "INNER JOIN ProductTypes ON ProductsPerSale.barCode = ProductTypes.barCode \n"
                            + "WHERE ProductsPerSale.id = ? ;";

                PreparedStatement pstmt = this.dbConnection.prepareStatement(sql2);
                pstmt.setInt(1, transactionID);
                ResultSet rs2 = pstmt.executeQuery();

                while (rs2.next()) {
                    String barCode = rs2.getString("barCode");
                    int amount = rs2.getInt("amount");
                    double pDiscountRate = rs2.getDouble("discountRate");
                    String productDescription = rs2.getString("productDescription");
                    double pricePerUnit = rs2.getDouble("pricePerUnit");

                    EZTicketEntry product = new EZTicketEntry(barCode, productDescription, amount, pricePerUnit, pDiscountRate);
                    entries.add(product);
                }

                saleTransactions.put(transactionID, new EZSaleTransaction(transactionID, entries, saleDiscountRate, price));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return saleTransactions;
    }

    /**
     ** Select all ReturnTransactions records
     */
    public HashMap<Integer, EZReturnTransaction> selectAllReturnTransactions(){
        HashMap<Integer, EZReturnTransaction> returnTransactions = new HashMap<>();
        String sql = "SELECT id, price, saleID \n"
                   + "FROM Transactions \n"
                   + "WHERE saleID IS NOT NULL;";

        try {
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs1    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs1.next()) {
                Integer transactionID = rs1.getInt("id");
                double price = rs1.getDouble("price");
                int saleID = rs1.getInt("saleID");

                // Internal query to retrieve ticketEntry (productPerSale)
                LinkedList<TicketEntry> entries = new LinkedList<>();
                String sql2 = "SELECT barCode, amount, discountRate, productDescription, pricePerUnit \n"
                        + "FROM ProductsPerSale \n"
                        + "INNER JOIN ProductTypes ON ProductsPerSale.barCode = ProductTypes.barCode \n"
                        + "WHERE ProductsPerSale.id = ? ;";

                PreparedStatement pstmt = this.dbConnection.prepareStatement(sql2);
                pstmt.setInt(1, transactionID);
                ResultSet rs2 = pstmt.executeQuery();

                while (rs2.next()) {
                    String barCode = rs2.getString("barCode");
                    int amount = rs2.getInt("amount");
                    double pDiscountRate = rs2.getDouble("discountRate");
                    String productDescription = rs2.getString("productDescription");
                    double pricePerUnit = rs2.getDouble("pricePerUnit");

                    EZTicketEntry product = new EZTicketEntry(barCode, productDescription, amount, pricePerUnit, pDiscountRate);
                    entries.add(product);
                }

                returnTransactions.put(transactionID, new EZReturnTransaction(transactionID, saleID, entries, price));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return returnTransactions;
    }

    /**
     ** Insert new SaleTransaction record
     */
    public Integer insertSaleTransaction(List<TicketEntry> entries, double discountRate, double price) {
        if (this.dbConnection == null)
            return defaultID;

        int transactionID = defaultID;
        String sql = "INSERT INTO Transactions(discountRate, price) \n"
                   + "VALUES(?,?);";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setDouble(1, discountRate);
            pstmt.setDouble(2, price);
            pstmt.executeUpdate();

            transactionID = this.lastInsertRowId();

            // Save in DB all the entries of the sale
            for (TicketEntry entry : entries)
                this.insertProductPerSale(entry.getBarCode(), transactionID, entry.getAmount(), entry.getDiscountRate());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return transactionID;
    }

    /**
     ** Insert new SaleTransaction record
     */
    public Integer insertReturnTransaction(List<TicketEntry> entries, int saleID, double price) {
        if (this.dbConnection == null)
            return defaultID;

        int transactionID = defaultID;
        String sql = "INSERT INTO Transactions(saleID, price) \n"
                + "VALUES(?,?);";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, saleID);
            pstmt.setDouble(2, price);
            pstmt.executeUpdate();

            transactionID = this.lastInsertRowId();

            // Save in DB all the entries of the sale
            for (TicketEntry entry : entries)
                this.insertProductPerSale(entry.getBarCode(), transactionID, entry.getAmount(), entry.getDiscountRate());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return transactionID;
    }

    /**
     ** Delete Transaction record with given id
     */
    public boolean deleteTransaction(Integer transactionID) {
        if (this.dbConnection == null || transactionID == null)
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM Transactions WHERE id=?";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, transactionID);
            pstmt.executeUpdate();
            deleted = true;

            // Delete all productPerSale relied to this transaction
            this.deleteAllProductsPerSale(transactionID);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Update SaleTransaction record
     */
    public boolean updateSaleTransaction(Integer transactionID, double discountRate, double price) {
        if (this.dbConnection == null || transactionID == null)
            return false;

        boolean updated = false;
        String sql = "UPDATE Transactions\n" +
                     "SET discountRate = ?, price = ?\n" +
                     "WHERE id = ?;";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setDouble(1, discountRate);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, transactionID);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }

    /**
     ** Update SaleTransaction record
     */
    public boolean updateReturnTransaction(Integer transactionID, int saleID, double price) {
        if (this.dbConnection == null || transactionID == null)
            return false;

        boolean updated = false;
        String sql = "UPDATE Transactions\n" +
                     "SET discountRate = ?, price = ?\n" +
                     "WHERE id = ?;";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, saleID);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, transactionID);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new ProductsPerSale table
     ** EZTicketEntry (String barCode, String productDescription, int amount, double pricePerUnit, double discountRate)
     */
    private void createProductsPerSaleTable() {
        if (this.dbConnection == null)
            return;

        // SQL statement for creating a new ProductsPerSale table
        String sql = "CREATE TABLE IF NOT EXISTS ProductsPerSale (\n"
                   + " barCode text NOT NULL, \n"
                   + " transactionID integer NOT NULL, \n"
                   + " amount integer, \n"
                   + " discountRate real, \n"
                   + "CONSTRAINT PK_ProductPerSale PRIMARY KEY (barCode, transactionID), \n"
                   + "FOREIGN KEY(barCode) REFERENCES ProductTypes(barCode),  \n"
                   + "FOREIGN KEY(transactionID) REFERENCES Transactions(id) \n"
                   + ");";

        try{
            Statement stmt = this.dbConnection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("createProductsPerSaleTable" + e.getMessage());
        }
    }

    /**
     ** Insert new ProductPerSale record
     */
    public boolean insertProductPerSale(String barCode, Integer transactionID, int amount, double discountRate) {
        if (this.dbConnection == null)
            return false;

        boolean inserted = false;
        String sql = "INSERT INTO ProductsPerSale(barCode, transactionID, amount, discountRate) \n"
                   + "VALUES(?,?,?,?,?);";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setString(1, barCode);
            pstmt.setInt(2, transactionID);
            pstmt.setInt(3, amount);
            pstmt.setDouble(4, discountRate);
            pstmt.executeUpdate();

            inserted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return inserted;
    }

    /**
     ** Delete ProductPerSale record with given id
     */
    public boolean deleteProductPerSale(String barCode, Integer transactionID) {
        if (this.dbConnection == null || transactionID == null)
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM ProductsPerSale WHERE barCode=? AND transactionID=?";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setString(1, barCode);
            pstmt.setInt(2, transactionID);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Delete all ProductsPerSale records related to the sale with given transactionID
     */
    public boolean deleteAllProductsPerSale(Integer transactionID) {
        if (this.dbConnection == null || transactionID == null)
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM ProductsPerSale WHERE transactionID=?";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, transactionID);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Update ProductPerSale record
     */
    public boolean updateProductPerSale(String barCode, Integer transactionID, int amount, double discountRate) {
        if (this.dbConnection == null || transactionID == null)
            return false;

        boolean updated = false;
        String sql = "UPDATE ProductsPerSale\n" +
                     "SET amount = ?, discountRate = ?\n" +
                     "WHERE barCode = ? AND transactionID = ?;";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, amount);
            pstmt.setDouble(2, discountRate);
            pstmt.setString(3, barCode);
            pstmt.setInt(4, transactionID);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }
}
