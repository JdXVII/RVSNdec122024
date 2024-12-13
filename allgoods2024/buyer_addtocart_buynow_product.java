package com.example.allgoods2024;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class buyer_addtocart_buynow_product extends AppCompatActivity {

    private Spinner provinceSpinner, citySpinner, barangaySpinner;
    private EditText zipCodeEditText, zoneEditText;
    private TextView firstNameTextView, lastNameTextView, deliveryPriceTextView;
    private Button buyButton;
    private ImageView backButton;
    private String productType, storeName, productName, deliveryMethod, paymentMethod, totalPrice, price, type, productId, userId, productImageUrl, category;
    private int quantity;

    // Delivery price mapping
    private Map<String, Map<String, Map<String, Double>>> deliveryPrices = new HashMap<String, Map<String, Map<String, Double>>>() {{
        put("Agno", new HashMap<String, Map<String, Double>>() {{
            put("Allabon", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Aloleng", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Bangan-Oda", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Baruan", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Boboy", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Cayungnan", new HashMap<String, Double>() {{
                put("1-50", 450.0);
                put("51-200", 720.0);
                put("200+", 900.0);
            }});
            put("Dangley", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Gayusan", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Macaboboni", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Magsaysay", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Namatucan", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Patar", new HashMap<String, Double>() {{
                put("1-50", 450.0);
                put("51-200", 720.0);
                put("200+", 900.0);
            }});
            put("Poblacion East", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Poblacion West", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("San Juan", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Tupa", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Viga", new HashMap<String, Double>() {{
                put("1-50", 450.0);
                put("51-200", 720.0);
                put("200+", 900.0);
            }});
        }});
        //Aguilar
        put("Aguilar", new HashMap<String, Map<String, Double>>() {{
            put("Bayaoas", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Baybay", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Bucacliw", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Bocboc East", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Bocboc West", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Buer", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("Calsib", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Laoag", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Manlocbo", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("Ninoy", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Panacol", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Pogomboa", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Pogonsili", new HashMap<String, Double>() {{
                put("1-50", 270.0);
                put("51-200", 450.0);
                put("200+", 585.0);
            }});
            put("San Jose", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Tampac", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
        }});
        //Alaminos
        put("Alaminos", new HashMap<String, Map<String, Double>>() {{
            put("Alos", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Amandiego", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Amangbangan", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("Balangobong", new HashMap<String, Double>() {{
                put("1-50", 350.0);
                put("51-200", 570.0);
                put("200+", 725.0);
            }});
            put("Balayang", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Baleyadaan", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Bisocol", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Bued", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Cabatuan", new HashMap<String, Double>() {{
                put("1-50", 330.0);
                put("51-200", 540.0);
                put("200+", 690.0);
            }});
            put("Cayucay", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Dulacac", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("Inirangan", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("Landoc", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("Linmansangan", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Lucap", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Maawin", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Macatiw", new HashMap<String, Double>() {{
                put("1-50", 330.0);
                put("51-200", 540.0);
                put("200+", 690.0);
            }});
            put("Magsaysay", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Mona", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("Palamis", new HashMap<String, Double>() {{
                put("1-50", 350.0);
                put("51-200", 570.0);
                put("200+", 725.0);
            }});
            put("Pandan", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Pangapisan", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 350.0);
                put("51-200", 580.0);
                put("200+", 725.0);
            }});
            put("Pocalpocal", new HashMap<String, Double>() {{
                put("1-50", 350.0);
                put("51-200", 570.0);
                put("200+", 725.0);
            }});
            put("Pogo", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Polo", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("Qibuar", new HashMap<String, Double>() {{
                put("1-50", 350.0);
                put("51-200", 570.0);
                put("200+", 725.0);
            }});
            put("Sabangan", new HashMap<String, Double>() {{
                put("1-50", 350.0);
                put("51-200", 570.0);
                put("200+", 725.0);
            }});
            put("San Antonio", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("San Jose", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("San Roque", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("San Vicente", new HashMap<String, Double>() {{
                put("1-50", 350.0);
                put("51-200", 570.0);
                put("200+", 725.0);
            }});
            put("Santa Maria", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("Tanaytay", new HashMap<String, Double>() {{
                put("1-50", 350.0);
                put("51-200", 570.0);
                put("200+", 395.0);
            }});
            put("Tangcarang", new HashMap<String, Double>() {{
                put("1-50", 350.0);
                put("51-200", 570.0);
                put("200+", 725.0);
            }});
            put("Tawintawin", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("Telbang", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("Victoria", new HashMap<String, Double>() {{
                put("1-50", 330.0);
                put("51-200", 540.0);
                put("200+", 690.0);
            }});
        }});
        //Alcala
        put("Alcala", new HashMap<String, Map<String, Double>>() {{
            put("Anulid", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Atainan", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Bersamin", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Canarvacan", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Caranglaan", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Curareng", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Gualsic", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Kasikis", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Laoac", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Macayo", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Pindangan Centro", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Pindangan East", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Pindangan West", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Poblacion East", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Poblacion West", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("San Juan", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("San Nicolas", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("San Pedro Apartado", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("San Pedro Ili", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("San Vicente", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Vacante", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
        }});
        //Anda
        put("Anda", new HashMap<String, Map<String, Double>>() {{
            put("Awag", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Awile", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Batiarao", new HashMap<String, Double>() {{
                put("1-50", 550.0);
                put("51-200", 870.0);
                put("200+", 1075.0);
            }});
            put("Cabungan", new HashMap<String, Double>() {{
                put("1-50", 530.0);
                put("51-200", 840.0);
                put("200+", 1040.0);
            }});
            put("Carot", new HashMap<String, Double>() {{
                put("1-50", 530.0);
                put("51-200", 840.0);
                put("200+", 1040.0);
            }});
            put("Dolaoan", new HashMap<String, Double>() {{
                put("1-50", 530.0);
                put("51-200", 840.0);
                put("200+", 1040.0);
            }});
            put("Imbo", new HashMap<String, Double>() {{
                put("1-50", 530.0);
                put("51-200", 840.0);
                put("200+", 1040.0);
            }});
            put("Macaleeng", new HashMap<String, Double>() {{
                put("1-50", 530.0);
                put("51-200", 840.0);
                put("200+", 1040.0);
            }});
            put("Macandocandong", new HashMap<String, Double>() {{
                put("1-50", 530.0);
                put("51-200", 840.0);
                put("200+", 1040.0);
            }});
            put("Malong", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Namagbagan", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Roxas", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Sablig", new HashMap<String, Double>() {{
                put("1-50", 530.0);
                put("51-200", 840.0);
                put("200+", 1040.0);
            }});
            put("San Jose", new HashMap<String, Double>() {{
                put("1-50", 530.0);
                put("51-200", 840.0);
                put("200+", 1040.0);
            }});
            put("Siapar", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Tondol", new HashMap<String, Double>() {{
                put("1-50", 530.0);
                put("51-200", 840.0);
                put("200+", 1040.0);
            }});
            put("Toritori", new HashMap<String, Double>() {{
                put("1-50", 550.0);
                put("51-200", 870.0);
                put("200+", 1075.0);
            }});
        }});
        //Asingan
        put("Asingan", new HashMap<String, Map<String, Double>>() {{
            put("Ariston Este", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Ariston Weste", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Bantog", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Baro", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Bobonan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Cabalitian", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Calepaan", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Carosucan Norte", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Carosucan Sur", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Kuldit", new HashMap<String, Double>() {{
                put("1-50", 1100.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Dumanpot", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Dupac", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Macalong", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Palares", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Poblacion East", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Poblacion West", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("San Vicente Este", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("San Vicente Weste", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Sanchez", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Sobol", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Toboy", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});

        }});
        //Balungao
        put("Balungao", new HashMap<String, Map<String, Double>>() {{
            put("Angayan Norte", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Angayan Sur", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Capulaan", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Esmeralda", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 280.0);
                put("200+", 270.0);
            }});
            put("Kita-Kita", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Mabini", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Mauban", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Pugaro", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Rajal", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("San Andres", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("San Aurelio 1st", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("San Aurelio 2nd", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("San Aurelio 3rd", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("San Joaquin", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("San Julian", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("San Leon", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("San Marcelino", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("San Miguel", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("San Raymundo", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
        }});
        //Bani
        put("Bani", new HashMap<String, Map<String, Double>>() {{
            put("Ambabaay", new HashMap<String, Double>() {{
                put("1-50", 410.0);
                put("51-200", 660.0);
                put("200+", 830.0);
            }});
            put("Aporao", new HashMap<String, Double>() {{
                put("1-50", 450.0);
                put("51-200", 720.0);
                put("200+", 830.0);
            }});
            put("Arwas", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Ballag", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Banog Norte", new HashMap<String, Double>() {{
                put("1-50", 410.0);
                put("51-200", 660.0);
                put("200+", 830.0);
            }});
            put("Banog Sur", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Calabeng", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Centro Toma", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Colayo", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Dacap Norte", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Dacap Sur", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Garrita", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("Luac", new HashMap<String, Double>() {{
                put("1-50", 450.0);
                put("51-200", 720.0);
                put("200+", 900.0);
            }});
            put("Macabit", new HashMap<String, Double>() {{
                put("1-50", 450.0);
                put("51-200", 720.0);
                put("200+", 900.0);
            }});
            put("Masidem", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Quinaoayanan", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Ranao", new HashMap<String, Double>() {{
                put("1-50", 450.0);
                put("51-200", 720.0);
                put("200+", 900.0);
            }});
            put("Ranom Iloco", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("San Jose", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("San Miguel", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("San Simon", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("San Vicente", new HashMap<String, Double>() {{
                put("1-50", 410.0);
                put("51-200", 660.0);
                put("200+", 830.0);
            }});
            put("Tiep", new HashMap<String, Double>() {{
                put("1-50", 450.0);
                put("51-200", 720.0);
                put("200+", 900.0);
            }});
            put("Tipor", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Tugui Grande", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Tugui Norte", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
        }});
        //Basista
        put("Basista", new HashMap<String, Map<String, Double>>() {{
            put("Anambongan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Bayoyong", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Cabeldatan", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Dumpay", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Malimpec East", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Mapolopolo", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Nalneran", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Navatat", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Obong", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Osmeña Sr.", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Palma", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+",340.0);
            }});
            put("Patacbo", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+",340.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
        }});
        //Bautista
        put("Bautista", new HashMap<String, Map<String, Double>>() {{
            put("Artacho", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Baluyot", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Cabuaan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Cacandongan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Diaz", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Ketegan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Nandacan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Nibaliw Norte", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Nibaliw Sur", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Palisoc", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Poblacion East", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Poblacion West", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Pogo", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Poponto", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Primicias", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Sinabaan", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Vacante", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Villanueva", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
        }});
        //Bayambang
        put("Bayambang", new HashMap<String, Map<String, Double>>() {{
            put("Alinggan", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Amamperez", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Amancosiling Norte", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Amancosiling Sur", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Ambayat I", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Ambayat II", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Apalen", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Asin", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Ataynan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Bacnono", new HashMap<String, Double>() {{
                put("1-50",110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Balaybuaya", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Banaban", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Bani", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Batangcawa", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Beleng", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Bical Norte", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Bical Sur", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Bongato East", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Bongato West", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Buayaen", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Buenlag 1st", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Buenlag 2nd", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Cadre Site", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Carungay", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Caturay", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Darawey", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Duera", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Dusoc", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Hermoza", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Idong", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Inanlorenzana", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Inirangan", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Iton", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Langiran", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Ligue", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("M. H. del Pilar", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Macayocayo", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Magsaysay", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Maigpa", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Malimpec", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Malioer", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Managos", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Manambong Norte", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Manambong Parte", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Manambong Sur", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Mangayao", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Nalsian Norte", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Nalsian Sur", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Pangdel", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Pantol", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Paragos", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Poblacion Sur", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Pugo", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Reynado", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("San Gabriel 1st", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("San Gabriel 2nd", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("San Vicente", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Sangcagulis", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Sanlibo", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 210.0);
                put("200+", 340.0);
            }});
            put("Sapang", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Tamaro", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Tambac", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Tampog", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Tanolong", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Tatarao", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Telbang", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Tococ East", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Tococ West", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Warding", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Wawa", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Zone I", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Zone II", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Zone III", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Zone IV", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Zone V", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Zone VI", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Zone VII", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
        }});
        //Binalonan
        put("Binalonan", new HashMap<String, Map<String, Double>>() {{
            put("Balangobong", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Bued", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Bugayong", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Camangaan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Canarvacanan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Capas", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Cili", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Dumayat", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Linmansangan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Mangcasuy", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Moreno", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Pasileng Norte", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Pasileng Sur", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("San Felipe Central", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("San Felipe Sur", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("San Pablo", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Santa Catalina", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Santa Maria Norte", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Santiago", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Santo Niño", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Sumabnit", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Tabuyoc", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Vacante", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
        }});
        //Binmaley
        put("Binmaley", new HashMap<String, Map<String, Double>>() {{
            put("Amancoro", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Balagan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Balogo", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Basing", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Baybay Lopez", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Baybay Polong", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Biec", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Buenlag", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Calit", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Caloocan Dupo", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Caloocan Norte", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 830.0);
                put("200+", 445.0);
            }});
            put("Caloocan Sur", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 830.0);
                put("200+", 445.0);
            }});
            put("Camaley", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Canaoalan", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Dulag", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Gayaman", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Linoc", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Lomboy", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Malindong", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Manat", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Nagpalangan", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Naguilayan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Pallas", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Papagueyan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Parayao", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Pototan", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Sabangan", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Salapingao", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 310.0);
                put("200+", 445.0);
            }});
            put("San Isidro Norte", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 310.0);
                put("200+", 445.0);
            }});
            put("San Isidro Sur", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 310.0);
                put("200+", 445.0);
            }});
            put("Santa Rosa", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 330.0);
                put("200+", 415.0);
            }});
            put("Tombor", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 330.0);
                put("200+", 415.0);
            }});
        }});
        //Bolinao
        put("Bolinao", new HashMap<String, Map<String, Double>>() {{
            put("Arnedo", new HashMap<String, Double>() {{
                put("1-50", 530.0);
                put("51-200", 840.0);
                put("200+", 1040.0);
            }});
            put("Balingasay", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Binabalian", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Cabuyao", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Catuday", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Catungi", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Concordia", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Culang", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Dewey", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Estanza", new HashMap<String, Double>() {{
                put("1-50", 530.0);
                put("51-200", 840.0);
                put("200+", 1040.0);
            }});
            put("Germinal", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Goyoden", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 4760.0);
            }});
            put("Ilogmalino", new HashMap<String, Double>() {{
                put("1-50", 550.0);
                put("51-200", 870.0);
                put("200+", 1075.0);
            }});
            put("Lambes", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Liwa-Liwa", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Lucero", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Luciente 1.0", new HashMap<String, Double>() {{
                put("1-50", 530.0);
                put("51-200", 840.0);
                put("200+", 1040.0);
            }});
            put("Luciente 2.0", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Luna", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Patar", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Pilar", new HashMap<String, Double>() {{
                put("1-50", 310.0);
                put("51-200", 570.0);
                put("200+", 725.0);
            }});
            put("Salud", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Samang Norte", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Samang Sur", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Sampaloc", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("San Roque", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Tara", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Tupa", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Victory", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Zaragoza", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
        }});
        //Bugallon
        put("Bugallon", new HashMap<String, Map<String, Double>>() {{
            put("Angarian", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Asinan", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("Bacabac", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Banaga", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Bolaoen", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Buenlag", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("Cabayaoasan", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Cayanga", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Gueset", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("Hacienda", new HashMap<String, Double>() {{
                put("1-50",230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Laguit Centro", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Laguit Padilla", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("Magtaking", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("Pangascasan", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Pantal", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Polong", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Portic", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Salasa", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("Salomague Norte", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Salomague Sur", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Samat", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("San Francisco", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("Umanday", new HashMap<String, Double>() {{
                put("1-50", 270.0);
                put("51-200", 450.0);
                put("200+", 585.0);
            }});
        }});

        //Burgos
        put("Burgos", new HashMap<String, Map<String, Double>>() {{
            put("Anapao", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Cacayasen", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Concordia", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Don Matias", new HashMap<String, Double>() {{
                put("1-50", 450.0);
                put("51-200", 720.0);
                put("200+", 900.0);
            }});
            put("Ilio-Ilio", new HashMap<String, Double>() {{
                put("1-50", 530.0);
                put("51-200", 840.0);
                put("200+", 1040.0);
            }});
            put("Papallasen", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 450.0);
                put("51-200", 720.0);
                put("200+", 900.0);
            }});
            put("Pogoruac", new HashMap<String, Double>() {{
                put("1-50", 530.0);
                put("51-200", 840.0);
                put("200+", 1040.0);
            }});
            put("San Miguel", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("San Pascual", new HashMap<String, Double>() {{
                put("1-50", 450.0);
                put("51-200", 720.0);
                put("200+", 900.0);
            }});
            put("San Vicente", new HashMap<String, Double>() {{
                put("1-50", 450.0);
                put("51-200", 720.0);
                put("200+", 900.0);
            }});
            put("Sapa Grande", new HashMap<String, Double>() {{
                put("1-50", 450.0);
                put("51-200", 720.0);
                put("200+", 900.0);
            }});
            put("Sapa Pequeña", new HashMap<String, Double>() {{
                put("1-50", 450.0);
                put("51-200", 720.0);
                put("200+", 900.0);
            }});
            put("Tambacan", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
        }});

        //Calasiao
        put("Calasiao", new HashMap<String, Map<String, Double>>() {{
            put("Ambonao", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Ambuetel", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Banaoang", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Bued", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Buenlag", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Cabilocaan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Dinalaoan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Doyong", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Gabon", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Lasip", new HashMap<String, Double>() {{
                put("1-50",150.0);
                put("51-200", 240.0);
                put("200+", 375.0);
            }});
            put("Longos", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Lumbang", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Macabito", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Malabago", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Mancup", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Nagsaing", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 240.0);
                put("200+", 375.0);
            }});
            put("Nalsian", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 240.0);
                put("200+", 375.0);
            }});
            put("Poblacion East", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 240.0);
                put("200+", 375.0);
            }});
            put("Poblacion West", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 240.0);
                put("200+", 375.0);
            }});
            put("Quesban", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 240.0);
                put("200+", 375.0);
            }});
            put("San Miguel", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 240.0);
                put("200+", 375.0);
            }});
            put("San Vicente", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 240.0);
                put("200+", 375.0);
            }});
            put("Songkoy", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Talibaew", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 240.0);
                put("200+", 375.0);
            }});
        }});


        //Dagupan
        put("Dagupan", new HashMap<String, Map<String, Double>>() {{
            put("Bacayao Norte", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Bacayao Sur", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Barangay I", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Barangay II", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Barangay IV", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Bolosan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Bonuan Binloc", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Bonuan Boquig", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Bonuan Gueset", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Calmay", new HashMap<String, Double>() {{
                put("1-50",210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Carael", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Caranglaan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Herrero", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Lasip Chico", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Lasip Grande", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Lomboy", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Lucao", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Malued", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Mamalingling", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Mangin", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Mayombo", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Pantal", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Poblacion Oeste", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Pogo Chico", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Pogo Grande", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Pugaro Suit", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Salapingao", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Salisay", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Tambac", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Tapuac", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Tebeng", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
        }});

        //Dasol
        put("Dasol", new HashMap<String, Map<String, Double>>() {{
            put("Alilao", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Amalbalan", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Bobonot", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Eguia", new HashMap<String, Double>() {{
                put("1-50", 530.0);
                put("51-200", 840.0);
                put("200+", 1040.0);
            }});
            put("Gais-Guipe", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Hermosa", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Macalang", new HashMap<String, Double>() {{
                put("1-50", 450.0);
                put("51-200", 720.0);
                put("200+", 900.0);
            }});
            put("Magsaysay", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Malacapas", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("Malimpin", new HashMap<String, Double>() {{
                put("1-50",470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Osmeña", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Petal", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 780.0);
                put("200+", 970.0);
            }});
            put("San Vicente", new HashMap<String, Double>() {{
                put("1-50", 490.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Tambac", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Tambobong", new HashMap<String, Double>() {{
                put("1-50", 530.0);
                put("51-200", 840.0);
                put("200+", 1040.0);
            }});
            put("Uli", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
            put("Viga", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
        }});

        //Infanta
        put("Infanta", new HashMap<String, Map<String, Double>>() {{
            put("Babuyan", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("Bamban", new HashMap<String, Double>() {{
                put("1-50", 470.0);
                put("51-200", 750.0);
                put("200+", 935.0);
            }});
            put("Batang", new HashMap<String, Double>() {{
                put("1-50", 550.0);
                put("51-200", 870.0);
                put("200+", 1075.0);
            }});
            put("Bayambang", new HashMap<String, Double>() {{
                put("1-50", 550.0);
                put("51-200", 870.0);
                put("200+", 1075.0);
            }});
            put("Cato", new HashMap<String, Double>() {{
                put("1-50", 550.0);
                put("51-200", 870.0);
                put("200+", 1075.0);
            }});
            put("Doliman", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Fatima", new HashMap<String, Double>() {{
                put("1-50", 550.0);
                put("51-200", 870.0);
                put("200+", 1075.0);
            }});
            put("Maya", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Nangalisan", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Nayom", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Pita", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 340.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Potol", new HashMap<String, Double>() {{
                put("1-50", 340.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
        }});

        //Labrador
        put("Labrador", new HashMap<String, Map<String, Double>>() {{
            put("Bolo", new HashMap<String, Double>() {{
                put("1-50", 270.0);
                put("51-200", 450.0);
                put("200+", 585.0);
            }});
            put("Bongalon", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("Dulig", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("Laois", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Magsaysay", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 270.0);
                put("51-200", 450.0);
                put("200+", 585.0);
            }});
            put("San Gonzalo", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("San Jose", new HashMap<String, Double>() {{
                put("1-50", 270.0);
                put("51-200", 450.0);
                put("200+", 585.0);
            }});
            put("Tobuan", new HashMap<String, Double>() {{
                put("1-50", 370.0);
                put("51-200", 600.0);
                put("200+", 760.0);
            }});
            put("Uyong", new HashMap<String, Double>() {{
                put("1-50", 350.0);
                put("51-200", 570.0);
                put("200+", 725.0);
            }});
        }});

        //Laoac
        put("Laoac", new HashMap<String, Map<String, Double>>() {{
            put("Anis", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Balligi", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Banuar", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Botique", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Caaringayan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Cabilaoan West", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Cabulalaan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Calaoagan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Calmay", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Casampagaan", new HashMap<String, Double>() {{
                put("1-50",130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Casanestebanan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Casantiagoan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Domingo Alarcio", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Inmanduyan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Lebueg", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Maraboc", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 240.0);
                put("200+", 375.0);
            }});
            put("Nanbagatan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Panaga", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 240.0);
                put("200+", 375.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Talogtog", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 240.0);
                put("200+", 375.0);
            }});
            put("Turko", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Yatyat", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
        }});

        //Lingayen
        put("Lingayen", new HashMap<String, Map<String, Double>>() {{
            put("Aliwekwek", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Baay", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Balangobong", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Balococ", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Bantayan", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Basing", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Capandanan", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Domalandan Center", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Domalandan East", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Domalandan West", new HashMap<String, Double>() {{
                put("1-50",230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Dorongan", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Dulag", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Estanza", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("Lasip", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Libsong East", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Libsong West", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Malawa", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Malimpuec", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Maniboc", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Matalava", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Naguelguel", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Namolan", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Pangapisan North", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 480.0);
            }});
            put("Pangapisan Sur", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Quibaol", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Rosario", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Sabangan", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("Talogtog", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Tonton", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Tumbar", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Wawa", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
        }});

        //Mabini
        put("Mabini", new HashMap<String, Map<String, Double>>() {{
            put("Bacnit", new HashMap<String, Double>() {{
                put("1-50", 410.0);
                put("51-200", 660.0);
                put("200+", 830.0);
            }});
            put("Barlo", new HashMap<String, Double>() {{
                put("1-50", 410.0);
                put("51-200", 660.0);
                put("200+", 830.0);
            }});
            put("Caabiangaan", new HashMap<String, Double>() {{
                put("1-50", 410.0);
                put("51-200", 660.0);
                put("200+", 830.0);
            }});
            put("Cabanaetan", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Cabinuangan", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Calzada", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("Caranglaan", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("De Guzman", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Luna", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("Magalong", new HashMap<String, Double>() {{
                put("1-50", 410.0);
                put("51-200", 660.0);
                put("200+", 830.0);
            }});
            put("Nibaliw", new HashMap<String, Double>() {{
                put("1-50", 410.0);
                put("51-200", 660.0);
                put("200+", 830.0);
            }});
            put("Patar", new HashMap<String, Double>() {{
                put("1-50", 410.0);
                put("51-200", 660.0);
                put("200+", 830.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+", 795.0);
            }});
            put("San Pedro", new HashMap<String, Double>() {{
                put("1-50", 430.0);
                put("51-200", 690.0);
                put("200+", 865.0);
            }});
            put("Tagudin", new HashMap<String, Double>() {{
                put("1-50", 390.0);
                put("51-200", 630.0);
                put("200+",795.0);
            }});
            put("Villacorta", new HashMap<String, Double>() {{
                put("1-50", 510.0);
                put("51-200", 810.0);
                put("200+", 1005.0);
            }});
        }});

        //Malasiqui
        put("Malasiqui", new HashMap<String, Map<String, Double>>() {{
            put("Abonagan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Agdaot", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Alacan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Aliaga", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Amacalan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Anolid", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Apaya", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 205.0);
            }});
            put("Asin Este", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Asin Weste", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Bacundao Este", new HashMap<String, Double>() {{
                put("1-50",70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Bacundao Weste", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Bakitiw", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Balite", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 190.0);
                put("200+", 270.0);
            }});
            put("Banawang", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 190.0);
                put("200+", 270.0);
            }});
            put("Barang", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Bawer", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Binalay", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Bobon", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Bolaoit", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Bongar", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Butao", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Cabatling", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Cabueldatan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Calbueg", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Canan Norte", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Canan Sur", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Cawayan Bogtong", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Don Pedro", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Gatang", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Goliman", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Gomez", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Guilig", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Ican", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Ingalagala", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Lareg-lareg", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Lasip", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Lepa", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Loqueb Este", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Loqueb Norte", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Loqueb Sur", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Lunec", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Mabulitec", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Malimpec", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Manggan-Dampay", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Nalsian Norte", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Nalsian Sur", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Nancapian", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Nansangaan", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Olea", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Pacuan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Palapar Norte", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Palapar Sur", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Palong", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Pamaranum", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Pasima", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Payar", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Polong Norte", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 205.0);
            }});
            put("Polong Sur", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Potiocan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("San Julian", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Tabo-Sili", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Talospatang", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Taloy", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Taloyan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Tambac", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Tobor", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Tolonguat", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 205.0);
            }});
            put("Tomling", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Umando", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Viado", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Waig", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Warey", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
        }});
        //Manaoag
        put("Manaoag", new HashMap<String, Map<String, Double>>() {{
            put("Babasit", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Baguinay", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Baritao", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Bisal", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Bucao", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Cabanbanan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Calaocan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Inamotan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Lelemaan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Licsi", new HashMap<String, Double>() {{
                put("1-50",110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Lipit Norte", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Lipit Sur", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 305.0);
            }});
            put("Matolong", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Mermer", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Nalsian", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Oraan East", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Oraan West", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Pantal", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Pao", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Parian", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Pugaro", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("San Ramon", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Santa Ines", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Sapang", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Tebuel", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
        }});
        //Mangaldan
        put("Mangaldan", new HashMap<String, Map<String, Double>>() {{
            put("Alitaya", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Amansabina", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Anolid", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Banaoang", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Bantayan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Bari", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Bateng", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Buenlag", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("David", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Embarcadero", new HashMap<String, Double>() {{
                put("1-50",130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Gueguesangen", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Guesang", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Guiguilonen", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Guilig", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 240.0);
                put("200+", 375.0);
            }});
            put("Inlambo", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 240.0);
                put("200+", 375.0);
            }});
            put("Lanas", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Landas", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Maasin", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Macayug", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Malabago", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Navaluan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Nibaliw", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Osiem", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Palua", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Pogo", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Salaan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Salay", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Talogtog", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Tebag", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
        }});
        //Mangatarem
        put("Mangatarem", new HashMap<String, Map<String, Double>>() {{
            put("Andangin", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Arellano Street", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Bantay", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Bantocaling", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 410.0);
            }});
            put("Baracbac", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Bogtong Bolo", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 410.0);
            }});
            put("Bogtong Bunao", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 410.0);
            }});
            put("Bogtong Centro", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 410.0);
            }});
            put("Bogtong Niog", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Bogtong Silag", new HashMap<String, Double>() {{
                put("1-50",190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Buaya", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Buenlag", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Bueno", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Bunagan", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Bunlalacao", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Burgos Street", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Cabaluyan 1st", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Cabaluyan 2nd", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Cabarabuan", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Cabaruan", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Cabayaoasan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Cabayugan", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Cacaoiten", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Calumboyan Norte", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Calumboyan Sur", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Calvo", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Casilagan", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Catarataraan", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Caturay Norte", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Caturay Sur", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Caviernesan", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Dorongan Ketaket", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Dorongan Linmansangan", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Dorongan Punta", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Dorongan Sawat", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Dorongan Valerio", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("General Luna", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Historia", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Lawak Langka", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Linmansangan", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Lopez", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Mabini", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Macarang", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Malabobo", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Malibong", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Malunec", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Maravilla", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Maravilla-Arellano Ext.", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Muelang", new HashMap<String, Double>() {{
                put("1-50", 250.0);
                put("51-200", 420.0);
                put("200+", 550.0);
            }});
            put("Naguilayan East", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Naguilayan West", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Nancasalan", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Niog-Cabison-Bulaney", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Olegario-Caoile", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Olo Cacamposan", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Olo Cafabrosan", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Olo Cagarlitan", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Osmeña", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Pacalat", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Pampano", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Parian", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Paul", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Peania Pedania", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Pogon-Aniat", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Pogon-Lomboy", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Ponglo-Baleg", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Ponglo-Muelag", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Quetegan", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Quezon", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Salavante", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Sapang", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Sonson Ongkit", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Suaco", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Tagac", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Takipan", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Talogtog", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Tococ Barikir", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Torre 1st", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Torre 2nd", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Torres Bugallon", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Umangan", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Zamora", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
        }});
        //Mapandan
        put("Mapandan", new HashMap<String, Map<String, Double>>() {{
            put("Amanoaoac", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Apaya", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Aserda", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Baloling", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Coral", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 305.0);
            }});
            put("Golden", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 305.0);
            }});
            put("Jimenez", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Lambayan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Luyan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Nilombot", new HashMap<String, Double>() {{
                put("1-50",130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Pias", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Primicias", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Santa Maria", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Torres", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
        }});
        //Natividad
        put("Natividad", new HashMap<String, Map<String, Double>>() {{
            put("Barangobong", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Batchelor East", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Batchelor West", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Burgos", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Cacandungan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Calapugan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Canarem", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Luna", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Poblacion East", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Poblacion West", new HashMap<String, Double>() {{
                put("1-50",150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Rizal", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Salud", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("San Eugenio", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("San Macario Norte", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("San Macario Sur", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("San Maximo", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("San Miguel", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Silag", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
        }});


        //Pozorrubio
        put("Pozorrubio", new HashMap<String, Map<String, Double>>() {{
            put("Alipangpang", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Amagbagan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Balacag", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Banding", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Bantugan", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Batakil", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Bobonan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 330.0);
                put("200+", 410.0);
            }});
            put("Buneg", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Cablong", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Casanfernandoant", new HashMap<String, Double>() {{
                put("1-50",190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Castaño", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Dilan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Don Benito", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Haway", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 455.0);
            }});
            put("Imbalbalatong", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Inoman", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Laoac", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Maambal", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Malasin", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Malokiat", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Manaol", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Nama", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Nantangalan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Palacpalac", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Palguyod", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Poblacion I", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Poblacion II", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Poblacion III", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Poblacion VI", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 300.0);
                put("200+", 445.0);
            }});
            put("Rosario", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Sugcong", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 300.0);
                put("200+", 445.0);
            }});
            put("Tulnac", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 300.0);
                put("200+", 445.0);
            }});
            put("Villegas", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
        }});

        //Rosales
        put("Rosales", new HashMap<String, Map<String, Double>>() {{
            put("Acop", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Bakitbakit", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Balingcanaway", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Cabalaoangan Norte", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Cabalaoangan Sur", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Calanutan", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Camangaan", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Capitan Tomas", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Carmay East", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Carmay West", new HashMap<String, Double>() {{
                put("1-50",50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Carmen East", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Carmen West", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Casanicolasan", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Coliling", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Don Antonio Village", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Guiling", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Palakipak", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Pangaoan", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Rabago", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Rizal", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Salvacion", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("San Angel", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("San Antonio", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("San Bartolome", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("San Isidro", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("San Luis", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("San Pedro East", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("San Pedro West", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("San Vicente", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Station District", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Tomana East", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Tomana West", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Zone I", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Zone II", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Zone III", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Zone IV", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Zone V", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
        }});


        //San Carlos
        put("San Carlos", new HashMap<String, Map<String, Double>>() {{
            put("Abanon", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Agdao", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Anando", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Ano", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Antipangol", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Aponit", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Bacnar", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Balaya", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Balayong", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Baldog", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Balite Sur", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Balococ", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Bani", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Bega", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Bocboc", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Bogaoan", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Bolingit", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Bolosan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Bonifacio", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Buenglat", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Bugallon-Posadas Street", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Burgos Padlan", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Cacaritan", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Caingal", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Calobaoan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Calomboyan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Caoayan-Kiling", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Capataan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Cobol", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Coliling", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Cruz", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Doyong", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Gamata", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Guelew", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Ilang", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Inerangan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Isla", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Libas", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Lilimasan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Longos", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Lucban", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("M. Soriano", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Mabalbalino", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Mabini", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Magtaking", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Malacañang", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Maliwara", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Mamarlao", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Manzon", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Matagdem", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Mestizo Norte", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Naguilayan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Nilentap", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Padilla-Gomez", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Pagal", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Paitan-Panoypoy", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Palaming", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Palaris", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Palospos", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Pangalangan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Pangoloan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Pangpang", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Parayao", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Payapa", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Payar", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Perez Boulevard", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("PNR Station Site", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Polo", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Quezon Boulevard", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Quintong", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Rizal", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Roxas Boulevard", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Salinap", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("San Juan", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("San Pedro-Taloy", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Sapinit", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Supo", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Talang", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Tamayo", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Tandang Sora", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Tandoc", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Tarece", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Tarectec", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Tayambani", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Tebag", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Turac", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
        }});






        //San Fabian
        put("San Fabian", new HashMap<String, Map<String, Double>>() {{
            put("Alacan", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Ambalangan-Dalin", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Angio", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Anonang", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Aramal", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 3755.0);
            }});
            put("Bigbiga", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Binday", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Bolaoen", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Bolasi", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Cabaruan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Cayanga", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Colisao", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Gomot", new HashMap<String, Double>() {{
                put("1-50", 230.0);
                put("51-200", 390.0);
                put("200+", 515.0);
            }});
            put("Inmalog", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Inmalog Norte", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Lakep-Butao", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Lipit-Tumeeng", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Longos", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Longos Proper", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Longos-Amangonan-Parac-Parac Fabrica", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Mabilao", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Nibaliw Central", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Nibaliw East", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Nibaliw Magliba", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Nibaliw Narvarte", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Nibaliw Vidal", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Palapad", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Rabon", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Sagud-Bahley", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Sobol", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Tempra-Guilig", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Tiblong", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Tocok", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
        }});

        //San Jacinto
        put("San Jacinto", new HashMap<String, Map<String, Double>>() {{
            put("Awai", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Bagong Pag-asa", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Bolo", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Capaoay", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Casibong", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Guibel", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Imelda", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Labney", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Lobong", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Macayug", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Magsaysay", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("San Guillermo", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("San Jose", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("San Juan", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("San Roque", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("San Vicente", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Santa Cruz", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Santa Maria", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Santo Tomas", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
        }});

        //San Manuel
        put("San Manuel", new HashMap<String, Map<String, Double>>() {{
            put("Cabacaraan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Cabaritan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Flores", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Guiset Norte", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Guiset Sur", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Lapalo", new HashMap<String, Double>() {{
                put("1-50", 290.0);
                put("51-200", 480.0);
                put("200+", 620.0);
            }});
            put("Nagsaag", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Narra", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("San Antonio-Arzadon", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("San Bonifacio", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("San Juan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("San Roque", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("San Vicente", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Santo Domingo", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
        }});


        //San Nicolas
        put("San Nicolas", new HashMap<String, Map<String, Double>>() {{
            put("Bensican", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Cabitnongan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Caboloan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Cacabugaoan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Calanutian", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Calaocan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Camanggaan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Camindoroan", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Casaratan", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Dalumpinas", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Fianza", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Lungao", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Malico", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Malilion", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Nagkaysa", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Nining", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Poblacion East", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Poblacion West", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Salingcob", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Salpad", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("San Felipe East", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("San Felipe West", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("San Isidro", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("San Jose", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("San Rafael Centro", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("San Rafael East", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("San Rafael West", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("San Roque", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Santa Maria East", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Santa Maria West", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Santo Tomas", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Siblot", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Sobol", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
        }});

        //San Quintin
        put("San Quintin", new HashMap<String, Map<String, Double>>() {{
            put("Alac", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Baligayan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Bantog", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 30.0);
                put("200+", 410.0);
            }});
            put("balintaguen", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Cabalaoangan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Cabangaran", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Calomboyan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Carayacan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Casantamarian", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Gonzalo", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Labuan", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Lagasit", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Lumayao", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Mabini", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Mantacdang", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Nangapugan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Poblacion Zone I", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Poblacion Zone II", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Poblacion Zone III", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("San Pedro", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Ungib", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
        }});
        //Santa Barbara
        put("Santa Barbara", new HashMap<String, Map<String, Double>>() {{
            put("Alibago", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Balingueo", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Banaoang", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Banzal", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Botao", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Cablong", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Carusocan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Dalongue", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Erfe", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Gueguesangen", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Leet", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Malanay", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Maningding", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Maronong", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Maticmatic", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Minien East", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Minien West", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Nilombot", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Patayac", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Payas", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Poblacion Norte", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Poblacion Sur", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Primicias", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Sapang", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Sonquil", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Tebag East", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Tebag West", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Tuliao", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Ventinilla", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
        }});
        //Santa Maria
        put("Santa Maria", new HashMap<String, Map<String, Double>>() {{
            put("Bal-loy", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Bantog", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Caboluan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Cal-litang", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Capandanan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Cauplasan", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Dalayap", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Libsong", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Namagbagan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Paitan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Pataquid", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Pilar", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Poblacion East", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Poblacion West", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Pugot", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Samon", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("San Alejandro", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("San Mariano", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("San Pablo", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("San Patricio", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("San Vicente", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Santa Cruz", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Santa Rosa", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
        }});
        //Santo Tomas
        put("Santo Tomas", new HashMap<String, Map<String, Double>>() {{
            put("La Luna", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Poblacion East", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Poblacion West", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Salvacion", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("San Agustin", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("San Antonio", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("San Jose", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("San Marcos", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Santo Domingo", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
            put("Santo Niño", new HashMap<String, Double>() {{
                put("1-50", 50.0);
                put("51-200", 120.0);
                put("200+", 200.0);
            }});
        }});
        //Sison
        put("Sison", new HashMap<String, Map<String, Double>>() {{
            put("Agat", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Alibeng", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Amagbagan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Artacho", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Asan Norte", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Asan Sur", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Bantay Insik", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Bila", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("binmeckeg", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Bulaoen East", new HashMap<String, Double>() {{
                put("1-50", 210.0);
                put("51-200", 360.0);
                put("200+", 480.0);
            }});
            put("Bulaoen West", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Cabarita", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Calunetan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Camangaan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Cauringan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Dungon", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Esperanza", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Inmalog", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Killo", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Labayug", new HashMap<String, Double>() {{
                put("1-50", 270.0);
                put("51-200", 450.0);
                put("200+", 585.0);
            }});
            put("Paldit", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Pandingan", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Pinmilapil", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Poblacion Central", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Poblacion Norte", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Poblacion Sur", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Sagunto", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Tara-tara", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
        }});
        //Sual
        put("Sual", new HashMap<String, Map<String, Double>>() {{
            put("baquioen", new HashMap<String, Double>() {{
                put("1-50", 290.0);
                put("51-200", 480.0);
                put("200+", 620.0);
            }});
            put("Baybay Norte", new HashMap<String, Double>() {{
                put("1-50", 350.0);
                put("51-200", 570.0);
                put("200+", 725.0);
            }});
            put("Baybay Sur", new HashMap<String, Double>() {{
                put("1-50", 330.0);
                put("51-200", 540.0);
                put("200+", 690.0);
            }});
            put("Bolaoen", new HashMap<String, Double>() {{
                put("1-50", 330.0);
                put("51-200", 540.0);
                put("200+", 690.0);
            }});
            put("Cabalitian", new HashMap<String, Double>() {{
                put("1-50", 330.0);
                put("51-200", 540.0);
                put("200+", 690.0);
            }});
            put("Calumbuyan", new HashMap<String, Double>() {{
                put("1-50", 330.0);
                put("51-200", 540.0);
                put("200+", 690.0);
            }});
            put("Camagsingalan", new HashMap<String, Double>() {{
                put("1-50", 330.0);
                put("51-200", 540.0);
                put("200+", 690.0);
            }});
            put("Caoayan", new HashMap<String, Double>() {{
                put("1-50", 310.0);
                put("51-200", 510.0);
                put("200+", 655.0);
            }});
            put("Capantolan", new HashMap<String, Double>() {{
                put("1-50", 310.0);
                put("51-200", 510.0);
                put("200+", 655.0);
            }});
            put("Macaycayawan", new HashMap<String, Double>() {{
                put("1-50", 310.0);
                put("51-200", 510.0);
                put("200+", 655.0);
            }});
            put("Paitan East", new HashMap<String, Double>() {{
                put("1-50", 310.0);
                put("51-200", 510.0);
                put("200+", 655.0);
            }});
            put("Paitan West", new HashMap<String, Double>() {{
                put("1-50", 330.0);
                put("51-200", 540.0);
                put("200+", 690.0);
            }});
            put("Pangascasan", new HashMap<String, Double>() {{
                put("1-50", 350.0);
                put("51-200", 570.0);
                put("200+", 725.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 290.0);
                put("51-200", 480.0);
                put("200+", 620.0);
            }});
            put("Santo Domingo", new HashMap<String, Double>() {{
                put("1-50", 330.0);
                put("51-200", 540.0);
                put("200+", 690.0);
            }});
            put("Seselangen", new HashMap<String, Double>() {{
                put("1-50", 330.0);
                put("51-200", 540.0);
                put("200+", 690.0);
            }});
            put("Sioasio East", new HashMap<String, Double>() {{
                put("1-50", 350.0);
                put("51-200", 570.0);
                put("200+", 725.0);
            }});
            put("Sioasio West", new HashMap<String, Double>() {{
                put("1-50", 350.0);
                put("51-200", 570.0);
                put("200+", 725.0);
            }});
            put("Victoria", new HashMap<String, Double>() {{
                put("1-50", 350.0);
                put("51-200", 570.0);
                put("200+", 725.0);
            }});
        }});
        //Tayug
        put("Tayug", new HashMap<String, Map<String, Double>>() {{
            put("Agno", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Amistad", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Barangay A", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Barangay B", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Barangay C", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Barangay D", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Barangobong", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("C. Lichauco", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Carriedo", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Evangelista", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Guzon", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Lawak", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Legaspi", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Libertad", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Magallanes", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Panganiban", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Saleng", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Santo Domingo", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Toketec", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Trenchera", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Zamora", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
        }});
        //Umingan
        put("Umingan", new HashMap<String, Map<String, Double>>() {{
            put("Abot Molina", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Alo-o", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Amaronan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Annam", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Bantug", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Baracbac", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Barat", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Buenavista", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Cabalitian", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Cabangaran", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Cabaruan", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Cabatuan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Cadiz", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Calitlitan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Carayungan Sur", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Carosalesan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Casilan", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Caurdanetaan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Conception", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Decreto", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Del Rosario", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Diaz", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Diket", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Don Justo Abalos", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Don Mariano", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Ezperanze", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Evangelista", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Flores", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Fulgosino", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Gonzales", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("La Paz", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("labuan", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Lauren", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Lubong", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Luna Este", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Luna Weste", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Mantacdang", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Maseil-seil", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Nampalcan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Nancalabasaan", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Pangangaan", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Papallasen", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Pamienta", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Poblacion East", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Poblacion West", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Prado", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Resureccion", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("Ricos", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("San Andres", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("San Juan", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("San Leon", new HashMap<String, Double>() {{
                put("1-50", 90.0);
                put("51-200", 180.0);
                put("200+", 270.0);
            }});
            put("San Pablo", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("San Vicente", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
            put("Santa Maria", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Santa Rosa", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Sinabaan", new HashMap<String, Double>() {{
                put("1-50", 110.0);
                put("51-200", 210.0);
                put("200+", 305.0);
            }});
            put("Tanggal Sawang", new HashMap<String, Double>() {{
                put("1-50", 130.0);
                put("51-200", 240.0);
                put("200+", 340.0);
            }});
        }});
        //Urbiztondo
        put("Urbiztondo", new HashMap<String, Map<String, Double>>() {{
            put("Angatel", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Balangay", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Batangcaoa", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Baug", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Bayaoas", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Bituag", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Camambugan", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Dalangiring", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Duplac", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Galarin", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Gueteb", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Malaca", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Malayo", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Malibong", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Pasibi East", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Pasibi West", new HashMap<String, Double>() {{
                put("1-50", 150.0);
                put("51-200", 270.0);
                put("200+", 375.0);
            }});
            put("Pisuac", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Real", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
            put("Salavante", new HashMap<String, Double>() {{
                put("1-50", 190.0);
                put("51-200", 330.0);
                put("200+", 445.0);
            }});
            put("Sawat", new HashMap<String, Double>() {{
                put("1-50", 170.0);
                put("51-200", 300.0);
                put("200+", 410.0);
            }});
        }});
        //Urdaneta
        put("Urdaneta City", new HashMap<String, Map<String, Double>>() {{
            put("Anonas", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Bactad East", new HashMap<String, Double>() {{
                put("1-50", 70.0);
                put("51-200", 150.0);
                put("200+", 235.0);
            }});
            put("Bayaoas", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Bolaoen", new HashMap<String, Double>() {{
                put("1-50", 70.0);//motor
                put("51-200", 150.0);//multicab
                put("200+", 235.0);//truck
            }});
            put("Cabaruan", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Cabuloan", new HashMap<String, Double>() {{
                put("1-50", 70.0);//motor
                put("51-200", 150.0);//multicab
                put("200+", 235.0);//truck
            }});
            put("Camanang", new HashMap<String, Double>() {{
                put("1-50", 70.0);//motor
                put("51-200", 150.0);//multicab
                put("200+", 235.0);//truck
            }});
            put("Camantiles", new HashMap<String, Double>() {{
                put("1-50", 70.0);//motor
                put("51-200", 150.0);//multicab
                put("200+", 235.0);//truck
            }});
            put("Casantaan", new HashMap<String, Double>() {{
                put("1-50", 70.0);//motor
                put("51-200", 150.0);//multicab
                put("200+", 235.0);//truck
            }});
            put("Catablan", new HashMap<String, Double>() {{
                put("1-50", 90.0);//motor
                put("51-200", 180.0);//multicab
                put("200+", 270.0);//truck
            }});
            put("Cayambanan", new HashMap<String, Double>() {{
                put("1-50", 70.0);//motor
                put("51-200", 150.0);//multicab
                put("200+", 235.0);//truck
            }});
            put("Consolacion", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Dilan Paurido", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Dr. Pedro T. Orata", new HashMap<String, Double>() {{
                put("1-50", 70.0);//motor
                put("51-200", 150.0);//multicab
                put("200+", 235.0);//truck
            }});
            put("Labit Proper", new HashMap<String, Double>() {{
                put("1-50", 70.0);//motor
                put("51-200", 150.0);//multicab
                put("200+", 235.0);//truck
            }});
            put("Labit West", new HashMap<String, Double>() {{
                put("1-50", 70.0);//motor
                put("51-200", 150.0);//multicab
                put("200+", 235.0);//truck
            }});
            put("Mabanogbog", new HashMap<String, Double>() {{
                put("1-50", 70.0);//motor
                put("51-200", 150.0);//multicab
                put("200+", 235.0);//truck
            }});
            put("Macalong", new HashMap<String, Double>() {{
                put("1-50", 70.0);//motor
                put("51-200", 150.0);//multicab
                put("200+", 235.0);//truck
            }});
            put("Nancalobasaan", new HashMap<String, Double>() {{
                put("1-50", 70.0);//motor
                put("51-200", 150.0);//multicab
                put("200+", 235.0);//truck
            }});
            put("Nancamaliran East", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Nancamaliran West", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Nancayasan", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Oltama", new HashMap<String, Double>() {{
                put("1-50", 70.0);//motor
                put("51-200", 150.0);//multicab
                put("200+", 235.0);//truck
            }});
            put("Palina East", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Palina West", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Pinmaludpod", new HashMap<String, Double>() {{
                put("1-50", 70.0);//motor
                put("51-200", 150.0);//multicab
                put("200+", 235.0);//truck
            }});
            put("Poblacion", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("San Jose", new HashMap<String, Double>() {{
                put("1-50", 90.0);//motor
                put("51-200", 180.0);//multicab
                put("200+", 270.0);//truck
            }});
            put("San Vicente", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Santa Lucia", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Santo Domingo", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Sugcong", new HashMap<String, Double>() {{
                put("1-50", 70.0);//motor
                put("51-200", 150.0);//multicab
                put("200+", 235.0);//truck
            }});
            put("Tipuso", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Tulong", new HashMap<String, Double>() {{
                put("1-50", 70.0);//motor
                put("51-200", 150.0);//multicab
                put("200+", 235.0);//truck
            }});
        }});
        // Villasis
        put("Villasis", new HashMap<String, Map<String, Double>>() {{
            put("Amamperez", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Bacag", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Barangobong", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Amamperez", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Barraca", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Capulaan", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Caramutan", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("La Paz", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Labit", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Lipay", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Lomboy", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Piaz", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Puelay", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("San Blas", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("San Nicolas", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Tombod", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Unzad", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Zone I", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Zone II", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Zone III", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Zone IV", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            put("Zone V", new HashMap<String, Double>() {{
                put("1-50", 50.0);//motor
                put("51-200", 120.0);//multicab
                put("200+", 200.0);//truck
            }});
            // Add more barangay
        }});
        // Add more cities
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_addtocart_buynow_product);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        // Initialize views and other components
        provinceSpinner = findViewById(R.id.spinnerProvince);
        citySpinner = findViewById(R.id.spinnerCity);
        barangaySpinner = findViewById(R.id.spinnerBarangay);
        zipCodeEditText = findViewById(R.id.code);
        zoneEditText = findViewById(R.id.zone);
        firstNameTextView = findViewById(R.id.firstname);
        deliveryPriceTextView = findViewById(R.id.delivery_price);
        lastNameTextView = findViewById(R.id.lastname);
        buyButton = findViewById(R.id.buy);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(buyer_addtocart_buynow_product.this, buyer_addtocart.class);
                startActivity(back);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        // GCash Payment container views
        LinearLayout gcashContainer = findViewById(R.id.gcash_container);
        TextView paymentUsername = findViewById(R.id.payment_username);
        TextView paymentNumber = findViewById(R.id.payment_number);

        // Maya Payment container views
        LinearLayout mayaContainer = findViewById(R.id.maya_container);
        TextView mayaUsername = findViewById(R.id.maya_username);
        TextView mayaNumbers = findViewById(R.id.maya_number);
        Spinner paymentOptionSpinner = findViewById(R.id.payment_option_spinner);
        LinearLayout paymentMode = findViewById(R.id.modeofpayment);

        // Get data from intent
        Intent intent = getIntent();
        storeName = intent.getStringExtra("storeName");
        productName = intent.getStringExtra("productName");
        quantity = intent.getIntExtra("quantity", 1);
        deliveryMethod = intent.getStringExtra("deliveryMethod");
        paymentMethod = intent.getStringExtra("paymentMethod");
        totalPrice = intent.getStringExtra("totalPrice");
        price = intent.getStringExtra("price");
        type = intent.getStringExtra("type");
        productId = intent.getStringExtra("productId");
        userId = intent.getStringExtra("userId");
        productImageUrl = intent.getStringExtra("productImageUrl");
        category = intent.getStringExtra("category");
        productType = intent.getStringExtra("productType");
        final String cartId = intent.getStringExtra("cartId");

        // Call the method to check the payment method
        checkPaymentMethod(gcashContainer, paymentUsername, paymentNumber);

        // Populate province spinner
        populateProvinceSpinner();

        // Fetch buyer's details
        fetchBuyerDetails();

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateBuyerDetails()) {
                    showConfirmationDialog(cartId);
                }
            }
        });

        // Check payment method and show spinner if "Online Payment"
        if ("Online Payment".equals(paymentMethod)) {
            paymentMode.setVisibility(View.VISIBLE);

            // Populate spinner for payment options
            ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, new String[]{"Select Payment Option", "GCash", "PayMaya"});
            paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            paymentOptionSpinner.setAdapter(paymentAdapter);

            // Handle spinner selection
            paymentOptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedPaymentOption = parent.getItemAtPosition(position).toString();

                    if ("GCash".equals(selectedPaymentOption)) {
                        // Fetch and show GCash details
                        fetchPaymentDetails("gcashName", "gcashNumber", productId, paymentUsername, paymentNumber, gcashContainer);
                        mayaContainer.setVisibility(View.GONE);
                    } else if ("PayMaya".equals(selectedPaymentOption)) {
                        // Fetch and show PayMaya details
                        fetchPaymentDetails("mayaName", "mayaNumber", productId, mayaUsername, mayaNumbers, mayaContainer);
                        gcashContainer.setVisibility(View.GONE);
                    } else {
                        // Hide both containers if no valid option is selected
                        gcashContainer.setVisibility(View.GONE);
                        mayaContainer.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    gcashContainer.setVisibility(View.GONE);
                    mayaContainer.setVisibility(View.GONE);
                }
            });

        } else {
            // Hide spinner and payment containers if not "Online Payment"
            paymentMode.setVisibility(View.GONE);
            paymentOptionSpinner.setVisibility(View.GONE);
            gcashContainer.setVisibility(View.GONE);
            mayaContainer.setVisibility(View.GONE);
        }
    }

    // Method to fetch payment details from Firebase
    private void fetchPaymentDetails(String nameKey, String numberKey, String productId, TextView usernameView, TextView numberView, LinearLayout container) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get values from the database
                String name = dataSnapshot.child(nameKey).getValue(String.class);
                String number = dataSnapshot.child(numberKey).getValue(String.class);

                // Set "Not Available" if values are missing
                if (name == null || name.isEmpty()) {
                    name = "Not Available";
                }
                if (number == null || number.isEmpty()) {
                    number = "Not Available";
                }

                // Update the TextViews
                usernameView.setText(name);
                numberView.setText(number);

                // Make the container visible
                container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buyer_addtocart_buynow_product.this, "Failed to load payment details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPaymentMethod(LinearLayout gcashContainer, TextView paymentUsername, TextView paymentNumber) {
        if ("Online Payment".equals(paymentMethod)) {
            // Fetch GCash details from Firebase if payment method is "Online Payment"
            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(productId);
            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String gcashName = dataSnapshot.child("gcashName").getValue(String.class);
                    String gcashNumber = dataSnapshot.child("gcashNumber").getValue(String.class);

                    if (gcashName != null && gcashNumber != null) {
                        // Set GCash details in TextViews
                        paymentUsername.setText(gcashName);
                        paymentNumber.setText(gcashNumber);
                        // Make the GCash container visible
                        gcashContainer.setVisibility(View.VISIBLE);
                    } else {
                        // Hide the GCash container if data is incomplete
                        gcashContainer.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(buyer_addtocart_buynow_product.this, "Failed to load GCash details", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Hide the GCash container if payment method is not "Online Payment"
            gcashContainer.setVisibility(View.GONE);
        }
    }

    private void populateProvinceSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.province_array, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(adapter);

    }

    private void fetchBuyerDetails() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("buyers").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.child("firstName").getValue(String.class);
                String lastName = dataSnapshot.child("lastName").getValue(String.class);
                String province = dataSnapshot.child("provinces").getValue(String.class);
                String city = dataSnapshot.child("cities").getValue(String.class);
                String barangay = dataSnapshot.child("barangay").getValue(String.class);
                String zone = dataSnapshot.child("zone").getValue(String.class);
                String zipCode = dataSnapshot.child("zipCode").getValue(String.class);

                // Set basic info
                if (firstName != null && lastName != null) {
                    firstNameTextView.setText(firstName);
                    lastNameTextView.setText(lastName);
                    zoneEditText.setText(zone);
                    zipCodeEditText.setText(zipCode);
                }

                // Set address info
                if (province != null) {
                    selectSpinnerItem(provinceSpinner, province);
                    updateCitySpinner(province, city, barangay);
                } else {
                    citySpinner.setAdapter(null);
                    barangaySpinner.setAdapter(null);
                }

                if ("Deliver".equals(deliveryMethod)) {
                    deliveryPriceTextView.setVisibility(View.VISIBLE);
                    updateDeliveryPrice((String) citySpinner.getSelectedItem(), barangay); // Set initial price
                } else {
                    deliveryPriceTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buyer_addtocart_buynow_product.this, "Failed to load buyer details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCitySpinner(final String selectedProvince, final String selectedCity, final String selectedBarangay) {
        int arrayId = getResources().getIdentifier("city_array_" + selectedProvince.toLowerCase().replace(" ", "_"), "array", getPackageName());

        if (arrayId != 0) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayId, R.layout.custom_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            citySpinner.setAdapter(adapter);

            citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCity = (String) parent.getItemAtPosition(position);
                    updateBarangaySpinner(selectedCity, selectedBarangay);
                    updateDeliveryPrice(selectedCity, (String) barangaySpinner.getSelectedItem());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            // Set city selection if data is provided
            if (selectedCity != null) {
                selectSpinnerItem(citySpinner, selectedCity);
            }
        } else {
            citySpinner.setAdapter(null);
            barangaySpinner.setAdapter(null);
            Toast.makeText(this, "No cities found for the selected province", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBarangaySpinner(final String selectedCity, final String selectedBarangay) {
        String resourceName = "brgy_array_" + selectedCity.toLowerCase().replace(" ", "_");
        int arrayId = getResources().getIdentifier(resourceName, "array", getPackageName());

        if (arrayId != 0) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayId, R.layout.custom_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            barangaySpinner.setAdapter(adapter);

            // Set barangay selection if data is provided
            if (selectedBarangay != null) {
                selectSpinnerItem(barangaySpinner, selectedBarangay);
            }

            barangaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedBarangay = (String) parent.getItemAtPosition(position);
                    updateDeliveryPrice((String) citySpinner.getSelectedItem(), selectedBarangay); // Pass barangay
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            barangaySpinner.setAdapter(null);
            Toast.makeText(this, "No barangays found for the selected city", Toast.LENGTH_SHORT).show();
        }
    }


    private void selectSpinnerItem(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(value);
            if (position >= 0) {
                spinner.setSelection(position);
            }
        }
    }

    private void updateDeliveryPrice(String city, String barangay) {
        double deliveryPrice = calculateDeliveryPrice(city, barangay, quantity);
        deliveryPriceTextView.setText("Delivery Price: PHP " + deliveryPrice);
    }

    private double calculateDeliveryPrice(String city, String barangay, int quantity) {
        Map<String, Map<String, Double>> cityPrices = deliveryPrices.get(city);
        if (cityPrices != null) {
            Map<String, Double> barangayPrices = cityPrices.get(barangay);
            if (barangayPrices != null) {
                if (quantity <= 50) {
                    return barangayPrices.getOrDefault("1-50", 0.0);
                } else if (quantity <= 200) {
                    return barangayPrices.getOrDefault("51-200", 0.0);
                } else {
                    return barangayPrices.getOrDefault("200+", 0.0);
                }
            }
        }
        return 0.0; // Return 0 if no price found
    }

    private boolean validateBuyerDetails() {
        // Perform validation checks for buyer's details
        if (provinceSpinner.getSelectedItem() == null ||
                citySpinner.getSelectedItem() == null ||
                barangaySpinner.getSelectedItem() == null ||
                zipCodeEditText.getText().toString().isEmpty() ||
                zoneEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ("Deliver".equals(deliveryMethod) && deliveryPriceTextView.getVisibility() == View.VISIBLE) {
            double deliveryPrice = calculateDeliveryPrice((String) citySpinner.getSelectedItem(), (String) barangaySpinner.getSelectedItem(), quantity);
            // Add any additional validation for the delivery price if needed
            // For example, ensure deliveryPrice is greater than 0
            if (deliveryPrice <= 0) {
                Toast.makeText(this, "Invalid delivery price", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void showConfirmationDialog(String cartId) {
        double deliveryPayment = 0.0;
        if ("Deliver".equals(deliveryMethod)) {
            deliveryPayment = calculateDeliveryPrice((String) citySpinner.getSelectedItem(), (String) barangaySpinner.getSelectedItem(), quantity);
        }

        double total = Double.parseDouble(totalPrice) + deliveryPayment;


        // Build the confirmation message
        String message = String.format("Store: %s\nProduct: %s\nQuantity: %d\nDelivery Method: %s\nPayment Method: %s\n" +
                        "%s\nPrice: %s\nTotal Price ₱%s\nDelivery Payment: ₱%.2f\n\nTotal ₱%.2f\n\n" +
                        "Address:\nProvince: %s\nCity: %s\nBarangay: %s\nZip Code: %s\nZone: %s",
                storeName, productName, quantity, deliveryMethod, paymentMethod, type, price, totalPrice,
                deliveryPayment, total,
                provinceSpinner.getSelectedItem().toString(),
                citySpinner.getSelectedItem().toString(),
                barangaySpinner.getSelectedItem().toString(),
                zipCodeEditText.getText().toString(),
                zoneEditText.getText().toString());

        double finalDeliveryPayment = deliveryPayment;
        new AlertDialog.Builder(this)
                .setTitle("Confirm Purchase")
                .setMessage(message)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        storePurchaseData(cartId, total, finalDeliveryPayment);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void storePurchaseData(final String cartId, double total, double deliveryPayment) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(productId);

        productRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Product product = mutableData.getValue(Product.class);

                if (product == null) {
                    return Transaction.success(mutableData);
                }

                int currentStock = Integer.parseInt(product.getStock());
                int currentSold = product.getSold() != null ? Integer.parseInt(product.getSold()) : 0;

                if (quantity > currentStock) {
                    Toast.makeText(buyer_addtocart_buynow_product.this, "Not enough stock available", Toast.LENGTH_SHORT).show();
                    return Transaction.abort();
                }

                product.setStock(String.valueOf(currentStock - quantity));
                product.setSold(String.valueOf(currentSold + quantity));
                mutableData.setValue(product);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Toast.makeText(buyer_addtocart_buynow_product.this, "Not enough stock available", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (committed) {
                    DatabaseReference purchaseRef = FirebaseDatabase.getInstance().getReference("purchases").push();
                    String purchaseId = purchaseRef.getKey();

                    if (purchaseId != null) {
                        Date currentDate = Calendar.getInstance().getTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String formattedDate = sdf.format(currentDate);

                        double deliveryPayment = 0.0;
                        if ("Deliver".equals(deliveryMethod)) {
                            deliveryPayment = calculateDeliveryPrice((String) citySpinner.getSelectedItem(), (String) barangaySpinner.getSelectedItem(), quantity);
                        }

                        // Calculate the fee as 2% of totalPrice
                        double fee = Double.parseDouble(totalPrice) * 0.02; // 2% fee

                        // Calculate the timestamp based on productType
                        String timestamp = calculateTimestamp(productType);

                        Purchase purchase = new Purchase(
                                storeName,
                                productName,
                                quantity,
                                deliveryMethod,
                                paymentMethod,
                                totalPrice,
                                price,
                                type,
                                productId,
                                userId,
                                firstNameTextView.getText().toString(),
                                lastNameTextView.getText().toString(),
                                provinceSpinner.getSelectedItem().toString(),
                                citySpinner.getSelectedItem().toString(),
                                barangaySpinner.getSelectedItem().toString(),
                                zipCodeEditText.getText().toString(),
                                zoneEditText.getText().toString(),
                                "Pending",
                                purchaseId,
                                productImageUrl,
                                category,
                                formattedDate,
                                deliveryPayment,
                                total,
                                fee,
                                productType,
                                timestamp
                        );

                        purchaseRef.setValue(purchase).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Remove item from cart after successful purchase
                                    DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("addtocart").child(cartId);
                                    cartRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(buyer_addtocart_buynow_product.this, "Purchase successful", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(buyer_addtocart_buynow_product.this, "Failed to remove item from cart", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(buyer_addtocart_buynow_product.this, "Purchase failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(buyer_addtocart_buynow_product.this, "Failed to generate purchase ID", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    // Function to calculate the timestamp based on productType
    private String calculateTimestamp(String productType) {
        Calendar calendar = Calendar.getInstance();

        if ("Pre Order".equals(productType)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1); // Add 1 day
        } else if ("Regular".equals(productType)) {
            calendar.add(Calendar.DAY_OF_YEAR, 2); // Add 2 days
        }

        // Set time to 5 PM
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(calendar.getTime());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Apply fade in and fade out animations

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
