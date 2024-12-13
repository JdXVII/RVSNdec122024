package com.example.allgoods2024;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class vendor_report_generation extends AppCompatActivity {

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isInternetAvailable(context)) {
                // If internet is available, reload the data
                fetchSalesReport();
                fetchCategorySalesData();
            } else {
                // Optionally, show a message indicating no connection
                Toast.makeText(vendor_report_generation.this, "No internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private TextView selectedDateTextView, vegetablesSold, vegetablesTotalPrice;
    private PieChart pieChartWeek, pieChartMonth, pieChartYear;
    private FirebaseDatabase database;
    private DatabaseReference reportsRef;
    private String userId;
    private Button selectDateButton, fetchReportButton;
    private BarChart barChart;
    private LinearLayout vegetablesContainer, fruitsContainer, driedFoodsContainer, condimentsContainer, othersContainer, allContainer;

    // Additional TextViews for other categories
    private TextView fruitsSold, fruitsTotalPrice;
    private TextView driedFoodsSold, driedFoodsTotalPrice;
    private TextView othersSold, othersTotalPrice;
    private TextView condimentsSold, condimentsTotalPrice;

    private ImageView savePdfButton;

    private LinearLayout events, orders, message, home;
    private TextView messageBadge;
    private static final String CHANNEL_ID = "vendor_message_notifications";
    private static final String PREF_LAST_NOTIFIED_COUNT = "lastNotifiedCount";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_report_generation);

        // Register network connectivity receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        // Initialize Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();  // Get the current vendor user ID
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        events = findViewById(R.id.events);
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent events = new Intent(vendor_report_generation.this, vendor_events.class);
                startActivity(events);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        orders = findViewById(R.id.orders);
        orders.setOnClickListener(view -> {
            Intent sales = new Intent(vendor_report_generation.this, vendor_orders.class);
            startActivity(sales);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        home = findViewById(R.id.home);
        home.setOnClickListener(view -> {
            Intent home = new Intent(vendor_report_generation.this, vendor_homepage.class);
            startActivity(home);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        messageBadge = findViewById(R.id.message_badge);
        message = findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent message = new Intent(vendor_report_generation.this, vendor_messages.class);
                startActivity(message);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        savePdfButton = findViewById(R.id.print);

        savePdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePDF();
            }
        });

        // Initialize UI elements
        selectedDateTextView = findViewById(R.id.selected_date);
        pieChartWeek = findViewById(R.id.pie_chart_week);
        pieChartMonth = findViewById(R.id.pie_chart_month);
        pieChartYear = findViewById(R.id.pie_chart_year);
        selectDateButton = findViewById(R.id.select_date_button);
        fetchReportButton = findViewById(R.id.fetch_report_button);
        barChart = findViewById(R.id.bar_chart);

        vegetablesContainer = findViewById(R.id.vegetablesContainer);
        fruitsContainer = findViewById(R.id.fruitsContainer);
        driedFoodsContainer = findViewById(R.id.driedFoodsContainer);
        condimentsContainer = findViewById(R.id.condimentsContainer);
        othersContainer = findViewById(R.id.othersContainer);
        allContainer = findViewById(R.id.allContainer);

        // Initialize additional TextViews for categories
        vegetablesSold = findViewById(R.id.vegetablesSold);
        vegetablesTotalPrice = findViewById(R.id.vegetablesTotalPrice);
        fruitsSold = findViewById(R.id.fruitsSold);
        fruitsTotalPrice = findViewById(R.id.fruitsTotalPrice);
        driedFoodsSold = findViewById(R.id.driedFoodsSold);
        driedFoodsTotalPrice = findViewById(R.id.driedFoodsTotalPrice);
        othersSold = findViewById(R.id.othersSold);
        othersTotalPrice = findViewById(R.id.othersTotalPrice);
        condimentsSold = findViewById(R.id.condimentsSold);
        condimentsTotalPrice = findViewById(R.id.condimentsTotalPrice);


        // Set the current date as the default in the selectedDateTextView
        setCurrentDate();

        // Initialize Firebase database references
        database = FirebaseDatabase.getInstance();
        reportsRef = database.getReference("reports");

        // Fetch report immediately using the default date
        fetchSalesReport();
        fetchCategorySalesData();

        fetchReportButton.setOnClickListener(view -> {
            fetchSalesReport();
            fetchCategorySalesData();
        });

        // DatePickerDialog for selecting date
        selectDateButton.setOnClickListener(view -> showDatePickerDialog());

        // Fetch report when button clicked
        fetchReportButton.setOnClickListener(view -> {
            fetchSalesReport();
            fetchCategorySalesData();
        });

        vegetablesContainer.setOnClickListener(view -> showItemsDialog("Vegetables"));
        driedFoodsContainer.setOnClickListener(view -> showItemsDialog("Dried Fish"));
        fruitsContainer.setOnClickListener(view -> showItemsDialog("Fruits"));
        condimentsContainer.setOnClickListener(view -> showItemsDialog("Condiments"));
        othersContainer.setOnClickListener(view -> showItemsDialog("Others"));
        allContainer.setOnClickListener(view -> showAllItemsDialog());


        checkForUnreadMessages();
        createNotificationChannel();

    }

    private String generatePdfContent(String year, String week) {
        // Collect data to be converted to PDF
        return
                "Vegetables " + vegetablesSold.getText() + "\n" +
                 vegetablesTotalPrice.getText() + "\n\n" +
                "Fruits " + fruitsSold.getText() + "\n" +
                 fruitsTotalPrice.getText() + "\n\n" +
                "Dried Fish " + driedFoodsSold.getText() + "\n" +
                 driedFoodsTotalPrice.getText() + "\n\n" +
                 "Condiments " + condimentsSold.getText() + "\n" +
                 condimentsTotalPrice.getText() + "\n\n" +
                "Others " + othersSold.getText() + "\n" +
                 othersTotalPrice.getText();
    }

    private void showPdfPreview(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_pdf_preview, null);
        TextView pdfContentTextView = dialogView.findViewById(R.id.pdf_content);
        pdfContentTextView.setText(content);

        builder.setView(dialogView)
                .setTitle("Preview PDF")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Save PDF", (dialog, which) -> savePDF(content))
                .create()
                .show();
    }

    private void generatePDF() {
        String selectedDate = selectedDateTextView.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date date = dateFormat.parse(selectedDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String week = "week_" + calendar.get(Calendar.WEEK_OF_YEAR);

            // Collect sales data
            fetchCategorySalesData();

            // Create PDF content
            String pdfContent = generatePdfContent(year, week);
            showPdfPreview(pdfContent);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing date: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void savePDF(String content) {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        // Set background color
        canvas.drawColor(Color.parseColor("#F5EBE0"));

        // Draw header
        drawHeader(canvas, paint);

        // Draw body (bar graph and table)
        drawBody(canvas, paint, content);

        // Draw footer
        drawFooter(canvas, paint);

        pdfDocument.finishPage(page);

        // Save the document
        saveDocument(pdfDocument);
    }

    private void drawHeader(Canvas canvas, Paint paint) {
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo); // Replace with your logo resource ID
        int logoWidth = 60;
        int logoHeight = 60;
        Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, logoWidth, logoHeight, false);
        canvas.drawBitmap(scaledLogo, 20, 20, paint);

        // Title and subtitle, aligned with the top of the logo
        paint.setColor(Color.BLACK);
        paint.setTextSize(24);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        String title = "Weekly Sales Report";
        float titleX = (canvas.getWidth() - paint.measureText(title)) / 2;
        canvas.drawText(title, titleX, 40, paint);

        paint.setTextSize(12);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        String date = "Generated on: " + getCurrentDate();
        String tagline = "Allgoods Bagsakan Villasis";
        float dateX = (canvas.getWidth() - paint.measureText(date)) / 2;
        float taglineX = (canvas.getWidth() - paint.measureText(tagline)) / 2;
        canvas.drawText(date, dateX, 60, paint);
        canvas.drawText(tagline, taglineX, 80, paint);

        // Line under header
        paint.setStrokeWidth(2);
        paint.setColor(Color.LTGRAY);
        canvas.drawLine(10, 100, canvas.getWidth() - 10, 100, paint);
    }

    private void drawBody(Canvas canvas, Paint paint, String content) {
        // Set up colors and paint properties for backgrounds and borders
        paint.setAntiAlias(true);
        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.LTGRAY);
        borderPaint.setStrokeWidth(3);
        borderPaint.setStyle(Paint.Style.STROKE);

        // Draw Sales Chart Background with rounded corners
        paint.setColor(Color.WHITE);
        RectF chartBackground = new RectF(40, 120, canvas.getWidth() - 40, 400);
        canvas.drawRoundRect(chartBackground, 10, 10, paint);
        canvas.drawRoundRect(chartBackground, 10, 10, borderPaint); // Border for the chart

        // Draw Sales Chart Title
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Sales Chart", (canvas.getWidth() - paint.measureText("Sales Chart")) / 2, 150, paint);

        // Bar Graph Data - use total sales (price) instead of sold count
        int[] totalSales = {
                Integer.parseInt(vegetablesTotalPrice.getText().toString().split(": ")[1]),
                Integer.parseInt(fruitsTotalPrice.getText().toString().split(": ")[1]),
                Integer.parseInt(driedFoodsTotalPrice.getText().toString().split(": ")[1]),
                Integer.parseInt(condimentsTotalPrice.getText().toString().split(": ")[1]),
                Integer.parseInt(othersTotalPrice.getText().toString().split(": ")[1])
        };

        String[] categories = {"Vegetables", "Fruits", "Dried Fish", "Condiments", "Others"};
        int maxBarHeight = 150; // Adjust as needed
        float barWidth = (chartBackground.width() - 60) / categories.length;
        float startX = chartBackground.left + 20;

        int[] colors = {Color.parseColor("#FFA07A"), Color.parseColor("#20B2AA"), Color.parseColor("#87CEEB"), Color.parseColor("#32CD32"), Color.parseColor("#FFD700")};

        // Calculate the maximum sales value to scale the bars
        int maxSalesValue = 0;
        for (int sales : totalSales) {
            if (sales > maxSalesValue) maxSalesValue = sales;
        }

        for (int i = 0; i < categories.length; i++) {
            paint.setColor(colors[i]);
            // Scale each bar height based on maxSalesValue and maxBarHeight
            float barHeight = ((float) totalSales[i] / maxSalesValue) * maxBarHeight;
            canvas.drawRect(startX, 350 - barHeight, startX + barWidth - 20, 350, paint);
            startX += barWidth;
        }

        // Draw Legend below chart
        paint.setTextSize(12);
        startX = chartBackground.left + 20;
        for (int i = 0; i < categories.length; i++) {
            paint.setColor(colors[i]);
            canvas.drawRect(startX, 370, startX + 20, 390, paint);
            paint.setColor(Color.BLACK);
            canvas.drawText(categories[i], startX + 25, 385, paint);
            startX += barWidth;
        }

        // Draw Table Title
        paint.setTextSize(18);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Category Sales", (canvas.getWidth() - paint.measureText("Category Sales")) / 2, 450, paint);

        // Table Header with background color and border
        float tableStartY = 470;
        paint.setTextSize(16); // Increased header text size
        paint.setColor(Color.parseColor("#a9bdb7"));
        canvas.drawRect(30, tableStartY, canvas.getWidth() - 30, tableStartY + 30, paint); // Increased header height

        // Draw header text with larger size and left alignment
        paint.setColor(Color.WHITE);
        float headerTextOffset = tableStartY + 20; // Adjusted for vertical centering in header row

        // Column X positions
        float column1X = 40; // Adjusted for left alignment in "Categories" column
        float column2X = 250; // Adjusted for left alignment in "Total Sold" column
        float column3X = 400; // Adjusted for left alignment in "Total Price" column

        canvas.drawText("Categories", column1X, headerTextOffset, paint);
        canvas.drawText("Total Sold", column2X, headerTextOffset, paint);
        canvas.drawText("Total Price", column3X, headerTextOffset, paint);

        // Table rows with border
        paint.setColor(Color.BLACK);
        String[] tableCategories = {"Vegetables", "Fruits", "Dried Fish", "Condiments", "Others"};
        int[] totalSold = {
                Integer.parseInt(vegetablesSold.getText().toString().split(": ")[1]),
                Integer.parseInt(fruitsSold.getText().toString().split(": ")[1]),
                Integer.parseInt(driedFoodsSold.getText().toString().split(": ")[1]),
                Integer.parseInt(condimentsSold.getText().toString().split(": ")[1]),
                Integer.parseInt(othersSold.getText().toString().split(": ")[1])};
        int[] totalPrice = {
                Integer.parseInt(vegetablesTotalPrice.getText().toString().split(": ")[1]),
                Integer.parseInt(fruitsTotalPrice.getText().toString().split(": ")[1]),
                Integer.parseInt(driedFoodsTotalPrice.getText().toString().split(": ")[1]),
                Integer.parseInt(condimentsTotalPrice.getText().toString().split(": ")[1]),
                Integer.parseInt(othersTotalPrice.getText().toString().split(": ")[1])
        };

        // Draw each row with white background and border
        float rowHeight = 30; // Increased row height
        float textSize = 14; // Adjusted row text size
        paint.setTextSize(textSize);
        borderPaint.setColor(Color.LTGRAY); // Light gray color for the border
        borderPaint.setStrokeWidth(1); // Thinner border

        for (int i = 0; i < tableCategories.length; i++) {
            float rowY = tableStartY + (i + 1) * rowHeight;

            // Draw row background
            paint.setColor(Color.WHITE);
            canvas.drawRect(30, rowY, canvas.getWidth() - 30, rowY + rowHeight, paint);

            // Draw text, left-aligned within the cells
            paint.setColor(Color.BLACK);
            float textOffset = rowY + rowHeight / 2 + textSize / 2 - 4; // Adjusts for vertical centering

            // Draw category text in "Categories" column
            canvas.drawText(tableCategories[i], column1X, textOffset, paint);

            // Draw total sold in "Total Sold" column, left-aligned
            canvas.drawText(String.valueOf(totalSold[i]), column2X, textOffset, paint);

            // Draw total price in "Total Price" column, left-aligned
            canvas.drawText(String.valueOf(totalPrice[i]), column3X, textOffset, paint);

            // Draw thin horizontal line for each row
            canvas.drawLine(30, rowY, canvas.getWidth() - 30, rowY, borderPaint);
        }

        // Draw outer table borders with thin lines
        canvas.drawLine(30, tableStartY, 30, tableStartY + (tableCategories.length + 1) * rowHeight, borderPaint); // Left border
        canvas.drawLine(canvas.getWidth() - 30, tableStartY, canvas.getWidth() - 30, tableStartY + (tableCategories.length + 1) * rowHeight, borderPaint); // Right border
        canvas.drawLine(30, tableStartY + (tableCategories.length + 1) * rowHeight, canvas.getWidth() - 30, tableStartY + (tableCategories.length + 1) * rowHeight, borderPaint); // Bottom border

    }

    private void drawFooter(Canvas canvas, Paint paint) {
        paint.setColor(Color.GRAY);
        paint.setTextSize(13);
        String footer = "allgoodsbagsakan.com 2024";
        float footerWidth = paint.measureText(footer);
        canvas.drawText(footer, (canvas.getWidth() - footerWidth) / 2, 820, paint);
    }

    private void saveDocument(PdfDocument pdfDocument) {
        String fileName = "Sales_Report_" + System.currentTimeMillis() + ".pdf";
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "SalesReports");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF saved: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }
    }


    // Method to get the current date in a specific format
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Method to set the current date in the selectedDateTextView
    private void setCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Format the date as YYYY-MM-DD
        String formattedDate = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
        selectedDateTextView.setText(formattedDate);
    }

    // Method to show DatePickerDialog and set selected date
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String formattedDate = selectedYear + "-" + String.format("%02d", (selectedMonth + 1)) + "-" + String.format("%02d", selectedDay);
                    selectedDateTextView.setText(formattedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void fetchSalesReport() {
        // Check Internet Connection
        if (!isInternetAvailable(this)) {
            Toast.makeText(this, "No internet connection. Please check your connection and try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show ProgressDialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching sales report...");
        progressDialog.setCancelable(false); // Prevent user from dismissing the dialog
        progressDialog.show();

        String selectedDate = selectedDateTextView.getText().toString();
        String[] dateParts = selectedDate.split("-");
        String selectedYear = dateParts[0];
        String selectedMonth = dateParts[1];

        reportsRef.child(userId).child("sales").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot salesData = task.getResult();
                if (salesData.exists()) {
                    List<PieEntry> weekEntries = new ArrayList<>();
                    List<PieEntry> monthEntries = new ArrayList<>();
                    List<PieEntry> yearEntries = new ArrayList<>();
                    int totalYearlySales = 0;

                    List<BarEntry> barEntries = new ArrayList<>();
                    int weekCounter = 1;

                    // Map to store total sales for each year
                    Map<String, Integer> yearlySalesMap = new HashMap<>();

                    // Process all years in sales data
                    for (DataSnapshot yearSnapshot : salesData.getChildren()) {
                        String yearKey = yearSnapshot.getKey();
                        int totalYearSales = 0; // Total sales for the current year

                        // Process each month for this year
                        for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                            String monthKey = monthSnapshot.getKey();
                            int totalMonthlySales = calculateTotalSales(monthSnapshot); // Calculate sales for the month

                            // Add to yearly sales total
                            totalYearSales += totalMonthlySales;

                            // Check if the selected year is the current year
                            if (yearKey.equals(selectedYear)) {
                                // Process selected month's weeks
                                if (monthKey.equals(selectedMonth)) {
                                    for (DataSnapshot weekSnapshot : monthSnapshot.getChildren()) {
                                        int weekSales = weekSnapshot.child("totalSales").getValue(Integer.class);
                                        int totalQuantity = weekSnapshot.child("totalQuantity").getValue(Integer.class);

                                        weekEntries.add(new PieEntry(weekSales, weekSnapshot.getKey()));
                                        barEntries.add(new BarEntry(weekCounter++, totalQuantity));
                                    }
                                    monthEntries.add(new PieEntry(totalMonthlySales, monthKey)); // Add selected month entry
                                } else {
                                    // Add other months of the selected year to monthEntries
                                    monthEntries.add(new PieEntry(totalMonthlySales, monthKey));
                                }
                            }
                        }

                        // Store total sales for each year
                        yearlySalesMap.put(yearKey, totalYearSales);
                    }

                    // Prepare yearEntries for the pie chart
                    for (Map.Entry<String, Integer> entry : yearlySalesMap.entrySet()) {
                        yearEntries.add(new PieEntry(entry.getValue(), entry.getKey())); // Add each year's total sales
                    }

                    // Update charts
                    updatePieChart(pieChartWeek, weekEntries, "Weekly Sales");
                    updatePieChart(pieChartMonth, monthEntries, "Monthly Sales");
                    updatePieChart(pieChartYear, yearEntries, "Yearly Sales");
                    updateBarChart(barChart, barEntries);

                } else {
                    // Reset charts if no data exists
                    updatePieChart(pieChartWeek, new ArrayList<>(), "Weekly Sales");
                    updatePieChart(pieChartMonth, new ArrayList<>(), "Monthly Sales");
                    updatePieChart(pieChartYear, new ArrayList<>(), "Yearly Sales");
                    updateBarChart(barChart, new ArrayList<>());

                    Toast.makeText(vendor_report_generation.this, "No sales data available.", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss(); // Dismiss ProgressDialog after data is fetched
            } else {
                progressDialog.dismiss(); // Dismiss ProgressDialog on error
                Toast.makeText(vendor_report_generation.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCategorySalesData() {
        // Check Internet Connection
        if (!isInternetAvailable(this)) {
            Toast.makeText(this, "No internet connection. Please check your connection and try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show ProgressDialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching category sales data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String selectedDate = selectedDateTextView.getText().toString();
        String[] dateParts = selectedDate.split("-");
        String selectedYear = dateParts[0];
        String selectedMonth = dateParts[1];

        reportsRef.child(userId).child("sales").child(selectedYear).child(selectedMonth).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot monthSnapshot = task.getResult();
                if (monthSnapshot.exists()) {
                    // Initialize counters for each category
                    int vegetablesSoldCount = 0;
                    int vegetablesTotalPriceCount = 0;
                    int fruitsSoldCount = 0;
                    int fruitsTotalPriceCount = 0;
                    int driedFoodsSoldCount = 0;
                    int driedFoodsTotalPriceCount = 0;
                    int condimentsSoldCount = 0;
                    int condimentsTotalPriceCount = 0;
                    int othersSoldCount = 0;
                    int othersTotalPriceCount = 0;

                    // Process each week in the month
                    for (DataSnapshot weekSnapshot : monthSnapshot.getChildren()) {
                        String startDate = weekSnapshot.child("startDate").getValue(String.class);
                        String endDate = weekSnapshot.child("endDate").getValue(String.class);

                        if (isDateInRange(selectedDate, startDate, endDate)) {
                            for (DataSnapshot categorySnapshot : weekSnapshot.child("categorySalesMap").getChildren()) {
                                String category = categorySnapshot.getKey();
                                for (DataSnapshot itemSnapshot : categorySnapshot.child("itemSalesMap").getChildren()) {
                                    int quantity = itemSnapshot.child("quantity").getValue(Integer.class);
                                    int price = itemSnapshot.child("price").getValue(Integer.class);

                                    // Aggregate sales data by category
                                    switch (category) {
                                        case "Vegetables":
                                            vegetablesSoldCount += quantity;
                                            vegetablesTotalPriceCount += price;
                                            break;
                                        case "Fruits":
                                            fruitsSoldCount += quantity;
                                            fruitsTotalPriceCount += price;
                                            break;
                                        case "Dried Fish":
                                            driedFoodsSoldCount += quantity;
                                            driedFoodsTotalPriceCount += price;
                                            break;
                                        case "Condiments":
                                            condimentsSoldCount += quantity;
                                            condimentsTotalPriceCount += price;
                                            break;
                                        default:
                                            // Add any uncategorized items to "Others"
                                            othersSoldCount += quantity;
                                            othersTotalPriceCount += price;
                                            break;
                                    }
                                }
                            }
                        }
                    }

                    // Update individual category TextViews
                    vegetablesSold.setText("Sold: " + vegetablesSoldCount);
                    vegetablesTotalPrice.setText("Total Price: " + vegetablesTotalPriceCount);
                    fruitsSold.setText("Sold: " + fruitsSoldCount);
                    fruitsTotalPrice.setText("Total Price: " + fruitsTotalPriceCount);
                    driedFoodsSold.setText("Sold: " + driedFoodsSoldCount);
                    driedFoodsTotalPrice.setText("Total Price: " + driedFoodsTotalPriceCount);
                    condimentsSold.setText("Sold: " + condimentsSoldCount);
                    condimentsTotalPrice.setText("Total Price: " + condimentsTotalPriceCount);
                    othersSold.setText("Sold: " + othersSoldCount);
                    othersTotalPrice.setText("Total Price: " + othersTotalPriceCount);

                    // Calculate and display overall totals
                    int allSoldCount = vegetablesSoldCount + fruitsSoldCount + driedFoodsSoldCount + condimentsSoldCount + othersSoldCount;
                    int allTotalPriceCount = vegetablesTotalPriceCount + fruitsTotalPriceCount + driedFoodsTotalPriceCount + condimentsTotalPriceCount + othersTotalPriceCount;

                    TextView allSold = findViewById(R.id.allSold);
                    TextView allTotalPrice = findViewById(R.id.allTotalPrice);
                    allSold.setText("Sold: " + allSoldCount);
                    allTotalPrice.setText("Total Price: " + allTotalPriceCount);

                    progressDialog.dismiss(); // Dismiss ProgressDialog after data is fetched
                } else {
                    resetCategoryData();
                    progressDialog.dismiss(); // Dismiss ProgressDialog on error
                    Toast.makeText(vendor_report_generation.this, "No sales data available for the selected month.", Toast.LENGTH_SHORT).show();
                }
            } else {
                progressDialog.dismiss(); // Dismiss ProgressDialog on error
                Toast.makeText(vendor_report_generation.this, "Failed to fetch category data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to check internet availability
    private boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return nc != null && (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }



    private void resetCategoryData() {
        vegetablesSold.setText("Sold: 0");
        vegetablesTotalPrice.setText("Total Price: 0");
        fruitsSold.setText("Sold: 0");
        fruitsTotalPrice.setText("Total Price: 0");
        driedFoodsSold.setText("Sold: 0");
        driedFoodsTotalPrice.setText("Total Price: 0");
        condimentsSold.setText("Sold: 0");
        condimentsTotalPrice.setText("Total Price: 0");
        othersSold.setText("Sold: 0");
        othersTotalPrice.setText("Total Price: 0");
    }


    // Check if a week is within the selected date range
    private boolean isDateInRange(String selectedDate, String startDate, String endDate) {
        return (selectedDate.compareTo(startDate) >= 0 && selectedDate.compareTo(endDate) <= 0);
    }

    private void showAllItemsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Sales Details");

        // Create a ScrollView to make the dialog content scrollable
        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);  // Set padding for the whole layout
        layout.setBackgroundColor(getResources().getColor(android.R.color.white));  // Set background color for the dialog

        scrollView.addView(layout);

        // HashMap to aggregate data by product name
        Map<String, ProductSalesData> productSalesMap = new HashMap<>();

        // Fetch the sales data for the selected month
        String selectedDate = selectedDateTextView.getText().toString();
        String[] dateParts = selectedDate.split("-");
        String selectedYear = dateParts[0];
        String selectedMonth = dateParts[1];

        reportsRef.child(userId).child("sales").child(selectedYear).child(selectedMonth).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot monthSnapshot = task.getResult();
                if (monthSnapshot.exists()) {
                    for (DataSnapshot weekSnapshot : monthSnapshot.getChildren()) {
                        String startDate = weekSnapshot.child("startDate").getValue(String.class);
                        String endDate = weekSnapshot.child("endDate").getValue(String.class);

                        if (isDateInRange(selectedDate, startDate, endDate)) {
                            for (DataSnapshot categorySnapshot : weekSnapshot.child("categorySalesMap").getChildren()) {
                                String category = categorySnapshot.getKey();
                                for (DataSnapshot itemSnapshot : categorySnapshot.child("itemSalesMap").getChildren()) {
                                    String itemId = itemSnapshot.getKey();  // Item ID
                                    String productName = itemSnapshot.child("name").getValue(String.class);  // Assuming 'name' holds the product name
                                    int quantitySold = itemSnapshot.child("quantity").getValue(Integer.class);
                                    int totalPrice = itemSnapshot.child("price").getValue(Integer.class);

                                    // Aggregate sales data by product name
                                    ProductSalesData productData = productSalesMap.getOrDefault(productName, new ProductSalesData(0, 0));
                                    productData.addSales(quantitySold, totalPrice);
                                    productSalesMap.put(productName, productData);
                                }
                            }
                        }
                    }

                    // Display the aggregated sales data for each product
                    if (!productSalesMap.isEmpty()) {
                        for (Map.Entry<String, ProductSalesData> entry : productSalesMap.entrySet()) {
                            String productName = entry.getKey();
                            ProductSalesData salesData = entry.getValue();

                            // Create a TextView for each product's sales data
                            TextView productDetailsTextView = new TextView(this);
                            productDetailsTextView.setText(productName + "\nSold: " + salesData.getQuantitySold() + "\nTotal Price: " + salesData.getTotalPrice());
                            productDetailsTextView.setTextSize(16);
                            productDetailsTextView.setTextColor(getResources().getColor(R.color.dark));
                            productDetailsTextView.setPadding(20, 15, 20, 20);
                            productDetailsTextView.setBackgroundColor(getResources().getColor(R.color.white));
                            productDetailsTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ));

                            // Set rounded corners using a drawable shape
                            productDetailsTextView.setBackgroundResource(R.drawable.dialogs_background);

                            // Add the TextView to the layout
                            layout.addView(productDetailsTextView);
                        }
                    } else {
                        TextView noDataTextView = new TextView(this);
                        noDataTextView.setText("No sales data available for the selected date range.");
                        noDataTextView.setTextColor(getResources().getColor(R.color.dark));
                        layout.addView(noDataTextView);
                    }

                    builder.setView(scrollView);  // Set the scrollable view
                    builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
                    builder.show();

                } else {
                    Toast.makeText(vendor_report_generation.this, "No sales data available for the selected month.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(vendor_report_generation.this, "Failed to fetch product data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Calculate total sales for a given DataSnapshot (month/year)
    private int calculateTotalSales(DataSnapshot snapshot) {
        int totalSales = 0;
        for (DataSnapshot weekSnapshot : snapshot.getChildren()) {
            Integer weekSales = weekSnapshot.child("totalSales").getValue(Integer.class);
            if (weekSales != null) {
                totalSales += weekSales;
            }
        }
        return totalSales;
    }

    private void updatePieChart(PieChart pieChart, List<PieEntry> entries, String label) {
        if (entries.isEmpty()) {
            pieChart.clear();
            return;
        }

        // PieDataSet with entries and label
        PieDataSet dataSet = new PieDataSet(entries, label);

        // Custom colors for different slices
        dataSet.setColors(new int[]{
                ContextCompat.getColor(this, R.color.color1),
                ContextCompat.getColor(this, R.color.color2),
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this, R.color.color4),
                ContextCompat.getColor(this, R.color.color5),
                ContextCompat.getColor(this, R.color.color6),
                ContextCompat.getColor(this, R.color.color7),
                ContextCompat.getColor(this, R.color.color8),
                ContextCompat.getColor(this, R.color.color9),
                ContextCompat.getColor(this, R.color.color10),
                ContextCompat.getColor(this, R.color.color11),
                ContextCompat.getColor(this, R.color.color12)
        });

        // Set value text color and size
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.white));
        dataSet.setValueTextSize(14f);

        // Create PieData and apply a percentage formatter
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ""; // Initially hide values
            }
        });

        // PieChart customization
        pieChart.setUsePercentValues(true); // Show values as percentages
        pieChart.setDrawHoleEnabled(true); // Enable hole in the center
        pieChart.setHoleRadius(58f); // Radius of the hole in the center
        pieChart.setHoleColor(Color.WHITE); // Hole color
        pieChart.setTransparentCircleRadius(61f); // Slightly larger transparent circle

        // Customize center text
        pieChart.setCenterText(label); // Set initial label
        pieChart.setCenterTextSize(15f); // Center text size
        pieChart.setCenterTextColor(ContextCompat.getColor(this, R.color.dark));

        // Add animation
        pieChart.animateY(1400, Easing.EaseInOutQuad); // Animate the Y-axis with easing

        // Disable description and entry labels
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);

        // Disable the legend
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false); // Disable legend

        // Set data to PieChart and refresh it
        pieChart.setData(data);
        pieChart.invalidate(); // Refresh chart

        // Detect clicks inside the center hole of the PieChart
        pieChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Get the chart's center coordinates
                    MPPointF center = pieChart.getCenterCircleBox();
                    float radius = pieChart.getRadius();
                    float holeRadius = pieChart.getHoleRadius() / 100f * radius;
                    float distanceToCenter = distance(event.getX(), event.getY(), center.x, center.y);

                    // Check if the touch is within the hole (center area)
                    if (distanceToCenter <= holeRadius) {
                        // Show dialog with all sales information
                        showSalesInfoDialog(label, entries); // Show the dialog when tapped inside the center
                        return true;
                    }
                }
                return false;
            }

            private float distance(float x1, float y1, float x2, float y2) {
                float dx = x1 - x2;
                float dy = y1 - y2;
                return (float) Math.sqrt(dx * dx + dy * dy);
            }

        });

        // Add a listener to update chart when a slice is selected
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            private int clickCount = 0; // To track the number of clicks

            @Override
            public void onValueSelected(com.github.mikephil.charting.data.Entry e, Highlight h) {
                // Increment click count
                clickCount++;

                // Get the selected PieEntry
                PieEntry selectedEntry = (PieEntry) e;

                // Determine which value to show based on click count
                if (clickCount == 1) {
                    // Show month name
                    data.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getPieLabel(float value, PieEntry pieEntry) {
                            return convertMonthToName(pieEntry.getLabel()); // Show month name
                        }
                    });
                    pieChart.setCenterText("Date"); // Update center text to "Month"
                } else if (clickCount == 2) {
                    // Show sales value
                    data.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return "₱" + String.format("%.2f", value); // Show sales amount
                        }

                        @Override
                        public String getPieLabel(float value, PieEntry pieEntry) {
                            return "₱" + String.format("%.2f", pieEntry.getValue()); // Show sales amount
                        }
                    });
                    pieChart.setCenterText("Sales"); // Update center text to "Sales"
                } else if (clickCount == 3) {
                    // Show percentage
                    data.setValueFormatter(new PercentFormatter(pieChart)); // Show percentages
                    pieChart.setCenterText("Percent"); // Update center text to "Percent"
                    clickCount = 0; // Reset the count after the third click
                }

                pieChart.invalidate(); // Refresh the chart with the new format
            }

            @Override
            public void onNothingSelected() {
                // Reset to hide values when nothing is selected
                data.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return ""; // Hide values
                    }
                });
                clickCount = 0; // Reset click count when nothing is selected
                pieChart.invalidate(); // Refresh chart
                pieChart.setCenterText(label); // Reset to original label
            }
        });
    }

    private void showSalesInfoDialog(String label, List<PieEntry> entries) {
        // Create a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(label + " Information");

        // Prepare a message with all the entries
        StringBuilder message = new StringBuilder();

        for (PieEntry entry : entries) {
            String entryLabel = entry.getLabel();

            // Convert the month number to name using the helper method
            String convertedLabel = convertMonthToName(entryLabel);

            // Format the message
            message.append("Date: ").append(convertedLabel)
                    .append("\nSales: ₱").append(String.format("%.2f", entry.getValue()))
                    .append("\n\n");  // Double newline for spacing between entries
        }

        // Set the message in the dialog
        builder.setMessage(message.toString());

        // Add an OK button to dismiss the dialog
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Helper method to convert month number to name
    private String convertMonthToName(String monthNumber) {
        switch (monthNumber) {
            case "01":
                return "January";
            case "02":
                return "February";
            case "03":
                return "March";
            case "04":
                return "April";
            case "05":
                return "May";
            case "06":
                return "June";
            case "07":
                return "July";
            case "08":
                return "August";
            case "09":
                return "September";
            case "10":
                return "October";
            case "11":
                return "November";
            case "12":
                return "December";
            default:
                return monthNumber; // Fallback to original value
        }
    }

    private void updateBarChart(BarChart barChart, List<BarEntry> entries) {
        if (entries.isEmpty()) {
            barChart.clear();
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Total Quantity");
        // Set colors for each bar
        int[] colors = new int[]{
                ContextCompat.getColor(this, R.color.green3),
                ContextCompat.getColor(this, R.color.green1),
                ContextCompat.getColor(this, R.color.green2),
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this, R.color.green)
        };// Set color for the bars

        dataSet.setColors(colors);
        BarData barData = new BarData(dataSet);
        barData.setValueTextColor(ContextCompat.getColor(this, R.color.dark)); // Set value text color
        barData.setValueTextSize(10f); // Set value text size

        // Configure BarChart
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.setDrawGridBackground(false);
        barChart.setBackgroundColor(ContextCompat.getColor(this, R.color.dw1));
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setTextColor(ContextCompat.getColor(this, R.color.dark));
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"Week 1", "Week 2", "Week 3", "Week 4", "Week 5"}));
        barChart.getAxisLeft().setTextColor(ContextCompat.getColor(this, R.color.dark));
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(1500, Easing.EaseInOutQuad);

        // Customize X-axis labels if needed
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // Only show 1 bar per week
        xAxis.setLabelCount(entries.size()); // Set label count to number of weeks
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return "Week " + (int) value; // Format label
            }
        });

        // Refresh the chart
        barChart.invalidate(); // Refresh chart
    }

    private void showItemsDialog(String category) {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.vendor_sales_item_list, null);

        // Initialize RecyclerView in the dialog
        RecyclerView itemsRecyclerView = dialogView.findViewById(R.id.itemsRecyclerView);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Item> itemList = new ArrayList<>();
        ItemAdapter adapter = new ItemAdapter(itemList);
        itemsRecyclerView.setAdapter(adapter);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        builder.setView(dialogView)
                .setTitle(category + " Items")
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                .create()
                .show();

        // Fetch items from Firebase based on selected date and category
        String selectedDate = selectedDateTextView.getText().toString();
        String[] dateParts = selectedDate.split("-");
        String selectedYear = dateParts[0];
        String selectedMonth = dateParts[1];

        reportsRef.child(userId).child("sales").child(selectedYear).child(selectedMonth).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot monthSnapshot = task.getResult();
                if (monthSnapshot.exists()) {
                    for (DataSnapshot weekSnapshot : monthSnapshot.getChildren()) {
                        String startDate = weekSnapshot.child("startDate").getValue(String.class);
                        String endDate = weekSnapshot.child("endDate").getValue(String.class);

                        if (isDateInRange(selectedDate, startDate, endDate)) {
                            // If "Others" is selected, gather all uncategorized items
                            if (category.equals("Others")) {
                                DataSnapshot categorySalesMap = weekSnapshot.child("categorySalesMap");
                                for (DataSnapshot categorySnapshot : categorySalesMap.getChildren()) {
                                    String currentCategory = categorySnapshot.getKey();
                                    if (!currentCategory.equals("Vegetables") &&
                                            !currentCategory.equals("Fruits") &&
                                            !currentCategory.equals("Dried Fish") &&
                                            !currentCategory.equals("Condiments")) {

                                        for (DataSnapshot itemSnapshot : categorySnapshot.child("itemSalesMap").getChildren()) {
                                            String itemName = itemSnapshot.child("name").getValue(String.class);
                                            int itemPrice = itemSnapshot.child("price").getValue(Integer.class);
                                            int itemQuantity = itemSnapshot.child("quantity").getValue(Integer.class);

                                            // Add the item to the list
                                            itemList.add(new Item(itemName, itemPrice, itemQuantity));
                                        }
                                    }
                                }
                            } else {
                                // Fetch items for the selected category
                                DataSnapshot categorySnapshot = weekSnapshot.child("categorySalesMap").child(category);
                                if (categorySnapshot.exists()) {
                                    for (DataSnapshot itemSnapshot : categorySnapshot.child("itemSalesMap").getChildren()) {
                                        String itemName = itemSnapshot.child("name").getValue(String.class);
                                        int itemPrice = itemSnapshot.child("price").getValue(Integer.class);
                                        int itemQuantity = itemSnapshot.child("quantity").getValue(Integer.class);

                                        // Add the item to the list
                                        itemList.add(new Item(itemName, itemPrice, itemQuantity));
                                    }
                                }
                            }

                            // Notify the adapter to update the RecyclerView
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            } else {
                Toast.makeText(vendor_report_generation.this, "Failed to fetch items.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to create a notification channel (required for Android 8.0 and above)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Message Notifications";
            String description = "Channel for unread message notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void sendNotification(int unreadCount) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, vendor_messages.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Use FLAG_IMMUTABLE to ensure the PendingIntent is immutable
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo) // Set your notification icon here
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo)) // Optional large icon
                .setContentTitle("Unread Messages")
                .setContentText("You have " + unreadCount + " unread messages from buyers.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Get the NotificationManagerCompat instance
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        // Display the notification
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(1, builder.build());
    }

    // Modified checkForUnreadMessages method to include notification
    private void checkForUnreadMessages() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats");

        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unreadCount = 0;
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot usersSnapshot = chatSnapshot.child("users");
                    DataSnapshot messagesSnapshot = chatSnapshot.child("messages");

                    String receiverId = usersSnapshot.child("receiverId").getValue(String.class);
                    if (receiverId != null && receiverId.equals(userId)) {
                        for (DataSnapshot messageSnapshot : messagesSnapshot.getChildren()) {
                            String category = messageSnapshot.child("category").getValue(String.class);
                            Boolean isRead = messageSnapshot.child("isRead").getValue(Boolean.class);
                            if ("buyer".equals(category) && Boolean.FALSE.equals(isRead)) {
                                unreadCount++;
                            }
                        }
                    }
                }

                if (unreadCount > 0) {
                    messageBadge.setVisibility(View.VISIBLE);
                    messageBadge.setText(String.valueOf(unreadCount));

                    // Get last notified count from SharedPreferences
                    int lastNotifiedCount = sharedPreferences.getInt(PREF_LAST_NOTIFIED_COUNT, -1);

                    // Only send notification if unreadCount is greater than lastNotifiedCount
                    if (unreadCount > lastNotifiedCount) {
                        sendNotification(unreadCount);
                    }

                    // Update last notified count in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(PREF_LAST_NOTIFIED_COUNT, unreadCount);
                    editor.apply();
                } else {
                    messageBadge.setVisibility(View.GONE);

                    // Update SharedPreferences to reflect zero count
                    int lastNotifiedCount = sharedPreferences.getInt(PREF_LAST_NOTIFIED_COUNT, -1);

                    if (lastNotifiedCount > 0) {
                        // Update the count to zero in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(PREF_LAST_NOTIFIED_COUNT, 0);
                        editor.apply();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_report_generation.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(vendor_report_generation.this, vendor_homepage.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the network receiver
        unregisterReceiver(networkReceiver);
    }
}
