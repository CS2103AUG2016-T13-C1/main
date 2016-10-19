package seedu.task.logic.commands;

import java.io.IOException;

import seedu.task.commons.events.ui.DisplayDirectoryChooserRequestEvent.SelectedFilePathEmptyException;

/**
 * Stores current task book in the specified location.
 */
public class StoreCommand extends Command{
	
	public static final String COMMAND_WORD = "store";
	public static final String MESSAGE_USAGE = COMMAND_WORD + ": Stores current data in the given location. " 
		    + "\nParameters: [FILE_LOCATION] " 
		    + "\nExample: " + COMMAND_WORD + " C:\\Users\\Jim\\Desktop";
	
	public static final String MESSAGE_SUCCESS = "Storage Location Updated: %1$s";
	public static final String MESSAGE_SAVE_CONFIG_FAIL = "Unable to save new location into config";
	public static final String MESSAGE_CANCEL_STORE_OPERATION = "No file location specified!";
	
	private String newSaveLocation;
	
	public StoreCommand(String fileLocation) {
		newSaveLocation = fileLocation;
	}

	@Override
	public CommandResult execute() {
		try {
			newSaveLocation = model.changeStorageFilePath(newSaveLocation);
		} catch (SelectedFilePathEmptyException e) {
			return new CommandResult(MESSAGE_CANCEL_STORE_OPERATION);
		} catch (IOException e) {
			return new CommandResult(MESSAGE_SAVE_CONFIG_FAIL);
		}
		return new CommandResult(String.format(MESSAGE_SUCCESS, newSaveLocation));
	}

}
