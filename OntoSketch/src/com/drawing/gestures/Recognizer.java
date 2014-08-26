package com.drawing.gestures;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.drawing.datastructure.GestureTypes;

/**
 * The source of several methods was taken from an experimental JAVA
 * implementation of the $1 Recognizer:
 * http://depts.washington.edu/aimgroup/proj/dollar/
 * 
 * @author Florian Schneider TU Dresden / SAP Next Business and Technology Dresden 
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 * @author Norman Papernick
 * 
 */

public class Recognizer {
	List<Template> Templates = new ArrayList<Template>();

	// Recognizer class constants
	//
	int NumTemplates = 16;
	int NumPoints = 64;
	float SquareSize = 250.0f;
	float HalfDiagonal = (float) (0.5 * Math
			.sqrt(250.0 * 250.0 + 250.0 * 250.0));
	float AngleRange = 45.0f;
	float AnglePrecision = 2.0f;
	float Phi = (float) (0.5 * (-1.0 + Math.sqrt(5.0))); // Golden Ratio
	float Infinity = (float) 1e9;

	/**
	 * Constructor of the <b>Recognizer</b> class that is responsible for
	 * gesture recognition. The gesture templates are initialized here.
	 */
	public Recognizer() {

		// rectangle
		List<Point> point2 = new ArrayList<Point>(Arrays.asList(new Point(78,
				149), new Point(78, 153), new Point(78, 157),
				new Point(78, 160), new Point(79, 162), new Point(79, 164),
				new Point(79, 167), new Point(79, 169), new Point(79, 173),
				new Point(79, 178), new Point(79, 183), new Point(80, 189),
				new Point(80, 193), new Point(80, 198), new Point(80, 202),
				new Point(81, 208), new Point(81, 210), new Point(81, 216),
				new Point(82, 222), new Point(82, 224), new Point(82, 227),
				new Point(83, 229), new Point(83, 231), new Point(85, 230),
				new Point(88, 232), new Point(90, 233), new Point(92, 232),
				new Point(94, 233), new Point(99, 232), new Point(102, 233),
				new Point(106, 233), new Point(109, 234), new Point(117, 235),
				new Point(123, 236), new Point(126, 236), new Point(135, 237),
				new Point(142, 238), new Point(145, 238), new Point(152, 238),
				new Point(154, 239), new Point(165, 238), new Point(174, 237),
				new Point(179, 236), new Point(186, 235), new Point(191, 235),
				new Point(195, 233), new Point(197, 233), new Point(200, 233),
				new Point(201, 235), new Point(201, 233), new Point(199, 231),
				new Point(198, 226), new Point(198, 220), new Point(196, 207),
				new Point(195, 195), new Point(195, 181), new Point(195, 173),
				new Point(195, 163), new Point(194, 155), new Point(192, 145),
				new Point(192, 143), new Point(192, 138), new Point(191, 135),
				new Point(191, 133), new Point(191, 130), new Point(190, 128),
				new Point(188, 129), new Point(186, 129), new Point(181, 132),
				new Point(173, 131), new Point(162, 131), new Point(151, 132),
				new Point(149, 132), new Point(138, 132), new Point(136, 132),
				new Point(122, 131), new Point(120, 131), new Point(109, 130),
				new Point(107, 130), new Point(90, 132), new Point(81, 133),
				new Point(76, 133)));
		AddTemplate("rectangle", point2, GestureTypes.INDIVIDUAL);

		// circle
		List<Point> point3 = new ArrayList<Point>(Arrays.asList(new Point(127,
				141), new Point(124, 140), new Point(120, 139), new Point(118,
				139), new Point(116, 139), new Point(111, 140), new Point(109,
				141), new Point(104, 144), new Point(100, 147), new Point(96,
				152), new Point(93, 157), new Point(90, 163),
				new Point(87, 169), new Point(85, 175), new Point(83, 181),
				new Point(82, 190), new Point(82, 195), new Point(83, 200),
				new Point(84, 205), new Point(88, 213), new Point(91, 216),
				new Point(96, 219), new Point(103, 222), new Point(108, 224),
				new Point(111, 224), new Point(120, 224), new Point(133, 223),
				new Point(142, 222), new Point(152, 218), new Point(160, 214),
				new Point(167, 210), new Point(173, 204), new Point(178, 198),
				new Point(179, 196), new Point(182, 188), new Point(182, 177),
				new Point(178, 167), new Point(170, 150), new Point(163, 138),
				new Point(152, 130), new Point(143, 129), new Point(140, 131),
				new Point(129, 136), new Point(126, 139)));

		 AddTemplate("circle", point3, GestureTypes.CONCEPT);

		// arrow
		List<Point> point7 = new ArrayList<Point>(Arrays.asList(new Point(68,
				222), new Point(70, 220), new Point(73, 218),
				new Point(75, 217), new Point(77, 215), new Point(80, 213),
				new Point(82, 212), new Point(84, 210), new Point(87, 209),
				new Point(89, 208), new Point(92, 206), new Point(95, 204),
				new Point(101, 201), new Point(106, 198), new Point(112, 194),
				new Point(118, 191), new Point(124, 187), new Point(127, 186),
				new Point(132, 183), new Point(138, 181), new Point(141, 180),
				new Point(146, 178), new Point(154, 173), new Point(159, 171),
				new Point(161, 170), new Point(166, 167), new Point(168, 167),
				new Point(171, 166), new Point(174, 164), new Point(177, 162),
				new Point(180, 160), new Point(182, 158), new Point(183, 156),
				new Point(181, 154), new Point(178, 153), new Point(171, 153),
				new Point(164, 153), new Point(160, 153), new Point(150, 154),
				new Point(147, 155), new Point(141, 157), new Point(137, 158),
				new Point(135, 158), new Point(137, 158), new Point(140, 157),
				new Point(143, 156), new Point(151, 154), new Point(160, 152),
				new Point(170, 149), new Point(179, 147), new Point(185, 145),
				new Point(192, 144), new Point(196, 144), new Point(198, 144),
				new Point(200, 144), new Point(201, 147), new Point(199, 149),
				new Point(194, 157), new Point(191, 160), new Point(186, 167),
				new Point(180, 176), new Point(177, 179), new Point(171, 187),
				new Point(169, 189), new Point(165, 194), new Point(164, 196)));
		// AddTemplate("arrow", point7, GestureTypes.PERSON);

	}

	/**
	 * Compare a given array of <b>Point</b> objects to the predefined templates
	 * and figure out the best suiting
	 * 
	 * @param points
	 *            The array of <b>Point</b> objects that should be examined
	 * @return The result of the examinations in form of a <b>Result</b> object
	 */
	public Result Recognize(List<Point> points) {

		points = Operations.Resample(points, NumPoints);
		points = Operations.RotateToZero(points);
		points = Operations.ScaleToSquare(points, SquareSize);
		points = Operations.TranslateToOrigin(points);

		// write template to file
		// TODO: disable before release

//		File logFile = new File("sdcard/sketchviz.file");
//		if (!logFile.exists()) {
//			try {
//
//				logFile.createNewFile();
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		try {
//
//			String text = "";
//
//			for (Point point : points) {
//				text += "new Point(" + (int) point.x + ", " + (int) point.y
//						+ "), \n";
//			}
//
//			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
//					true));
//			buf.append(text);
//			buf.newLine();
//			buf.close();
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//		}

		float best = Infinity;
		float sndBest = Infinity;
		int t = -1;
		for (int i = 0; i < Templates.size(); i++) {
			float d = Operations.DistanceAtBestAngle(points, Templates.get(i),
					-AngleRange, AngleRange, AnglePrecision);
			if (d < best) {
				sndBest = best;
				best = d;
				t = i;
			} else if (d < sndBest) {
				sndBest = d;
			}
		}

		float score = (float) (1.0 - (best / HalfDiagonal));
		float otherScore = (float) (1.0 - (sndBest / HalfDiagonal));
		float ratio = otherScore / score;
		// The threshold is arbitrary, and not part of the original code.
		if (t > -1 && score > 0.80) {
			return new Result(Templates.get(t).Name, score, ratio,
					Templates.get(t).Type);
		} else {
			return new Result("- none - ", 0.0f, 1.0f, GestureTypes.NOGESTURE);
		}
	}

	int AddTemplate(String name, List<Point> points, GestureTypes type) {
		Templates.add(new Template(name, points, type));
		int num = 0;
		for (int i = 0; i < Templates.size(); i++) {
			if (Templates.get(i).Name == name) {
				num++;
			}
		}
		return num;
	}

	/**
	 * Delete all stored Templates for gesture Recognition
	 */
	void DeleteUserTemplates() {
		Templates = Operations.subset(Templates, 0, NumTemplates);
	}

}
