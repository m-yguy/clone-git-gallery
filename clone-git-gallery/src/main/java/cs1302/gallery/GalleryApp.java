package cs1302.gallery;

import cs1302.gallery.ItunesResponse;
import cs1302.gallery.ItunesResult;
import javafx.concurrent.Task;
import java.lang.Thread;
import java.lang.Runnable;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.concurrent.Task;
import javafx.util.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Represents an iTunes Gallery App.
 */
public class GalleryApp extends Application {

    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)           // uses HTTP protocol version 2 where possible
        .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
        .build();                                     // builds and returns a HttpClient object

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();                                    // builds and returns a Gson object

    private Stage stage;
    private Scene scene;
    private VBox root;
    // User HBox with buttons & search.
    private HBox userBox;
    private String query;
    private Button playButton;
    private Timer timer;
    private boolean isPlaying = true;
    private Label label;
    private TextField search;
    private ComboBox<String> dropDown;
    private Button getImages;
    // Instructions HBox
    private HBox instructions;
    private Label infoLabel;
    // ImageView Grid
    private TilePane imageBox;
    private ImageView[] imgViews;
    private Image[] newImages;
    private Image image = new Image("file:resources/default.png", 100, 100, true, false);
    String jsonString;
    ItunesResponse itunesResponse;
    ItunesResult itunesResult;
    private double progress = 0;

    // Progress Bar HBox
    private HBox progressBox;
    private ProgressBar progressBar;
    private Label iTunesLabel;


    /**
     * Constructs a {@code GalleryApp} object}.
     */
    public GalleryApp() {
        this.stage = null;
        this.scene = null;
        this.root = new VBox(8);

        this.userBox = new HBox(8);
        this.playButton = new Button("Play");
        playButton.setMinWidth(60);
        this.label = new Label("Search:");
        this.search = new TextField("Drake");
        this.dropDown = new ComboBox<String>();
        this.getImages = new Button("Get Images");

        this.instructions = new HBox();
        this.infoLabel = new Label();

        this.imageBox = new TilePane();
        this.imgViews = new ImageView[24];
        for (int i = 0; i < imgViews.length; i++) {
            imgViews[i] = new ImageView(image);
        }

        this.progressBox = new HBox();
        this.progressBar = new ProgressBar(0);
        this.iTunesLabel = new Label();
    } // GalleryApp

    /** {@inheritDoc} */
    @Override
    public void init() {
        // feel free to modify this method
        root.setMaxWidth(1280);
        root.setMaxHeight(720);
        HBox.setHgrow(imageBox, Priority.ALWAYS);
        // Creating the components of the first HBox.
        playButton.setDisable(true);
        dropDown.getItems().addAll(
            "movie",
            "podcast",
            "music",
            "musicVideo",
            "audiobook",
            "shortFilm",
            "tvShow",
            "software",
            "ebook",
            "all");

        dropDown.getSelectionModel().select("music");

        // Creating the components of the second HBox.
        infoLabel = new Label(" Type in a term, select a media type, then click the button.");
        instructions.getChildren().addAll(infoLabel);

        // Creating the TilePane
        imageBox.setPadding(new Insets(10));

        for (int i = 0; i < 20; i++) {
            this.imageBox.getChildren().add(imgViews[i]);
        }

        progressBar.setPrefWidth(250);
        iTunesLabel = new Label("   Images provided by iTunes Search API.");

        System.out.println("init() called");
    } // init

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        this.stage = stage;

        userBox.getChildren().addAll(playButton, label, search, dropDown, getImages);
        progressBox.getChildren().addAll(progressBar, iTunesLabel);

        this.root.getChildren().addAll(userBox, instructions, imageBox, progressBox);
        this.scene = new Scene(this.root);
        this.stage.setOnCloseRequest(event -> Platform.exit());
        this.stage.setTitle("GalleryApp!");
        this.stage.setScene(this.scene);
        this.stage.sizeToScene();
        this.stage.show();
        Platform.runLater(() -> this.stage.setResizable(false));

        getImages.setOnAction(e -> getImgs());

        playButton.setOnAction(e -> toggleRandomSwapping());
    } // start


    /**
     *Method to perform the random image swap.
     *
     */
    private void performRandomSwap() {
        // Get a random index of the displayed images
        int displayIndex = (int) (Math.random() * imgViews.length);

        // Get a random index of the downloaded images
        int downloadIndex = (int) (Math.random() * newImages.length);

        // Ensure that the downloaded image is not already being displayed
        while (newImages[downloadIndex].getUrl().equals(
            imgViews[displayIndex].getImage().getUrl())) {
            downloadIndex = (int) (Math.random() * newImages.length);
        }

        // Swap the images
        imgViews[displayIndex].setImage(newImages[downloadIndex]);
    }

    /**
     * Method to start the random swapping.
     *
     */
    private void startRandomSwapping() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    performRandomSwap();
                }
            }, 0, 2000); // Swap every two seconds
        isPlaying = true;
    }

    /**
     * Method to stop the random swapping.
     */
    private void stopRandomSwapping() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        isPlaying = false;
    }

    /**
     * Method to toggle the random swapping when the "Play" button is clicked.
     */
    private void toggleRandomSwapping() {
        if (isPlaying) {
            stopRandomSwapping();
            playButton.setText("Play");
        } else {
            startRandomSwapping();
            playButton.setText("Pause");
        }
    }


    /**
     * What happens when the Get Images button is clicked.
     */

    public void getImgs() {
        playButton.setDisable(true);
        getImages.setDisable(true);
        //infoLabel.setText("Getting Images...");
        //progressBar.setProgress(0);

        try {
            // Builds query
            buildQuery();
            // Build request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(query))
                .build();
            // Send request / receive response in form of a String.
            HttpResponse<String> response = HTTP_CLIENT
                .send(request, BodyHandlers.ofString());
            // Ensure request is okay
            if (response.statusCode() != 200) {
                throw new IOException(response.toString());
            }
            // Get request body
            jsonString = response.body();
            // System.out.println("*******************RAW JSON STRING: **********************");
            // System.out.println(jsonString.trim());


            itunesResponse = GSON
                .fromJson(jsonString, ItunesResponse.class);

            if (itunesResponse.resultCount < 21) {
                throw new IllegalArgumentException("21 or more are needed.");
            }

            System.out.println("Started downloading!");
            progressBar.setProgress(0);
            downloads();
            System.out.println("Finished downloading!");

        } catch (IOException | InterruptedException e) {
            makeAlertBox(e);
        } catch (IllegalArgumentException ex) {
            makeAlertBox(ex);
        }
    }

    /**
     * Method to simulate the downloading of images and also sets up the grid after download.
     */
    private void downloads() {
        infoLabel.setText("Getting Images..");
        newImages = new Image[itunesResponse.resultCount];
        int length = newImages.length;
        //updateProgressBar(); // Initialize progress bar

        for (int i = 0; i < newImages.length; i++) {
            // Download image
            newImages[i] = new Image(itunesResponse.results[i].artworkUrl100, true);

            // Increment progress after each image is downloaded
            int finalI = i; // For lambda expression
            newImages[i].progressProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.doubleValue() >= 1.0) {
                    double progress = (double) (finalI + 1) / length;
                    progressBar.setProgress(progress);

                    if (progress >= 1.0) {
                        // Update grid after all images are downloaded
                        Platform.runLater(() -> {
                            setGrid();
                        });
                    }
                }
            });
        }
    }


    /**
     * Makes an Alert Box.
     * @param e or any exception.
     *
     */
    private void makeAlertBox (Exception e) {
        infoLabel.setText(" Last attempt to get images failed...");
        if (imgViews[7].getImage() == image) {
            progressBar.setProgress(0);
            playButton.setDisable(true);
        } else {
            progressBar.setProgress(1);
            playButton.setDisable(false);
        }
        getImages.setDisable(false);
        Alert errorAlert = new Alert(AlertType.ERROR);
        errorAlert.setHeaderText("Error");
        errorAlert.setContentText("URI" + query + "\n\n" + "Exception: " + e);
        errorAlert.showAndWait();
    }

    /**
     * Builds the query.
     */
    private void buildQuery() {
        String userInput = search.getCharacters().toString();
        String selectedDropDown = dropDown.getSelectionModel().getSelectedItem();
        String stringURL = URLEncoder.encode(userInput, StandardCharsets.UTF_8);

        query = new String("https://itunes.apple.com/search?term=" + stringURL +
        "&limit=200&media=" + selectedDropDown);
        System.out.println(query);
    }



    /**
     * Meant to practically set the images onto the tilePane.
     */
    private void setGrid() {
        for (int i = 0; i < 20; i++) {
            imgViews[i].setImage(new Image(itunesResponse.results[i].artworkUrl100));
            progressBar.setProgress(i / 20);
        }

        playButton.setDisable(false);
        getImages.setDisable(false);
        infoLabel.setText(query);

    }


    /**
     * Print a response from the iTunes Search API.
     * @param itunesResponse the response object
     */
    private static void printItunesResponse(ItunesResponse itunesResponse) {
        System.out.println();
        System.out.println("********** PRETTY JSON STRING: **********");
        System.out.println(GSON.toJson(itunesResponse));
        System.out.println();
        System.out.println("********** PARSED RESULTS: **********");
        System.out.printf("resultCount = %s\n", itunesResponse.resultCount);
        for (int i = 0; i < itunesResponse.results.length; i++) {
            System.out.printf("itunesResponse.results[%d]:\n", i);
            ItunesResult result = itunesResponse.results[i];
            System.out.printf(" - wrapperType = %s\n", result.wrapperType);
            System.out.printf(" - kind = %s\n", result.kind);
            System.out.printf(" - artworkUrl100 = %s\n", result.artworkUrl100);
        } // for
    } // parseItunesResponse


        /**
         * Creates and immediately starts a new daemon thread that executes
         * {@code target.run()}. This method, which may be called from any thread,
         * will return immediately its the caller.
         * @param target the object whose {@code run} method is invoked when this
         * thread is started
         */
    public static void runNow(Runnable target) {
        Thread t = new Thread(target);
        t.setDaemon(true);
        t.start();
    } // runNow




    /** {@inheritDoc} */
    @Override
    public void stop() {
        // feel free to modify this method
        System.out.println("stop() called");
    } // stop



} // GalleryApp
