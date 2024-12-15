import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;
import java.util.Random;

public class BatchUpdatePerformance extends Application {
    private TextArea outputArea = new TextArea();
    private Connection connection;
    private Button batchUpdateBtn = new Button("Batch Update");
    private Button nonBatchUpdateBtn = new Button("Non-Batch Update");
    private Button connectBtn = new Button("Connect to Database");

    @Override
    public void start(Stage primaryStage) {
        // Layout setup
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(connectBtn, batchUpdateBtn, nonBatchUpdateBtn, outputArea);

        // Button actions
        connectBtn.setOnAction(e -> connectToDatabase());
        batchUpdateBtn.setOnAction(e -> performBatchUpdate());
        nonBatchUpdateBtn.setOnAction(e -> performNonBatchUpdate());

        // Stage setup
        primaryStage.setTitle("Batch Update Performance Test");
        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.show();
    }

    private void connectToDatabase() {
        try {
            // Database connection setup
            String url = "jdbc:mysql://localhost:3306/javabook";
            String user = "scott";
            String password = "tiger";
            connection = DriverManager.getConnection(url, user, password);
            outputArea.appendText("Connected to Database\n");
        } catch (SQLException ex) {
            outputArea.appendText("Failed to connect to Database\n" + ex.getMessage() + "\n");
        }
    }

    private void performBatchUpdate() {
        if (connection == null) {
            outputArea.appendText("Database connection is not established.\n");
            return;
        }
        long startTime = System.currentTimeMillis();
        String sql = "INSERT INTO Temp(num1, num2, num3) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            Random random = new Random();
            for (int i = 0; i < 1000; i++) {
                stmt.setDouble(1, random.nextDouble());
                stmt.setDouble(2, random.nextDouble());
                stmt.setDouble(3, random.nextDouble());
                stmt.addBatch();
            }
            stmt.executeBatch();
            long endTime = System.currentTimeMillis();
            outputArea.appendText("Batch update completed. Elapsed time: " + (endTime - startTime) + " ms\n");
        } catch (SQLException ex) {
            outputArea.appendText("Batch update failed: " + ex.getMessage() + "\n");
        }
    }

    private void performNonBatchUpdate() {
        if (connection == null) {
            outputArea.appendText("Database connection is not established.\n");
            return;
        }
        long startTime = System.currentTimeMillis();
        String sql = "INSERT INTO Temp(num1, num2, num3) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            Random random = new Random();
            for (int i = 0; i < 1000; i++) {
                stmt.setDouble(1, random.nextDouble());
                stmt.setDouble(2, random.nextDouble());
                stmt.setDouble(3, random.nextDouble());
                stmt.executeUpdate();
            }
            long endTime = System.currentTimeMillis();
            outputArea.appendText("Non-batch update completed. Elapsed time: " + (endTime - startTime) + " ms\n");
        } catch (SQLException ex) {
            outputArea.appendText("Non-batch update failed: " + ex.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
