package cx.ajneb97.database;

import cx.ajneb97.Codex;
import cx.ajneb97.managers.MessagesManager;
import cx.ajneb97.model.data.PlayerData;
import cx.ajneb97.model.data.PlayerDataCategory;
import cx.ajneb97.model.data.PlayerDataDiscovery;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MySQLConnection {

    private Codex plugin;
    private HikariConnection connection;
    private final Queue<Runnable> writeQueue = new ConcurrentLinkedQueue<>();
    private final Object writeLock = new Object();
    private final AtomicBoolean draining = new AtomicBoolean(false);
    private BukkitTask writeTask;
    private boolean shuttingDown;

    public MySQLConnection(Codex plugin){
        this.plugin = plugin;
    }

    public void setupMySql(){
        FileConfiguration config = plugin.getConfigsManager().getMainConfigManager().getConfig();
        try {
            connection = new HikariConnection(config);
            createTables();
            startWriteWorker();
            Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage(plugin.prefix+"&aSuccessfully connected to the Database."));
        }catch(Exception e) {
            Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage(plugin.prefix+"&cError while connecting to the Database."));
        }
    }

    public Connection getConnection() throws SQLException {
        if(connection == null){
            throw new SQLException("MySQL connection is not initialized");
        }
        return connection.getHikari().getConnection();
    }

    private void enqueue(Runnable task){
        synchronized(writeLock){
            if(shuttingDown){
                return;
            }
            writeQueue.add(task);
            if(writeTask == null || writeTask.isCancelled()){
                startWriteWorkerLocked();
            }
        }
    }

    private void startWriteWorker(){
        synchronized(writeLock){
            startWriteWorkerLocked();
        }
    }

    private void startWriteWorkerLocked(){
        if(shuttingDown || (writeTask != null && !writeTask.isCancelled())){
            return;
        }
        writeTask = new BukkitRunnable(){
            @Override
            public void run(){
                if(shuttingDown || !draining.compareAndSet(false,true)){
                    return;
                }
                try{
                    Runnable queuedTask;
                    while((queuedTask = writeQueue.poll()) != null){
                        runWriteTask(queuedTask);
                    }
                }finally{
                    draining.set(false);
                }
            }
        }.runTaskTimerAsynchronously(plugin,1L,1L);
    }

    private void runWriteTask(Runnable task){
        try{
            task.run();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void shutdown(){
        synchronized(writeLock){
            shuttingDown = true;
            if(writeTask != null){
                writeTask.cancel();
                writeTask = null;
            }
        }

        while(draining.get()){
            try{
                Thread.sleep(1);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }

        Runnable queuedTask;
        while((queuedTask = writeQueue.poll()) != null){
            runWriteTask(queuedTask);
        }

        synchronized(writeLock){
            if(connection != null){
                connection.disable();
                connection = null;
            }
        }
    }

    public void createTables() {
        try(Connection connection = getConnection()){
            PreparedStatement statement1 = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS codex_players" +
                    " (UUID varchar(36) NOT NULL, " +
                    " PLAYER_NAME varchar(50), " +
                    " PRIMARY KEY ( UUID ))"
            );
            statement1.executeUpdate();
            PreparedStatement statement2 = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS codex_players_discoveries" +
                    " (ID int NOT NULL AUTO_INCREMENT, " +
                    " UUID varchar(36) NOT NULL, " +
                    " CATEGORY varchar(100), " +
                    " DISCOVERY varchar(100), " +
                    " DATE varchar(100), " +
                    " MILLIS_ACTIONS_EXECUTED bigint, " +
                    " PRIMARY KEY ( ID ), " +
                    " FOREIGN KEY (UUID) REFERENCES codex_players(UUID))");
            statement2.executeUpdate();
            PreparedStatement statement3 = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS codex_players_completed_categories" +
                            " (ID int NOT NULL AUTO_INCREMENT, " +
                            " UUID varchar(36) NOT NULL, " +
                            " CATEGORY varchar(100), " +
                            " PRIMARY KEY ( ID ), " +
                            " FOREIGN KEY (UUID) REFERENCES codex_players(UUID))");
            statement3.executeUpdate();

            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet rs = dbm.getColumns(null, null, "codex_players_discoveries", "MILLIS_ACTIONS_EXECUTED");
            if (!rs.next()) {
                // Column doesn't exist.
                PreparedStatement alterStatement = connection.prepareStatement(
                        "ALTER TABLE codex_players_discoveries ADD COLUMN MILLIS_ACTIONS_EXECUTED bigint");
                alterStatement.executeUpdate();
                alterStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getPlayer(String uuid,PlayerCallback callback){
        new BukkitRunnable(){
            @Override
            public void run() {
                PlayerData player = null;
                try(Connection connection = getConnection()){
                    PreparedStatement statement = connection.prepareStatement(
                            "SELECT codex_players.UUID, codex_players.PLAYER_NAME, " +
                                    "(SELECT GROUP_CONCAT(cc.Category) FROM codex_players_completed_categories cc WHERE cc.UUID = codex_players.UUID) AS COMPLETED_CATEGORIES, " +
                                    "codex_players_discoveries.CATEGORY AS DISCOVERY_CATEGORY, " +
                                    "codex_players_discoveries.DISCOVERY, " +
                                    "codex_players_discoveries.DATE, " +
                                    "codex_players_discoveries.MILLIS_ACTIONS_EXECUTED " +
                                    "FROM codex_players " +
                                    "LEFT JOIN codex_players_discoveries ON codex_players.UUID = codex_players_discoveries.UUID " +
                                    "WHERE codex_players.UUID = ?");

                    statement.setString(1, uuid);
                    ResultSet result = statement.executeQuery();

                    while(result.next()){
                        UUID uuid = UUID.fromString(result.getString("UUID"));
                        String playerName = result.getString("PLAYER_NAME");
                        String completedCategories = result.getString("COMPLETED_CATEGORIES");
                        String discoveryCategoryName = result.getString("DISCOVERY_CATEGORY");
                        String discoveryName = result.getString("DISCOVERY");
                        String discoveryDate = result.getString("DATE");
                        long discoveryMillisActionsExecuted = result.getLong("MILLIS_ACTIONS_EXECUTED");

                        if(player == null){
                            player = new PlayerData(uuid,playerName);
                        }

                        if(discoveryCategoryName != null){
                            boolean hasCompletedCategory = completedCategories != null && completedCategories.contains(discoveryCategoryName);
                            PlayerDataCategory playerDataCategory = player.getCategory(discoveryCategoryName);
                            if(playerDataCategory == null){
                                playerDataCategory = new PlayerDataCategory(discoveryCategoryName,hasCompletedCategory,new ArrayList<>());
                                player.getCategories().add(playerDataCategory);
                            }

                            playerDataCategory.getDiscoveries().add(new PlayerDataDiscovery(discoveryName,discoveryDate,discoveryMillisActionsExecuted));
                        }
                    }

                    PlayerData finalPlayer = player;
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            callback.onDone(finalPlayer);
                        }
                    }.runTask(plugin);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void createPlayer(PlayerData player){
        final String uuid = player.getUuid().toString();
        final String playerName = player.getName();
        enqueue(() -> {
            try(Connection connection = getConnection()){
                try(PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO codex_players " +
                                "(UUID, PLAYER_NAME) VALUE (?,?)")){

                    statement.setString(1, uuid);
                    statement.setString(2, playerName);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updatePlayerName(PlayerData player){
        final String uuid = player.getUuid().toString();
        final String playerName = player.getName();
        enqueue(() -> {
            try(Connection connection = getConnection()){
                try(PreparedStatement statement = connection.prepareStatement(
                        "UPDATE codex_players SET " +
                                "PLAYER_NAME=? WHERE UUID=?")){

                    statement.setString(1, playerName);
                    statement.setString(2, uuid);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void addDiscovery(String uuid,String categoryName,String discoveryName,String discoveryDate){
        enqueue(() -> {
            try(Connection connection = getConnection()){
                try(PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO codex_players_discoveries " +
                                "(UUID, CATEGORY, DISCOVERY, DATE, MILLIS_ACTIONS_EXECUTED) VALUE (?,?,?,?,?)")){

                    statement.setString(1, uuid);
                    statement.setString(2, categoryName);
                    statement.setString(3, discoveryName);
                    statement.setString(4, discoveryDate);
                    statement.setLong(5, 0);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateMillisActionsExecuted(String uuid, String categoryName, String discoveryName) {
        final long millis = System.currentTimeMillis();
        enqueue(() -> {
            try(Connection connection = getConnection()){
                try(PreparedStatement statement = connection.prepareStatement(
                        "UPDATE codex_players_discoveries SET " +
                                "MILLIS_ACTIONS_EXECUTED=? WHERE UUID=? AND CATEGORY=? AND DISCOVERY=?")){

                    statement.setLong(1, millis);
                    statement.setString(2, uuid);
                    statement.setString(3, categoryName);
                    statement.setString(4, discoveryName);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateCompletedCategories(String uuid,String categoryName){
        enqueue(() -> {
            try(Connection connection = getConnection()){
                try(PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO codex_players_completed_categories " +
                                "(UUID, CATEGORY) VALUE (?,?)")){

                    statement.setString(1, uuid);
                    statement.setString(2, categoryName);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void resetDataPlayer(String uuid, String categoryName, String discoveryName){
        enqueue(() -> {
            try(Connection connection = getConnection()){
                if(categoryName == null) {
                    if (uuid.equals("*")) {
                        // Reset all data for all players
                        try(PreparedStatement statement = connection.prepareStatement(
                                "DELETE FROM codex_players_discoveries");
                            PreparedStatement statement2 = connection.prepareStatement(
                                "DELETE FROM codex_players_completed_categories")){
                            statement.executeUpdate();
                            statement2.executeUpdate();
                        }
                    }else{
                        // Reset all data for player
                        try(PreparedStatement statement = connection.prepareStatement(
                                "DELETE FROM codex_players_discoveries WHERE UUID=?");
                            PreparedStatement statement2 = connection.prepareStatement(
                                "DELETE FROM codex_players_completed_categories WHERE UUID=?")){
                            statement.setString(1, uuid);
                            statement2.setString(1, uuid);
                            statement.executeUpdate();
                            statement2.executeUpdate();
                        }
                    }
                }else{
                    if(discoveryName == null) {
                        if (uuid.equals("*")) {
                            // Reset category for all players
                            try(PreparedStatement statement = connection.prepareStatement(
                                    "DELETE FROM codex_players_discoveries WHERE CATEGORY=?");
                                PreparedStatement statement2 = connection.prepareStatement(
                                    "DELETE FROM codex_players_completed_categories WHERE CATEGORY=?")){
                                statement.setString(1, categoryName);
                                statement2.setString(1, categoryName);
                                statement.executeUpdate();
                                statement2.executeUpdate();
                            }
                        }else{
                            // Reset category for player
                            try(PreparedStatement statement = connection.prepareStatement(
                                    "DELETE FROM codex_players_discoveries WHERE UUID=? AND CATEGORY=?");
                                PreparedStatement statement2 = connection.prepareStatement(
                                    "DELETE FROM codex_players_completed_categories WHERE UUID=? AND CATEGORY=?")){
                                statement.setString(1, uuid);statement.setString(2, categoryName);
                                statement2.setString(1, uuid);statement2.setString(2, categoryName);
                                statement.executeUpdate();
                                statement2.executeUpdate();
                            }
                        }
                    }else{
                        if (uuid.equals("*")) {
                            // Reset discovery for all players
                            try(PreparedStatement statement = connection.prepareStatement(
                                    "DELETE FROM codex_players_discoveries WHERE CATEGORY=? AND DISCOVERY=?")){
                                statement.setString(1, categoryName);statement.setString(2, discoveryName);
                                statement.executeUpdate();
                            }
                        }else{
                            // Reset discovery for player
                            try(PreparedStatement statement = connection.prepareStatement(
                                    "DELETE FROM codex_players_discoveries WHERE UUID=? AND CATEGORY=? AND DISCOVERY=?")){
                                statement.setString(1, uuid);statement.setString(2, categoryName);
                                statement.setString(3, discoveryName);
                                statement.executeUpdate();
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
