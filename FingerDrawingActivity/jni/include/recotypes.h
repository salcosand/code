/* ************************************************************************************* */
/* *    PhatWare WritePad SDK                                                           * */
/* ************************************************************************************* */

/* ************************************************************************************* *
 *
 * File: RecoTypes.h
 *
 * Unauthorized distribution of this code is prohibited. For more information
 * refer to the End User Software License Agreement provided with this 
 * software.
 *
 * This source code is distributed and supported by PhatWare Corp.
 * http://www.phatware.com
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
 * 530 Showers Drive Suite 7 #333 Mountain View, CA 94040
 *
 * ************************************************************************************* */

#ifndef __RecoTypes_h__
#define __RecoTypes_h__

#include "recodefs.h"

#ifndef __MACTYPES__

// define some types that are used for compatibility with MAC OS

typedef unsigned char   UInt8;
typedef signed char     SInt8;
typedef unsigned short  UInt16;
typedef signed short    SInt16;
typedef unsigned long   UInt32;
typedef signed long     SInt32;

typedef SInt16          OSErr;
typedef SInt32          OSStatus;

enum {
    noErr  = 0
};

#endif // __MACTYPES__

#ifndef CGGEOMETRY_H_

typedef float			CGFloat;

struct CGPoint {
    CGFloat x;
    CGFloat y;
};
typedef struct CGPoint CGPoint;

struct CGSize {
    CGFloat width;
    CGFloat height;
};
typedef struct CGSize CGSize;

struct CGRect {
    CGPoint origin;
    CGSize size;
};
typedef struct CGRect CGRect;

#endif // CGGEOMETRY_H_

#define RW_WEIGHTMASK		0x000000FF
#define RW_DICTIONARYWORD	0x00004000

// define some used Windows types
typedef unsigned char	BYTE;
typedef BYTE *			LPBYTE;
typedef UCHR			WCHAR;
typedef WCHAR *			LPWSTR;
typedef const WCHAR *	LPCWSTR;
typedef UInt16			WORD;
typedef unsigned char	UCHAR;
typedef UInt32			COLORREF;
typedef UInt32			DWORD;
typedef UInt16			USHORT;
typedef UInt32			ULONG;
typedef signed char		BOOL; 

typedef unsigned short	UNCHAR;
typedef UNCHAR *		LPUSTR;
typedef const UNCHAR *	LPCUSTR;

// define Windows POINT
typedef struct 
{
    float		x;
    float		y;
} POINTS, *LPPOINTS;

typedef struct 
{
    int		x;
    int		y;
} POINT, *LPPOINT;


// define Windows RECT
typedef struct 
{
    float		left;
    float		top;
    float		right;
    float		bottom;
} RECT, *LPRECT;

// define Windows GUID
typedef struct _GUID {
    unsigned long  Data1;
    unsigned short Data2;
    unsigned short Data3;
    unsigned char  Data4[ 8 ];
} GUID;

// define Windows BLOB
typedef struct tagBLOB
{
    ULONG	cbSize;
    BYTE *	pBlobData;
} BLOB;

typedef struct tagTracePoint
{
    POINTS	pt;
    int		pressure;
} TracePoint;


inline float max( float x, float y )
{
    return (x >= y) ? x : y;
}

inline float min( float x, float y )
{
    return (x <= y) ? x : y;
}

#ifndef MAKELONG
#define HIWORD(l)			((WORD)((((DWORD)(l)) >> 16) & 0xFFFF))
#define LOWORD(l)			((WORD)(((DWORD)(l)) & 0xFFFF))
#define MAKELONG( l, h )	((DWORD)((WORD)((DWORD)(l) & 0xFFFF) | ((DWORD)(((WORD)((h) & 0xFFFF)) << 16))))
#endif // MAKELONG

#ifndef MAKEWORD
#define MAKEWORD( l, h )	((WORD)((BYTE)((WORD)(l) & 0xFF) | ((WORD)(((BYTE)((h) & 0xFF)) << 8))))
#define HIBYTE(l)			((BYTE)((((WORD)(l)) >> 8) & 0xFF))
#define LOBYTE(l)			((BYTE)(((WORD)(l)) & 0xFF))
#endif // MAKEWORD

// Autocorrector flags
#define WCF_IGNORECASE		0x0001
#define WCF_ALWAYS			0x0002
#define WCF_DISABLED		0x0004

/* ------------------------- Language ID ------------------------------------- */

#define LANGUAGE_NONE			0
#define LANGUAGE_ENGLISH		1
#define LANGUAGE_FRENCH			2
#define LANGUAGE_GERMAN			3
#define LANGUAGE_SPANISH		4
#define LANGUAGE_ITALIAN		5
#define LANGUAGE_SWEDISH		6
#define LANGUAGE_NORWEGIAN		7
#define LANGUAGE_DUTCH		    8
#define LANGUAGE_DANISH		    9
#define LANGUAGE_PORTUGUESE	    10
#define LANGUAGE_PORTUGUESEB    11
#define LANGUAGE_MEDICAL        12
#define LANGUAGE_FINNISH		13

#define LANG_MEDICAL            0x71        // special entry for medical dictionary

typedef enum {
    SHAPE_UNKNOWN		= 0,
    SHAPE_TRIANGLE		= 0x0001,
    SHAPE_CIRCLE		= 0x0002,
    SHAPE_ELLIPSE		= 0x0004,
    SHAPE_RECTANGLE		= 0x0008,
    SHAPE_LINE			= 0x0010,
    SHAPE_ARROW			= 0x0020,
    SHAPE_SCRATCH		= 0x0040,
    SHAPE_ALL			= 0x00FF
} SHAPETYPE;

#define IMAGE_SUPPORT	1		// support image storage

#endif // __RecoTypes_h__
