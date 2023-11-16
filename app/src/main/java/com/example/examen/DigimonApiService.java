package com.example.examen;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DigimonApiService {
    @GET("api/digimon")
    Call<DigimonModel> getDigimon();
}
