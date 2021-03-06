package seedu.task.ui;

import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import seedu.task.MainApp;
import seedu.task.commons.core.ComponentManager;
import seedu.task.commons.core.Config;
import seedu.task.commons.core.LogsCenter;
import seedu.task.commons.events.storage.DataSavingExceptionEvent;
import seedu.task.commons.events.ui.DatePickedOnCalendarEvent;
import seedu.task.commons.events.ui.DisplayDirectoryChooserRequestEvent;
import seedu.task.commons.events.ui.JumpToListRequestEvent;
import seedu.task.commons.events.ui.ListAllButtonEvent;
import seedu.task.commons.events.ui.ListButtonEvent;
import seedu.task.commons.events.ui.ListCompleteButtonEvent;
import seedu.task.commons.events.ui.ListPendingButtonEvent;
import seedu.task.commons.events.ui.TaskPanelSelectionChangedEvent;
import seedu.task.commons.events.ui.ShowHelpRequestEvent;
import seedu.task.commons.events.ui.TaskPanelDataChangedEvent;
import seedu.task.commons.util.DateUtil;
import seedu.task.commons.util.StringUtil;
import seedu.task.logic.Logic;
import seedu.task.logic.commands.ListCommand;
import seedu.task.model.UserPrefs;

import java.io.File;
import java.util.logging.Logger;

/**
 * The manager of the UI component.
 */
public class UiManager extends ComponentManager implements Ui {
    private static final Logger logger = LogsCenter.getLogger(UiManager.class);
    private static final String ICON_APPLICATION = "/images/task_book_32.png";

    private Logic logic;
    private Config config;
    private UserPrefs prefs;
    private MainWindow mainWindow;

    public UiManager(Logic logic, Config config, UserPrefs prefs) {
        super();
        this.logic = logic;
        this.config = config;
        this.prefs = prefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting UI...");
        primaryStage.setTitle(config.getAppTitle());

        //Set the application icon.
        primaryStage.getIcons().add(getImage(ICON_APPLICATION));

        try {
            mainWindow = MainWindow.load(primaryStage, config, prefs, logic);
            mainWindow.show(); //This should be called before creating other UI parts
            mainWindow.fillInnerParts();

        } catch (Throwable e) {
            logger.severe(StringUtil.getDetails(e));
            showFatalErrorDialogAndShutdown("Fatal error during initializing", e);
        }
    }

    @Override
    public void stop() {
        prefs.updateLastUsedGuiSetting(mainWindow.getCurrentGuiSetting());
        mainWindow.hide();
       
    }

    private void showFileOperationAlertAndWait(String description, String details, Throwable cause) {
        final String content = details + ":\n" + cause.toString();
        showAlertDialogAndWait(AlertType.ERROR, "File Op Error", description, content);
    }

    private Image getImage(String imagePath) {
        return new Image(MainApp.class.getResourceAsStream(imagePath));
    }

    void showAlertDialogAndWait(Alert.AlertType type, String title, String headerText, String contentText) {
        showAlertDialogAndWait(mainWindow.getPrimaryStage(), type, title, headerText, contentText);
    }

    private static void showAlertDialogAndWait(Stage owner, AlertType type, String title, String headerText,
                                               String contentText) {
        final Alert alert = new Alert(type);
        alert.getDialogPane().getStylesheets().add("view/DarkTheme.css");
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    private void showFatalErrorDialogAndShutdown(String title, Throwable e) {
        logger.severe(title + " " + e.getMessage() + StringUtil.getDetails(e));
        showAlertDialogAndWait(Alert.AlertType.ERROR, title, e.getMessage(), e.toString());
        Platform.exit();
        System.exit(1);
    }

    //==================== Event Handling Code =================================================================

    @Subscribe
    private void handleDataSavingExceptionEvent(DataSavingExceptionEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        showFileOperationAlertAndWait("Could not save data", "Could not save data to file", event.exception);
    }

    @Subscribe
    private void handleShowHelpEvent(ShowHelpRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        mainWindow.handleHelp();
    }

    @Subscribe
    private void handleJumpToListRequestEvent(JumpToListRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        mainWindow.getTaskListPanel().scrollTo(event.targetIndex);
    }

    @Subscribe
    private void handleTaskPanelSelectionChangedEvent(TaskPanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        
    }
    //@@author A0138704E
    @Subscribe
    private void handleTaskPanelDataChangedEvent(TaskPanelDataChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        mainWindow.getTaskListPanel().refresh();
    }
    //@@author A0153723J
    /**
     * Display a message when a date on the calendar selected
     */
    @Subscribe
    private void handleDatePickedOnCalendarEvent(DatePickedOnCalendarEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        mainWindow.getResultDisplay().postMessage(String.format(ListCommand.MESSAGE_LIST_DATE_SUCCESS, 
                        DateUtil.formatLocalDateToString(event.date)));
    }
    
    @Subscribe
    private void handleListCompleteEvent(ListCompleteButtonEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        mainWindow.getResultDisplay().postMessage("Listed completed tasks");
    }

    @Subscribe
    private void handleListPendingEvent(ListPendingButtonEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        mainWindow.getResultDisplay().postMessage("Listed pending tasks");
    }

    @Subscribe
    private void handleListAllEvent(ListAllButtonEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        mainWindow.getResultDisplay().postMessage("Listed all tasks");
    }

    @Subscribe
    private void handleListEvent(ListButtonEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        mainWindow.getResultDisplay().postMessage("Listed tasks due today");
    }

    @Subscribe
    private void handleDisplayDirectoryChooserRequestEvent(DisplayDirectoryChooserRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File selectedDirectory = directoryChooser.showDialog(mainWindow.getPrimaryStage());
        if (selectedDirectory != null) {
        	event.setSelectedFilePath(selectedDirectory.getAbsolutePath());
        } else {
        	event.setSelectedFilePath("");
        }
    }

}
