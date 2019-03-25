package com.homer.linefake;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.reactivestreams.Subscriber;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

class RxJava {
    private static final RxJava ourInstance = new RxJava();
    private static RequestQueue requestQueue = null;

    static RxJava getInstance() {
        return ourInstance;
    }

    private RxJava() {
        //TODO  for Volley initialize:  requestQueue = Volley.newRequestQueue(getApplicationContext());
    }
    //TODO Observable created by Single
    public Single<String> getObservableSingle(final int method, final String json_url){
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(final SingleEmitter<String> singleEmitter) throws Exception {
                StringRequest stringRequest = new StringRequest(method, json_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // send message to observor
                                singleEmitter.onSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // send error to observor
                                singleEmitter.onError(error);
                            }
                        });
                // observor start
                requestQueue.add(stringRequest);
            }
        });
    }

    // TODO Observable from future???
    // https://stackoverflow.com/questions/32701331/rxjava-and-volley-requests , not really understand
}
