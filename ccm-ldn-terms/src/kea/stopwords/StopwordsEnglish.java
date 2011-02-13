package kea.stopwords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import org.apache.log4j.Logger;

/**
 * Class that can test whether a given string is a stop word.
 * Lowercases all words before the test.
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @version 1.0
 */
public class StopwordsEnglish extends Stopwords {

    private static final Logger logger =
                                Logger.getLogger(StopwordsEnglish.class);
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /** The hashtable containing the list of stopwords */
    private static Hashtable m_Stopwords = null;

    static {
        logger.debug("Static initializer is starting...");
        if (m_Stopwords == null) {
            m_Stopwords = new Hashtable();
            Double dummy = new Double(0);
            File txt = new File("data/stopwords/stopwords_en.txt");
            InputStreamReader is;
            String sw = null;
            try {
                is = new InputStreamReader(new FileInputStream(txt), "UTF-8");
                BufferedReader br = new BufferedReader(is);
                while ((sw = br.readLine()) != null) {
                    m_Stopwords.put(sw, dummy);
                }
            } catch (Exception e) {
                logger.error("Static initalizer failed.", e);
                throw new RuntimeException(e);
            }
        }
        logger.debug("Static initalizer is finished.");
    }

    /**
     * Returns true if the given string is a stop word.
     */
    public boolean isStopword(String str) {

        return m_Stopwords.containsKey(str.toLowerCase());
    }
}
