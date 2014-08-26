package com.drawing.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents.Insert;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.drawing.datastructure.CustomConstants;
import com.drawing.datastructure.CustomPath;
import com.drawing.datastructure.DrawingCompositeWord;
import com.drawing.datastructure.PersonObject;
import com.drawing.datastructure.TopicObject;
import com.drawing.datastructure.TopicType;
import com.drawing.test2.R;
import com.phatware.android.RecoInterface.RecognizerService;
import com.phatware.android.RecoInterface.WritePadAPI;

/**
 * 
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 * 
 */

public class FingerDrawingActivity extends Activity {

	// begin region PhatWare

	/**
	 * @author Christian Brändel TU Dresden / SAP Research Dresden
	 * @author PhatWare Corp
	 * 
	 *         Interface that enables to use a Handler that listens to events
	 *         from the handwriting recognition class
	 * 
	 */
	public interface OnInkViewListener {

		/**
		 * 
		 */
		void cleanView();

		/**
		 * Getter for the handler of handwriting recognition events
		 * 
		 * @return The handler for handwriting recognition events
		 */
		Handler getHandler();
	}

	private OnInkViewListener mListener;

	private String[] filenames;

	@SuppressWarnings("unused")
	private boolean mRecoInit;

	/**
	 * The service binding for the handwriting recognition API
	 */
	public RecognizerService mBoundService;
	
	private boolean boundToService = true;
		
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			mBoundService = ((RecognizerService.RecognizerBinder) service)
					.getService();
			mBoundService.mHandler = mListener.getHandler();
						
			
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			mBoundService = null;
	
			boundToService = false;
		}
	};

	// end region PhatWare

	/**
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	public void onConfigurationChanged(android.content.res.Configuration newConfig) 
	{
	
		//recalculate size of the canvas, after the orientation of the device has changed
		
		drawView.recalculateScreenSize();
		
		super.onConfigurationChanged(newConfig);
		
	};
	
	protected void onDestroy() {

		if(boundToService && mConnection != null)
		{
		unbindService(mConnection);
		boundToService = false;
		}
		
		super.onDestroy();
		
	}
	
	@Override
	protected void onPause() {
	

		
		super.onPause();
	}
	
	@Override
	protected void onResume() {
					
		super.onResume();
	}
	
	/**
	 * Overwrote the Android back key functionality in order to ensure that
	 * temporary changes to the canvas and created objects aren't discarded  
	 * @see android.app.Activity#onBackPressed()
	 */
	public void onBackPressed()
	{
		
		   Intent setIntent = new Intent(Intent.ACTION_MAIN);
		   setIntent.addCategory(Intent.CATEGORY_HOME);
		   setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		   startActivity(setIntent);
		
	}
	
	DrawView drawView;
	
	AnnotationView annotationView;

	private ImageButton annotateButton, modifyButton;

	private ImageButton undoButton, clearButton, groupButton, ungroupButton;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * @see android.os.bundle.Bundle
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		// begin region PhatWare

		// load and initialize recognizer library
		File dir = getDir("user", 0);
		String sDir = dir.getAbsolutePath();
		String lName = null;

		int init = WritePadAPI.recoInit(sDir, WritePadAPI.LANGUAGE_ENGLISH);
		if (init >= 0) {
			lName = WritePadAPI.getLanguageName();
			boolean res = false;
			if ((init & WritePadAPI.FLAG_CORRECTOR) == 0)
				res = WritePadAPI.recoResetAutocorrector();
			if ((init & WritePadAPI.FLAG_ANALYZER) == 0)
				res = WritePadAPI.recoResetLearner();
			if ((init & WritePadAPI.FLAG_USERDICT) == 0)
				res = WritePadAPI.recoResetUserDict();
			if ((init & WritePadAPI.FLAG_MAINDICT) == 0) {
				try {
					// if dictionary was
					InputStream dict = getAssets().open("English.dct");
					if (dict != null) {
						int size = dict.available();
						// Read the entire asset into a local byte buffer.
						byte[] buffer = new byte[size + 1];
						dict.read(buffer);
						dict.close();
						// true for main; false for alternative dictionary
						res = WritePadAPI.recoSetDict(buffer,
								WritePadAPI.FLAG_MAINDICT);

						if (res) {
							Log.d("recoInit", "Main dictionary did not load!");
						}

					}
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			mRecoInit = true;

			Log.d("recoInit", String.format(
					"Engine initialized: Lnaguage %s; Flags: %08x.", lName,
					init));
		}

		// end region PhatWare

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

//		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);
		
		drawView = (DrawView) findViewById(R.id.drawView);

		// set LinearLayout for recognition suggestions
		drawView.initializeAlternativeWordListView((LinearLayout) findViewById(R.id.alternativeWordsPanel));

		// set TypeSelection menu for Topic objects
		drawView.initializeTypeSelectionMenu((RelativeLayout) findViewById(R.id.topicTypeSelectionButtons));

		drawView.requestFocus();

		mListener = drawView;


		initializeButtonListeners();
		

		// startService
		
		ComponentName recognizerService = startService(new Intent(
				this, RecognizerService.class));

		bindService(new Intent(this,
				RecognizerService.class), mConnection, Context.BIND_AUTO_CREATE);

		boundToService = true;
		
		filenames = getFilesDir().list();
		
		initializeTypeList();
		
	}
	
	private boolean annotationModeEnabled = false;
	
	public void toggleAnnotateMode()
	{
		
		
		if (annotationModeEnabled) {
			
			//store last drawn annotation to highlighted object if existent
			annotationView.assignAnnotationToSelectedObject();
			
			//remove Highlights
			annotationView.removeHighlightFromPaths();
			
			setContentView(R.layout.main);
			
			drawView = (DrawView) findViewById(R.id.drawView);
			
			drawView.requestFocus();
			
			initializeButtonListeners();
			
			// set TypeSelection menu for Topic objects
			drawView.initializeTypeSelectionMenu((RelativeLayout) findViewById(R.id.topicTypeSelectionButtons));
			
			// set LinearLayout for recognition suggestions
			drawView.initializeAlternativeWordListView((LinearLayout) findViewById(R.id.alternativeWordsPanel));
			
			drawView.setDrawingObjects(annotationView.getDrawingObjects());
			
			drawView.restoreMatrices(annotationView.getMatrices());
			
			annotationModeEnabled = false;
						
			annotationView = null;
			
		} else {
			
			
			//remove Highlights
			drawView.removeHighlightFromPaths();
			
			setContentView(R.layout.annotation_main);
			
			annotationView = (AnnotationView) findViewById(R.id.annotationView);
			
			annotationView.requestFocus();
			
			annotationView.initializeButtonListeners();

			
			annotationView.setDrawingObjects(drawView.getDrawingObjects());
			
			annotationView.restoreMatrices(drawView.getMatrices());
			
			annotationModeEnabled = true;
			
			drawView = null;
			
		}
	}	
	
	private void initializeButtonListeners() {

		
		modifyButton = (ImageButton) findViewById(R.id.modifyButton);

		annotateButton = (ImageButton) findViewById(R.id.annotateButton);

		undoButton = (ImageButton) findViewById(R.id.undoButton);

		clearButton = (ImageButton) findViewById(R.id.clearButton);

		groupButton = (ImageButton) findViewById(R.id.groupButton);

		ungroupButton = (ImageButton) findViewById(R.id.ungroupButton);

		groupButton.setEnabled(false);

		clearButton.setEnabled(false);

		undoButton.setEnabled(false);

		ungroupButton.setVisibility(Button.INVISIBLE);

		annotateButton.setOnClickListener(new View.OnClickListener() {

			

			public void onClick(View v) {

				toggleAnnotateMode();

			}
		});

		modifyButton.setOnClickListener(new View.OnClickListener() {

			private boolean enabled = false;

			public void onClick(View v) {

				if (enabled) {
					drawView.toggleModificationMode(false);
					enabled = false;
				} else {
					drawView.toggleModificationMode(true);
					enabled = true;
				}

			}
		});

		undoButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				drawView.undo();

			}
		});

		clearButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				drawView.clear();

			}
		});

		groupButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				drawView.groupHighlightedObjects();

			}
		});

		ungroupButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				drawView.ungroupHighlightedObjects();

			}
		});
		
	}

	/**
	 * The list of all available topic types that can be assigned to {@link TopicObject}s
	 */
	public ArrayList<TopicType> topicTypeList;

	private void initializeTypeList() {
		
		topicTypeList = DataStorageHelper.loadTypeList(this);
		
		if(topicTypeList.size() == 0)
		{
			
			//initialize a list with two basic types
			
			topicTypeList = new ArrayList<TopicType>();
			
			topicTypeList.add(new TopicType("Topic", getResources().getColor(R.color.green), "The default topic type", TopicType.generateID(), "T"));
			
			topicTypeList.add(new TopicType("Organization", getResources().getColor(R.color.blue), "The default organization type", TopicType.generateID(), "O"));
			
			topicTypeList.add(new TopicType("New", getResources().getColor(R.color.lightorange), "A new collection", TopicType.generateID(), "N"));
			
		}
		
	}
	
	/**
	 * Save the current state of the list of available topic types that can be assigned to composite objects
	 */
	public void saveTypeList()
	{
		
		DataStorageHelper.saveTypeList(this, topicTypeList);
		
	}

	/**
	 * Initialize Options menu
	 * 
	 * @param menu
	 *            The Android options menu
	 * @return true if the menu was successfully created
	 */
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		
		inflater.inflate(R.menu.main_menu , menu);
		
//		menu.add(0, CLEAR_ID, 0, "Clean HW");
//
//		menu.add(0, STORE_ID, 0, "Store State");
//
//		menu.add(0, LOAD_ID, 0, "Load State");
//
//		menu.add(0, DRAWGRADIENT_ID, 0, "Toggle Gradient");

//		return super.onCreateOptionsMenu(menu);
		
		return true;
	}

	/**
	 * Define the behavior of the menu buttons
	 * 
	 * @param item
	 *            The <b>MenuItem</b> that was selected
	 * @return true if an item was successfully selected
	 */
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.clear_hw:
			this.drawView.confirmPlaceholderWord();
			break;
			
		case R.id.save:

			showSaveDialog();

			break;
			
		case R.id.load:

			showLoadDialog();

			break;

//		case R.id.toggle_gradient:
//
//			if(annotationModeEnabled)
//				toggleAnnotateMode();
//			
//			if (drawView.isDrawGradient())
//			
//				drawView.setDrawGradient(false);
//			
//			else
//				
//				drawView.setDrawGradient(true);

		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Handler for the started contact picker <b>Intent</b>
	 * 
	 * @param reqCode
	 *            The integer value of the request code
	 * @param resultCode
	 *            The integer value of the result code
	 * @param resultData
	 *            The resulting data
	 */
	public void onActivityResult(int reqCode, int resultCode, Intent resultData) {
		super.onActivityResult(reqCode, resultCode, resultData);

		try {

			if (reqCode == CustomConstants.PICK_CONTACT)
				if (resultCode == Activity.RESULT_OK) {
					Uri contactData = resultData.getData();

					importContact(contactData);

				}

		} catch (IllegalArgumentException e) {

			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void importContact(Uri contactData) {
		ContentResolver contentResolver = getContentResolver();

		Cursor cursor = contentResolver.query(contactData, null, null, null,
				null);

		if (cursor.moveToFirst()) {
			String id = cursor.getString(cursor
					.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
			String contactName = "";
			String phoneNumber = "";
			Uri photoThumbnailUri = null;
			String eMail = "";

			Cursor phoneCursor = contentResolver.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
					new String[] { id }, null);

			if (phoneCursor.moveToFirst()) {
				contactName = phoneCursor
						.getString(phoneCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				phoneNumber = phoneCursor
						.getString(phoneCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

				try {

					String uriString = phoneCursor
							.getString(phoneCursor
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));

					photoThumbnailUri = Uri.parse(uriString);
				} catch (NullPointerException npe) {
					photoThumbnailUri = null;
				}
			}

			Cursor eMailCursor = contentResolver.query(
					ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
					new String[] { id }, null);

			if (eMailCursor.moveToFirst()) {
				eMail = eMailCursor
						.getString(eMailCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
			}

			Bitmap bitmap = null;

			if (photoThumbnailUri != null)
				try {
					bitmap = MediaStore.Images.Media.getBitmap(
							getContentResolver(), photoThumbnailUri);
					
					bitmap.setDensity(DisplayMetrics.DENSITY_LOW);
					
				} catch (FileNotFoundException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}

			PersonObject person = new PersonObject(new CustomPath(),
					new Matrix(), contactName, phoneNumber, eMail, bitmap,
					photoThumbnailUri, 1);

			drawView.addContact(person);

		}
	}

	/**
	 * Method that opens the contact list and inserts the word as preset for the
	 * contact name, if the creation of a new contact was selected
	 * 
	 * @param word
	 *            The name of the contact that should be created
	 */
	public void createNewContact(String word) {

		final String contactName = word;
		
		AlertDialog.Builder createNewContactDialogBuilder = new AlertDialog.Builder(this);

		createNewContactDialogBuilder.setTitle("New Contact");

		createNewContactDialogBuilder.setMessage("Do you want to create a new contact with the name " + word + "?");
		
		createNewContactDialogBuilder.setPositiveButton("Confirm",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
						intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);

						intent.putExtra(Insert.NAME, contactName);
						startActivity(intent);
						
					}
				});

		createNewContactDialogBuilder.setNegativeButton("Abort",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

					}
				});

		AlertDialog saveDialog = createNewContactDialogBuilder.create();

		saveDialog.show();
		


	}



	/**
	 * Set the pixel format of the window in order to enhance the graphical
	 * quality
	 * 
	 * @see android.app.Activity#onAttachedToWindow()
	 */
	public void onAttachedToWindow() {

		super.onAttachedToWindow();

		Window window = getWindow();

		window.setFormat(PixelFormat.RGBA_8888);

	}

	/**
	 * Method that is invoked after the save button was pressed and that
	 * initiates the storage of the temporary state of the application
	 * 
	 * @param filename
	 *            The filename that will be associated with the stored state
	 */
	private void storeData(String filename) {
		if(annotationModeEnabled)
			toggleAnnotateMode();
		
		DataStorageHelper.storeData(this, drawView, filename);
	}

	/**
	 * Method that is invoked after the load button was pressed and that
	 * initiates loading the previously stored state of the application
	 * 
	 * @param filename
	 *            The filename that will be associated with the stored state
	 */
	private void loadData(String filename) {
		if(annotationModeEnabled)
			toggleAnnotateMode();
			
		drawView.setDrawingObjects(DataStorageHelper.loadData(this, filename));
	}

	/**
	 * Display error message in case of an invalid filename
	 */
	private void showStorageError() {
		Toast toast = Toast.makeText(this,
				"Invalid filename! The content could not be stored!",
				Toast.LENGTH_LONG);
		toast.show();
	}

	/**
	 * Method that is invoked after the save button in the save_state_dialog
	 * widget was pressed and initiates the storage of the temporary state of
	 * the application
	 */
	private void showSaveDialog() {

		final EditText input = new EditText(this);
		input.setSingleLine();
		input.setText("");

		AlertDialog.Builder saveDialogBuilder = new AlertDialog.Builder(this);

		saveDialogBuilder.setTitle("Save State");

		saveDialogBuilder.setMessage("Enter filename");

		saveDialogBuilder.setView(input);

		saveDialogBuilder.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						boolean fileAlreadyExisting = false;

						String filename = input.getText().toString();

						for (String name : filenames) {
							if (name.compareTo(filename) == 0) {
								fileAlreadyExisting = true;
								break;
							}
						}

						if ((input.getText().toString().length() > 0 && input
								.getText().toString() != null)
								&& !fileAlreadyExisting)

							storeData(filename);

						else if (fileAlreadyExisting) {

							showOverwriteFileDialog(filename);

						} else

							showStorageError();

					}
				});

		saveDialogBuilder.setNegativeButton("Abort",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

					}
				});

		AlertDialog saveDialog = saveDialogBuilder.create();

		saveDialog.show();

	}

	/**
	 * Method that is invoked if the selected filename for storing the current
	 * application state is already existing
	 * @param filename The name of the file that should be stored but is already existing
	 */
	private void showOverwriteFileDialog(final String filename)
	{
		
		AlertDialog.Builder overwriteDialogBuilder = new AlertDialog.Builder(this);

		overwriteDialogBuilder.setTitle("Filename already existing!");

		overwriteDialogBuilder.setMessage("Do you want to overwrite the existing file?");
		
		overwriteDialogBuilder.setPositiveButton("Overwrite", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {

				//overwrite the existing file
				storeData(filename);
				
			}
		});
		
		overwriteDialogBuilder.setNegativeButton("Abort", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
			
				//close the dialog
				
			}
		});
		
		AlertDialog overwriteDialog = overwriteDialogBuilder.create();
		
		overwriteDialog.show();
		
	}
	
	
	/**
	 * The file name that was selected in the load dialog
	 */
	public String selectedFileName;
	
	/**
	 * The name property of the contact that was selected in the import dialog 
	 */
	public String selectedContactName;
	
	/**
	 * Method that is invoked after the load button was pressed and initiates
	 * loading the previously stored state of the application
	 */
	private void showLoadDialog() {
		
		File dir = new File(getFilesDir().toString());
		
		File[] files = dir.listFiles();
		
		
		for(File f:files)
		{
			Long lastModified = f.lastModified(); 
		}

		filenames = getFilesDir().list();
		
		final Spinner listBox = new Spinner(this);

		ArrayList<String> elements = new ArrayList<String>();

		String[] listItems;
		
		
		

		for (String name : filenames) {
			if (!name.contains("_matrices") && !name.contains("sketchviz_topictypelist")) {
				
				elements.add(name);
				
			}
		}

		//TODO: configure order of list elements here
		
		listItems = new String[elements.size()];

		Collections.sort(elements);
		
		for (int i = 0; i < elements.size(); i++) {

			listItems[i] = elements.get(i);

		}

		
		 
		
		if(listItems.length > 0)
		selectedFileName = listItems[0];
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item , listItems);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		listBox.setAdapter(adapter);

		listBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				
				Object item = parent.getItemAtPosition(pos);
				
				selectedFileName = item.toString();
				
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		AlertDialog.Builder saveDialogBuilder = new AlertDialog.Builder(this);

		saveDialogBuilder.setTitle("Load State");

		saveDialogBuilder.setMessage("Choose a file");

		saveDialogBuilder.setView(listBox);

		saveDialogBuilder.setPositiveButton("Load",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						loadData(selectedFileName);
						
					}
				});

		saveDialogBuilder.setNeutralButton("Delete",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						deleteData(selectedFileName);
						
					}
				});
		
		saveDialogBuilder.setNegativeButton("Abort",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

					}
				});

		AlertDialog saveDialog = saveDialogBuilder.create();

		saveDialog.show();

	}

protected void deleteData(String fileName) {

	boolean deleted = false;
	
	deleted = DataStorageHelper.deleteData(this, fileName);
	
	if(deleted)
	{
		
		Toast toast = Toast.makeText(this,
				"The file has been deleted successfully!",
				Toast.LENGTH_LONG);
		toast.show();
		
	}else
	{
		Toast toast = Toast.makeText(this,
				"An error has occurred during deletion! Please try again!",
				Toast.LENGTH_LONG);
		toast.show();
	}
		
	}

	/**
	 * Alert dialog that offers all available contacts that matched the submitted search term
	 * @param matchingContacts The list containing the results of the query
	 * @param contactWord The word that was used as query argument
	 */
	public void showMatchingContactsSelection(
			ArrayList<PersonObject> matchingContacts, DrawingCompositeWord contactWord) {
	
		final ArrayList<PersonObject> selectableContacts = matchingContacts;
		
		final DrawingCompositeWord contactWordToDelete = contactWord;
						
		final Spinner listBox = new Spinner(this);
		
		String [] listItems = new String[matchingContacts.size()];
		
		for(int i = 0; i < matchingContacts.size(); i++)
		{
			listItems[i] = matchingContacts.get(i).getContactName();
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item , listItems);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		listBox.setAdapter(adapter);

		listBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				
				Object item = parent.getItemAtPosition(pos);
				
				selectedContactName = item.toString();
				
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		AlertDialog.Builder selectContactDialogBuilder = new AlertDialog.Builder(this);

		selectContactDialogBuilder.setTitle("Contact Match");

		selectContactDialogBuilder.setMessage("Choose a contact to import!");

		selectContactDialogBuilder.setView(listBox);

		selectContactDialogBuilder.setPositiveButton("Import",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						for(PersonObject person : selectableContacts)
						{
							if(person.getContactName().compareTo(selectedContactName) == 0 )
							{
								addContactToView(person, contactWordToDelete);
							}
						}
						
					

}
				});
		
		selectContactDialogBuilder.setNeutralButton("New Contact", 
				new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
					
						createNewContact(selectedContactName);
						
					}
				});

		selectContactDialogBuilder.setNegativeButton("Abort",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

					}
					
				});

		AlertDialog selectContactDialog = selectContactDialogBuilder.create();

		selectContactDialog.show();
				
	}
	
	private void addContactToView(PersonObject person, DrawingCompositeWord wordToDelete) {

		drawView.getDrawingObjects().removeCompositeComponent(wordToDelete);
		
		drawView.addContact(person);
		
	}

	
}