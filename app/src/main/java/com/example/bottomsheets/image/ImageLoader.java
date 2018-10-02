package com.example.bottomsheets.image;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ImageLoader<INPUT, RESULT> {

    private Observable<RESULT> observable;

//    public ImageLoader(INPUT params) {
//        observable = this.buildObservable(params)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    private Observable<RESULT> buildObservable(INPUT params) {
//        return new Observable<RESULT>() {
//            @Override
//            protected void subscribeActual(Observer<? super RESULT> observer) {
//            }
//        }
//    }
}
