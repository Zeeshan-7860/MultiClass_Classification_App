package com.example.imageclassififcation;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.imageclassififcation.ml.Modelzeeshan;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // Model Variables

    private Interpreter tfliteInterpreter;

    // UI Variables
    private Button buttonSelectImage;

    TextToSpeech t1;
    private TextView textViewPrediction;
    ImageView imageView;

    // Image Selection Request Code
    private static final int REQUEST_IMAGE_SELECT = 100;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        buttonSelectImage = findViewById(R.id.button);
        textViewPrediction = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        t1=new TextToSpeech(getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                t1.setLanguage(Locale.UK);
            }
        });


        // Set click listener for image selection button
        buttonSelectImage.setOnClickListener(view -> openGalleryForImage());

        // Initialize TensorFlow Lite Interpreter
        try {
            tfliteInterpreter = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load the TensorFlow Lite model from the assets folder
    private MappedByteBuffer loadModelFile() throws IOException {
        FileInputStream inputStream = new FileInputStream(getAssets().openFd("model.tflite").getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
    }

    // Open gallery to select an image
    private void openGalleryForImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_SELECT);
    }

    // Process the selected image and make predictions using the TensorFlow Lite model
    // Process the selected image and make predictions using the TensorFlow Lite model


    // Process the selected image and make predictions using the TensorFlow Lite model
    @SuppressLint("SetTextI18n")
    private void processSelectedImage(Bitmap image) {
        try {
            Modelzeeshan model = Modelzeeshan.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 32, 32, 3}, DataType.FLOAT32);
            int imageSize = 32;
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for(int i = 0; i < imageSize; i ++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Modelzeeshan.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            String[] classes = new String[]{"Airplane", "Automobile", "Bird", "Cat", "Deer", "Dog", "Frog", "Horse", "Ship", "Truck"};
            if (maxPos == 0){
                t1.speak("An Airplane", TextToSpeech.QUEUE_FLUSH, null);
                textViewPrediction.setText("Prediction: "+classes[maxPos]);
            }
            if (maxPos == 1){
                t1.speak("An Automobile", TextToSpeech.QUEUE_FLUSH, null);
                textViewPrediction.setText("Prediction: "+classes[maxPos]);
            }
            if (maxPos == 2){
                t1.speak("A Bird", TextToSpeech.QUEUE_FLUSH, null);
                textViewPrediction.setText("Prediction: "+classes[maxPos]);
            }
            if (maxPos == 3){
                t1.speak("A Cat", TextToSpeech.QUEUE_FLUSH, null);
                textViewPrediction.setText("Prediction: "+classes[maxPos]);
            }
            if (maxPos == 4){
                t1.speak("A Deer", TextToSpeech.QUEUE_FLUSH, null);
                textViewPrediction.setText("Prediction: "+classes[maxPos]);
            }
            if (maxPos == 5){
                t1.speak("A Dog", TextToSpeech.QUEUE_FLUSH, null);
                textViewPrediction.setText("Prediction: "+classes[maxPos]);
            }
            if (maxPos == 6){
                t1.speak("A Frog", TextToSpeech.QUEUE_FLUSH, null);
                textViewPrediction.setText("Prediction: "+classes[maxPos]);
            }
            if (maxPos == 7){
                t1.speak("a Horse", TextToSpeech.QUEUE_FLUSH, null);
                textViewPrediction.setText("Prediction: "+classes[maxPos]);
            }
            if (maxPos == 8){
                t1.speak("A Ship", TextToSpeech.QUEUE_FLUSH, null);
                textViewPrediction.setText("Prediction: "+classes[maxPos]);
            }
            if (maxPos == 9){
                t1.speak("A Truck", TextToSpeech.QUEUE_FLUSH, null);
                textViewPrediction.setText("Prediction: "+classes[maxPos]);
            }

            ;

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
//
    }


    // Handle the result of the image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            int imageSize = 32;
            if (requestCode == 3) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);


                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                processSelectedImage(image);
                imageView.setImageBitmap(image);
            } else {
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                processSelectedImage(image);
                imageView.setImageBitmap(image);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}