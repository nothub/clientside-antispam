package org.zerobzerot.antispam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GSON {
    final static Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .create();
}
