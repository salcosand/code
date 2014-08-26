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

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * The Service responsible for calling the WritePadAPI and recognizing written words
 * 
 * @author PhatWare
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 *
 */
public class RecognizerService extends Service 
{
    private ConditionVariable mCondition;
    private boolean mRunRecognizerThread;
    private int mStrokeCnt;
    private boolean mReady;
    
    public Handler mHandler;
         
    @Override
    public void onCreate()  
    { 
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.
        mRunRecognizerThread = true;
        mStrokeCnt = 0; 
        mReady = false;
        mHandler = null;
        Thread recognizeThread = new Thread(null, mTask, "RecognizerService");
        mCondition = new ConditionVariable(false);
        recognizeThread.start();
    }

    @Override
    public void onDestroy() 
    {
        synchronized( mCondition )
        {
            // Stop the thread from generating further notifications
            // stopRecognizer(); -- causes problems when recognizing
            mRunRecognizerThread = false;
            mCondition.notify();
        }
    }

    private Runnable mTask = new Runnable() 
    {
        public void run() 
        {
            while( mRunRecognizerThread ) 
            { 
                int strokes = 0;
                synchronized( mCondition )
                {
                    while ( ! mReady )
                    {
                        try 
                        {
                            mCondition.wait();
                        } 
                        catch (InterruptedException e) 
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            continue;
                        }
                    }
                    mReady = false;
                }
                if ( ! mRunRecognizerThread )
                    break;
                                
                synchronized( this )
                {
                    strokes = mStrokeCnt;
                }
                
                if ( strokes > 0 && mHandler != null )
                {
                       // call recognizer
                    String result  = null;
                    result = WritePadAPI.recoInkData( strokes, true, false, false );
                    if ( result != null )  
                    {
                        // send message to view
                        Message msg = mHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("result", result);
                        
                        
                        // get alternatives...
                        int words = WritePadAPI.recoResultColumnCount();
                        
                        List<String> wordCombinations = new ArrayList<String>();
                        
                        for ( int w = 0; w < words; w++ )
                        {
                        	int alternatives = WritePadAPI.recoResultRowCount( w );
                        	
                        	for ( int a = 0; a < alternatives; a++ )
                        	{
                        		String 	word = WritePadAPI.recoResultWord( w, a );				// word alternative
                        		int    	weight = WritePadAPI.recoResulWeight( w, a );			// probability of alternative (51...100)
                        		int 	str = WritePadAPI.recoResultStrokesNumber( w, a );	// number of strokes used for this alternative
                        		
                        		//remove all symbols that neither are a character nor a number
                        		if(word != null)
                        		word = word.replaceAll("\\W", "");
 
                        		if(wordCombinations.size() > a && weight > 70)
                        		{
                        		wordCombinations.set(a, wordCombinations.get(a) + " " + word);
                        		}
                        		else
                        		{
                        		if(word != null)
                        		wordCombinations.add(word);
                        		                        		
                        		}
                        		
                       		
                        		//Log.d( "recoResult", String.format( "%s  %d   %d", word, weight, str ) );
                        		}
                        		
                        		
                        	}
                        
                        
                        for(int i = 0; i < wordCombinations.size(); i++)
                        {
                        	
                        	b.putString(wordCombinations.get(i), wordCombinations.get(i));
                        	
//                        	Log.d("recoResult", wordCombinations.get(i));
                        }
                        
                        msg.setData(b);
                        mHandler.sendMessage(msg);
                        
                        
                        

                        
                    }
                }
            }
            // Done with our work...  stop the service!
            RecognizerService.this.stopSelf();
        } 
    };
        
      public void dataNotify( int nStrokeCnt )
    {
        WritePadAPI.recoStop();
        synchronized( this )
        {
            mStrokeCnt = nStrokeCnt;
        }
        synchronized( mCondition )
        {
            mReady = true;
            mCondition.notify();
        }
    }   

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    // Local Binder for Recognizer service 
    public class RecognizerBinder extends Binder 
    {
        public RecognizerService getService() 
        {
            return RecognizerService.this;
        }
        
    }
   
    // Instantiate local binder
    private final IBinder mBinder = new RecognizerBinder();
 
}

