/* ************************************************************************************* */
/* *    PhatWare WritePad SDK                                                          * */
/* *    Copyright (c) 1997-2012 PhatWare(r) Corp. All rights reserved.                 * */
/* ************************************************************************************* */

/* ************************************************************************************* *
 *
 * WritePad Android Sample
 *
 * Unauthorized distribution of this code is prohibited. For more information
 * refer to the End User Software License Agreement provided with this 
 * software.
 *
 * This source code is distributed and supported by PhatWare Corp.
 * http://www.phatware.com
 *
 * THIS SAMPLE CODE CAN BE USED  AS A REFERENCE AND, IN ITS BINARY FORM, 
 * IN THE USER'S PROJECT WHICH IS INTEGRATED WITH THE WRITEPAD SDK. 
 * ANY OTHER USE OF THIS CODE IS PROHIBITED.
 * 
 * THE MATERIAL EMBODIED ON THIS SOFTWARE IS PROVIDED TO YOU "AS-IS"
 * AND WITHOUT WARRANTY OF ANY KIND, EXPRESS, IMPLIED OR OTHERWISE,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT SHALL PHATWARE CORP.  
 * BE LIABLE TO YOU OR ANYONE ELSE FOR ANY DIRECT, SPECIAL, INCIDENTAL, 
 * INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY KIND, OR ANY DAMAGES WHATSOEVER, 
 * INCLUDING WITHOUT LIMITATION, LOSS OF PROFIT, LOSS OF USE, SAVINGS 
 * OR REVENUE, OR THE CLAIMS OF THIRD PARTIES, WHETHER OR NOT PHATWARE CORP.
 * HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH LOSS, HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE
 * POSSESSION, USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 * US Government Users Restricted Rights 
 * Use, duplication, or disclosure by the Government is subject to
 * restrictions set forth in EULA and in FAR 52.227.19(c)(2) or subparagraph
 * (c)(1)(ii) of the Rights in Technical Data and Computer Software
 * clause at DFARS 252.227-7013 and/or in similar or successor
 * clauses in the FAR or the DOD or NASA FAR Supplement.
 * Unpublished-- rights reserved under the copyright laws of the
 * United States.  Contractor/manufacturer is PhatWare Corp.
 * 10414 W. Highway 2, Ste 4-121 Spokane, WA 99224
 *
 * ************************************************************************************* */

package com.phatware.android.RecoInterface;

public class WritePadAPI extends Object
{ 
    public static final int FLAG_SEPLET = 0x00000001;
    public static final int FLAG_USERDICT = 0x00000002;
    public static final int FLAG_MAINDICT = 0x00000004;
    public static final int FLAG_ONLYDICT = 0x00000008;
    public static final int FLAG_STATICSEGMENT = 0x00000010;
    public static final int FLAG_SINGLEWORDONLY = 0x00000020;
    public static final int FLAG_INTERNATIONAL = 0x00000040;
    public static final int FLAG_SUGGESTONLYDICT = 0x00000080;
    public static final int FLAG_ANALYZER = 0x00000100;
	public static final int FLAG_CORRECTOR = 0x00000200;
    public static final int FLAG_SPELLIGNORENUM = 0x00000400;
    public static final int FLAG_SPELLIGNOREUPPER = 0x00000800;
    public static final int FLAG_NOSINGLELETSPACE = 0x00001000;
    public static final int FLAG_ENABLECALC = 0x00002000;
    public static final int FLAG_NOSPACE = 0x00004000;
    public static final int FLAG_ALTDICT = 0x00008000;
    public static final int FLAG_ERROR = 0xFFFFFFFF;
	
    // gestures
    public static final int GEST_NONE = 0x00000000;
    public static final int GEST_DELETE = 0x00000001;    //
    public static final int GEST_SCROLLUP = 0x00000002;
    public static final int GEST_BACK = 0x00000004;    //
    public static final int GEST_SPACE = 0x00000008;    //
    public static final int GEST_RETURN = 0x00000010;    //
    public static final int GEST_CORRECT = 0x00000020;
    public static final int GEST_SPELL = 0x00000040;
    public static final int GEST_SELECTALL = 0x00000080;
    public static final int GEST_UNDO = 0x00000100;    //
    public static final int GEST_SMALLPT = 0x00000200;
    public static final int GEST_COPY = 0x00000400;
    public static final int GEST_CUT = 0x00000800;
    public static final int GEST_PASTE = 0x00001000;
    public static final int GEST_TAB = 0x00002000;    //
    public static final int GEST_MENU = 0x00004000;
    public static final int GEST_LOOP = 0x00008000;
    public static final int GEST_REDO = 0x00010000;
    public static final int GEST_SCROLLDN = 0x00020000;
    public static final int GEST_SAVE = 0x00040000;
    public static final int GEST_SENDMAIL = 0x00080000;
    public static final int GEST_OPTIONS = 0x00100000;
    public static final int GEST_SENDTODEVICE = 0x00200000;
    public static final int GEST_BACK_LONG = 0x00400000;
    public static final int GEST_LEFTARC = 0x10000000;
    public static final int GEST_RIGHTARC = 0x20000000;
    public static final int GEST_ARCS = 0x30000000;
	
    public static final int GEST_ALL = 0x0FFFFFFF;
	
    // language ID
    public static final int LANGUAGE_NONE = 0;
    public static final int LANGUAGE_ENGLISH = 1;
    public static final int LANGUAGE_FRENCH = 2;
    public static final int LANGUAGE_GERMAN = 3;
    public static final int LANGUAGE_SPANISH = 4;
    public static final int LANGUAGE_ITALIAN = 5;
    public static final int LANGUAGE_SWEDISH = 6;
    public static final int LANGUAGE_NORWEGIAN = 7;
    public static final int LANGUAGE_DUTCH = 8;
    public static final int LANGUAGE_DANISH = 9;
    public static final int LANGUAGE_PORTUGUESE = 10;
    public static final int LANGUAGE_PORTUGUESEB = 11;
    public static final int LANGUAGE_MEDICAL = 12;
    public static final int LANGUAGE_FINNISH = 13;
	
    public static final int HW_MAXWORDLEN = 50;
	
    public static final int HW_SPELL_CHECK = 0x0000;
    public static final int HW_SPELL_LIST = 0x0001;
    public static final int HW_SPELL_USERDICT = 0x0002;
    public static final int HW_SPELL_USEALTDICT = 0x0004;
    public static final int HW_SPELL_IGNORENUM = 0x0008;
    public static final int HW_SPELL_IGNOREUPPER = 0x0010;
	
    public static final int MIN_RECOGNITION_WEIGHT = 70;
    public static final int MAX_RECOGNITION_WEIGHT = 100;
	
    public static final int RECMODE_GENERAL = 0;
    public static final int RECMODE_NUM = 1;
    public static final int RECMODE_CAPITAL = 2;
    public static final int RECMODE_INTERNET = 3;
	
    //Autocorrector Flags
    public static final int FLAG_IGNORECASE = 0x0001;
    public static final int FLAG_ALWAYS_REPLACE = 0x0002;
    public static final int FLAG_DISABLED = 0x0004;
	
    private static native int recognizerInit(String sDir, int language );
    
    private static native boolean resetLearner();
	
    private static native int getRecognizerFlags();
	
    private static native void setRecognizerFlags(int flags);
	
    private static native int getRecognizerMode();
	
    private static native void setRecognizerMode(int mode);
	
    private static native void freeRecognizer();
	
    public static native boolean resetAutocorrector();
	
    public static native boolean reloadAutocorrector();
	
    private static native boolean reloadUserDict();
	
    private static native boolean reloadLearner();
	
    public static native boolean saveWordList();
	
    public static native boolean saveUserDict();
		
    private static native boolean saveLearner();
	
    private static native boolean resetRecognizer();
	
    private static native boolean resetUserDict();
	
    private static native boolean setDictionaryData(byte[] buffer, int flag );
	
    private static native String recognizeInkData(int nDataLen, boolean bAsync, boolean bFlipY, boolean bSort);
	
    private static native boolean preRecognizeInkData(int nDataLen);
	
    private static native int stopRecognizer();
	
    private static native int newStroke(int width, int color);
	
    private static native int addPixelToStroke(int stroke, float x, float y);
	
    private static native int getStrokeCount();
	
    private static native void resetInkData();
	
    private static native int detectGesture(int type);
	
    private static native boolean deleteLastStroke();
	
    private static native boolean newUserDict();
	
    private static native boolean addWordToUserDict(String word);
	
    private static native boolean isWordInDict(String word);
	
    private static native String languageName();
	
    private static native int languageID();
	
    private static native boolean resetResult();
	
    private static native int getResultColumnCount();
	
    private static native int getResultRowCount(int column);
	
    private static native String getRecognizedWord(int column, int row);
	
    private static native String getAutocorrectorWord(String inWord);
	
    private static native int getRecognizedWeight(int column, int row);
    
    private static native int getResultStrokesNumber(int column, int row);
	
    private static native boolean learnerAddNewWord(String word, int weight);
	
    private static native boolean autocorrectorLearnWord(String word1, String word2, int flags, boolean replace);
	
    private static native boolean learnerReplaceWord(String word1, int weight1, String word2, int weight2);
	
    private static native int checkStrokeNewLine(int stroke);
	
    private static native boolean isPointStroke(int stroke);
	
    private static native boolean deleteStroke(int stroke);
	
	public static native int getEnumWordList();
	
    public static native String spellCheckWord(String word, boolean list);
	
    public static native int getEnumUserWordsList();
    
	//Christian Br�ndel
	private static native boolean deleteStrokeByIndex(int index);
	
	private static native int getStrokeIndexByPoint(float x, float y);
	
	
    
    static WritePadAPI.OnAutocorrectorListener onAutocorrectorWordListener;
	
    static WritePadAPI.OnUserDictionaryListener onUserDictionaryListener;
		
    /**
     * 
     * @param index
     * @return
     * @author Christian Br�ndel
     */
    public static final boolean recoDeleteStrokeByIndex(int index)
    {
    	return deleteStrokeByIndex(index);
    }
    
    /**
     * 
     * @param x
     * @param y
     * @return
     * @author Christian Br�ndel
     */
    public static final int recoGetStrokeByPoint(float x, float y)
    {
    	return  getStrokeIndexByPoint(x,y);	
    }
	
    public interface OnAutocorrectorListener {
        public void onWordPairAdded(String wordFrom, String wordTo, int flags);
    }
	
	
    public interface OnUserDictionaryListener {
        public void onWordAdded(String word);
    }
	
    // load recognizer library and define recognizer functions for this class
    static 
    { 
        System.loadLibrary("WritePadReco");
    } 
    
    public void setOnAutocorrectorWordListener(OnAutocorrectorListener listener) {
        onAutocorrectorWordListener = listener;
    }
	
    public static int getWordList() {
        return getEnumWordList();
    }
	
    public static boolean saveUserDictionary() {
        return saveUserDict();
    }
	
    public static boolean saveWords() {
        return saveWordList();
    }
	
    public static String recoSpellCheckWord(String s, boolean list) {
        return spellCheckWord(s, list);
    }
	
    public static int getEnumUserWords() {
        return getEnumUserWordsList();
    }
	
	
    public static void setOnUserDictionaryListener(WritePadAPI.OnUserDictionaryListener listener) {
        onUserDictionaryListener = listener;
    }
	
	
    public static void onEnumWord(String from, String to, int flags) {
        if (onAutocorrectorWordListener != null) {
            onAutocorrectorWordListener.onWordPairAdded(from, to, flags);
        }
		
    }
	
    public static void onEnumUserWords(String word) {
        if (onUserDictionaryListener != null) {
            onUserDictionaryListener.onWordAdded(word);
        }
		
    }
	
    public static boolean learnAutocorrectorWord(String word1, String word2, int flags, boolean replace) {
        return autocorrectorLearnWord(word1, word2, flags, replace);
    }
	
    public static boolean learnReplaceWord(String word1, int weight1, String word2, int weight2) {
        return learnerReplaceWord(word1, weight1, word2, weight2);
    }
	
    public static boolean learnNewWord(String word, int weight) {
        return learnerAddNewWord(word, weight);
    }
	
    public static boolean recoResetResult() {
        return resetResult();
    }
	
    public static int recoResultColumnCount() {
        return getResultColumnCount();
    }
	
    public static int recoResultRowCount(int column) {
        return getResultRowCount(column);
    }
	
    public static String recoResultWord(int column, int row) {
        return getRecognizedWord(column, row);
    }
	
    public static int recoResulWeight(int column, int row) {
        return getRecognizedWeight(column, row);
    }
	
    public static int recoResultStrokesNumber(int column, int row) {
        return getResultStrokesNumber(column, row);
    }

    public static String autorecognizerWord(String inWord) {
        return getAutocorrectorWord(inWord);
    }
	
    public static boolean isDictionaryWord(String word) {
        return isWordInDict(word);
    }
	
    public static int getLanguageID() {
        return languageID();
    }
	
    public static String getLanguageName() {
        return languageName();
    }
	
    public static boolean recoResetAutocorrector() {
        return resetAutocorrector();
    }
	
    public static boolean recoReloadAutocorrector() {
        return resetAutocorrector();
    }
	
    public static boolean recoReloadUserDict() {
        return reloadUserDict();
    }
	
    public static boolean recoReloadLearner() {
        return reloadLearner();
    }
	
    public static boolean recoNewDict() {
        return newUserDict();
    }
	
    public static boolean recoReset() {
        return resetRecognizer();
    }
	
    public static boolean recoResetLearner() {
        return resetLearner();
    }
	
    public static boolean recoSaveLearner() {
        return saveLearner();
    }
	
    public static boolean recoSetDict(byte[] buffer, int flag ) {
        return setDictionaryData(buffer, flag );
    }
	
    public static boolean recoAddWord(String word) {
        return addWordToUserDict(word);
    }
	
    public static boolean recoResetUserDict() {
        return resetUserDict();
    }
	
    public static int recoGesture(int type) {
        return detectGesture(type);
    }
	
    public static boolean recoDeleteLastStroke() {
        return deleteLastStroke();
    }
	
    public static int recoStrokeCount() {
        return getStrokeCount();
    }
	
    public static int recoAddPixel(int stroke, float x, float y) {
        return addPixelToStroke(stroke, x, y);
    }
	
    public static void recoResetInk() {
        resetInkData();
    }
	
    public static int recoNewStroke(int width, int color) {
        return newStroke(width, color);
    }
	
    public static int recoGetMode() {
        return getRecognizerMode();
    }
	
    public static void recoSetMode(int nMode) {
        setRecognizerMode(nMode);
    }
	
    public static int recoGetFlags() {
        return getRecognizerFlags();
    }
	
    public static void recoSetFlags(int flags) {
        setRecognizerFlags(flags);
    }
	
    public static int recoStop() {
        return stopRecognizer();
    }
	
    public static String recoInkData(int nDataLen, boolean bAsync, boolean bFlipY, boolean bSort) {
        return recognizeInkData(nDataLen, bAsync, bFlipY, bSort);
    }
	
    public static boolean preRecoInkData(int nDataLen) {
        return preRecognizeInkData(nDataLen);
    }
	
    public static int recoInit(String sDir, int language ) {
        return recognizerInit(sDir, language );
    }
	
    public static void recoFree() {
        freeRecognizer();
    }
	
    public static boolean strokeIsPoint(int stroke) {
        return isPointStroke(stroke);
    }
	
    public static boolean strokeIsNewLine(int stroke, int xMarkerOffsetX, int yMarkerOffsetY ) {
        int offset = checkStrokeNewLine(stroke);
        int xOffset = offset & 0xffff;
        int yOffset = (offset >> 16) & 0xffff;
        if (xOffset > 0 && xOffset < xMarkerOffsetX && yOffset > 0 && yOffset < yMarkerOffsetY )
            return true;
        return false;
    }
	
    public static boolean strokeDelete(int stroke) {
        return deleteStroke(stroke);
    }
    
}
