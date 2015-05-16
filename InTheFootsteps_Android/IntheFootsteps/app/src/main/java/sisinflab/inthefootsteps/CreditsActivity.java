package sisinflab.inthefootsteps;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class CreditsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        setTitle("Credits");

        WebView webView = (WebView) findViewById(R.id.webViewCredits);
        String crediti = "<html><body> </p>Paolo Albano </br><font color=\"blue\"><u><mailto:paoloalbano90@gmail.com> paoloalbano90@gmail.com</font></u></mailto>" +
                "</p>Leonardo Avena </br><font color=\"blue\"><u><mailto:leonardo.avena88@gmail.com> leonardo.avena88@gmail.com</font></u></mailto>" +
                "</p>Vito Bellini </br><font color=\"blue\"><u><mailto:v.bellini@gmail.com>v.bellini@gmail.com</font></u></mailto>" +
                "</p>Gianluca Capodivento </br><font color=\"blue\"><u><mailto:gianluca.capodivento@gmail.com>gianluca.capodivento@gmail.com</font></u></mailto>" +
                "</p>Giuseppe De Santis </br><font color=\"blue\"><u><mailto:giuseppedes90@gmail.com>giuseppedes90@gmail.com</font></u></mailto>" +
                "</p>Carmen Manzulli </br><font color=\"blue\"><u><mailto:carmenmanzulli@gmail.com>carmenmanzulli@gmail.com</font></u></mailto>" +
                "</p>Francesco Marchitelli </br><font color=\"blue\"><u><mailto:marchitelli.francesco@gmail.com>marchitelli.francesco@gmail.com</font></u></mailto> </body></html>";
        webView.loadData(crediti, "text/html", null);
    }

}
