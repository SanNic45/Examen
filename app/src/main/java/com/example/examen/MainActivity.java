package com.example.examen;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView digimonImageView = findViewById(R.id.digimonImageView);
        TextView nameTextView = findViewById(R.id.nameDigi);
        TextView trainingLevelTextView = findViewById(R.id.nivel);

        if (isNetworkAvailable()) {
            new FetchDigimonTask(this, nameTextView, trainingLevelTextView, digimonImageView)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static class RetrofitClient {
        private static final String BASE_URL = "https://digimon-api.vercel.app/";

        private static Retrofit retrofit;

        public static Retrofit getRetrofitInstance() {
            if (retrofit == null) {
                Gson gson = new GsonBuilder().setLenient().create(); // Usar Gson con tolerancia lenient
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }
            return retrofit;
        }
    }

    private static class FetchDigimonTask extends AsyncTask<Void, Void, DigimonModel> {
        private final WeakReference<Context> contextRef;
        private final WeakReference<TextView> nameTextViewRef;
        private final WeakReference<TextView> trainingLevelTextViewRef;
        private final WeakReference<ImageView> digimonImageViewRef;

        FetchDigimonTask(Context context, TextView nameTextView, TextView trainingLevelTextView, ImageView digimonImageView) {
            contextRef = new WeakReference<>(context);
            nameTextViewRef = new WeakReference<>(nameTextView);
            trainingLevelTextViewRef = new WeakReference<>(trainingLevelTextView);
            digimonImageViewRef = new WeakReference<>(digimonImageView);
        }

        @Override
        protected DigimonModel doInBackground(Void... voids) {
            try {
                DigimonApiService apiService = RetrofitClient.getRetrofitInstance().create(DigimonApiService.class);
                Call<DigimonModel> call = apiService.getDigimon();
                retrofit2.Response<DigimonModel> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    return response.body();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(DigimonModel digimon) {
            Context context = contextRef.get();
            TextView nameTextView = nameTextViewRef.get();
            TextView trainingLevelTextView = trainingLevelTextViewRef.get();
            ImageView digimonImageView = digimonImageViewRef.get();

            if (digimon != null && nameTextView != null && trainingLevelTextView != null && digimonImageView != null && context != null) {
                nameTextView.setText(context.getString(R.string.digimon_name_placeholder, digimon.getName()));
                trainingLevelTextView.setText(context.getString(R.string.training_level_placeholder, digimon.getLevel()));
                Picasso.get().load(digimon.getImageUrl()).into(digimonImageView);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
