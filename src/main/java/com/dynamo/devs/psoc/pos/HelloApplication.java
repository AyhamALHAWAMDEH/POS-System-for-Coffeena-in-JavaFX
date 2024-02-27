package com.dynamo.devs.psoc.pos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class HelloApplication extends Application {

    // متغير لتتبع المنتجات المضافة وكمياتها
    private static Map<String, Integer> productQuantities = new HashMap<>();
    private static Map<String, Double> productPrices = new HashMap<>(); // لتخزين أسعار المنتجات

    private static VBox productListContainer; // سيحتوي على قائمة المنتجات
    private static int quantity = 0; // يجب أن يكون متغير الكمية متاحاً عبر الفئة


    
    @Override
    public void start(Stage stage) throws IOException {
        Locale.setDefault(Locale.US); // وضع هذا السطر في بداية دالة start

// تهيئة أسعار المنتجات
        initializeProductPrices();
        // الحصول على أبعاد الشاشة
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Font.loadFont(getClass().getResourceAsStream("/Lato-Regular.ttf"), 18);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        StackPane root = new StackPane(); // استخدام StackPane كجذر
        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

        // إضافة ملف CSS إلى المشهد
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // إنشاء AnchorPane للخلفية باللون البني
        AnchorPane brownBackground = new AnchorPane();
        brownBackground.setStyle("-fx-background-color: #38220F;");
        brownBackground.prefWidthProperty().bind(scene.widthProperty().divide(3));
        brownBackground.prefHeightProperty().bind(scene.heightProperty());

        // إنشاء AnchorPane للخلفية باللون البيج
        AnchorPane beigeBackground = new AnchorPane();
        beigeBackground.setStyle("-fx-background-color: #ECE0D1;");
        beigeBackground.prefWidthProperty().bind(scene.widthProperty().multiply(2).divide(3));
        beigeBackground.prefHeightProperty().bind(scene.heightProperty());

        // إضافة HBox مع الخلفيات إلى root
        HBox background = new HBox(beigeBackground, brownBackground);
        root.getChildren().add(background); // لا تضيف الـ 0 هنا

        // إنشاء VBox للنصوص والبطاقات وإضافتها إلى root
        VBox contentBox = new VBox();
        contentBox.setAlignment(Pos.TOP_LEFT); // تحديد موقع البداية للعناصر داخل VBox
        contentBox.setPadding(new Insets(50, 0, 0, 100)); // تحديد الهوامش

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10); // التباعد الأفقي بين البطاقات
        gridPane.setVgap(10); // التباعد العمودي بين البطاقات

// البيانات للبطاقات
        String[][] cardData = {
                {"Cappuccino", "/images/cappuccino.png", "25 ml", "2.99 $"},
                {"With cream", "/images/with_cream.png", "25 ml", "2.89 $"},
                {"Espresso", "/images/espresso.png", "25 ml", "3.99 $"},
                {"To go", "/images/to_go.png", "25 ml", "3.99 $"},
                {"Iced late", "/images/iced_late.png", "25 ml", "1.99 $"},
                {"Irich", "/images/irich.png", "25 ml", "2.59 $"},
                {"Tea", "/images/tea.png", "25 ml", "2.99 $"},
                {"Matcha", "/images/matcha.png", "25 ml", "3.49 $"},
        };



// إضافة بطاقات إلى GridPane
        int cardIndex = 0;
        for (int row = 0; row < 2; row++) { // صفين
            for (int col = 0; col < 4; col++) { // أربعة أعمدة
                if (cardIndex < cardData.length) {
                    String[] data = cardData[cardIndex++];
                    VBox card = createCard(data[0], data[1], data[2], data[3]);
                    GridPane.setMargin(card, new Insets(5)); // هوامش حول البطاقة
                    gridPane.add(card, col, row); // إضافة البطاقة إلى الخلية (col, row)
                }
            }
        }



        // إضافة النص إلى contentBox
        Label titleLabel = new Label("Welcome to Coffeena");
        titleLabel.getStyleClass().add("title-label");
        contentBox.getChildren().add(titleLabel); // إضافة النص إلى contentBox

        Label subTitleLabel = new Label("Choose your favorite coffee as you like!");
        subTitleLabel.getStyleClass().add("sub-title-label");
        contentBox.getChildren().add(subTitleLabel); // إضافة النص إلى contentBox



        // إضافة GridPane إلى الواجهة
        contentBox.getChildren().add(gridPane);
        // الآن إضافة contentBox فوق background في StackPane
        root.getChildren().add(contentBox);


// تعريف VBox للفواتير داخل AnchorPane البني
        VBox billsBox = new VBox();
        billsBox.setAlignment(Pos.TOP_LEFT);
        AnchorPane.setTopAnchor(billsBox, 50.0); // بادنغ من الأعلى
        AnchorPane.setLeftAnchor(billsBox, 50.0); // بادنغ من اليسار
        AnchorPane.setBottomAnchor(billsBox, 50.0); // بادنغ من الأسفل
        AnchorPane.setRightAnchor(billsBox, 100.0); // بادنغ من اليمين

        // إضافة العنوان الرئيسي للفواتير
        Label billsTitle = new Label("Bills");
        billsTitle.setFont(Font.font("Lato", FontWeight.BOLD, 36));
        billsTitle.setTextFill(Color.web("#ECE0D1"));
        VBox.setMargin(billsTitle, new Insets(0, 0, 32, 0)); // الأعلى، اليمين، الأسفل، اليسار

        // إضافة العناوين الفرعية للأعمدة
        HBox headers = new HBox(100);
        headers.setAlignment(Pos.BASELINE_LEFT);
        headers.getChildren().addAll(
                createHeaderLabel("Article", "Lato", FontWeight.NORMAL, 24),
                createHeaderLabel("QTY", "Lato", FontWeight.NORMAL, 24),
                createHeaderLabel("Total", "Lato", FontWeight.NORMAL, 24)
        );

        // إضافة خط الفاصل
        Region divider = new Region();
        divider.setPrefWidth(screenBounds.getWidth() * 1/3 - 40);
        divider.setStyle("-fx-border-color: #ECE0D1; -fx-border-width: 1 0 0 0;");
        VBox.setMargin(divider, new Insets(0, 0, 32, 0));

        // إضافة كل العناصر إلى VBox الخاص بالفواتير
        billsBox.getChildren().addAll(billsTitle, headers, divider);

        brownBackground.getChildren().add(billsBox);

        // إعداد منطقة عرض المنتجات المضافة
        productListContainer = new VBox(10);
        productListContainer.setAlignment(Pos.TOP_CENTER);
        // إضافة منطقة عرض المنتجات المضافة إلى billsBox
        billsBox.getChildren().add(productListContainer);


        // تحديث عرض القائمة
        ProductSummary summary = updateProductListDisplay();



        stage.setTitle("Coffeena");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.show();
    }

    private void initializeProductPrices() {
        productPrices.put("Cappuccino", 2.99);
        productPrices.put("With cream", 2.89);
        productPrices.put("Espresso", 3.99);
        productPrices.put("To go", 3.99);
        productPrices.put("Iced late", 1.99);
        productPrices.put("Irich", 2.59);
        productPrices.put("Tea", 2.99);
        productPrices.put("Matcha", 3.49);
    }
    public VBox createCard(String title, String imagePath, String volume, String price) {
        VBox card = new VBox();
        card.getStyleClass().add("card");
        card.setPrefSize(200, 260);
        card.setStyle("-fx-border-radius: 12; -fx-background-radius: 12;");

        // القسم العلوي للصورة
        StackPane imageContainer = new StackPane();
        imageContainer.setStyle("-fx-background-color: #967259; -fx-background-radius: 12 12 0 0;");
        imageContainer.setPrefHeight(150);
        imageContainer.setPrefHeight(150);
        ImageView imageView = new ImageView(new Image(HelloApplication.class.getResourceAsStream(imagePath)));
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);
        StackPane.setAlignment(imageView, Pos.CENTER);
        imageContainer.getChildren().add(imageView);

        // القسم السفلي للنصوص والأزرار
        VBox textContainer = new VBox();
        textContainer.setStyle("-fx-background-color: #DBC1AC; -fx-background-radius: 0 0 12 12; -fx-padding: 10;");
        textContainer.setPrefHeight(110);
        textContainer.setPrefHeight(110);

        Label titleLabel = new Label(title.toUpperCase());
        titleLabel.getStyleClass().add("card-title");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #140C05;");

        Label volumeLabel = new Label(volume);
        volumeLabel.getStyleClass().add("card-volume");
        volumeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #140C05;");

        // إنشاء Label لعرض الكمية
        Label quantityLabel = new Label(String.valueOf(quantity));
        quantityLabel.getStyleClass().add("quantity-label");
        quantityLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #140C05;");

        // إنشاء ImageView لزر النقصان
        ImageView decreaseImageView = new ImageView(new Image(HelloApplication.class.getResourceAsStream("/images/minus.png")));
        decreaseImageView.setFitWidth(18); // حسب الحجم المطلوب للصورة
        decreaseImageView.setFitHeight(18); // حسب الحجم المطلوب للصورة
        decreaseImageView.getStyleClass().add("control-image");
        // منطق الزر للنقصان
        decreaseImageView.setOnMouseClicked(event -> {
            if (quantity > 0) {
                quantity--; // نقصان الكمية
                quantityLabel.setText(String.valueOf(quantity)); // تحديث العرض
            }
        });

        // إنشاء ImageView لزر الزيادة
        ImageView increaseImageView = new ImageView(new Image(HelloApplication.class.getResourceAsStream("/images/plus.png")));
        increaseImageView.setFitWidth(18); // حسب الحجم المطلوب للصورة
        increaseImageView.setFitHeight(18); // حسب الحجم المطلوب للصورة
        increaseImageView.getStyleClass().add("control-image");
        // منطق الزر للزيادة
        increaseImageView.setOnMouseClicked(event -> {
            // افترض أن لديك متغير اسم المنتج
            String productName = title; // تأكد من أن لديك هذا المتغير متوفرًا

            // تحديث كمية المنتج في الخريطة
            productQuantities.put(productName, productQuantities.getOrDefault(productName, 0) + 1);

            // تحديث القائمة المعروضة
            ProductSummary summary = updateProductListDisplay();

            // تحديث الكمية المعروضة داخل البطاقة إذا لزم الأمر
            // قد تحتاج إلى تعديل هذا بناءً على كيفية تتبعك للكميات
            quantityLabel.setText(String.valueOf(productQuantities.get(productName)));
        });


        Label priceLabel = new Label(price);
        priceLabel.getStyleClass().add("card-price");
        priceLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #140C05;");

        // إنشاء HBox للأزرار والكمية
        HBox controlsBox = new HBox(5); // استخدم 5px للتباعد بين الأزرار والكمية
        controlsBox.setAlignment(Pos.CENTER_LEFT);
        controlsBox.getChildren().addAll(decreaseImageView, quantityLabel, increaseImageView);

        // إنشاء HBox للسعر وضبط التباعد بحيث يكون على يمين الأزرار
        HBox priceBox = new HBox();
        priceBox.setAlignment(Pos.CENTER_RIGHT);
        priceBox.getChildren().add(priceLabel);

        // إنشاء HBox رئيسي لدمج الأزرار والسعر في سطر واحد
        HBox controlAndPriceBox = new HBox(5); // استخدم 5px للتباعد بين الأزرار والسعر
        controlAndPriceBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(priceBox, Priority.ALWAYS); // تمدد priceBox لدفع السعر إلى اليمين

        // إضافة العناصر إلى الـ HBox
        controlAndPriceBox.getChildren().add(decreaseImageView);
        controlAndPriceBox.getChildren().add(quantityLabel);
        controlAndPriceBox.getChildren().add(increaseImageView);

        // إضافة controlsBox و priceBox إلى controlAndPriceBox
        controlAndPriceBox.getChildren().addAll(controlsBox, priceBox);

        // إضافة عنوان المنتج وحجمه إلى textContainer قبل إضافة controlAndPriceBox
        textContainer.getChildren().addAll(titleLabel, volumeLabel);

        // إضافة controlAndPriceBox إلى textContainer بعد إضافة عنوان المنتج وحجمه
        textContainer.getChildren().add(controlAndPriceBox);

        // إضافة imageContainer و textContainer إلى VBox الرئيسي للبطاقة
        card.getChildren().addAll(imageContainer, textContainer);


        return card;
    }

    public class ProductSummary {
        private double subtotal;
        private double tax;
        private double total;

        // Constructor
        public ProductSummary(double subtotal, double tax, double total) {
            this.subtotal = subtotal;
            this.tax = tax;
            this.total = total;
        }

        // Getter for subtotal
        public double getSubtotal() {
            return subtotal;
        }

        // Getter for tax
        public double getTax() {
            return tax;
        }

        // Getter for total
        public double getTotal() {
            return total;
        }

        // أي طرق أخرى ضرورية
    }
    private ProductSummary  updateProductListDisplay() {
        productListContainer.getChildren().clear(); // إزالة كل المحتويات الحالية

        GridPane productGrid = new GridPane();
        productGrid.setHgap(10); // التباعد الأفقي
        productGrid.setVgap(10); // التباعد العمودي

        ColumnConstraints col1 = new ColumnConstraints(170, 170, Double.MAX_VALUE); // عرض العمود الأول
        ColumnConstraints col2 = new ColumnConstraints(130, 130, Double.MAX_VALUE); // عرض العمود الثاني
        ColumnConstraints col3 = new ColumnConstraints(70, 70, Double.MAX_VALUE); // عرض العمود الثالث
        productGrid.getColumnConstraints().addAll(col1, col2, col3); // إضافة قيود الأعمدة إلى الـ GridPane

        GridPane taxGrid = new GridPane();
        taxGrid.setHgap(10); // التباعد الأفقي
        taxGrid.setVgap(10); // التباعد العمودي

        ColumnConstraints col6 = new ColumnConstraints(310, 310, Double.MAX_VALUE); // عرض العمود الأول
        ColumnConstraints col7 = new ColumnConstraints(70, 70, Double.MAX_VALUE); // عرض العمود الثاني
        taxGrid.getColumnConstraints().addAll(col6, col7); // إضافة قيود الأعمدة إلى الـ GridPane

        GridPane totalGrid = new GridPane();
        totalGrid.setHgap(10); // التباعد الأفقي
        totalGrid.setVgap(10); // التباعد العمودي

        ColumnConstraints col8 = new ColumnConstraints(310, 310, Double.MAX_VALUE); // عرض العمود الأول
        ColumnConstraints col9 = new ColumnConstraints(70, 70, Double.MAX_VALUE); // عرض العمود الثاني
        totalGrid.getColumnConstraints().addAll(col8, col9); // إضافة قيود الأعمدة إلى الـ GridPane


        int rowIndex = 0; // بداية الصفوف في GridPane
        double subtotal = 0.0; // Initialize subtotal

        // Constants for tax rate and initial values

        final double TAX_RATE = 0.10;

        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            if (entry.getValue() > 0) {
                String product = entry.getKey();
                int qty = entry.getValue();
                double price = productPrices.getOrDefault(product, 0.0); // الحصول على سعر المنتج
                double totalPrice = qty * price; // حساب السعر الكلي

                // إنشاء وتعديل عناصر الصف
                Label productLabel = new Label(product);
                productLabel.setFont(Font.font("Lato", FontWeight.NORMAL, 18)); // تعيين الخط
                productLabel.setTextFill(Color.web("#ECE0D1")); // تعيين لون الخط

                Label quantityLabel = new Label("x " + qty);
                quantityLabel.setFont(Font.font("Lato", FontWeight.NORMAL, 18)); // تعيين الخط
                quantityLabel.setTextFill(Color.web("#ECE0D1")); // تعيين لون الخط

                Label totalPriceLabel = new Label(String.format(Locale.US, "%.2f $", totalPrice));
                totalPriceLabel.setFont(Font.font("Lato", FontWeight.NORMAL, 18)); // تعيين الخط
                totalPriceLabel.setTextFill(Color.web("#ECE0D1")); // تعيين لون الخط

                // إضافة العناصر إلى الـ GridPane
                productGrid.add(productLabel, 0, rowIndex); // العمود الأول
                productGrid.add(quantityLabel, 1, rowIndex); // العمود الثاني
                productGrid.add(totalPriceLabel, 2, rowIndex); // العمود الثالث

                rowIndex++; // زيادة الصف للإضافة القادمة

                subtotal += entry.getValue() * productPrices.getOrDefault(entry.getKey(), 0.0);



            }
        }


        productListContainer.getChildren().addAll(productGrid);

        if (!productQuantities.isEmpty()) {
            // Add a new divider after the list of products
            Region newDivider = new Region();
            newDivider.setStyle("-fx-border-color: #ECE0D1; -fx-border-width: 1 0 0 0;");
            // Set the preferred width for the divider here, you may need to adjust this value
            newDivider.setPrefWidth(300); // Example width, adjust as necessary
            VBox.setMargin(newDivider, new Insets(32, 0, 32, 0)); // Margin around the new divider
            productListContainer.getChildren().add(newDivider);


            GridPane subtotalGrid = new GridPane();
            subtotalGrid.setHgap(10); // التباعد الأفقي
            subtotalGrid.setVgap(10); // التباعد العمودي

            ColumnConstraints col4 = new ColumnConstraints(310, 310, Double.MAX_VALUE); // عرض العمود الأول
            ColumnConstraints col5 = new ColumnConstraints(70, 70, Double.MAX_VALUE); // عرض العمود الثاني
            subtotalGrid.getColumnConstraints().addAll(col4, col5); // إضافة قيود الأعمدة إلى الـ GridPane

            // Subtotal title label
            Label subtotalTitleLabel = new Label("Subtotal");
            subtotalTitleLabel.setFont(Font.font("Lato", FontWeight.NORMAL, 18));
            subtotalTitleLabel.setTextFill(Color.web("#ECE0D1"));

            // Subtotal value label
            Label subtotalValueLabel = new Label(String.format(Locale.US, "%.2f $", subtotal));
            subtotalValueLabel.setFont(Font.font("Lato", FontWeight.NORMAL, 18));
            subtotalValueLabel.setTextFill(Color.web("#ECE0D1"));

            // Add labels to the GridPane
            subtotalGrid.add(subtotalTitleLabel, 0, 0); // Add to first column
            subtotalGrid.add(subtotalValueLabel, 1, 0); // Add to second column

            // Add GridPane to productListContainer
            productListContainer.getChildren().add(subtotalGrid);

        } else {
            Label emptyMessageLabel = new Label("No article have been added yet");
            emptyMessageLabel.setFont(Font.font("Lato", FontWeight.NORMAL, 24));
            emptyMessageLabel.setTextFill(Color.web("#ECE0D1"));
            emptyMessageLabel.setAlignment(Pos.CENTER);
            VBox.setMargin(emptyMessageLabel, new Insets(50, 0, 50, 0));
            productListContainer.setAlignment(Pos.CENTER);
            productListContainer.getChildren().add(emptyMessageLabel);
        }

        // Calculate tax and total
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        Region divider2 = new Region();
        divider2.setStyle("-fx-border-color: #ECE0D1; -fx-border-width: 1 0 0 0;");
        // Set the preferred width for the divider here, you may need to adjust this value
        divider2.setPrefWidth(300); // Example width, adjust as necessary
        VBox.setMargin( divider2, new Insets(32, 0, 32, 0)); // Margin around the new divider

        // Tax title label
        Label taxTitleLabel = new Label("Tax");
        taxTitleLabel.setFont(Font.font("Lato", FontWeight.NORMAL, 18));
        taxTitleLabel.setTextFill(Color.web("#ECE0D1"));

        // Tax value label
        Label taxValueLabel = new Label(String.format(Locale.US, "%.2f $",  tax));
        taxValueLabel.setFont(Font.font("Lato", FontWeight.NORMAL, 18));
        taxValueLabel.setTextFill(Color.web("#ECE0D1"));

        // Add labels to the GridPane
        taxGrid.add(taxTitleLabel, 0, 0); // Add to first column
        taxGrid.add(taxValueLabel, 1, 0); // Add to sec


        // Total title label
        Label totalTitleLabel = new Label("Total");
        totalTitleLabel.setFont(Font.font("Lato", FontWeight.NORMAL, 18));
        totalTitleLabel.setTextFill(Color.web("#ECE0D1"));

        // Total value label
        Label totalValueLabel = new Label(String.format(Locale.US, "%.2f $",   total));
        totalValueLabel.setFont(Font.font("Lato", FontWeight.NORMAL, 18));
        totalValueLabel.setTextFill(Color.web("#ECE0D1"));

        // Add labels to the GridPane
        totalGrid.add(totalTitleLabel, 0, 0); // Add to first column
        totalGrid.add(totalValueLabel, 1, 0); // Add to sec

        // Add the checkout button with required styling and margin
        Button checkoutButton = new Button("CHECK OUT");
        checkoutButton.setFont(Font.font("Lato", FontWeight.NORMAL, 28));
        checkoutButton.setTextFill(Color.web("#38220F"));
        checkoutButton.setStyle("-fx-background-color: #ECE0D1; -fx-padding: 10; -fx-background-radius: 12;");
        checkoutButton.setMaxWidth(Double.MAX_VALUE);
        // Set margins around the checkout button
        Insets buttonMargins = new Insets(32, 100, 50, 100); // Top, Right, Bottom, Left
        VBox.setMargin(checkoutButton, buttonMargins);
        checkoutButton.toFront();

// إنشاء كائن ProductSummary
        ProductSummary summary = new ProductSummary(subtotal, tax, total);
        checkoutButton.setOnAction(event -> {
            System.out.println("Checkout button clicked!");
            createPDFInvoice(summary);
        });

        productListContainer.getChildren().addAll( divider2, taxGrid, totalGrid, checkoutButton);

        return new ProductSummary(subtotal, tax, total);


    }

    // تعديل توقيع الدالة لتقبل كائن ProductSummary
    private void createPDFInvoice(ProductSummary summary) {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        try {
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream("Invoice.pdf"));
            document.open();
            com.itextpdf.text.Font font = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.COURIER, 16, com.itextpdf.text.BaseColor.BLACK);

            // Add PDF content with the new ProductSummary parameter
            addInvoiceContent(document, font, summary);

        } catch (com.itextpdf.text.DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            // Handle exception, maybe show user an error message
        } finally {
            document.close();
        }
    }


    // تعديل التوقيع واستخدام ProductSummary
    private void addInvoiceContent(com.itextpdf.text.Document document, com.itextpdf.text.Font font, ProductSummary summary) throws com.itextpdf.text.DocumentException {
        document.add(new com.itextpdf.text.Paragraph("Invoice", font));
        document.add(new com.itextpdf.text.Paragraph("Date: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), font));

        // Add invoice details here using ProductSummary
        document.add(new com.itextpdf.text.Paragraph("Subtotal: " + summary.getSubtotal(), font)); // استخدم getSubtotal إذا كان subtotal خاصية بدلاً من حقل عام
        document.add(new com.itextpdf.text.Paragraph("Tax: " + summary.getTax(), font)); // استخدم getTax بنفس الطريقة
        document.add(new com.itextpdf.text.Paragraph("Total: " + summary.getTotal(), font)); // وكذلك getTotal
    }



    private Label createHeaderLabel(String text, String fontFamily, FontWeight weight, int size) {
        Label label = new Label(text);
        label.setFont(Font.font(fontFamily, weight, size));
        label.setStyle("-fx-text-fill: #ECE0D1;"); // استخدم اللون المحدد بدلاً من Color.web للتوافق مع بقية الكود
        return label;
    }

    public static void main(String[] args) {
        launch();

    }
}