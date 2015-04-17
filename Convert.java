//----------------------------------------------------------------------------
// W A R N I N G :
//----------------------------------------------------------------------------
// In order to compile this file on Windows, you will need the following 
// environment variable creating:
//
// set JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF8"
//
// This source file contains UTF8 characters for those damned smart quotes,
// which strangely, Microsoft insist on using in Word etc, but their system
// runs in CP1252 by default!
//
// If your "javac" compile fails with this error:
//
//      C:\Software\Wiki>javac Convert.java
//      Convert.java: error: unmappable character for encoding Cp1252
//                    aQuote = thisLine.indexOf("ÔÇ?");
//                                                     ^
// Then you must set the Env Var as above, either permanently in Control
// Panel -> System, or in the session itself as per the above command.
//----------------------------------------------------------------------------
// It's possible that Java 1.6 (Java 6) will not compile this file. I haven't
// tested it to see, I only have Java 1.7 (aka 7).
//----------------------------------------------------------------------------

//----------------------------------------------------------------------------
// U S A G E:
//----------------------------------------------------------------------------
// java Convert.java File_name.html > output.filename.html
// or
// java Convert.java File_name.html > output.filename.html 2>filename.log
//----------------------------------------------------------------------------

//----------------------------------------------------------------------------
// L I C E N C E:
//----------------------------------------------------------------------------
// Lets say it's Public Domain. Free for use and abuse as you see fit, with no
// warranty implied or expressed. Use (and abuse) at your own risk, after all
// what could possibly go wrong?
//----------------------------------------------------------------------------

//----------------------------------------------------------------------------
// A U T H O R:
//----------------------------------------------------------------------------
// Norman @ Dunbar - it . co . uk
//----------------------------------------------------------------------------

//----------------------------------------------------------------------------
// P U R P O S E:
//----------------------------------------------------------------------------
// Converts a DokuWiki page, saved from the browser as HTML source, to get rid 
// of the crud, and hopefully, extract the relevant information. 
// The HTML input file should have been saved from the browser using the "view
// source" feature and the resulting source saved as an HTML file. It can then
// be stuffed through this abysmal piece of Java code to extract the wheat
// the chaff. :-)
//----------------------------------------------------------------------------

//----------------------------------------------------------------------------
// C A V E A T S:
//----------------------------------------------------------------------------
// 1. Images need to be saved from the Wiki Page with the same name as per the
//    message(s) output to the log file (on standard error) or console. The 
//    Wiki insists on absolute paths to images, why not, but I prefer to have
//    mine relative to the source HTML file, so that's how it is. By default
//    images are expected to be in the _media subdirectory beneath where the
//    converted HTML page is located.
// 2. Smilies. The Wiki keeps those in a different place. Check the output 
//    messages to find where you need to put yours, if you can be bothered! I
//    personally, can't be! (Bothered.)
// 3. Other media files - I have no idea, we don't use them.
// 4. If future versions of DokuWiki change the end of section comments, we 
//    could be up that famous creek, without a canoe, never mind a paddle!
// 5. There is no CSS extracted. All the class names, ID names etc remain in
//    the converted HTML. This MIGHT be useful, perhaps.
// 6. External links remain as external links. This means, they work!
// 7. Internal (to Wiki pages I mean) will most likely no longer work.
// 8. Older versions of DokuWiki didn't always put a class on <img tags. Sigh.
//    In these cases, you will still get advised, but the filename will be 
//    "null", but you can always check it and see what is expected.  
// 9. Also, those older versions didn't use URLs for some images, they used
//    a "fetch.php" function call to get the image. Another sigh.
//----------------------------------------------------------------------------

//----------------------------------------------------------------------------
// B R I E F   D E S C R I P T I O N:
//----------------------------------------------------------------------------
// Open the passed file for reading in UTF8 mode.
// Scan through it, writing to System.out until we find <\title>.
// Write out </head> and <body>.
// Keep scanning but ignore everything until we reach "<!-- TOC END -->".
// Keep scanning and write everything out until we hit "<!-- wikipage stop -->".
// Advise the user if any image files are required.
// Convert any damned "smart" quotes (E2809C and E2809D) to &quot;
// Close the <body> and <html> tags.
// Done.
//----------------------------------------------------------------------------

//----------------------------------------------------------------------------
// B Y   T H E   W A Y:
//----------------------------------------------------------------------------
// I never said this would be the world's most efficient Java code, did I?
// This is only my third ever Java program, and Java doesn't do efficient.
//----------------------------------------------------------------------------

import java.io.*;


public class Convert {

    public static void main(String[] args) throws Exception 
    {
      
        String  thisLine = null;        // A line of HTML from the input file.
        String  tempLine = null;        // Temp buffer to extract image file names.
        String  imgFile = null;         // Image file name.
        int     lineNumber = 0;         // Which line am I on?

        try
        {

            //---------------------------------------------------------------------------
            // So, I want to open a text file that's in UTF8 format, how hard can it be?
            // This hard ...
            //---------------------------------------------------------------------------
            BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(args[0]), "UTF8")
            );


            //---------------------------------------------------------------------------
            // Scan until we reach the <body> tag. Write out each line as we go.
            //---------------------------------------------------------------------------
            System.err.println("Reading: " + args[0] + "...");
            System.err.println("Looking for the \"</title>\" tag....");
            while ((thisLine = br.readLine()) != null) {
                lineNumber++;
    
                //---------------------------------------------------------------------------
                // Write out this line, and check if we are finished copying the header stuff.
                //---------------------------------------------------------------------------
                System.out.println(thisLine);
                if (thisLine.trim().indexOf("</title") > -1) {
                    System.err.println("Found it at line " + lineNumber + ".");
                    break;
                }
            }

            System.out.println("</head>");
            System.out.println("<body>");

            
            //---------------------------------------------------------------------------
            // We are in the <body>.
            // Ignore everything until we reach the <!-- TOC END --> line. 
            //---------------------------------------------------------------------------
            System.err.println("Deleting down to \"<!-- TOC END -->\"");

            while ((thisLine = br.readLine()) != null) {
                lineNumber++;
                if (thisLine.trim().startsWith("<!-- TOC END")) {
                    // Not done yet, ignore this line.
                    break;
                }

            }

            System.err.println("Finished deleting at line " + lineNumber + ".");

            //---------------------------------------------------------------------------
            // Now we can copy the meat & gravy of the information to the output file!
            //---------------------------------------------------------------------------
            System.err.println("Copying down to \"<!-- wikipage stop -->\"....");

            while ((thisLine = br.readLine()) != null) {
                lineNumber++;

                //---------------------------------------------------------------------------
                // Remove those "edit" buttons...
                //---------------------------------------------------------------------------
                if (thisLine.trim().startsWith("<div class=\"secedit\"")) {
                    continue;
                }


                //---------------------------------------------------------------------------
                // Warn if we find an image.
                //---------------------------------------------------------------------------
                int imgTag = thisLine.indexOf("<img ");
                if (imgTag > -1) {
                    //-----------------------------------------------------------------------
                    // We have an image. Find the source...
                    //-----------------------------------------------------------------------
                    System.err.println("Found <img> tag at line " + 
                                        lineNumber + ", column " + imgTag);

                    //-----------------------------------------------------------------------
                    // We are doing it this way because there might be more than one
                    // src= or class= in the line. We are only interested in those we
                    // see AFTER the initial <img tag. Trust me, not all <img tags are
                    // the same.
                    //-----------------------------------------------------------------------
                    tempLine = thisLine;
    
                    int srcTag = tempLine.substring(imgTag).indexOf("src=\"");
                    if (srcTag > -1) {
                        srcTag += imgTag;
                        int classTag = tempLine.substring(srcTag).indexOf("class=");
                        if (classTag > -1) {
                            // Extract the filename...
                            classTag += srcTag;

                            //---------------------------------------------------------------
                            // Trim off the leading slash from the image path. It's causing
                            // the resulting HTML to be absolute path, we want relative paths
                            // to images now.
                            //---------------------------------------------------------------
                            imgFile = "\"" + tempLine.substring(srcTag + 6, classTag).trim();

                            //---------------------------------------------------------------
                            // Now, change the line we write out to use relative paths.
                            //---------------------------------------------------------------
                            thisLine = thisLine.substring(0, srcTag) +
                                       "src=" + imgFile + " " +
                                       thisLine.substring(classTag) ;
                        }
                        //-------------------------------------------------------------------
                        // And inform the user about it.
                        //-------------------------------------------------------------------
                        System.err.println("\tThe image file is " + imgFile);

                        //-------------------------------------------------------------------
                        // And write a suitable comment to the output HTML file.
                        //-------------------------------------------------------------------
                        System.out.println("<!-- I M A G E   F I L E   " + imgFile + 
                                            "   R E Q U I R E D   H E R E -->");
                    } else {
                        //-------------------------------------------------------------------
                        // How strange, an image tag with no src attribute.
                        //-------------------------------------------------------------------
                        System.err.println("\tThe image file has no src attribute!");
                    }
                }
                

                //---------------------------------------------------------------------------
                // Check if we are done yet?
                //---------------------------------------------------------------------------
                if (thisLine.trim().startsWith("<!-- wikipage stop")) {
                    break;
                }

                //---------------------------------------------------------------------------
                // Not done yet, copy this line out. However, we need to remove
                // the Wiki's insistence on using so called "smart" quotes. These
                // appear as a single character, but are actually three!
                //---------------------------------------------------------------------------
                int aQuote = thisLine.indexOf("“");  // E2 80 9C
                if (aQuote > -1) {
                    thisLine = thisLine.substring(0, aQuote) +
                               "&quot;" +
                                thisLine.substring(aQuote + 1);
                }
                
                aQuote = thisLine.indexOf("”");  // E2 80 9D
                if (aQuote > -1) {
                    thisLine = thisLine.substring(0, aQuote) +
                               "&quot;" +
                                thisLine.substring(aQuote + 1);
                }
                System.out.println(thisLine);
            }

            System.err.println("Finished copying at line " + lineNumber + ".");

            System.out.println("</body>");
            System.out.println("</html>");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}