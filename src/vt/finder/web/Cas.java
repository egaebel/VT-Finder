package vt.finder.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.util.Log;

/**
 * Used to connect to the Virginia
 * Tech CAS authentication system via SSL. Secure. CAS must be logged into in order for 
 * most services contained in this API to be used. Service's class comments will identify
 * whether the Service requires the user to be logged into Cas. 
 * 
 * @author Ethan Gaebel (egaebel)
 *
 */
public class Cas {

    //~Constants-----------------------------------------------
    /**
     * the users agents to pass along with the response
     */
    private static final String AGENTS = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.116 Safari/537.36";
    /**
     * The url for hokiespa to login at.
     */
    private static final String LOGIN = "https://auth.vt.edu/login?service=https://webapps.banner.vt.edu/banner-cas-prod/authorized/banner/SelfService";
    /**
     * The URL to logout of CAS entirely.
     */
    private static final String CAS_LOGOUT = "https://auth.vt.edu/logout";
    /**
     * The URL to login to CAS when the recovery options need to be picked up.
     */
    private static final String RECOVERY_OPTIONS_LOGIN = "https://banweb.banner.vt.edu/ssb/prod/twbkwbis.P_GenMenu?name=bmenu.P_MainMnu";
    
    //~Data Fields---------------------------------------------
    /**
     * The cookies that are used with HokieSpa.
     */
    private static Map<String, String> cookies;
    /**
     * The SSL certificate used to connect to CAS.
     */
    public static File cert;
    /**
     * Boolean value denoting whether this CAS object has an active
     * connection to the VT CAS system. True if its active, false otherwise.
     */
    private static boolean active;

    //~Constructors--------------------------------------------

    //~Methods-------------------------------------------------
    /**
     * Grabs the needed SSL Certificate from the CAS login page. Saves the file to the passed in
     * directory.
     * 
     * @return true if successful, false if there was an IOException, 
     *          MalformedURLException, SSLPeerUnverifiedException, 
     *          or a CertificateEncodingException.
     *          See StackTrace to figure out which one.
     */
    private static boolean grabCertificate(String filePath) {
        
        try {
            URL url = new URL(LOGIN);
            HttpsURLConnection connect = (HttpsURLConnection)url.openConnection();
            connect.connect();
            Certificate[] certs = connect.getServerCertificates();
            
            if (certs.length > 0) {
             
                cert = new File(filePath + "auth.vt.edu.jks");
                //write the certificate obtained to the cert file.
                OutputStream os = new FileOutputStream(cert);
                os.write(certs[0].getEncoded());
                os.close();

                return true;
            }
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
        }
        catch (SSLPeerUnverifiedException e) {
            e.printStackTrace();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Takes in a username and password and logs into CAS
     * with the supplied username and password. Also takes in a String that specifies
     * the directory where the SSL should be saved. This is especially useful in Android
     * usage, because the file system differs from java's default one.
     * 
     * For best performance this method call should be done in a separate thread.
     * 
     * @param username the username of the user.
     * @param password the password of the user.
     * @return returns true if successful, false if there was an IOException, 
     *          and false if the Certificate wasn't correctly obtained. 
     * 
     * @throws WrongLoginException indicates that the username or password was incorrect
     */
    public static boolean login(String username, String password, String filePath) throws WrongLoginException {

        try {

            if(grabCertificate(filePath)) {
            
                System.setProperty("javax.net.ssl.trustStore", cert.getAbsolutePath());
    
                // get three hidden fields, and cookies from initial Login Page
                Response loginPageResp = Jsoup.connect(LOGIN).execute();
    
                // save JSESSION cookie from the LOGIN URL's response
                cookies = loginPageResp.cookies();
    
                // get the document from the response to retrieve hidden fields
                Document doc = loginPageResp.parse();
    
                // select the correct div section under form-->fieldset
                Elements divs = doc.select("form fieldset div");
                Element div6 = divs.get(5);
    
                // hashmap to hold hiddenFields in document, as well as username,
                // password
                Map<String, String> hiddenFields = new HashMap<String, String>();
    
                // place hidden fields & _submit into hashmap for passing
                hiddenFields.put("lt", div6.getElementsByIndexEquals(0).val());
                hiddenFields.put("execution", div6.getElementsByIndexEquals(1)
                        .val());
                hiddenFields
                        .put("_eventId", div6.getElementsByIndexEquals(2).val());
    
                // will always be this value on the CAS page
                hiddenFields.put("submit", "_submit");
    
                // place username and password into hashmap for passing
                hiddenFields.put("username", username);
                hiddenFields.put("password", password);
    
                // enter in the hidden fields as well as username and pasword --
                // press submit, USE GET METHOD!!!
                Response resp = Jsoup
                        .connect(LOGIN)
                        .data(hiddenFields)
                        .cookie("JSESSIONID", cookies.get("JSESSIONID"))
                        .method(Method.GET)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .referrer(LOGIN)
                        .userAgent(AGENTS)
                        .execute();

                // get all cookies from the resp generated above to use in future
                // authentication
                cookies.putAll(resp.cookies());
    
                Document loginCheck = resp.parse();

                // check to see if the login was successful, that is, had a correct PASSWORD and USERNAME
                Elements checkEls = loginCheck.select("#login-error");
                Elements checkRecovery = loginCheck.select("#warn");
                String RECOVERY_OPTIONS_STRING = "You have not updated account recovery options in the past";
                
                if (checkEls.size() >= 8) {

                    Element checkedEl = checkEls.get(8);
    
                    if (checkedEl.text().equals("Invalid username or password.")) {

                        // if unsuccessful login throw WrongLoginException
                        throw new WrongLoginException();
                    }
                }
                //Check for account recovery options page
                /*else if (checkRecovery != null && checkRecovery.text().contains(RECOVERY_OPTIONS_STRING)) {

                    //Go to redirect page to acquire cookies etc.
                    resp = Jsoup.connect(RECOVERY_OPTIONS_LOGIN)
                            .cookie("JSESSIONID", cookies.get("JSESSIONID"))
                            .cookie("CASTGC", cookies.get("CASTGC"))
                            //.cookies(hiddenFields)
                            .method(Method.GET)
                            .referrer(LOGIN)
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .userAgent(AGENTS).execute();

                    cookies.putAll(resp.cookies());
                }*/
                else if (checkEls.size() == 1) {
                    
                    Element checkedEl = checkEls.get(0);
            
                    if (checkedEl.text().equals("Invalid username or password.")) {

                        // if unsuccessful login throw WrongLoginException
                        throw new WrongLoginException();
                    }
                }
                else {
                    
                    //Go to redirect page to acquire cookies etc.
                    /*resp = Jsoup.connect(RECOVERY_OPTIONS_LOGIN)
                            .cookie("JSESSIONID", cookies.get("JSESSIONID"))
                            .cookie("CASTGC", cookies.get("CASTGC"))
                            //.cookies(hiddenFields)
                            .method(Method.GET)
                            .referrer(LOGIN)
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .userAgent(AGENTS).execute();

                    cookies.putAll(resp.cookies());*/
//Log.i("DEBUGGER", "DAT ELSED LOGIN TEXT\n" + resp.parse().toString());
                }
    
                active = true;
Log.i("DEBUGGER", "FUCKING LOGGED IN, FUCK YEAH!!!!!!!");

                return true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Log out of CAS. If this returns false then the session will remain open.
     * @return true if successful, false if an error occurred.
     */
    public static boolean logout() {

        try {

            active = false;
            cookies = null;
            
            // logs out of CAS. closing the session
            Jsoup.connect(CAS_LOGOUT).post();

            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //~Getters and Setters--------------------------------------------------------------
    /**
     * Getter for cookies.
     * 
     * @return cookies the cookies that have been pulled from Cas, if a login has occurred. Otherwise, returns null
     */
    /*protected*/public static Map<String, String> getCookies() {
        
        return cookies;
    }

    /**
     * Tests to see if this CAS object has an active
     * login session with hokiespa.
     * 
     * @return true if active, false otherwise.
     */
    public static boolean isActive() {

        return active;
    }
}
