package com.drawing.datastructure;

import java.io.File;
import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;

import com.drawing.application.FingerDrawingActivity;
import com.drawing.test2.R;

/**
 * Object that represents a Person entity within the object structure
 * 
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 * 
 */
public class PersonObject extends DrawingLeaf {

	private int MAXSIZE = 1500;
	
	transient private Bitmap contactPhoto;

	transient private Rect sourceRect;

	transient private Rect destinationRect;

	transient private Paint paint;

	transient private Uri imageUri;

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -3168176645885430101L;

	private String contactName;

	private String phoneNumber;

	private String eMail;

	private float x;

	private float y;

	private int pictureSize = -1;

	private String stringImageUri;

	private final float offset = 20;

	private final float textSize = 20;

	private float textScale;

	float[] points;

	private float scale;

	private final int frameOffset = 3;

	/**
	 * Constructor of the object that represents a Person within the object
	 * structure
	 * 
	 * @param path
	 *            The path object that visually represents this component
	 * @param pathMatrix
	 *            The transformation matrix that belongs to the respective path
	 * @param contactName
	 *            The name of the phone contact that is associated with this
	 *            object
	 * @param phoneNumber
	 *            The phone number of the phone contact that is associated with
	 *            this object
	 * @param eMail
	 *            The email address of the phone contact that is associated with
	 *            this object
	 * @param contactPhoto
	 *            The contact photo of the phone contact that is associated with
	 *            this object
	 * @param imageUri
	 *            The {@link Uri} for the contact photo
	 * @see com.drawing.datastructure.DrawingLeaf
	 */
	public PersonObject(CustomPath path, Matrix pathMatrix, String contactName,
			String phoneNumber, String eMail, Bitmap contactPhoto,
			Uri imageUri, float scale) {

		super(path, pathMatrix);

		if (scale == 0)
			scale = 1.0f;

		int imageSize = (int) (96 * scale);

		if(imageSize > MAXSIZE)
		{
			imageSize = MAXSIZE;
		}
		
		this.contactName = contactName;

		this.phoneNumber = phoneNumber;

		this.eMail = eMail;

		this.scale = scale;

		if (contactPhoto != null) {

			contactPhoto = Bitmap.createScaledBitmap(contactPhoto, imageSize,
					imageSize, false);

			this.contactPhoto = contactPhoto;

		}

		paint = new Paint();

		paint.setColor(Color.DKGRAY);

		this.imageUri = imageUri;

		if (imageUri != null) {
			stringImageUri = imageUri.toString();
		} else {
			stringImageUri = null;
		}

	}

	/**
	 * @return Name of the contact
	 */
	public String getContactName() {
		return contactName;
	}

	/**
	 * @param contactName
	 *            Name of the contact
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	/**
	 * 
	 * @return Phone number of the Contact
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * 
	 * @param phoneNumber
	 *            Phone Number of the contact
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * 
	 * @return Email address of the contact
	 */
	public String geteMail() {
		return eMail;
	}

	/**
	 * 
	 * @param eMail
	 *            Email address of the contact
	 */
	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	/**
	 * 
	 * @return contact image of the contact
	 */
	public Bitmap getContactPhoto() {
		return contactPhoto;
	}

	/**
	 * 
	 * @param contactPhoto
	 *            contact image of the contact
	 */
	public void setContactPhoto(Bitmap contactPhoto) {
		this.contactPhoto = contactPhoto;
	}

	/**
	 * Draw method for <b>PersonObjects</b>
	 * 
	 * @param canvas
	 *            The canvas object of the calling <b>DrawView</b> and
	 *            accordingly applying necessary geometrical transformations
	 * @param matrix
	 *            The current transformation matrix of the respective
	 *            <b>Canvas</b>
	 * @param scale
	 *            The current scale that is included in the transformation
	 *            matrix
	 * @see com.drawing.application.DrawView
	 */
	public void drawContactObjects(Canvas canvas, Matrix matrix) {

		textScale = 1.0f;

		destinationRect = sourceRect;

		RectF tempRect = new RectF();

		path.computeBounds(tempRect, true);

		if (!highlighted) {

			paint.setColor(Color.DKGRAY);

		} else {

			paint.setColor(Color.CYAN);

		}

		matrix.mapRect(tempRect);

		destinationRect = new Rect((int) tempRect.left - frameOffset,
				(int) tempRect.top - frameOffset, (int) tempRect.right
						+ frameOffset, (int) tempRect.bottom + frameOffset);

		pictureSize = destinationRect.height();

		try {

			textScale = tempRect.width() / contactPhoto.getWidth();

			points = new float[] { path.getVertices().get(0).x,
					path.getVertices().get(0).y, pictureSize };

			matrix.mapPoints(points);

			Rect frame = new Rect(destinationRect.left - frameOffset,
					destinationRect.top - frameOffset, destinationRect.right
							+ frameOffset, destinationRect.bottom + frameOffset);
			
			if(highlighted)
			canvas.drawRect(frame, paint);

			
			canvas.drawBitmap(contactPhoto, null, destinationRect, paint);

		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}

		paint.setTextSize(textSize * textScale * scale);

		if(Math.abs(tempRect.right - tempRect.left) > 35)
		canvas.drawText(contactName, points[0],
				points[1] + destinationRect.height() + offset * textScale
						* scale, paint);

	}

	/**
	 * Setter for the Position of the upper left corner of the
	 * <b>PersonObject</b>
	 * 
	 * @param x
	 *            x Position of the upper left corner
	 * @param y
	 *            y Position of the upper left corner
	 */
	public void setPosition(float x, float y) {

		float[] anchor = new float[] { x, y, pictureSize };

		Matrix matrix = new Matrix();

		matrix.setValues(pathMatrixValues);

		Matrix inverse = new Matrix();

		matrix.invert(inverse);

		inverse.mapPoints(anchor);

		this.x = anchor[0];
		this.y = anchor[1];
		pictureSize = (int) anchor[2];

		sourceRect = new Rect((int) this.x, (int) this.y, (int) this.x
				+ pictureSize, (int) this.y + pictureSize);

		CustomPath tempPath = new CustomPath();

		if (pictureSize > -1) {

			tempPath = generateVertices(this.x, this.y, pictureSize,
					pictureSize);

		} else {

			tempPath = generateVertices(this.x, this.y,
					contactPhoto.getWidth(), contactPhoto.getHeight());
		}

		tempPath.setHighlighted(path.isHighlighted());
		tempPath.setType(path.getType());

		UUID uuid = path.getUid();

		path = tempPath;

		//path.setUid(uuid);

	}

	/**
	 * Creating a path representation for the <b>PersonObject</b> including the
	 * respective vertex data
	 * 
	 * @param x0
	 *            The upper left x coordinate
	 * @param y0
	 *            The upper left y coordinate
	 * @param width
	 *            The width of the object
	 * @param height
	 *            The height of the object
	 * @return A <b>CustomPath</b> object representing the <b>PersonObject</b>
	 */
	public CustomPath generateVertices(float x0, float y0, int width, int height) {

		CustomPath tempPath = new CustomPath();

		float x1, y1, x2, y2, x3, y3, tempCounterX, tempCounterY = 0;

		float samplingOffset = 5;

		x1 = x0;
		y1 = y0 + height;
		x2 = x0 + width;
		y2 = y0;
		x3 = x2;
		y3 = y1;

		tempPath.moveTo(x0, y0);

		tempCounterY = y0;

		while (tempCounterY + samplingOffset < y1) {
			tempCounterY += samplingOffset;

			tempPath.lineTo(x0, tempCounterY);
		}

		tempPath.lineTo(x1, y1);

		tempCounterX = x1;
		tempCounterY = y1;

		while (tempCounterX + samplingOffset < x2
				&& tempCounterY + samplingOffset < y2) {

			tempCounterX += samplingOffset;
			tempCounterY += samplingOffset;

			tempPath.lineTo(tempCounterX, tempCounterY);

		}

		tempPath.lineTo(x2, y2);

		tempCounterX = x2;

		while (tempCounterX - samplingOffset > x0) {

			tempCounterX -= samplingOffset;

			tempPath.lineTo(tempCounterX, y0);

		}

		tempPath.lineTo(x0, y0);

		tempCounterX = x0;
		tempCounterY = y0;

		while (tempCounterX + samplingOffset < x3
				&& tempCounterY + samplingOffset < y3) {

			tempCounterX += samplingOffset;
			tempCounterY += samplingOffset;

			tempPath.lineTo(tempCounterX, tempCounterY);

		}

		tempPath.lineTo(x3, y3);

		tempCounterY = y3;

		while (tempCounterY - samplingOffset > y2) {
			tempCounterY -= samplingOffset;

			tempPath.lineTo(x2, tempCounterY);
		}

		tempPath.lineTo(x2, y2);

		tempCounterX = x2;
		tempCounterY = y2;

		while (tempCounterX - samplingOffset > x1
				&& tempCounterY - samplingOffset > y1) {

			tempCounterX -= samplingOffset;
			tempCounterY -= samplingOffset;

			tempPath.lineTo(tempCounterX, tempCounterY);

		}

		tempPath.lineTo(x1, y1);

		tempCounterX = x1;

		while (tempCounterX + samplingOffset < x3) {
			tempCounterX += samplingOffset;

			tempPath.lineTo(tempCounterX, y3);
		}

		return tempPath;
	}

	/**
	 * @see com.drawing.datastructure.DrawingLeaf#redrawPathsafterDeserialization()
	 */
	public void redrawPathsafterDeserialization(FingerDrawingActivity fda) {

		// restore Bitmap

		if (stringImageUri != null) {
			imageUri = Uri.parse(stringImageUri);
		}

		BitmapFactory.Options bfOptions=new BitmapFactory.Options();

        bfOptions.inDither=false;                     //Disable Dithering mode

        bfOptions.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared

        bfOptions.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future

        bfOptions.inTempStorage=new byte[32 * 1024]; 
		
		try {

			if (imageUri != null) {

				contactPhoto = MediaStore.Images.Media.getBitmap(
						fda.getContentResolver(), imageUri);

			} else {

				contactPhoto = BitmapFactory.decodeResource(fda.getResources(),
						R.drawable.ic_contact_picture5, bfOptions);

			}

		} catch (Exception e) {
			e.printStackTrace();

			// contact has been deleted in address book, but reference is still
			// existing -> assign standard picture
			contactPhoto = BitmapFactory.decodeResource(fda.getResources(),
					R.drawable.ic_contact_picture5, bfOptions);
		}

		paint = new Paint();
		paint.setColor(Color.DKGRAY);

		scale = 1.0f;

		super.redrawPathsafterDeserialization(fda);

	}

	/**
	 * Getter for the {@link Uri} of the {@link PersonObject}'s contact photo
	 * 
	 * @return The {@link Uri} for the contact photo of this
	 *         {@link PersonObject}
	 */
	public Uri getImageUri() {
		return imageUri;
	}

	/**
	 * Setter for the {@link Uri} of the {@link PersonObject}'s contact photo
	 * 
	 * @param imageUri
	 *            The {@link Uri} for the contact photo of this
	 *            {@link PersonObject}
	 */
	public void setImageUri(Uri imageUri) {
		this.imageUri = imageUri;
		if (imageUri != null) {
			stringImageUri = imageUri.toString();
		}
	}

}
